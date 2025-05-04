package onyx.mixins;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import onyx.items.TornadoRod;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "onLanding", at = @At("HEAD"), cancellable = true)
    private void preventFallDamage(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity instanceof PlayerEntity player) {
            if (TornadoRod.shouldPreventFallDamage(player)) {
                player.fallDistance = 0.0f;
                ci.cancel(); // Cancel default landing behavior that causes fall damage
            }
        }
    }
}
