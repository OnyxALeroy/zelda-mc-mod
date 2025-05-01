package onyx;

import java.util.UUID;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import onyx.songs.Song;

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
}
