package onyx.blocks.blockentities;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import onyx.blocks.ZeldaBlocks;

public class ZeldaBlockEntities {
    // Registerer
	private static <T extends BlockEntity> BlockEntityType<T> register(
			String name, FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory, Block... blocks
	) {
		Identifier id = Identifier.of("zelda-oot-mod", name);
		return Registry.register(Registries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build());
	}


	// Blocks to register ---------------------------------------------------------------------------------------------------------------------------

    public static final BlockEntityType<HookableBlockEntity> HOOKABLE_BLOCK_ENTITY =
        register("hookable", HookableBlockEntity::new, ZeldaBlocks.HOOK_TARGET);

	public static final BlockEntityType<EyeSwitchEntity> EYE_SWITCH_ENTITY =
		register("eye_switch", EyeSwitchEntity::new, ZeldaBlocks.EYE_SWITCH);

    public static void initialize() {}
}
