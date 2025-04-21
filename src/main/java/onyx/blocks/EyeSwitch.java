package onyx.blocks;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;

import onyx.blocks.blockentities.EyeSwitchEntity;
import onyx.entities.DekuSeedEntity;

public class EyeSwitch extends BlockWithEntity {
	public static final EnumProperty<Direction> FACING = FacingBlock.FACING;
	public static final BooleanProperty VERTICAL = BooleanProperty.of("vertical");
	public static final BooleanProperty ACTIVATED = BooleanProperty.of("activated");

	// Vertical slab shapes (flush with side it's facing)
	public static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0, 0, 8, 16, 16, 16);
	public static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 16, 8);
	public static final VoxelShape WEST_SHAPE = Block.createCuboidShape(8, 0, 0, 16, 16, 16);
	public static final VoxelShape EAST_SHAPE = Block.createCuboidShape(0, 0, 0, 8, 16, 16);

	// Horizontal slab shapes
	public static final VoxelShape BOTTOM_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 8, 16);
	public static final VoxelShape TOP_SHAPE = Block.createCuboidShape(0, 8, 0, 16, 16, 16);


    public EyeSwitch(Settings settings){
        super(settings);
		this.setDefaultState(this.stateManager.getDefaultState()
			.with(FACING, Direction.NORTH)
			.with(VERTICAL, false)
			.with(ACTIVATED, false));
	}

	@Override
	protected MapCodec<? extends BlockWithEntity> getCodec() {
		return createCodec(EyeSwitch::new);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new EyeSwitchEntity(pos, state);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, VERTICAL, ACTIVATED);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction clickedFace = ctx.getSide(); // The face the player clicked
		Direction playerFacing = ctx.getHorizontalPlayerFacing();

		boolean vertical = (clickedFace == Direction.WEST || clickedFace == Direction.EAST
		                 || clickedFace == Direction.NORTH || clickedFace == Direction.SOUTH);

		return this.getDefaultState()
			.with(FACING, playerFacing.getOpposite())
			.with(VERTICAL, vertical);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		Direction facing = state.get(FACING);
		boolean vertical = state.get(VERTICAL);

		if (vertical) {
			switch (facing) {
				case NORTH: return NORTH_SHAPE;
				case SOUTH: return SOUTH_SHAPE;
				case WEST:  return WEST_SHAPE;
				case EAST:  return EAST_SHAPE;
				default:    return BOTTOM_SHAPE;
			}
		} else {
			if (facing == Direction.UP) {
				return BOTTOM_SHAPE;
			} else if (facing == Direction.DOWN) {
				return TOP_SHAPE;
			}
		}

		return BOTTOM_SHAPE;
	}

	@Override
	public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
		if (projectile instanceof DekuSeedEntity) {
			BlockPos pos = hit.getBlockPos();
			if (!world.isClient()) {

				boolean current = state.get(ACTIVATED);
				world.setBlockState(pos, state.with(ACTIVATED, !current), Block.NOTIFY_ALL);
	
				// Play sound effect
				world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_HIT, SoundCategory.BLOCKS, 0.6F, current ? 0.5F : 0.6F);
	
				// Update redstone
				world.updateNeighborsAlways(pos, this);
			}
		}
	}

	// Redstone ------------------------------------------------------------------------------------------------------------

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
		// Power near-by Redstone components
		if (!world.isClient) {
			boolean powered = world.isReceivingRedstonePower(pos);
			if (powered != state.get(ACTIVATED)) {
				world.setBlockState(pos, state.with(ACTIVATED, powered), Block.NOTIFY_ALL);
			}
		}
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}
	
	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.get(ACTIVATED) ? 15 : 0;
	}
	
	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return getWeakRedstonePower(state, world, pos, direction);
	}
}
