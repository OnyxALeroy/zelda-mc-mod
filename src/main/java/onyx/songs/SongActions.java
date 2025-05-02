package onyx.songs;

import java.util.Timer;
import java.util.TimerTask;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
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
