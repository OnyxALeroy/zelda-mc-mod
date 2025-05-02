package onyx.server;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record PlayMelodyC2SPayload(String melodyID) implements CustomPayload {
    public static final Identifier PLAY_MELODY_PAYLOAD_ID = Identifier.of("zelda-oot-mod", "play_melody");
    public static final CustomPayload.Id<PlayMelodyC2SPayload> ID = new CustomPayload.Id<>(PLAY_MELODY_PAYLOAD_ID);
	public static final PacketCodec<RegistryByteBuf, PlayMelodyC2SPayload> CODEC = PacketCodec.tuple(
        PacketCodecs.STRING, PlayMelodyC2SPayload::melodyID,
        PlayMelodyC2SPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
