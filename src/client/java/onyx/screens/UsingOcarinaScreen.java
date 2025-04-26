package onyx.screens;

import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

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
import onyx.ocarina.OcarinaNote;
import onyx.sounds.OcarinaMelody;

@Environment(EnvType.CLIENT)
public class UsingOcarinaScreen extends Screen {
    public Screen parent;
    private static Identifier BACKGROUND_TEXTURE = Identifier.of("zelda-oot-mod", "textures/gui/using_ocarina.png");

    private Queue<String> playedNotes = new LinkedList<>();
    private ButtonWidget AButton;
    private ButtonWidget XButton;
    private ButtonWidget YButton;
    private ButtonWidget LButton;
    private ButtonWidget RButton;
    private final Map<ButtonWidget, Integer> flashButtons = new HashMap<>();
    private final int flashingTime = 10; // 1 tick (~0.05s)

    public UsingOcarinaScreen(Screen parent){
        super(Text.empty());
        this.parent = parent;
    }

	@Override
	protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int ocarinaButtonSize = 30;
        int ocarinaBigButtonWidth = 40;
        int ocarinaBigButtonHeight = 20;

        // Back Button
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.zelda-oot-mod.back"), (btn) -> {
            this.close();
        }).dimensions(centerX - 60, centerY + 10, 120, 20).build());

        // Ocarina Buttons
        AButton = ButtonWidget.builder(Text.of("A"), (btn) -> {
            this.playNote(OcarinaNote.allNotes.get("A"));
        }).dimensions(centerX + 20, centerY - 46, ocarinaButtonSize, ocarinaButtonSize).build();
        XButton = ButtonWidget.builder(Text.of("X"), (btn) -> {
            this.playNote(OcarinaNote.allNotes.get("X"));
        }).dimensions(centerX - 50, centerY - 46, ocarinaButtonSize, ocarinaButtonSize).build();
        YButton = ButtonWidget.builder(Text.of("Y"), (btn) -> {
            this.playNote(OcarinaNote.allNotes.get("Y"));
        }).dimensions(centerX - 15, centerY - 80, ocarinaButtonSize, ocarinaButtonSize).build();
        LButton = ButtonWidget.builder(Text.of("L"), (btn) -> {
            this.playNote(OcarinaNote.allNotes.get("L"));
        }).dimensions(centerX - 75, centerY - 99, ocarinaBigButtonWidth, ocarinaBigButtonHeight).build();
        RButton = ButtonWidget.builder(Text.of("R"), (btn) -> {
            this.playNote(OcarinaNote.allNotes.get("R"));
        }).dimensions(centerX + 37, centerY - 99, ocarinaBigButtonWidth, ocarinaBigButtonHeight).build();
        this.addDrawableChild(AButton);
        this.addDrawableChild(XButton);
        this.addDrawableChild(YButton);
        this.addDrawableChild(LButton);
        this.addDrawableChild(RButton);
	}

    @Override
    public void close() {
        this.client.setScreen(this.parent);
        clearNotes();
    }

    // ---------------------------------------------------------------------------------------------------------------------

    @Override
    public void tick() {
        super.tick();

        flashButtons.entrySet().removeIf(entry -> {
            int timeLeft = entry.getValue() - 1;
            if (timeLeft <= 0) {
                return true; // Remove when finished
            } else {
                entry.setValue(timeLeft); // Update countdown
                return false;
            }
        });
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(context);
        int fakeMouseX = mouseX;
        int fakeMouseY = mouseY;
    
        if (!flashButtons.isEmpty()) {
            // Simulate mouse over the first flashing button
            ButtonWidget button = flashButtons.keySet().iterator().next();
            fakeMouseX = button.getX() + button.getWidth() / 2;
            fakeMouseY = button.getY() + button.getHeight() / 2;
        }
    
        super.render(context, fakeMouseX, fakeMouseY, delta);
        flashButtons.clear();
    }

    private void renderBackgroundTexture(DrawContext context) {
        int ocarinaWidth = 225;
        int ocarinaHeight = 100;
        int yOffset = 50;

        int topLeftX = (this.width - ocarinaWidth) / 2;
        int topLeftY = (this.height - ocarinaHeight) / 2 - yOffset;

        context.drawTexture(
            RenderLayer::getGuiTextured,
            BACKGROUND_TEXTURE,
            topLeftX, topLeftY,
            0.0F, 0.0F,
            ocarinaWidth, ocarinaHeight,
            ocarinaWidth, ocarinaHeight
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
        if (OcarinaNote.allNotes.get("A").doesMatch(keyCode, scanCode)){
            if (AButton != null) {
                AButton.onPress(); 
                flashButtons.put(AButton, flashingTime);
            }
        } else if (OcarinaNote.allNotes.get("X").doesMatch(keyCode, scanCode)){
            if (XButton != null) {
                XButton.onPress(); 
                flashButtons.put(XButton, flashingTime);
            }
        } else if (OcarinaNote.allNotes.get("Y").doesMatch(keyCode, scanCode)){
            if (YButton != null) {
                YButton.onPress(); 
                flashButtons.put(YButton, flashingTime);
            }
        } else if (OcarinaNote.allNotes.get("L").doesMatch(keyCode, scanCode)){
            if (LButton != null) {
                LButton.onPress(); 
                flashButtons.put(LButton, flashingTime);
            }
        } else if (OcarinaNote.allNotes.get("R").doesMatch(keyCode, scanCode)){
            if (RButton != null) {
                RButton.onPress(); 
                flashButtons.put(RButton, flashingTime);
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }


    // ---------------------------------------------------------------------------------------------------------------------

    // Playing notes
    public void playNote(OcarinaNote note) {
        if (this.client != null && this.client.player != null) {
            note.play(this.client.player);
            playedNotes.offer(note.getNote());
            checkIfMelodyPlayed();
        }
    }

    // Check if a melody was played
    private void checkIfMelodyPlayed(){
        List<String> melodySixNotes = new ArrayList<>();
        List<String> melodyEightNotes = new ArrayList<>();

        // Peeking into the first 6 notes if they exist
        int count = 0;
        for (String element : playedNotes) {
            if (count >= 8) break;
            if (count < 6) melodySixNotes.add(element);
            melodyEightNotes.add(element);
            count++;
        }

        // Check if the melody is in the list of melodies
        for (Map.Entry<String, OcarinaMelody> entry : OcarinaNote.allMelodies.entrySet()) {
            if (entry.getValue().getNotes().equals(melodySixNotes) || entry.getValue().getNotes().equals(melodyEightNotes)) {
                // Melody found
                SystemToast.add(
                    this.client.getToastManager(),
                    SystemToast.Type.NARRATOR_TOGGLE,
                    Text.literal("Melody Played!"),
                    Text.literal("You played: " + entry.getKey())
                );
                playedNotes = new LinkedList<>();
                return;
            }
        }

        // Else, if more than 10 notes are played, remove the first one
        if (playedNotes.size() > 10) {
            playedNotes.poll();
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
