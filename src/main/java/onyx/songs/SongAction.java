package onyx.songs;

import java.util.UUID;

import net.minecraft.server.MinecraftServer;

public interface SongAction {
    void execute(MinecraftServer server, UUID playerUuid);
}
