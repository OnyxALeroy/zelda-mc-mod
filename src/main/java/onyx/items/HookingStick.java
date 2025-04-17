package onyx.items;

import java.util.*;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import onyx.components.ZeldaComponents;
import net.minecraft.text.Text;

public class HookingStick extends Item {
    public HookingStick(Settings settings) { super(settings); }

    @Override public boolean hasGlint(ItemStack stack) { return true; }    

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("itemTooltip.zelda-oot-mod.hooking_stick.1").formatted(Formatting.DARK_PURPLE));
        tooltip.add(Text.translatable("itemTooltip.zelda-oot-mod.hooking_stick.2").formatted(Formatting.DARK_PURPLE));
    }

    /*
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
    
        if (world.isClient()) { return ActionResult.PASS; }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null) {
            ComponentMap components = blockEntity.getComponents();
            boolean alreadyContains = components.contains(ZeldaComponents.IS_HOOKABLE);
            boolean newValue;
            if (alreadyContains){
                boolean value = (boolean) components.get(ZeldaComponents.IS_HOOKABLE);
                newValue = !value;
            } else {
                newValue = true;
            }
            components.setComponent(ZeldaComponents.IS_HOOKABLE, newValue);

            // Feedback
            context.getPlayer().sendMessage(Text.literal("Toggled hookable: " + newValue), true);
            return ActionResult.SUCCESS;
        }
    
        return ActionResult.PASS;
    }*/
}
