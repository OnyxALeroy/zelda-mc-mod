package onyx.sounds;

import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;

public class ZeldaSounds {
	private ZeldaSounds() {}

	public static final SoundEvent OCARINA_A_NOTE = registerSound("ocarina_a");
	public static final SoundEvent OCARINA_X_NOTE = registerSound("ocarina_x");
	public static final SoundEvent OCARINA_Y_NOTE = registerSound("ocarina_y");
	public static final SoundEvent OCARINA_L_NOTE = registerSound("ocarina_l");
	public static final SoundEvent OCARINA_R_NOTE = registerSound("ocarina_r");

	// actual registration of all the custom SoundEvents
	private static SoundEvent registerSound(String id) {
		Identifier identifier = Identifier.of("zelda-oot-mod", id);
		return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
	}

	public static void initialize() {}
}
