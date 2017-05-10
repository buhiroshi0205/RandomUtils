package com.hotmail.buhiroshi.RandomUtils;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class RandomUtils extends JavaPlugin implements Listener {
    
    boolean maintenance = false;
    RamTracker ramtracker = new RamTracker(this);
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            if (!(sender instanceof Player)) {
                switch (label.toLowerCase()) {
                    case "maintenance":
                        maintenance = !maintenance;
                        this.getLogger().log(Level.INFO, "Server maintenance status set to {0}!", maintenance);
                        break;
                    case "show":
                        ramtracker.show();
                        break;
                    case "dump":
                        ramtracker.show();
                        ramtracker.reset();
                        break;
                    default:
                        sender.sendMessage("Command sender must be a player!");
                        break;
                }
                return false;
            }
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }
    
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, ramtracker, 6000, 6000);
        getDataFolder().mkdir();
    }
    
    @Override
    public void onDisable() {
        ramtracker.show();
    }
    
    @EventHandler
    public void onPlayerLogin(AsyncPlayerPreLoginEvent e) {
        if (!maintenance || e.getName().equals("buhiroshi0205")
                || e.getName().equals("Veritas233")
                || e.getName().equals("NoodlesDragon")
                || e.getName().equals("dragonslayer105")) return;
        e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Server under Maintenance!");
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.getPlayer().teleport(new Location(e.getPlayer().getWorld(), -340.5, 134, 134.5, -90, 0));
    }
    
}
