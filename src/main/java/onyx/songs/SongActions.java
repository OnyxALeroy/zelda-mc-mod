package onyx.songs;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.network.ServerPlayerEntity;
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

            // Play the song's sound for the player
            player.getWorld().playSound(null, player.getBlockPos(), ZeldaSounds.SONG_OF_SUN, SoundCategory.RECORDS, 0.75f, 1.0f);
        }
    };

    public static final SongAction GIVE_COOKIE = (server, playerUuid) -> {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerUuid);
    };

    public static final SongAction SUMMON_LIGHTNING = (server, playerUuid) -> {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerUuid);
    };
}
