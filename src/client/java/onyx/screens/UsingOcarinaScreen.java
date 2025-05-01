package onyx.screens;

import net.fabricmc.api.Environment;

import java.util.List;
import java.util.UUID;

import net.fabricmc.api.EnvType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import onyx.songs.Song;

@Environment(EnvType.CLIENT)
public class UsingOcarinaScreen extends Screen {
    public Screen parent;
    public UUID playerUuid;

    public UsingOcarinaScreen(Screen parent, UUID playerUuid) {
        super(Text.empty());
        this.parent = parent;
        this.playerUuid = playerUuid;
    }

	@Override
	protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Back Button
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.zelda-oot-mod.back"), (btn) -> {
            this.close();
        }).dimensions(centerX - 60, centerY + 10, 120, 20).build());

        // All "Song" buttons
        List<Song> songs = Song.getAllAvailableSongs(playerUuid);
        int songCount = songs.size();

        for (int i = 0; i < songCount; i++) {
            Song song = songs.get(i);
            int buttonY = centerY - 10 + (i * 25);

            this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.zelda-oot-mod.song." + song.getId()), (btn) -> {
                song.play(playerUuid);
            }).dimensions(centerX - 60, buttonY, 120, 20).build());
        }
	}

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    // ---------------------------------------------------------------------------------------------------------------------

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(context);
        super.render(context, mouseX, mouseY, delta);
    }
    private void renderBackgroundTexture(DrawContext context) {}

    // ---------------------------------------------------------------------------------------------------------------------

    // Clicking behavior
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.client != null || this.client.player != null){}

        return super.mouseClicked(mouseX, mouseY, button);
    }

    // ---------------------------------------------------------------------------------------------------------------------
}
