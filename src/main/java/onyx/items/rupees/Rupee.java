package onyx.items.rupees;

import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import onyx.components.ZeldaComponents;
import onyx.items.ZeldaItems;

public abstract class Rupee extends Item {
    public Rupee(Settings settings) { super(settings); }
    
    public abstract int getValue();

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("itemTooltip.zelda-oot-mod.rupee", this.getValue()).formatted(Formatting.AQUA));
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient()) { return ActionResult.PASS; }

        ItemStack rupee_stack = user.getStackInHand(hand);

        // Trying to find the wallet
        ItemStack wallet = null;
        for (int i = 0; i < user.getInventory().size(); i++) {
            ItemStack stack = user.getInventory().getStack(i);

            if (stack.getItem() == ZeldaItems.RUPEES_WALLET) {
                wallet = stack;
                break;
            }
        }

        if (wallet != null){
            int rubies = wallet.getOrDefault(ZeldaComponents.RUBIES_POSSESSED, 0);

            if (user.isSneaking()){
                // All stack
                rubies = rubies + this.getValue() * rupee_stack.getCount();
                rupee_stack.decrement(rupee_stack.getCount());
            } else {
                // One at a time
                rubies = rubies + this.getValue();
                rupee_stack.decrement(1);
            }

            wallet.set(ZeldaComponents.RUBIES_POSSESSED, rubies);
            return ActionResult.SUCCESS;
        } else {
            // There's not wallet in this inventory
            user.sendMessage(
                Text.translatable("item.zelda-oot-mod.no_rupee_wallet_found").formatted(Formatting.RED, Formatting.BOLD, Formatting.ITALIC),
                true
            );
            return ActionResult.PASS;
        }
    }
}
