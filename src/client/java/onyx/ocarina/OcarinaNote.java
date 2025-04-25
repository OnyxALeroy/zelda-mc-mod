package onyx.ocarina;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import onyx.sounds.OcarinaMelody;

import org.lwjgl.glfw.GLFW;

public class OcarinaNote {
    public static final Map<String, OcarinaNote> allNotes = new HashMap<>();
    public static final Map<String, OcarinaMelody> allMelodies = new HashMap<>();

    private String note;
    private KeyBinding keyBinding;

    public OcarinaNote(String note, int keyCode) {
        this.note = note;
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

    // --------------------------------------------------------------------------------------------

    public static void initialize() {
        // Initialize 6 notes with default key bindings
        allNotes.put("X", new OcarinaNote("X", GLFW.GLFW_KEY_W));
        allNotes.put("B", new OcarinaNote("B", GLFW.GLFW_KEY_S));
        allNotes.put("Y", new OcarinaNote("Y", GLFW.GLFW_KEY_Q));
        allNotes.put("A", new OcarinaNote("A", GLFW.GLFW_KEY_D));
        allNotes.put("L", new OcarinaNote("L", GLFW.GLFW_KEY_U));
        allNotes.put("R", new OcarinaNote("R", GLFW.GLFW_KEY_O));

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
