package onyx.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TornadoRod extends Item {
    private static int cooldownDuration = 30;
    private static final Map<UUID, TornadoData> ACTIVE_TORNADOS = new HashMap<>();
    private static final Map<UUID, Integer> LAST_TORNADO_END_TICK = new HashMap<>();
    private static final int FALL_DAMAGE_GRACE_PERIOD = 100;
    private static final int TORNADO_LOCK_DURATION = 30;

    public TornadoRod(Settings settings) { super(settings); }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClient) {

            // Apply cooldown
            if (player instanceof ServerPlayerEntity serverPlayer) {
                ItemStack stack = player.getStackInHand(hand);
                serverPlayer.getItemCooldownManager().set(stack, cooldownDuration);
            }
            
            // Set data
            TornadoData data = new TornadoData(player.getX(), player.getZ(), FALL_DAMAGE_GRACE_PERIOD);
            ACTIVE_TORNADOS.put(player.getUuid(), data);

            // Propel onwards & reset fall distance
            player.addVelocity(0, 1.5, 0);
            player.velocityModified = true;
            player.fallDistance = 0.0f;

            // Play wind sound effect
            world.playSound(
                null, // No specific player (all nearby players will hear)
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.ENTITY_ENDER_DRAGON_FLAP, // A good wind sound
                SoundCategory.PLAYERS,
                1.0F, // Volume
                1.2F + (float)(Math.random() * 0.4) // Random pitch variation
            );
        }

        player.swingHand(hand);
        return ActionResult.SUCCESS;
    }


    private static class TornadoData {
        private final double lockedX;
        private final double lockedZ;
        private int lockDuration;
        private int graceDuration;
    
        public TornadoData(double lockedX, double lockedZ, int graceDuration) {
            this.lockedX = lockedX;
            this.lockedZ = lockedZ;
            this.lockDuration = TORNADO_LOCK_DURATION;
            this.graceDuration = graceDuration;
        }
    
        public boolean tick() {
            if (lockDuration > 0) lockDuration--;
            graceDuration--;
            return graceDuration > 0;
        }
    
        public boolean isLocking() {
            return lockDuration > 0;
        }
    }

    public static void tickTornado(PlayerEntity player) {
        UUID uuid = player.getUuid();
        TornadoData data = ACTIVE_TORNADOS.get(uuid);

        if (data != null) {
            player.fallDistance = 0.0f;

            if (data.isLocking()) {
                // Only lock movement while in lock phase
                double currentX = player.getX();
                double currentZ = player.getZ();
                if (Math.abs(currentX - data.lockedX) > 0.01 || Math.abs(currentZ - data.lockedZ) > 0.01) {
                    player.updatePosition(data.lockedX, player.getY(), data.lockedZ);
                    player.setVelocity(0, player.getVelocity().y, 0);
                    player.velocityModified = true;
                }

                // Spawn tornado particles (spiral pattern)
                spawnTornadoParticles(player.getWorld(), player);
                
                // Play occasional wind sound for continuous effect
                if (player.age % 10 == 0) {
                    player.getWorld().playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.ENTITY_PHANTOM_FLAP,  // Alternating with another wind sound
                        SoundCategory.PLAYERS,
                        0.6F, // Lower volume for ambient effect
                        1.5F  // Higher pitch for wind sound
                    );
                }
            }

            if (!data.tick()) {
                ACTIVE_TORNADOS.remove(uuid);
                LAST_TORNADO_END_TICK.put(uuid, player.age);
            }
        }
    }

    public static boolean shouldPreventFallDamage(PlayerEntity player) {
        UUID uuid = player.getUuid();

        if (ACTIVE_TORNADOS.containsKey(uuid)) {
            return true;
        }

        int lastEnd = LAST_TORNADO_END_TICK.getOrDefault(uuid, -1000);
        return player.age - lastEnd <= FALL_DAMAGE_GRACE_PERIOD;
    }

    private static void spawnTornadoParticles(World world, PlayerEntity player) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) return;
        
        // Base position at player's location
        double baseX = player.getX();
        double baseY = player.getY();
        double baseZ = player.getZ();
        
        // Only generate particles during the lift period, not at the end
        TornadoData data = ACTIVE_TORNADOS.get(player.getUuid());
        if (data == null || !data.isLocking()) return;
        
        // Tornado parameters
        int baseParticleCount = 2; // Particles per layer
        int layers = 6;           // Number of vertical layers
        double maxHeight = 1.9;    // Maximum height of tornado

        // Animation cycle for rotation
        double rotationSpeed = 0.6;
        double currentAngle = (player.age * rotationSpeed) % (Math.PI * 2);
        
        // Create particles in a rotating pattern around the player
        for (int layer = 0; layer < layers; layer++) {
            // Layer height progression
            double heightPercent = layer / (double)(layers - 1);
            double y = baseY + heightPercent * maxHeight;
            
            // Radius gets slightly smaller toward the top
            double radius = 0.8 - (heightPercent * 0.3);
            
            // Offset each layer's rotation to create a spiral effect
            double layerAngle = currentAngle + (heightPercent * Math.PI * 1.5);
            
            // Particles per layer (more at the bottom, fewer at top)
            int particlesInLayer = baseParticleCount + (int)(baseParticleCount * (1.0 - heightPercent));
            
            for (int i = 0; i < particlesInLayer; i++) {
                // Distribute particles evenly in a circle at this layer
                double angle = layerAngle + (i * Math.PI * 2 / particlesInLayer);
                double x = baseX + Math.cos(angle) * radius;
                double z = baseZ + Math.sin(angle) * radius;
                
                // Calculate rotational velocity (tangential to the circle)
                double vx = Math.cos(angle + Math.PI/2) * 0.05;
                double vz = Math.sin(angle + Math.PI/2) * 0.05;
                double vy = 0.03 + (heightPercent * 0.04); // Upward drift
                
                // Select particle type based on height
                net.minecraft.particle.ParticleEffect particleType;
                if (heightPercent < 0.3) {
                    particleType = ParticleTypes.POOF; // Dust at bottom
                } else if (heightPercent > 0.7) {
                    particleType = ParticleTypes.CLOUD; // Clouds at top
                } else {
                    particleType = ParticleTypes.CAMPFIRE_COSY_SMOKE; // Smoke in middle
                }
                
                // Spawn particles with rotational velocity
                serverPlayer.getServerWorld().spawnParticles(
                    particleType,
                    x, y, z,
                    0, // Count of 0 enables velocity parameters
                    vx, vy, vz, 
                    1.0 // Speed multiplier
                );
            }
        }
        
        // Add some ground effect particles at the base
        if (player.age % 4 == 0) {
            for (int i = 0; i < 3; i++) {
                double angle = Math.random() * Math.PI * 2;
                double distance = 0.3 + Math.random() * 0.9;
                double x = baseX + Math.cos(angle) * distance;
                double z = baseZ + Math.sin(angle) * distance;
                
                // Velocity directed toward the center, then upward
                double vx = (baseX - x) * 0.05;
                double vz = (baseZ - z) * 0.05;
                
                serverPlayer.getServerWorld().spawnParticles(
                    ParticleTypes.CLOUD,
                    x, baseY, z,
                    0,
                    vx, 0.05, vz,
                    1.0
                );
            }
        }
    }
}
