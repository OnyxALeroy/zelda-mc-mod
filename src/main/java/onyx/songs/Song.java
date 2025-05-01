package onyx.songs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.util.Identifier;

public class Song {
    public static List<Song> songs = new ArrayList<>();

    private String id;
    private Identifier soundPath;
    private Map<UUID, Boolean> playerPermissions = new HashMap<>();


    public Song(String id) {
        this.id = id;
        this.soundPath = getIdentifier();
    }

    public String getId() { return id; }
    public Identifier getIdentifier() { return Identifier.of("zelda-oot-mod", "songs/" + id); }
    public Identifier getSoundPath(){ return soundPath; }

    // Check if a player can play this song
    public boolean canPlayerPlay(UUID playerUuid) {
        return playerPermissions.getOrDefault(playerUuid, false);
    }

    // Set whether a player can play this song
    public void setPlayerPermission(UUID playerUuid, boolean canPlay) {
        playerPermissions.put(playerUuid, canPlay);
    }

    // Play the song if the player has permission
    public boolean play(UUID playerUuid) {
        if (canPlayerPlay(playerUuid)) {
            // TODO: Implement playing logic here
            return true;
        }
        return false;
    }

    // ----------------------------------------------------------------------------------------------------------------------------------------------

    // Initialize all songs
    public static void initialize(){
        songs.add(new Song("song_of_time"));
    }

    // Helper method to find a song by ID
    public static Song getSongById(String id) {
        for (Song song : songs) {
            if (song.getId().equals(id)) {
                return song;
            }
        }
        return null;
    }

    // Helper method to get all available songs
    public static List<Song> getAllAvailableSongs(UUID playerUuid) {
        List<Song> availableSongs = new ArrayList<>();
        for (Song song : songs) {
            if (song.canPlayerPlay(playerUuid)) {
                availableSongs.add(song);
            }
        }
        return availableSongs;
    }
}
