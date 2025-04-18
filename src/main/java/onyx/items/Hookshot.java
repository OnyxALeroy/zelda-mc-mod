package onyx.items;

import java.util.*;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import onyx.blocks.blockentities.HookableBlockEntity;
import onyx.items.behaviourmanagers.HookshotHandler;
import net.minecraft.text.Text;

public class Hookshot extends Item {
    public static final Map<UUID, Vec3d> HOOKED_PLAYERS = new HashMap<>();
    public static final Map<UUID, UUID> HOOKED_ENTITIES = new HashMap<>();
    private static final Map<UUID, Long> HOOK_COOLDOWNS = new HashMap<>();
    private static final long COOLDOWN_MS = 1000;

    public Hookshot(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("itemTooltip.zelda-oot-mod.hookshot.1").formatted(Formatting.GOLD));
        tooltip.add(Text.translatable("itemTooltip.zelda-oot-mod.hookshot.2").formatted(Formatting.GOLD));
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) { return ActionResult.PASS; }

        long now = System.currentTimeMillis();
        if (HOOK_COOLDOWNS.containsKey(user.getUuid()) &&
            now - HOOK_COOLDOWNS.get(user.getUuid()) < COOLDOWN_MS) {
            return ActionResult.FAIL;
        }

        HOOK_COOLDOWNS.put(user.getUuid(), now);
        world.playSound(null, user.getBlockPos(), SoundEvents.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0F, 0.7F);
        world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.PLAYERS, 0.05F, 0.7F);

        Vec3d start = user.getCameraPosVec(1.0F);
        Vec3d direction = user.getRotationVec(1.0F);
        Vec3d end = start.add(direction.multiply(20));

        List<Entity> candidates = world.getOtherEntities(user, user.getBoundingBox().expand(20), entity ->
            entity instanceof LivingEntity &&
            entity.isAlive() &&
            entity != user
        );

        // Hooking to an entity?
        Entity closestEntity = null;
        double closestDistance = 20;
        for (Entity entity : candidates) {
            Vec3d toEntity = entity.getEyePos().subtract(start);
            double distance = toEntity.length();

            if (distance < closestDistance &&
                direction.dotProduct(toEntity.normalize()) > 0.9961947) { // cos(5°) = 0.9961947 for the aiming cone
                closestEntity = entity;
                closestDistance = distance;
            }
        }
        if (closestEntity != null) {
            HOOKED_ENTITIES.put(closestEntity.getUuid(), user.getUuid());

            // Tell client to animate cooldown
            if (user instanceof ServerPlayerEntity serverPlayer) {
                ItemStack stack = user.getStackInHand(hand);
                serverPlayer.getItemCooldownManager().set(stack, 20);
            }

            return ActionResult.SUCCESS;
        }

        // fallback: hook to block
        HitResult hit = world.raycast(new RaycastContext(start,end,RaycastContext.ShapeType.OUTLINE,RaycastContext.FluidHandling.NONE,user));
        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hit;
            BlockPos hitBlockPos = blockHit.getBlockPos();

            BlockEntity blockEntity = world.getBlockEntity(hitBlockPos);
            if (blockEntity instanceof HookableBlockEntity) {
                Vec3d hitPos = Vec3d.ofCenter(hitBlockPos);

                HOOKED_PLAYERS.put(user.getUuid(), hitPos);
                HookshotHandler.HOOK_START_TIMES.put(user.getUuid(), System.currentTimeMillis());

                if (user instanceof ServerPlayerEntity serverPlayer) {
                    ItemStack stack = user.getStackInHand(hand);
                    serverPlayer.getItemCooldownManager().set(stack, 20);
                }

                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.FAIL;
    }
}
