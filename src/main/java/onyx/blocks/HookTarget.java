package onyx.blocks;

import org.jetbrains.annotations.Nullable;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

import onyx.blocks.blockentities.HookableBlockEntity;

public class HookTarget extends BlockWithEntity {
    public HookTarget(Settings settings){
        super(settings);
    }

	@Override
	protected MapCodec<? extends BlockWithEntity> getCodec() {
		return createCodec(HookTarget::new);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new HookableBlockEntity(pos, state);
	}
}
