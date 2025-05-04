package onyx.songs;

import java.util.UUID;

import net.minecraft.server.MinecraftServer;

@FunctionalInterface
public interface SongAction {
    void execute(MinecraftServer server, UUID playerUuid);
}
