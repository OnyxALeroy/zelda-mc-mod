package onyx.sounds;

import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;

public class ZeldaSounds {
	private ZeldaSounds() {}

	public static final SoundEvent SONG_OF_STORMS = registerSound("song_of_storms");
	public static final SoundEvent SONG_OF_SUN = registerSound("song_of_sun");

	private static SoundEvent registerSound(String id) {
		Identifier identifier = Identifier.of("zelda-oot-mod", id);
		return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
	}

	public static void initialize() {}
}
