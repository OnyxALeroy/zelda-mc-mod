package onyx.mixins;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import onyx.components.PlayerNoteTracker;
import onyx.items.TornadoRod;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    private void writeNoteData(NbtCompound nbt, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;
        PlayerNoteTracker.getInstance().savePlayerData(player, nbt);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
    private void readNoteData(NbtCompound nbt, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;
        PlayerNoteTracker.getInstance().loadPlayerData(player, nbt);
    }

    @Inject(method = "copyFrom", at = @At("RETURN"))
    private void copyNoteData(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        // Copy data when player respawns
        ServerPlayerEntity newPlayer = (ServerPlayerEntity)(Object)this;
        if (alive) { // Only copy if keeping inventory
            List<String> oldNotes = PlayerNoteTracker.getInstance().getPlayedNotes(oldPlayer);
            for (String note : oldNotes) {
                PlayerNoteTracker.getInstance().addNote(newPlayer, note);
            }
        }
    }

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
