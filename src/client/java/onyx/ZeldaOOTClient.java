package onyx;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import onyx.entities.ZeldaEntities;
import onyx.renderer.DekuSeedEntityRenderer;

public class ZeldaOOTClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ZeldaEntities.DEKU_SEED_ENTITY, (context) -> new DekuSeedEntityRenderer(context));
    }
}
