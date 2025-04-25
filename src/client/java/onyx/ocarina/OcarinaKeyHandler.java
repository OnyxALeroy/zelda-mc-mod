package onyx.ocarina;

import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import onyx.components.PlayerNoteTracker;

public class OcarinaKeyHandler {
    public static void playNote(PlayerEntity player, String note) {
        PlayerNoteTracker tracker = PlayerNoteTracker.getInstance();
        
        // Check if this is the first time they played this note
        boolean firstTime = !tracker.hasPlayedNote(player, note);
        
        // Record the note
        tracker.addNote(player, note);
        
        // Example of what you might do with this information
        if (firstTime) {
            player.sendMessage(Text.literal("You played note " + note + " for the first time!"), true);
        } else {
            player.sendMessage(Text.literal("You played note " + note + " again."), true);
        }

        // You could also check for specific sequences
        List<String> playedNotes = tracker.getPlayedNotes(player);
        checkForMelody(player, playedNotes);
    }

    private static void checkForMelody(PlayerEntity player, List<String> playedNotes) {
        if (playedNotes.size() < 4) return;

        // Get the last 4 notes
        int size = playedNotes.size();
        List<String> lastFour = playedNotes.subList(size - 4, size);
        
        // Check for a specific melody
        if (lastFour.equals(List.of("X", "X", "X", "X"))) {
            player.sendMessage(Text.literal("You played a special melody!"), true);
            // Add special effects or rewards here
        }
    }
}
