package onyx.items.behaviourmanagers;

import onyx.items.Hookshot;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class HookshotHandler {
    public static final Map<UUID, Long> HOOK_START_TIMES = new HashMap<>();

    private static final double PULLING_SPEED = 0.8;
    private static final long MAX_PULL_TIME_MS = 1250;

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            Iterator<Map.Entry<UUID, Vec3d>> iterator = Hookshot.HOOKED_PLAYERS.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<UUID, Vec3d> entry = iterator.next();
                UUID uuid = entry.getKey();
                Vec3d targetPos = entry.getValue();

                ServerPlayerEntity player = world.getServer().getPlayerManager().getPlayer(uuid);
                if (player == null) {
                    iterator.remove();
                    HOOK_START_TIMES.remove(uuid);
                    continue;
                }

                long now = System.currentTimeMillis();
                long startTime = HOOK_START_TIMES.getOrDefault(uuid, now);
                if (now - startTime > MAX_PULL_TIME_MS) {
                    iterator.remove();
                    HOOK_START_TIMES.remove(uuid);
                    player.setVelocity(Vec3d.ZERO);
                    continue;
                }

                Vec3d currentPos = player.getPos().add(0, 1.0, 0); // aim for torso-ish height
                Vec3d diff = targetPos.subtract(currentPos);
                double distance = diff.length();

                if (distance < 2) {
                    // Arrived close enough to target
                    player.setVelocity(Vec3d.ZERO);
                    iterator.remove();
                    HOOK_START_TIMES.remove(uuid);
                    continue;
                }

                Vec3d pullVec = diff.normalize().multiply(PULLING_SPEED);
                player.setVelocity(pullVec);
                player.velocityModified = true;

                if ((world.getTime() % 2) == 0) {
                    world.playSound(
                        null,
                        player.getBlockPos(),
                        net.minecraft.sound.SoundEvents.BLOCK_CHAIN_PLACE,
                        net.minecraft.sound.SoundCategory.PLAYERS,
                        0.5f, // volume
                        0.5f  // pitch
                    );
                }

                // --- Chain particle trail ---
                Vec3d from = player.getPos().add(0, 1.0, 0); // Slightly above for visual clarity
                Vec3d to = targetPos;

                int particles = 10;
                for (int i = 0; i <= particles; i++) {
                    double progress = i / (double) particles;
                    Vec3d pos = from.lerp(to, progress);

                    world.spawnParticles(
                        ParticleTypes.CRIT, // You can replace with your custom chain particle
                        pos.x, pos.y, pos.z,
                        1, 0, 0, 0, 0.0
                    );
                }
            }
            Iterator<Map.Entry<UUID, UUID>> entIterator = Hookshot.HOOKED_ENTITIES.entrySet().iterator();

            while (entIterator.hasNext()) {
                Map.Entry<UUID, UUID> entry = entIterator.next();
                UUID entityId = entry.getKey();
                UUID playerId = entry.getValue();

                ServerPlayerEntity player = world.getServer().getPlayerManager().getPlayer(playerId);
                Entity entity = world.getEntity(entityId);

                if (player == null || entity == null || !entity.isAlive()) {
                    entIterator.remove();
                    continue;
                }

                Vec3d toPlayer = player.getPos().add(0, 1, 0).subtract(entity.getPos()); // aim for torso-ish height
                double distance = toPlayer.length();
                
                if (distance < 0.4) {
                    entity.setVelocity(Vec3d.ZERO);
                    entIterator.remove();
                    continue;
                }
                
                // Add instead of set for smoother "constant force" pull
                Vec3d pullVec = toPlayer.normalize().multiply(1.3);
                entity.addVelocity(pullVec.x, pullVec.y, pullVec.z);
                entity.velocityModified = true;        
                
                
                // --- Chain particle trail ---
                Vec3d from = player.getPos().add(0, 1.0, 0); // Slightly above for visual clarity
                Vec3d to = entity.getPos();

                int particles = 10;
                for (int i = 0; i <= particles; i++) {
                    double progress = i / (double) particles;
                    Vec3d pos = from.lerp(to, progress);

                    world.spawnParticles(
                        ParticleTypes.CRIT, // You can replace with your custom chain particle
                        pos.x, pos.y, pos.z,
                        1, 0, 0, 0, 0.0
                    );
                }
            }
        });
    }
}
