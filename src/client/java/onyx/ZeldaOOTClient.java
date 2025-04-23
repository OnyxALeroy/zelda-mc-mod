package onyx;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import onyx.entities.ZeldaEntities;
import onyx.items.rupees.RupeesWalletTemplate;
import onyx.renderer.DekuSeedEntityRenderer;
import onyx.server.OpenRupeeWalletS2CPayload;
import onyx.screens.RupeeWalletScreen;


@Environment(EnvType.CLIENT)
public class ZeldaOOTClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ZeldaEntities.DEKU_SEED_ENTITY, (context) -> new DekuSeedEntityRenderer(context));

        // What to do when receiving Payloads
        ClientPlayNetworking.registerGlobalReceiver(OpenRupeeWalletS2CPayload.ID, (payload, context) -> {
            ClientWorld world = context.client().world;
            if (world == null) { return; }

            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) { return; }

            Hand hand = Hand.values()[payload.handID()];
            ItemStack walletStack = player.getStackInHand(hand);
            if (!(walletStack.getItem() instanceof RupeesWalletTemplate)) { return; }

            Screen currentScreen = MinecraftClient.getInstance().currentScreen;
            MinecraftClient.getInstance().execute(() -> {
                MinecraftClient.getInstance().setScreen(new RupeeWalletScreen(currentScreen, walletStack));
            });
        });
    }
}
