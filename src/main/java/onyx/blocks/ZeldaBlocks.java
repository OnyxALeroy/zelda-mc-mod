package onyx.blocks;

import java.util.function.Function;

import net.minecraft.block.Block;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import onyx.blocks.blockentities.ZeldaBlockEntities;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;

public class ZeldaBlocks {
    // Registerer
	private static RegistryKey<Block> keyOfBlock(String name) { return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of("zelda-oot-mod", name)); }
	private static RegistryKey<Item> keyOfItem(String name) { return RegistryKey.of(RegistryKeys.ITEM, Identifier.of("zelda-oot-mod", name)); }
	private static Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings, boolean shouldRegisterItem) {
		// Create a registry key for the block
		RegistryKey<Block> blockKey = keyOfBlock(name);
		// Create the block instance
		Block block = blockFactory.apply(settings.registryKey(blockKey));

		// Sometimes, you may not want to register an item for the block.
		// Eg: if it's a technical block like `minecraft:moving_piston` or `minecraft:end_gateway`
		if (shouldRegisterItem) {
			RegistryKey<Item> itemKey = keyOfItem(name);
			BlockItem blockItem = new BlockItem(block, new Item.Settings().registryKey(itemKey));
			Registry.register(Registries.ITEM, itemKey, blockItem);
		}

		return Registry.register(Registries.BLOCK, blockKey, block);
	}

	// Blocks to register ---------------------------------------------------------------------------------------------------------------------------

    public static final Block BLOCK_OF_TIME = register(
        "block_of_time",
        Block::new,
        AbstractBlock.Settings.create().sounds(BlockSoundGroup.ANCIENT_DEBRIS),
        true
    );

    public static final Block HOOK_TARGET = register(
        "hook_target",
        HookTarget::new,
        AbstractBlock.Settings.create().sounds(BlockSoundGroup.CALCITE),
        true
    );

    public static final Block EYE_SWITCH = register(
        "eye_switch",
        EyeSwitch::new,
        AbstractBlock.Settings.create().sounds(BlockSoundGroup.AMETHYST_CLUSTER).nonOpaque(),
        true
    );

    // "Fake" blocks, used for rendering
    public static final Block DEKU_SEED_BLOCK = register(
        "deku_seed_block",
        Block::new,
        AbstractBlock.Settings.create().sounds(BlockSoundGroup.BAMBOO),
        false
    );

	// ----------------------------------------------------------------------------------------------------------------------------------------------

    public static void initialize(RegistryKey<ItemGroup> group_key) {
		ZeldaBlockEntities.initialize();
        ItemGroupEvents.modifyEntriesEvent(group_key).register((itemGroup) -> {
            itemGroup.add(ZeldaBlocks.BLOCK_OF_TIME.asItem());
            itemGroup.add(ZeldaBlocks.HOOK_TARGET.asItem());
            itemGroup.add(ZeldaBlocks.EYE_SWITCH.asItem());
        });
    }
}
