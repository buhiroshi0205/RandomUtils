package com.hotmail.buhiroshi.RandomUtils;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class RandomUtils extends JavaPlugin implements Listener{

    Methods methods = new Methods();
    Map<String, Map<String, Location>> locations = new HashMap();
    final String SPAWN_WORLD = "spawn";
    String errormessage = "";
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Command sender must be a player!");
                return true;
            };
            Player p = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("hub")) {
                p.teleport(new Location(Bukkit.getWorld(SPAWN_WORLD), 0.5F, 65F, 0.5F, 135F, 7F));
            } else if (cmd.getName().equalsIgnoreCase("test")) {
                
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }
    
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        try{
            getDataFolder().mkdir();
            for (World world : Bukkit.getWorlds()) {
                locations.put(world.getName(), methods.getLocationData(world.getName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onDisable() {
        try {
            for (Player p : Bukkit.getOnlinePlayers()) {
                locations.get(p.getWorld().getName()).put(p.getName(), p.getLocation());
            }
            for (Map.Entry<String, Map<String, Location>> entry : locations.entrySet()) {
                methods.saveLocationData(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        try {
            e.getPlayer().teleport(new Location(Bukkit.getWorld(SPAWN_WORLD), 0.5F, 65F, 0.5F, 135F, 7F));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        try {
            String from = e.getFrom().getWorld().getName();
            String to = e.getTo().getWorld().getName();
            if (from.equals(to)) return;
            if (!from.equals(SPAWN_WORLD)) {
                locations.get(from).put(e.getPlayer().getName(), e.getFrom());
            }
            if (!to.equals(SPAWN_WORLD)) {
                Location temp = locations.get(to).get(e.getPlayer().getName());
                if (temp == null) {
                    //teleport to world spawn
                } else {
                    e.setTo(temp);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        try {
            Player p = e.getPlayer();
            locations.get(p.getWorld().getName()).put(p.getName(), p.getLocation());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}