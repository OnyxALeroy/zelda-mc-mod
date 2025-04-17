package onyx.items.materials;

import net.minecraft.util.Identifier;

import java.util.Map;

import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.SoundEvents;

public class GreenTunicMaterial {
    public static final int BASE_DURABILITY = 1;
    public static final RegistryKey<EquipmentAsset> GREEN_TUNIC_MATERIAL_KEY = RegistryKey.of(EquipmentAssetKeys.REGISTRY_KEY, Identifier.of("zelda-oot-mod", "green_tunic"));

    public static final ArmorMaterial INSTANCE = new ArmorMaterial(
            BASE_DURABILITY,
            Map.of(
                    EquipmentType.HELMET, 0,
                    EquipmentType.CHESTPLATE, 0,
                    EquipmentType.LEGGINGS, 0,
                    EquipmentType.BOOTS, 0
            ),
            999,
            SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
            0.0F,
            0.0F,
            null,
            GREEN_TUNIC_MATERIAL_KEY
    );
}
