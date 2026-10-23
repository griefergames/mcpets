package fr.nocsy.mcpets.listeners;

import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.sql.Databases;
import fr.nocsy.mcpets.data.sql.PlayerData;
import fr.nocsy.mcpets.events.PetLevelUpEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class DatabaseRefreshListener implements Listener {

    @EventHandler
    public void refresh_DB_onjoin(PlayerJoinEvent e) {
        Databases.loadDataAsync(e.getPlayer().getUniqueId());
    }

    /*
    WARNING: this technique was quite heavy on performance depending on how pets would gain experience
             instead, I suggest the following data saves:
             - regular x ticks (not possible on Bungee servers)
             - levelup / evolution
             - player quit
    @EventHandler
    public void saveExperienceDB(PetGainExperienceEvent e) {
        // Adding a layer of saving for MySQL users, that saves the DB for each experience gain
        // since there seems to be recurrent saving issues
        if (GlobalConfig.getInstance().isDatabaseSupport()) {
            UUID owner = e.getPet().getOwner();
            if (owner != null) {
                Databases.savePlayerData(owner);
            }
        }
    }
    */

    @EventHandler
    public void saveDB(PetLevelUpEvent e) {
        if (GlobalConfig.getInstance().isDatabaseSupport()) {
            UUID owner = e.getPet().getOwner();
            if (owner != null) {
                Databases.savePlayerDataAsync(owner);
            }
        }
    }

    @EventHandler
    public void saveDB(PlayerQuitEvent e) {
        if (GlobalConfig.getInstance().isDatabaseSupport()) {
            UUID owner = e.getPlayer().getUniqueId();
            if (PlayerData.isRegistered(owner)) {
                Databases.savePlayerDataAsync(owner);
            }
        }
    }
}
