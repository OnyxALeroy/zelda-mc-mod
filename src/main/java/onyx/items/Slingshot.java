package onyx.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import onyx.entities.DekuSeedEntity;
import onyx.items.behaviourmanagers.SlingshotHandler;

public class Slingshot extends Item {
    private static int cooldownDuration = 10;
    public static int GetCooldownDuration() { return cooldownDuration; }

    public Slingshot(Settings settings) { super(settings); }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (user.isCreative()) {
            if (world.isClient()) { 
                return ActionResult.PASS;
            }
            if (user instanceof ServerPlayerEntity serverPlayer) {
                ItemStack stack = user.getStackInHand(hand);
                serverPlayer.getItemCooldownManager().set(stack, cooldownDuration);
            }
            useSlingshot(world, user, true);
            return ActionResult.SUCCESS;
        }

        // Check cooldown using SlingshotCooldownHandler
        if (SlingshotHandler.canUseSlingshot((ServerPlayerEntity) user)) {
            if (world.isClient()) {
                return ActionResult.PASS;
            }
            if (user instanceof ServerPlayerEntity serverPlayer) {
                ItemStack stack = user.getStackInHand(hand);
                serverPlayer.getItemCooldownManager().set(stack, cooldownDuration);
            }
            useSlingshot(world, user, false);
            SlingshotHandler.onSlingshotUsed((ServerPlayerEntity) user);
            return ActionResult.SUCCESS;
        } else {
            // Player is still on cooldown
            return ActionResult.FAIL;
        }
    }

    private void useSlingshot(World world, PlayerEntity user, boolean isInCreativeMode) {
        // Check inventory for Deku Seeds and fire the slingshot projectile
        for (int i = 0; i < user.getInventory().size(); i++) {
            ItemStack stack = user.getInventory().getStack(i);
            if (stack.getItem() == ZeldaItems.DEKU_SEED || isInCreativeMode) {
                if (!isInCreativeMode){ stack.decrement(1); }

                // Play SE
                world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_ENDER_PEARL_THROW, SoundCategory.PLAYERS, 1.0F, 0.7F);

                // Create and spawn projectile
                DekuSeedEntity seed = new DekuSeedEntity(world, user);
                seed.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 1.0F); // similar to arrows
                world.spawnEntity(seed);

                return;
            }
        }
    }
}
