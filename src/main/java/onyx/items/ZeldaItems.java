package onyx.items;

import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

import java.util.function.Function;

public class ZeldaItems {
    // Registerer
    	public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
		// Create the item key.
		RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of("zelda-oot-mod", name));

		// Create the item instance.
		Item item = itemFactory.apply(settings.registryKey(itemKey));

		// Register the item.
		Registry.register(Registries.ITEM, itemKey, item);

		return item;
	}

	// All Tool material
	public static final ToolMaterial KOKIRI_SWORD_TOOL = new ToolMaterial(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 0, 0, 0, 800, null);

	// Items to register
    public static final Item HOOKSHOT = register("hookshot", Hookshot::new, new Item.Settings());
	public static final Item KOKIRI_SWORD = register(
			"kokiri_sword",
			settings -> new UnbreakableSword(KOKIRI_SWORD_TOOL, 1f, 1f, settings),
			new Item.Settings()
	);

	// Registering the items in the init
    public static void initialize(){
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((itemGroup) -> {
			itemGroup.add(ZeldaItems.HOOKSHOT);
			itemGroup.add(ZeldaItems.KOKIRI_SWORD);
		});
    }
}
