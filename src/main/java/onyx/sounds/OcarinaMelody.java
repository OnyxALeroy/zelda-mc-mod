package onyx.sounds;

import java.util.ArrayList;
import java.util.List;

public class OcarinaMelody {
    private String name;
    private String description;
    private String imagePath;
    private String soundPath;
    private List<String> notes = new ArrayList<>();

    public OcarinaMelody(String name, String description, String imagePath, String soundPath, List<String> notes) {
        this.name = name;
        this.description = description;
        this.imagePath = imagePath;
        this.soundPath = soundPath;
        this.notes = notes;
    }

    public List<String> getNotes(){
        return notes;
    }
}
