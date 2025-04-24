package onyx.items.rupees;

import java.util.List;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import onyx.components.ZeldaComponents;
import onyx.items.ZeldaItems;
import onyx.server.OpenRupeeWalletS2CPayload;

public abstract class RupeesWalletTemplate extends Item {
    public RupeesWalletTemplate(Settings settings) { super(settings); }

    public abstract int getCapacity();

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        int count = stack.get(ZeldaComponents.RUPEES_POSSESSED);
        tooltip.add(Text.translatable("itemTooltip.zelda-oot-mod.rupee_wallet.info", count).formatted(Formatting.AQUA));
        tooltip.add(Text.translatable("itemTooltip.zelda-oot-mod.rupee_wallet.info_max", this.getCapacity()).formatted(Formatting.AQUA));
        tooltip.add(Text.translatable("itemTooltip.zelda-oot-mod.rupee_wallet.1").formatted(Formatting.GOLD));
        tooltip.add(Text.translatable("itemTooltip.zelda-oot-mod.rupee_wallet.2").formatted(Formatting.GOLD));
        tooltip.add(Text.translatable("itemTooltip.zelda-oot-mod.rupee_wallet.3").formatted(Formatting.GOLD));
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient()) { return ActionResult.PASS; }

        // Play opening SE
        world.playSound(null, user.getBlockPos(), SoundEvents.BLOCK_WOOL_BREAK, SoundCategory.PLAYERS, 1.0F, 0.7F);

        // Get or set the value stored on this item
        ItemStack wallet_stack = user.getStackInHand(hand);
        int rupees = wallet_stack.getOrDefault(ZeldaComponents.RUPEES_POSSESSED, 0);

        if (user.isSneaking()){
            for (int i = 0; i < user.getInventory().size(); i++) {
                ItemStack stack = user.getInventory().getStack(i);
                boolean hasMaxCapacityReached = false;
            
                if (isRupee(stack.getItem())) {

                    for (int j = 0; j < stack.getCount(); j++){
                        Rupee rupee = (Rupee) stack.getItem();
                        if (rupees + rupee.getValue() <= this.getCapacity()){
                            rupees += rupee.getValue();
                            stack.decrement(1);
                        } else {
                            if (!hasMaxCapacityReached){
                                user.sendMessage(
                                    Text.translatable("itemTooltip.zelda-oot-mod.rupee_wallet.max_reached").formatted(Formatting.RED, Formatting.BOLD, Formatting.ITALIC),
                                    true
                                );
                                hasMaxCapacityReached = true;
                            }
                            break;
                        }
                    }
                }
            }

            wallet_stack.set(ZeldaComponents.RUPEES_POSSESSED, rupees);

            return ActionResult.SUCCESS;
        } else {
            OpenRupeeWalletS2CPayload payload = new OpenRupeeWalletS2CPayload(user.getId(), hand.ordinal());
            for (ServerPlayerEntity player : PlayerLookup.world((ServerWorld) world)) {
                ServerPlayNetworking.send(player, payload);
            }

            return ActionResult.SUCCESS;
        }
    }

    private boolean isRupee(Item item){
        return item == ZeldaItems.GREEN_RUPEE
        || item == ZeldaItems.BLUE_RUPEE
        || item == ZeldaItems.RED_RUPEE
        || item == ZeldaItems.PURPLE_RUPEE
        || item == ZeldaItems.GOLD_RUPEE;
    }
}
