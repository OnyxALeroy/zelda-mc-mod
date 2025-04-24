package onyx.items.ocarina;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import onyx.server.UseOcarinaS2CPayload;

public abstract class OcarinaTemplate extends Item {
    public OcarinaTemplate(Settings settings) { super(settings); }

    public abstract String getOcarinaName();

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient()) { return ActionResult.PASS; }

        UseOcarinaS2CPayload payload = new UseOcarinaS2CPayload(this.getOcarinaName());
        for (ServerPlayerEntity player : PlayerLookup.world((ServerWorld) world)) {
            ServerPlayNetworking.send(player, payload);
        }

        return ActionResult.SUCCESS;
    }
}
