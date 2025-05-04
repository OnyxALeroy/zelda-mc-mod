package onyx.songs;

import java.util.EnumSet;
import java.util.Timer;
import java.util.TimerTask;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import onyx.ZeldaOOTMod;
import onyx.components.ZeldaComponents;
import onyx.items.ocarina.OcarinaTemplate;
import onyx.sounds.ZeldaSounds;

public class SongActions {
    public static SongAction SONG_OF_STORMS = (server, playerUuid) -> {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerUuid);
        if (player != null) {
            // Run the /weather clear command
            try {
                server.getCommandManager().getDispatcher().execute(
                    "weather thunder", 
                    server.getCommandSource().withLevel(4).withEntity(player).withSilent()
                );
            } catch (CommandSyntaxException e) { e.printStackTrace(); }

            // Play the song's sound for the player
            player.getWorld().playSound(null, player.getBlockPos(), ZeldaSounds.SONG_OF_STORMS, SoundCategory.RECORDS, 0.75f, 1.0f);
        }
    };

    public static SongAction SONG_OF_SUN = (server, playerUuid) -> {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerUuid);
        if (player != null) {
            // Run the /weather clear command
            try {
                server.getCommandManager().getDispatcher().execute(
                    "weather clear", 
                    server.getCommandSource().withLevel(4).withEntity(player).withSilent()
                );
            } catch (CommandSyntaxException e) { e.printStackTrace(); }


            // NOTE: Day is approximately 0-12000, night is 12000-24000
            ServerWorld world = player.getServerWorld();
            long currentTime = world.getTimeOfDay() % 24000;
            long targetTime;
            if (currentTime >= 12000) {
                targetTime = 24000;
            } else {
                targetTime = 13000;
            }
            scheduleTimeAcceleration(server, world, currentTime, targetTime, 100L); // 100 ticks = 5 seconds

            // Play the song's sound for the player
            player.getWorld().playSound(null, player.getBlockPos(), ZeldaSounds.SONG_OF_SUN, SoundCategory.RECORDS, 0.75f, 1.0f);
        }
    };

    // ----------------------------------------------------------------------------------------------------------------------------------------------

    public static WarpSongAction MINUET_OF_FOREST = (server, playerUuid, childWarpPos, childFacing, adultWarpPos, adultFacing) -> {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerUuid);
        if (player != null) {
            ServerWorld world = player.getServerWorld();
            
            // Play the song's sound
            world.playSound(null, player.getBlockPos(), ZeldaSounds.MINUET_OF_FOREST, SoundCategory.RECORDS, 0.75f, 1.0f);
            
            // Show particles
            world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, 
                player.getX(), player.getY() + 1, player.getZ(),
                20, 0.5, 0.5, 0.5, 0.1);

            // Teleport logic
            BlockPos dest;
            float facing;
            ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
            if (stack.getItem() instanceof OcarinaTemplate) {
                if (stack.getOrDefault(ZeldaComponents.IS_OWNER_ADULT, false)){
                    dest = adultWarpPos;
                    facing = adultFacing;
                } else {
                    dest = childWarpPos;
                    facing = childFacing;
                }
            } else {
                ZeldaOOTMod.LOGGER.info("Trying to use an Ocarina without using one.");
                return;
            }

            player.teleport(
                world,
                dest.getX() + 0.5,
                dest.getY() + 1.0,
                dest.getZ() + 0.5,
                EnumSet.noneOf(PositionFlag.class), facing,
                player.getPitch(), false
            );
                
            // Effects and messages
            world.spawnParticles(ParticleTypes.PORTAL,
                childWarpPos.getX() + 0.5, childWarpPos.getY() + 1.5, childWarpPos.getZ() + 0.5,
                400, 0.5, 0.5, 0.5, 0.1);
            player.sendMessage(Text.literal("§2You have been transported by the Minuet of Forest."), true);
        }
    };

    // ----------------------------------------------------------------------------------------------------------------------------------------------

    private static void scheduleTimeAcceleration(MinecraftServer server, ServerWorld world, long startTime, long targetTime, long durationTicks) {
        final long adjustedTargetTime = targetTime <= startTime ? targetTime + 24000 : targetTime;
        final long timeDifference = adjustedTargetTime - startTime;
        final long intervalMs = 50L;
        final long steps = Math.max(durationTicks * 50 / intervalMs, 1);
    
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            long currentStep = 0;
    
            @Override
            public void run() {
                if (currentStep >= steps) {
                    world.setTimeOfDay(adjustedTargetTime % 24000);
                    timer.cancel();
                    return;
                }
    
                // Cosine interpolation (ease-in-out)
                double t = (double) currentStep / steps;
                double easedT = 0.5 * (1 - Math.cos(Math.PI * t));
                long newTime = (startTime + (long) (timeDifference * easedT)) % 24000;
    
                world.setTimeOfDay(newTime);
                currentStep++;
            }
        }, 0L, intervalMs);
    }
}
