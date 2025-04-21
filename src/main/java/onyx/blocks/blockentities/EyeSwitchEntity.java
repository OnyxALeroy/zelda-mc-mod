package onyx.blocks.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class EyeSwitchEntity extends BlockEntity {
	public EyeSwitchEntity(BlockPos pos, BlockState state) {
		super(ZeldaBlockEntities.EYE_SWITCH_ENTITY, pos, state);
	}
}
