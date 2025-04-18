package onyx.blocks.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class HookableBlockEntity extends BlockEntity {
	public HookableBlockEntity(BlockPos pos, BlockState state) {
		super(ZeldaBlockEntities.HOOKABLE_BLOCK_ENTITY, pos, state);
	}
}
