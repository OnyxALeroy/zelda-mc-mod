package onyx.components;

import net.minecraft.util.Identifier;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ZeldaComponents {
    public static final ComponentType<Boolean> IS_HOOKABLE = Registry.register(
        Registries.DATA_COMPONENT_TYPE,
        Identifier.of("zelda-oot-mod", "is_hookable"),
        ComponentType.<Boolean>builder().codec(Codec.BOOL).build()
    );

    public static final ComponentType<Integer> RUPEES_POSSESSED = Registry.register(
        Registries.DATA_COMPONENT_TYPE,
        Identifier.of("zelda-oot-mod", "rubies_possessed"),
        ComponentType.<Integer>builder().codec(Codec.INT).build()
    );

	protected static void initialize(){}
}
