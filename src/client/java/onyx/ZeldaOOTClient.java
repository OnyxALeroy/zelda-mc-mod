package onyx;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import onyx.entities.ZeldaEntities;
import onyx.renderer.DekuSeedEntityRenderer;

@Environment(EnvType.CLIENT)
public class ZeldaOOTClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ZeldaEntities.DEKU_SEED_ENTITY, (context) -> new DekuSeedEntityRenderer(context));

        // Initialize all payloads
        PayloadInitializer.InitializePayloads();
    }
}
