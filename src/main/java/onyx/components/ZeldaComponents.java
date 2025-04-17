package onyx.components;

import net.minecraft.util.Identifier;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ZeldaComponents {
    public static final ComponentType<?> IS_HOOKABLE = Registry.register(
        Registries.DATA_COMPONENT_TYPE,
        Identifier.of("zelda-oot-mod", "is_hookable"),
        ComponentType.<Boolean>builder().codec(Codec.BOOL).build()
    );

	protected static void initialize(){}
}
