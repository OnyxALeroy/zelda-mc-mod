package onyx.server;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record UseOcarinaS2CPayload(String ocarina) implements CustomPayload {
    public static final Identifier USE_OCARINA_PAYLOAD_ID = Identifier.of("zelda-oot-mod", "use_ocarina");
    public static final CustomPayload.Id<UseOcarinaS2CPayload> ID = new CustomPayload.Id<>(USE_OCARINA_PAYLOAD_ID);
	public static final PacketCodec<RegistryByteBuf, UseOcarinaS2CPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, UseOcarinaS2CPayload::ocarina, UseOcarinaS2CPayload::new);


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
