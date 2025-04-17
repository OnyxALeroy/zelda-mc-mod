package onyx.items;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.SwordItem;

public class UnbreakableSword extends SwordItem {
    public UnbreakableSword(ToolMaterial mat, float attackDamage, float attackSpeed, Settings settings) {
        super(mat, attackDamage, attackSpeed, settings);
    }

    @Override
    public void postDamageEntity(ItemStack item, LivingEntity target, LivingEntity attacker)
    {
        item.damage(0, attacker, EquipmentSlot.MAINHAND);
    }
}
