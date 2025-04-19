package onyx.items.behaviourmanagers;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;

import onyx.items.Slingshot;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class SlingshotHandler {
    public static final Map<UUID, Long> SLINGSHOT_LAST_USED = new HashMap<>();
    private static final long SLINGSHOT_COOLDOWN_MS = Slingshot.GetCooldownDuration();

    public static void register() {
        // Register the server tick event to manage the cooldowns
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            // Iterate through each player in the cooldown map
            Iterator<Map.Entry<UUID, Long>> iterator = SLINGSHOT_LAST_USED.entrySet().iterator();
            
            while (iterator.hasNext()) {
                Map.Entry<UUID, Long> entry = iterator.next();
                UUID uuid = entry.getKey();
                long lastUsedTime = entry.getValue();

                // Get the player from the server
                ServerPlayerEntity player = world.getServer().getPlayerManager().getPlayer(uuid);
                if (player == null) {
                    iterator.remove(); // If player is no longer online, remove them from the cooldown map
                    continue;
                }

                long now = System.currentTimeMillis();
                // If the cooldown has passed, remove the player from the cooldown list
                if (now - lastUsedTime > SLINGSHOT_COOLDOWN_MS) {
                    iterator.remove();
                }
            }
        });
    }

    // Method to check if the player can use the slingshot (i.e., if they are off cooldown)
    public static boolean canUseSlingshot(ServerPlayerEntity player) {
        long now = System.currentTimeMillis();
        long lastUsedTime = SLINGSHOT_LAST_USED.getOrDefault(player.getUuid(), 0L);
        return now - lastUsedTime > SLINGSHOT_COOLDOWN_MS;
    }

    // Method to update the cooldown after the player uses the slingshot
    public static void onSlingshotUsed(ServerPlayerEntity player) {
        SLINGSHOT_LAST_USED.put(player.getUuid(), System.currentTimeMillis());
    }
}
