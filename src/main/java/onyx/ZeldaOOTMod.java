package onyx;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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
import onyx.songs.WarpSong;
import onyx.sounds.ZeldaSounds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

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

		this.initializeAllPayloads();
		this.initializeAllTickEvents();
		this.initializeAllCommands();

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

	private void initializeAllCommands(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("learn_melody")
                .then(CommandManager.argument("player", EntityArgumentType.player())
                    .then(CommandManager.argument("melody", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            for (Song song : Song.songs) {
                                builder.suggest(song.getId());
                            }
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            // Get the melody ID
                            String melodyId = StringArgumentType.getString(context, "melody");
                            Song song = Song.getSongById(melodyId);
                            
                            // Check if the song is a warp song
                            if (song instanceof WarpSong) {
                                // If it's a warp song, throw an exception indicating that a BlockPos is required
                                throw new SimpleCommandExceptionType(
                                    Text.of("This is a warp song! You must specify a block position: /learn_melody <player> " + melodyId + " <x> <y> <z>")
                                ).create();
                            }
                            
                            // If not a warp song, process normally
					        return ZeldaCommands.executeLearnMelodyCommand(context);
                        })
                        .then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                            .executes(context -> {
                                // Get the melody ID and check if it's a warp song
                                String melodyId = StringArgumentType.getString(context, "melody");
                                Song song = Song.getSongById(melodyId);
                                
                                if (!(song instanceof WarpSong)) {
                                    // If it's not a warp song but position is provided, inform the user
                                    throw new SimpleCommandExceptionType(
                                        Text.of("This is not a warp song! No position needed: /learn_melody <player> " + melodyId)
                                    ).create();
                                }
                                
                                BlockPos pos = BlockPosArgumentType.getBlockPos(context, "pos");
                                return executeLearnWarpSongCommand(context, pos, "north");
                            })
                            .then(CommandManager.argument("facing", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    // Suggest valid facing directions
                                    builder.suggest("NORTH");
                                    builder.suggest("SOUTH");
                                    builder.suggest("EAST");
                                    builder.suggest("WEST");
                                    builder.suggest("UP");
                                    builder.suggest("DOWN");
                                    return builder.buildFuture();
                                })
                                .executes(context -> {
                                    // Get the melody ID and check if it's a warp song
                                    String melodyId = StringArgumentType.getString(context, "melody");
                                    Song song = Song.getSongById(melodyId);
                                    
                                    if (!(song instanceof WarpSong)) {
                                        throw new SimpleCommandExceptionType(
                                            Text.of("This is not a warp song! No position or facing needed: /learn_melody <player> " + melodyId)
                                        ).create();
                                    }

                                    // Get the block position and facing direction
                                    BlockPos pos = BlockPosArgumentType.getBlockPos(context, "pos");
                                    String facing = StringArgumentType.getString(context, "facing");

                                    // Validate the facing direction
									facing = facing.toLowerCase();
                                    if (!(facing.equals("north") || facing.equals("south") || 
									facing.equals("east") || facing.equals("west"))) {
                                        throw new SimpleCommandExceptionType(
                                            Text.of("Invalid facing direction! Use north, south, east or west")
                                        ).create();
                                    }

                                    // Execute with the warp position and facing
                                    return executeLearnWarpSongCommand(context, pos, facing);
                                }))))));
        });
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("forget_melody")
				.then(CommandManager.argument("player", EntityArgumentType.player())
					.then(CommandManager.argument("melody", StringArgumentType.string())
						.suggests((context, builder) -> {
							for (Song song : Song.songs) {
								builder.suggest(song.getId());
							}
							return builder.buildFuture();
						})
						.executes(context -> ZeldaCommands.executeForgetMelodyCommand(context)))));
		});
	}

	// ---------------------------------------------------------------------------------------------------------------------

    private static int executeLearnWarpSongCommand(CommandContext<ServerCommandSource> context, BlockPos pos, String facing) throws CommandSyntaxException {
        String melodyId = StringArgumentType.getString(context, "melody");
        Song song = Song.getSongById(melodyId);
        if (song instanceof WarpSong warpSong) {
            warpSong.setWarpPos(pos);
			ZeldaOOTMod.LOGGER.info(facing);
			warpSong.setFacing(Direction.byName(facing));

            // Now execute the learn command
            int result = ZeldaCommands.executeLearnMelodyCommand(context);
            
            // Provide feedback
            context.getSource().sendFeedback(() -> 
                Text.of("Warp song " + melodyId + " learned with warp point set to " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + " facing " + facing), 
                true);
                
            return result;
        }
        
        throw new SimpleCommandExceptionType(Text.of("Error processing warp song.")).create();
    }
}
