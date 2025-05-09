package onyx;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import onyx.blocks.ZeldaBlocks;
import onyx.entities.ZeldaEntities;
import onyx.items.TornadoRod;
import onyx.items.ZeldaItems;
import onyx.items.behaviourmanagers.Managers;
import onyx.server.GiveRupeeC2SPayload;
import onyx.server.OpenRupeeWalletS2CPayload;
import onyx.server.PlayMelodyC2SPayload;
import onyx.server.UseOcarinaS2CPayload;
import onyx.songs.Song;
import onyx.sounds.ZeldaSounds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZeldaOOTMod implements ModInitializer {
	public static final String MOD_ID = "zelda-oot-mod";

	// Group in inventory
	public static final RegistryKey<ItemGroup> ZELDA_BLOCK_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of("zelda-oot-mod", "zelda_block_group"));;
	public static final ItemGroup ZELDA_BLOCK_GROUP = FabricItemGroup.builder()
		.icon(() -> new ItemStack(ZeldaBlocks.BLOCK_OF_TIME))
		.displayName(Text.translatable("itemGroup.zelda-oot-mod.block_group"))
		.build();
	public static final RegistryKey<ItemGroup> ZELDA_ITEM_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of("zelda-oot-mod", "zelda_item_group"));;
	public static final ItemGroup ZELDA_ITEM_GROUP = FabricItemGroup.builder()
		.icon(() -> new ItemStack(ZeldaItems.OCARINA_OF_TIME))
		.displayName(Text.translatable("itemGroup.zelda-oot-mod.item_group"))
		.build();

	// Logger
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// Adding create-inventory groups
		Registry.register(Registries.ITEM_GROUP, ZELDA_BLOCK_GROUP_KEY, ZELDA_BLOCK_GROUP);
		Registry.register(Registries.ITEM_GROUP, ZELDA_ITEM_GROUP_KEY, ZELDA_ITEM_GROUP);

		// Initializers
		ZeldaBlocks.initialize(ZELDA_BLOCK_GROUP_KEY);
		ZeldaItems.initialize(ZELDA_ITEM_GROUP_KEY);
		ZeldaEntities.initialize();
		ZeldaSounds.initialize();
		Managers.initialize();
		ZeldaCommands.initialize();

		this.initializeAllPayloads();
		this.initializeAllTickEvents();

		Song.initialize();
	}

	// --------------------------------------------------------------------------------------------

	private void initializeAllPayloads(){
		PayloadTypeRegistry.playC2S().register(GiveRupeeC2SPayload.ID, GiveRupeeC2SPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(OpenRupeeWalletS2CPayload.ID, OpenRupeeWalletS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(UseOcarinaS2CPayload.ID, UseOcarinaS2CPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(PlayMelodyC2SPayload.ID, PlayMelodyC2SPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(GiveRupeeC2SPayload.ID, (payload, context) -> {
			ServerPlayerEntity player = context.player();
		    context.server().execute(() -> {
				ItemStack stack = new ItemStack(payload.rupee().getItem(), 1);
				boolean added = player.getInventory().insertStack(stack);

				if (!added || !stack.isEmpty()){
					// Dropping the item on the ground
					player.dropItem(stack, false);
				}
			});
		});

		ServerPlayNetworking.registerGlobalReceiver(PlayMelodyC2SPayload.ID, (payload, context) -> {
			ServerPlayerEntity player = context.player();
			context.server().execute(() -> {
				String melodyID = payload.melodyID();
				Song song = Song.getSongById(melodyID);
				if (song != null){
					song.play(context.player().getServer(), player.getUuid());
				}
			});
		});
	}

	private void initializeAllTickEvents(){
        // Register server tick event for tornado effect
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (var player : server.getPlayerManager().getPlayerList()) {
                TornadoRod.tickTornado(player);
            }
        });
	}

}
