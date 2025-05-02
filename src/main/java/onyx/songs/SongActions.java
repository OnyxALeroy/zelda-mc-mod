package onyx.songs;

import net.minecraft.server.network.ServerPlayerEntity;

public class SongActions {

    public static final SongAction GIVE_COOKIE = (server, playerUuid) -> {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerUuid);
    };

    public static final SongAction SUMMON_LIGHTNING = (server, playerUuid) -> {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerUuid);
    };
}
