package onyx.items;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.util.Formatting;
import net.minecraft.text.Text;

public class Hookshot extends Item {
    public Hookshot(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("itemTooltip.zelda-oot-mod.hookshot.1").formatted(Formatting.GOLD));
        tooltip.add(Text.translatable("itemTooltip.zelda-oot-mod.hookshot.2").formatted(Formatting.GOLD));
    }
}
