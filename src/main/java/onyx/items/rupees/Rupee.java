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

            if (stack.getItem() == ZeldaItems.RUPEE_WALLET || stack.getItem() == ZeldaItems.ADULT_RUPEE_WALLET || stack.getItem() == ZeldaItems.GIANT_RUPEE_WALLET) {
                wallet = stack;
                break;
            }
        }

        if (wallet != null){
            int rupees = wallet.getOrDefault(ZeldaComponents.RUPEES_POSSESSED, 0);
            RupeesWalletTemplate walletTemplate = (RupeesWalletTemplate) wallet.getItem();
            int maxCapacity = walletTemplate.getCapacity();

            if (user.isSneaking()){
                // All stack
                int rupeesAdded = 0;
                while (rupees + this.getValue() <= maxCapacity && rupeesAdded <= rupee_stack.getCount()){
                    rupees = rupees + this.getValue();
                    rupeesAdded += 1;
                }
                if (rupeesAdded != rupee_stack.getCount()){
                    user.sendMessage(
                        Text.translatable("itemTooltip.zelda-oot-mod.rupee_wallet.max_reached").formatted(Formatting.RED, Formatting.BOLD, Formatting.ITALIC),
                        true
                    );
                }
                rupee_stack.decrement(rupeesAdded);

            } else {
                // One at a time
                if (rupees + this.getValue() <= maxCapacity){
                    rupees = rupees + this.getValue();
                    rupee_stack.decrement(1);
                } else {
                    user.sendMessage(
                        Text.translatable("itemTooltip.zelda-oot-mod.rupee_wallet.max_reached").formatted(Formatting.RED, Formatting.BOLD, Formatting.ITALIC),
                        true
                    );
                }
            }

            wallet.set(ZeldaComponents.RUPEES_POSSESSED, rupees);
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
