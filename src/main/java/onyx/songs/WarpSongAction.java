package onyx.songs;

import java.util.UUID;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

@FunctionalInterface
public interface WarpSongAction {
    void execute(MinecraftServer server, UUID playerUuid, BlockPos warpPos, float facing, BlockPos adultWarpPos, float adultFacing);
}
