package onyx.ocarina;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import onyx.sounds.OcarinaMelody;
import onyx.sounds.ZeldaSounds;

import org.lwjgl.glfw.GLFW;

public class OcarinaNote {
    public static final Map<String, OcarinaNote> allNotes = new HashMap<>();
    public static final Map<String, OcarinaMelody> allMelodies = new HashMap<>();

    private String note;
    private SoundEvent note_sound;
    private KeyBinding keyBinding;

    public OcarinaNote(String note, SoundEvent sound, int keyCode) {
        this.note = note;
        this.note_sound = sound;
        this.keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.onyx.ocarina." + note.toLowerCase(), 
            InputUtil.Type.KEYSYM, 
            keyCode, 
            "category.onyx.ocarina"
        ));
    }

    public String getNote() {
        return note;
    }

    public KeyBinding getKeyBinding() {
        return keyBinding;
    }

    public boolean isPressed() {
        return keyBinding.wasPressed(); 
    }

    public boolean doesMatch(int keyCode, int scanCode) {
        return keyBinding.matchesKey(keyCode, scanCode);
    }

    public void play(PlayerEntity user){
        user.playSound(note_sound, 1.0F, 1.0F);
    }

    // --------------------------------------------------------------------------------------------

    public static void initialize() {
        // Initialize 6 notes with default key bindings
        allNotes.put("X", new OcarinaNote("X", ZeldaSounds.OCARINA_X_NOTE, GLFW.GLFW_KEY_A));
        allNotes.put("Y", new OcarinaNote("Y", ZeldaSounds.OCARINA_Y_NOTE, GLFW.GLFW_KEY_W));
        allNotes.put("A", new OcarinaNote("A", ZeldaSounds.OCARINA_A_NOTE, GLFW.GLFW_KEY_D));
        allNotes.put("L", new OcarinaNote("L", ZeldaSounds.OCARINA_L_NOTE, GLFW.GLFW_KEY_Q));
        allNotes.put("R", new OcarinaNote("R", ZeldaSounds.OCARINA_R_NOTE, GLFW.GLFW_KEY_E));

        // Initialize melodies
        allMelodies.put("Song of Time", new OcarinaMelody(
            "Song of Time",
            "A song that can manipulate time.",
            "textures/ocarina/song_of_time.png",
            "sounds/ocarina/song_of_time.ogg",
            List.of("Y", "L", "R", "Y", "L", "R")
        ));
    }
}
