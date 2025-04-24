package onyx;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import onyx.items.rupees.RupeesWalletTemplate;
import onyx.screens.RupeeWalletScreen;
import onyx.screens.UsingOcarinaScreen;
import onyx.server.OpenRupeeWalletS2CPayload;
import onyx.server.UseOcarinaS2CPayload;

public class PayloadInitializer {
    public static void InitializePayloads(){

        // Ask for opening the Rupee Wallet Screen
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

        // Ask for opening the Ocarina Screen
        ClientPlayNetworking.registerGlobalReceiver(UseOcarinaS2CPayload.ID, (payload, context) -> {
            if (context.client().world == null) { return; }
            if (MinecraftClient.getInstance().player == null) { return; }

            Screen currentScreen = MinecraftClient.getInstance().currentScreen;
            MinecraftClient.getInstance().execute(() -> {
                MinecraftClient.getInstance().setScreen(new UsingOcarinaScreen(currentScreen, payload.ocarina()));
            });
        });
    }
}
