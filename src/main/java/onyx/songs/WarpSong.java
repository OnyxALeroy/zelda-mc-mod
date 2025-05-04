package onyx.songs;

import java.util.UUID;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import onyx.ZeldaOOTMod;

public class WarpSong extends Song {
    private WarpSongAction action;
    private BlockPos childWarpPos;
    private Direction childFacing;
    private BlockPos adultWarpPos;
    private Direction adultFacing;

    public WarpSong(String id, WarpSongAction action, BlockPos cWarpPos, Direction cFacing, BlockPos aWarpPos, Direction aFacing) {
        super(id, null);
        this.action = action;
        this.childWarpPos = cWarpPos;
        this.childFacing = cFacing;
        this.adultWarpPos = aWarpPos;
        this.adultFacing = aFacing;
    }

    public void setChildWarpPos(BlockPos warpPos) { this.childWarpPos = warpPos; }
    public void setChildFacing(Direction facing) { this.childFacing = facing; }
    public void setAdultWarpPos(BlockPos warpPos) { this.adultWarpPos = warpPos; }
    public void setAdultFacing(Direction facing) { this.adultFacing = facing; }

    private float getYaw(Direction f) {
        return switch (f) {
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
                action.execute(server, playerUuid, childWarpPos, getYaw(childFacing), adultWarpPos, getYaw(adultFacing));
            }
            return true;
        }
        return false;
    }
}
