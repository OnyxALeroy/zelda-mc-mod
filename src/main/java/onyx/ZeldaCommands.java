package onyx;

import java.util.UUID;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import onyx.components.ZeldaComponents;
import onyx.items.ocarina.OcarinaTemplate;
import onyx.songs.Song;
import onyx.songs.WarpSong;

public class ZeldaCommands {
    public static int executeLearnMelodyCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        String melodyId = StringArgumentType.getString(context, "melody");
        UUID playerUuid = player.getUuid();

        // Check if the melody exists
        Song song = Song.getSongById(melodyId);
        if (song == null) {
            context.getSource().sendError(Text.literal("Unknown melody: " + melodyId));
            return 0;
        }

        // Grant permission to play the melody
        if (song.canPlayerPlay(playerUuid)) {
            context.getSource().sendError(Text.literal(player.getName().getString() + " already knows the melody: " + melodyId));
            return 0;
        }
        song.setPlayerPermission(playerUuid, true);

        // Send feedback
        context.getSource().sendFeedback(
            () -> Text.literal(player.getName().getString() + " has learned the melody: " + melodyId),
            true
        );
        
        return 1;
    }

    public static int executeForgetMelodyCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        String melodyId = StringArgumentType.getString(context, "melody");
        UUID playerUuid = player.getUuid();

        // Check if the melody exists
        Song song = Song.getSongById(melodyId);
        if (song == null) {
            context.getSource().sendError(Text.literal("Unknown melody: " + melodyId));
            return 0;
        }

        // Grant permission to play the melody
        if (!song.canPlayerPlay(playerUuid)) {
            context.getSource().sendError(Text.literal(player.getName().getString() + " already doesn't know the melody: " + melodyId));
            return 0;
        }
        song.setPlayerPermission(playerUuid, false);

        // Send feedback
        context.getSource().sendFeedback(
            () -> Text.literal(player.getName().getString() + " has forget the melody: " + melodyId),
            true
        );
        
        return 1;
    }

    public static int executeLearnWarpSongCommand(CommandContext<ServerCommandSource> context, BlockPos pos1, String facing1, BlockPos pos2, String facing2) throws CommandSyntaxException {
        String melodyId = StringArgumentType.getString(context, "melody");
        Song song = Song.getSongById(melodyId);
        if (song instanceof WarpSong warpSong) {
            warpSong.setChildWarpPos(pos1);
			warpSong.setChildFacing(Direction.byName(facing1));
            warpSong.setAdultWarpPos(pos2);
            warpSong.setAdultFacing(Direction.byName(facing2));

            // Now execute the learn command
            return ZeldaCommands.executeLearnMelodyCommand(context);
        }
        
        throw new SimpleCommandExceptionType(Text.of("Error processing warp song.")).create();
    }

    public static int executeSwitchTimePeriod(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");

        // Iterate through main inventory
        for (ItemStack stack : player.getInventory().main) {
            Item item = stack.getItem();
            if (item instanceof OcarinaTemplate) {
                stack.set(ZeldaComponents.IS_OWNER_ADULT, !stack.getOrDefault(ZeldaComponents.IS_OWNER_ADULT, false));
            }
        }

        for (ItemStack stack : player.getInventory().offHand) {
            Item item = stack.getItem();
            if (item instanceof OcarinaTemplate) {
                stack.set(ZeldaComponents.IS_OWNER_ADULT, !stack.getOrDefault(ZeldaComponents.IS_OWNER_ADULT, false));
            }
        }

        return 0;
    }

    // ---------------------------------------------------------------------------------------------------------------------

	public static void initialize(){
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
                        .then(CommandManager.argument("pos1", BlockPosArgumentType.blockPos())
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
                                
                                BlockPos pos1 = BlockPosArgumentType.getBlockPos(context, "pos1");
                                return executeLearnWarpSongCommand(context, pos1, "north", pos1, "north");
                            })
                            .then(CommandManager.argument("facing1", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    // Suggest valid facing directions
                                    builder.suggest("NORTH");
                                    builder.suggest("SOUTH");
                                    builder.suggest("EAST");
                                    builder.suggest("WEST");
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
                                    BlockPos pos1 = BlockPosArgumentType.getBlockPos(context, "pos1");
                                    String facing1 = StringArgumentType.getString(context, "facing1");

                                    // Validate the facing direction
									facing1 = facing1.toLowerCase();
                                    if (!(facing1.equals("north") || facing1.equals("south") || 
									facing1.equals("east") || facing1.equals("west"))) {
                                        throw new SimpleCommandExceptionType(
                                            Text.of("Invalid facing direction! Use north, south, east or west")
                                        ).create();
                                    }

                                    // Execute with the warp position and facing
                                    return executeLearnWarpSongCommand(context, pos1, facing1, pos1, facing1);
                                })
                                .then(CommandManager.argument("pos2", BlockPosArgumentType.blockPos())
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

                                        // Get the block position and facing direction
                                        BlockPos pos1 = BlockPosArgumentType.getBlockPos(context, "pos1");
                                        String facing1 = StringArgumentType.getString(context, "facing1");
                                        BlockPos pos2 = BlockPosArgumentType.getBlockPos(context, "pos2");
    
                                        // Validate the facing direction
                                        facing1 = facing1.toLowerCase();
                                        if (!(facing1.equals("north") || facing1.equals("south") || 
                                        facing1.equals("east") || facing1.equals("west"))) {
                                            throw new SimpleCommandExceptionType(
                                                Text.of("Invalid facing direction! Use north, south, east or west")
                                            ).create();
                                        }

                                        return executeLearnWarpSongCommand(context, pos1, facing1, pos2, facing1);
                                    })
                                    .then(CommandManager.argument("facing2", StringArgumentType.word())
                                        .suggests((context, builder) -> {
                                            // Suggest valid facing directions
                                            builder.suggest("NORTH");
                                            builder.suggest("SOUTH");
                                            builder.suggest("EAST");
                                            builder.suggest("WEST");
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
                                            BlockPos pos1 = BlockPosArgumentType.getBlockPos(context, "pos1");
                                            String facing1 = StringArgumentType.getString(context, "facing1");
                                            BlockPos pos2 = BlockPosArgumentType.getBlockPos(context, "pos2");
                                            String facing2 = StringArgumentType.getString(context, "facing2");
        
                                            // Validate the facing directions
                                            facing1 = facing1.toLowerCase();
                                            if (!(facing1.equals("north") || facing1.equals("south") || 
                                            facing1.equals("east") || facing1.equals("west"))) {
                                                throw new SimpleCommandExceptionType(
                                                    Text.of("Invalid facing direction! Use north, south, east or west")
                                                ).create();
                                            }
                                            facing2 = facing2.toLowerCase();
                                            if (!(facing2.equals("north") || facing2.equals("south") || 
                                            facing2.equals("east") || facing2.equals("west"))) {
                                                throw new SimpleCommandExceptionType(
                                                    Text.of("Invalid facing direction! Use north, south, east or west")
                                                ).create();
                                            }
        
                                            // Execute with the warp position and facing
                                            return executeLearnWarpSongCommand(context, pos1, facing1, pos2, facing2);
                                        }))))))));
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
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("switch_time_period")
				.then(CommandManager.argument("player", EntityArgumentType.player())
						.executes(context -> ZeldaCommands.executeSwitchTimePeriod(context))));
		});
	}
}
