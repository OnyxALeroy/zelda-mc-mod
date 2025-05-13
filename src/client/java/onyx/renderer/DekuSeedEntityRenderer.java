package onyx.renderer;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.render.entity.state.FlyingItemEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import onyx.blocks.ZeldaBlocks;
import onyx.entities.custom.DekuSeedEntity;
import net.minecraft.client.MinecraftClient;

public class DekuSeedEntityRenderer extends FlyingItemEntityRenderer<DekuSeedEntity> {
    private static float scale = 0.15F;
    public DekuSeedEntityRenderer(EntityRendererFactory.Context context){
        super(context);
    }

    @Override
    public void render(FlyingItemEntityRenderState renderState, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.scale(scale, scale, scale);

        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(
            ZeldaBlocks.DEKU_SEED_BLOCK.getDefaultState(),
            matrices,
            vertexConsumers,
            light,
            OverlayTexture.DEFAULT_UV
        );

        matrices.pop();
    }
}
