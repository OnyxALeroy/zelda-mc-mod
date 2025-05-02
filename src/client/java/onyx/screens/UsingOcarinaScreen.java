package onyx.screens;

import net.fabricmc.api.Environment;

import java.util.List;
import java.util.UUID;

import net.fabricmc.api.EnvType;
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
        int buttonWidth = 160;
        int buttonHeight = 20;
        int startX = centerX - (buttonWidth / 2);

        int buttonPerRow = 2;
        int melodyButtonWidth = buttonWidth / 2;

        List<Song> songs = Song.getAllAvailableSongs(playerUuid);
        int songCount = songs.size();

        // Calculate how many rows of buttons are needed
        int rowCount = (int) Math.ceil(songCount / (double) buttonPerRow);
        int totalButtonBlockHeight = rowCount * buttonHeight;
        int baseButtonY = centerY - totalButtonBlockHeight - 10;

        for (int i = 0; i < songCount; i++) {
            Song song = songs.get(i);

            int buttonX = startX + (i % buttonPerRow) * melodyButtonWidth;
            int buttonY = baseButtonY + (i / buttonPerRow) * buttonHeight;

            this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("gui.zelda-oot-mod.song." + song.getId()),
                (btn) -> song.play(this.playerUuid)
            ).dimensions(buttonX, buttonY, melodyButtonWidth, buttonHeight).build());
        }

        // Back Button — placed below the last row of song buttons
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.zelda-oot-mod.back"),
            (btn) -> this.close()
        ).dimensions(startX, centerY + 10, buttonWidth, buttonHeight).build());
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    // ---------------------------------------------------------------------------------------------------------------------

    // Clicking behavior
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.client != null || this.client.player != null){}

        return super.mouseClicked(mouseX, mouseY, button);
    }

    // ---------------------------------------------------------------------------------------------------------------------
}
