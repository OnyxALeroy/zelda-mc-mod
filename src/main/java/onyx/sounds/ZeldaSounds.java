package onyx.sounds;

import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;

public class ZeldaSounds {
	private ZeldaSounds() {}

	public static final SoundEvent SONG_OF_STORMS = registerSound("song_of_storms");
	public static final SoundEvent SONG_OF_SUN = registerSound("song_of_sun");

	public static final SoundEvent MINUET_OF_FOREST = registerSound("minuet_of_forest");
	public static final SoundEvent BOLERO_OF_FIRE = registerSound("bolero_of_fire");
	public static final SoundEvent SERENADE_OF_WATER = registerSound("serenade_of_water");
	public static final SoundEvent NOCTURNE_OF_SHADOW = registerSound("nocturne_of_shadow");
	public static final SoundEvent REQUIEM_OF_SPIRIT = registerSound("requiem_of_spirit");
	public static final SoundEvent PRELUDE_OF_LIGHT = registerSound("prelude_of_light");

	private static SoundEvent registerSound(String id) {
		Identifier identifier = Identifier.of("zelda-oot-mod", id);
		return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
	}

	public static void initialize() {}
}
