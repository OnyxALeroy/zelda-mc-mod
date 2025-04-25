package onyx.songs;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.Identifier;

public abstract class Song {
    public static List<Song> songs = new ArrayList<>();

    protected String name;
    protected String[] beginningNotes;

    public Song(String name, String[] beginningNotes) {
        this.name = name;
        this.beginningNotes = beginningNotes;
    }

    public Identifier getIdentifier() {
        return Identifier.of("zelda-oot-mod", "songs/" + name);
    }

    public void play(){
        // TODO: Implement the play method
    }

    // Initialize all songs
    public static void initialize(){
        // Initialize all notes

        // Initialize all songs
        songs.add(new SongOfTime());
        // songs.add(new SongOfStorms());
    }
}
