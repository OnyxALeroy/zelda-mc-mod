package onyx.components;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerNoteTracker {
    private static final PlayerNoteTracker INSTANCE = new PlayerNoteTracker();

    // Map to store player notes by UUID
    private final Map<UUID, List<String>> playerNotes = new HashMap<>();

    private PlayerNoteTracker() {}
    public static PlayerNoteTracker getInstance() {
        return INSTANCE;
    }

    public void addNote(PlayerEntity player, String note) {
        UUID playerId = player.getUuid();
        List<String> notes = playerNotes.computeIfAbsent(playerId, k -> new ArrayList<>());
        notes.add(note);
    }

    public boolean hasPlayedNote(PlayerEntity player, String note) {
        UUID playerId = player.getUuid();
        List<String> notes = playerNotes.get(playerId);
        return notes != null && notes.contains(note);
    }

    public List<String> getPlayedNotes(PlayerEntity player) {
        UUID playerId = player.getUuid();
        List<String> notes = playerNotes.get(playerId);
        return notes != null ? new ArrayList<>(notes) : new ArrayList<>();
    }

    public void clearNotes(PlayerEntity player) {
        playerNotes.remove(player.getUuid());
    }

    public void savePlayerData(ServerPlayerEntity player, NbtCompound playerData) {
        UUID playerId = player.getUuid();
        List<String> notes = playerNotes.get(playerId);
        
        if (notes != null && !notes.isEmpty()) {
            NbtList notesList = new NbtList();
            for (String note : notes) {
                notesList.add(NbtString.of(note));
            }
            
            // Store in a compound for your mod
            NbtCompound modData = new NbtCompound();
            modData.put("PlayedNotes", notesList);
            
            // Add to player data
            playerData.put("zelda-oot-mod.notes", modData);
        }
    }
    
    public void loadPlayerData(ServerPlayerEntity player, NbtCompound playerData) {
        UUID playerId = player.getUuid();
        
        // Clear existing data
        playerNotes.remove(playerId);
        
        // Load from NBT if exists
        if (playerData.contains("zelda-oot-mod.notes")) {
            NbtCompound modData = playerData.getCompound("zelda-oot-mod.notes");
            if (modData.contains("PlayedNotes")) {
                List<String> notes = new ArrayList<>();
                NbtList notesList = modData.getList("PlayedNotes", NbtString.STRING_TYPE);
                
                for (int i = 0; i < notesList.size(); i++) {
                    notes.add(notesList.getString(i));
                }
                
                playerNotes.put(playerId, notes);
            }
        }
    }
}
