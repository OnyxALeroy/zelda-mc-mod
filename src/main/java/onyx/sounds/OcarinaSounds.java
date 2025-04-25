package onyx.sounds;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import onyx.ZeldaOOTMod;

import java.util.HashMap;
import java.util.Map;

public class OcarinaSounds {
    public static final Map<String, SoundEvent> OCARINA_NOTES = new HashMap<>();

    public static void registerSounds() {
        registerSound("C");
        registerSound("D");
        registerSound("E");
        registerSound("F");
        registerSound("G");
        registerSound("A");

        ZeldaOOTMod.LOGGER.info("Registered Ocarina sounds");
    }

    private static void registerSound(String note) {
        Identifier id = Identifier.of(ZeldaOOTMod.MOD_ID, "ocarina_" + note.toLowerCase());
        SoundEvent soundEvent = SoundEvent.of(id);
        OCARINA_NOTES.put(note, soundEvent);
        Registry.register(Registries.SOUND_EVENT, id, soundEvent);
    }
}
