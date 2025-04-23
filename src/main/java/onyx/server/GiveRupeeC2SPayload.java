package onyx.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record GiveRupeeC2SPayload(ItemStack rupee) implements CustomPayload {
    public static final Identifier GET_RUPEE_PAYLOAD_ID = Identifier.of("zelda-oot-mod", "get_rupee");
	public static final CustomPayload.Id<GiveRupeeC2SPayload> ID = new CustomPayload.Id<>(GET_RUPEE_PAYLOAD_ID);
	public static final PacketCodec<RegistryByteBuf, GiveRupeeC2SPayload> CODEC = PacketCodec.tuple(ItemStack.PACKET_CODEC, GiveRupeeC2SPayload::rupee, GiveRupeeC2SPayload::new);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
