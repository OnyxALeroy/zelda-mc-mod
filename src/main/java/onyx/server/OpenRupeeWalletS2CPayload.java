package onyx.server;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record OpenRupeeWalletS2CPayload(Integer playerID, Integer handID) implements CustomPayload {
    public static final Identifier OPEN_RUPEE_WALLET_PAYLOAD_ID = Identifier.of("zelda-oot-mod", "open_rupee_screen");
    public static final CustomPayload.Id<OpenRupeeWalletS2CPayload> ID = new CustomPayload.Id<>(OPEN_RUPEE_WALLET_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, OpenRupeeWalletS2CPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, OpenRupeeWalletS2CPayload::playerID,
            PacketCodecs.INTEGER, OpenRupeeWalletS2CPayload::handID,
            OpenRupeeWalletS2CPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
