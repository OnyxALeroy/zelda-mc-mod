package onyx.songs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class Song {
    public static List<Song> songs = new ArrayList<>();

    private String id;
    private Map<UUID, Boolean> playerPermissions = new HashMap<>();
    private SongAction action;

    public Song(String id, SongAction action) {
        this.id = id;
        this.action = action;
    }

    public String getId() { return id; }
    public void setAction(SongAction action) { this.action = action; }

    // Check if a player can play this song
    public boolean canPlayerPlay(UUID playerUuid) {
        return playerPermissions.getOrDefault(playerUuid, false);
    }

    // Set whether a player can play this song
    public void setPlayerPermission(UUID playerUuid, boolean canPlay) {
        playerPermissions.put(playerUuid, canPlay);
    }

    // Play the song if the player has permission
    public boolean play(MinecraftServer server, UUID playerUuid) {
        if (canPlayerPlay(playerUuid)) {
            if (action != null) {
                action.execute(server, playerUuid);
            }
            return true;
        }
        return false;
    }

    // ----------------------------------------------------------------------------------------------------------------------------------------------

    // Initialize all songs
    public static void initialize(){
        songs.add(new Song("song_of_sun", SongActions.SONG_OF_SUN));
        songs.add(new Song("song_of_storms", SongActions.SONG_OF_STORMS));

        songs.add(new WarpSong("minuet_of_forest", SongActions.MINUET_OF_FOREST, 
            new BlockPos(0, 0, 0), Direction.NORTH, new BlockPos(0, 0, 0), Direction.NORTH));

        // songs.add(new Song("song_of_time", SongActions.GIVE_COOKIE));
        // songs.add(new Song("song_of_storms", SongActions.SUMMON_LIGHTNING));
        // songs.add(new Song("song_of_saria", (server, uuid) -> {
        //     // Custom inline action
        // }));
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
