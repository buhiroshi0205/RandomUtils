package com.hotmail.buhiroshi.RandomUtils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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


public class RandomUtils extends JavaPlugin implements Listener {
    Map<String, Map<String, Location>> locations = new HashMap<String, Map<String, Location>>();
    final String SPAWN = "future";
    Map<String, Location> spawns = new HashMap<String, Location>();
    Map<String, Location> shops = new HashMap<String, Location>();
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Command sender must be a player!");
                return true;
            }
            Player p = (Player) sender;
            switch (cmd.getName().toLowerCase()) {
                case "hub":
                    p.teleport(spawns.get(SPAWN));
                    break;
                case "spawn":
                    p.teleport(spawns.get(p.getWorld().getName()));
                    break;
                case "shop":
                    Location temp = shops.get(p.getWorld().getName());
                    if (temp == null) {
                        p.sendMessage(ChatColor.RED + "You are not in a world with a valid admin shop!");
                    } else {
                        p.teleport(temp);
                    }
                    break;
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
                locations.put(world.getName(), Methods.getLocationData(world.getName()));
            }
            spawns.put(SPAWN, new Location(Bukkit.getWorld(SPAWN), 0.5, 65, 0.5, 135, 7));
            spawns.put("staff", new Location(Bukkit.getWorld("staff"),34.5,113,-48.5,90,0));
            spawns.put("survival", new Location(Bukkit.getWorld("survival"),52.5,99,19.5,0,0));
            shops.put("survival", new Location(Bukkit.getWorld("survival"),6.5,72,-28.5,90,0));
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
                Methods.saveLocationData(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        try {
            e.getPlayer().teleport(spawns.get(SPAWN));
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
            if (!from.equals(SPAWN)) {
                locations.get(from).put(e.getPlayer().getName(), e.getFrom());
            }
            if (!to.equals(SPAWN)) {
                Location temp = locations.get(to).get(e.getPlayer().getName());
                if (temp == null) {
                    e.setTo(spawns.get(e.getTo().getWorld().getName()));
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