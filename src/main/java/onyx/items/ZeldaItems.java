package onyx.items;

import java.util.function.Function;

import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.UnbreakableComponent;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

import onyx.items.materials.*;

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
	public static final ToolMaterial KOKIRI_SWORD_TOOL = new ToolMaterial(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 0, 0, 0, 999, null);

	// Items to register ----------------------------------------------------------------------------------------------------------------------------

	// Items
    public static final Item OCARINA_OF_TIME = register("ocarina_of_time", OcarinaOfTime::new, new Item.Settings());
    public static final Item SLINGSHOT = register("slingshot", Slingshot::new, new Item.Settings());
    public static final Item DEKU_SEED = register("deku_seed", Item::new, new Item.Settings());
    public static final Item HOOKSHOT = register("hookshot", Hookshot::new, new Item.Settings());
	public static final Item KOKIRI_SWORD = register(
			"kokiri_sword",
			settings -> new UnbreakableSword(KOKIRI_SWORD_TOOL, 1f, 1f, settings),
			new Item.Settings()
	);

	// Armors
	public static final Item GREEN_TUNIC_HELMET = register("green_tunic_helmet",
		settings -> new ArmorItem(GreenTunicMaterial.INSTANCE, EquipmentType.HELMET, settings),
		new Item.Settings().maxDamage(EquipmentType.HELMET.getMaxDamage(GreenTunicMaterial.BASE_DURABILITY))
		.component(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(true))
	);
	public static final Item GREEN_TUNIC_CHESTPLATE = register("green_tunic_chestplate",
			settings -> new ArmorItem(GreenTunicMaterial.INSTANCE, EquipmentType.CHESTPLATE, settings),
			new Item.Settings().maxDamage(EquipmentType.CHESTPLATE.getMaxDamage(GreenTunicMaterial.BASE_DURABILITY))
			.component(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(true))
	);
	public static final Item GREEN_TUNIC_LEGGINGS = register("green_tunic_leggings",
			settings -> new ArmorItem(GreenTunicMaterial.INSTANCE, EquipmentType.LEGGINGS, settings),
			new Item.Settings().maxDamage(EquipmentType.LEGGINGS.getMaxDamage(GreenTunicMaterial.BASE_DURABILITY))
			.component(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(true))
	);
	public static final Item GREEN_TUNIC_BOOTS = register("green_tunic_boots",
			settings -> new ArmorItem(GreenTunicMaterial.INSTANCE, EquipmentType.BOOTS, settings),
			new Item.Settings().maxDamage(EquipmentType.BOOTS.getMaxDamage(GreenTunicMaterial.BASE_DURABILITY))
				.component(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(true))
	);

	// ----------------------------------------------------------------------------------------------------------------------------------------------

	// Registering the items in the init
    public static void initialize(RegistryKey<ItemGroup> group_key){
        ItemGroupEvents.modifyEntriesEvent(group_key).register((itemGroup) -> {
			itemGroup.add(ZeldaItems.OCARINA_OF_TIME);
			itemGroup.add(ZeldaItems.SLINGSHOT);
			itemGroup.add(ZeldaItems.DEKU_SEED);
			itemGroup.add(ZeldaItems.HOOKSHOT);
			itemGroup.add(ZeldaItems.KOKIRI_SWORD);
			itemGroup.add(ZeldaItems.GREEN_TUNIC_HELMET);
			itemGroup.add(ZeldaItems.GREEN_TUNIC_CHESTPLATE);
			itemGroup.add(ZeldaItems.GREEN_TUNIC_LEGGINGS);
			itemGroup.add(ZeldaItems.GREEN_TUNIC_BOOTS);
		});
    }
}
