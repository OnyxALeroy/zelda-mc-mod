package onyx.screens;

import net.fabricmc.api.Environment;

import java.util.Map;

import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import onyx.components.PlayerNoteTracker;
import onyx.ocarina.OcarinaKeyHandler;
import onyx.ocarina.OcarinaNote;

@Environment(EnvType.CLIENT)
public class UsingOcarinaScreen extends Screen {
    public Screen parent;
    private static Identifier BACKGROUND_TEXTURE = Identifier.of("zelda-oot-mod", "textures/gui/ocarina.png");
    private static final int BG_WIDTH = 176;
    private static final int BG_HEIGHT = 166;

    public UsingOcarinaScreen(Screen parent){
        super(Text.empty());
        this.parent = parent;
    }

	@Override
	protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Back Button
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.zelda-oot-mod.back"), (btn) -> {
            this.close();
        }).dimensions(centerX - 60, centerY + 10, 120, 20).build());
	}

    @Override
    public void close() {
        this.client.setScreen(this.parent);
        clearNotes();
    }

    // ---------------------------------------------------------------------------------------------------------------------

    // Rendering
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(context);
        super.render(context, mouseX, mouseY, delta);
    }

    private void renderBackgroundTexture(DrawContext context) {
        int centerX = (this.width - BG_WIDTH) / 2;
        int centerY = (this.height - BG_HEIGHT) / 2;

        context.drawTexture(
            RenderLayer::getGuiTextured,
            BACKGROUND_TEXTURE,
            centerX, centerY,
            0.0F, 0.0F,
            this.width, this.height,
            this.width, this.height
        );
    }

    // ---------------------------------------------------------------------------------------------------------------------

    // Clicking behavior
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.client != null || this.client.player != null){}

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (Map.Entry<String, OcarinaNote> entry : OcarinaNote.allNotes.entrySet()) {
            if (entry.getValue().doesMatch(keyCode, scanCode)) {
                playNote(entry.getValue());
                OcarinaKeyHandler.playNote(MinecraftClient.getInstance().player, entry.getKey());
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }    

    // ---------------------------------------------------------------------------------------------------------------------

    // Playing notes
    public void playNote(OcarinaNote note) {
        if (this.client != null && this.client.player != null) {
            // Display a toast notification
            SystemToast.add(
                this.client.getToastManager(),
                SystemToast.Type.NARRATOR_TOGGLE,
                Text.literal("Note Played!"),
                Text.literal("You played: " + note.getNote())
            );
            System.out.println("Player played note: " + note.getNote());
        }
    }

    // Clearing notes
    private static void clearNotes(){
        PlayerNoteTracker tracker = PlayerNoteTracker.getInstance();
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if (player != null) {
            tracker.clearNotes(player);
        }
    }
}
