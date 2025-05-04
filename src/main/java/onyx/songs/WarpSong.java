package onyx.songs;

import java.util.UUID;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import onyx.ZeldaOOTMod;

public class WarpSong extends Song {
    private BlockPos warpPos;
    private WarpSongAction action;
    private Direction facing;

    public WarpSong(String id, WarpSongAction action, BlockPos warpPos) {
        super(id, null);
        this.action = action;
        this.warpPos = warpPos;
        this.facing = Direction.NORTH;
    }

    public WarpSong(String id, WarpSongAction action, BlockPos warpPos, Direction facing) {
        super(id, null);
        this.action = action;
        this.warpPos = warpPos;
        this.facing = facing;
    }

    public BlockPos getWarpPos() { return warpPos; }
    public void setWarpPos(BlockPos warpPos) { this.warpPos = warpPos; }
    public Direction getFacing() { return facing; }
    public void setFacing(Direction facing) { this.facing = facing; }

    private float getYaw() {
        return switch (this.facing) {
            case SOUTH -> 0.0f;
            case WEST -> 90.0f;
            case NORTH -> 180.0f;
            case EAST -> 270.0f;
            default -> 0.0f;
        };
    }

    @Override
    public boolean play(MinecraftServer server, UUID playerUuid) {
        if (canPlayerPlay(playerUuid)) {
            if (action != null) {
                ZeldaOOTMod.LOGGER.info("Executing action for song: " + getId());
                action.execute(server, playerUuid, warpPos, getYaw());
            }
            return true;
        }
        return false;
    }
}
