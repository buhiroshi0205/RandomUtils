package com.hotmail.buhiroshi.RandomUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class RandomUtils extends JavaPlugin implements Listener {
    Map<String, Map<String, Location>> locations = new HashMap<String, Map<String, Location>>();
    final String HUB = "hub";
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
                    p.teleport(spawns.get(HUB));
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
                case "selfrank":
                    String[] check = {"NoodlesDragon", "PilipKim2010", "admin", "buhiroshi0205", "donor",
                        "dragonslayer105", "helper", "member", "moderator", "novice", "srmember"};
                    if (Arrays.binarySearch(check, p.getName()) >= 0) {
                        if (args.length == 1 && Arrays.binarySearch(check, args[0]) >= 0) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + p.getName() + " group set " + args[0]);
                            p.sendMessage("Your rank is now: " + args[0]);
                        } else {
                            p.sendMessage("Unknown rank. Make sure that the input is all lower case.");
                        }
                    } else {
                        p.sendMessage("Unknown command. Type \"/help\" for help.");
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
            spawns.put(HUB, new Location(Bukkit.getWorld(HUB), 0.5, 65, 0.5, 135, 7));
            spawns.put("staff", new Location(Bukkit.getWorld("staff"),34.5,113,-48.5,90,0));
            spawns.put("survival", new Location(Bukkit.getWorld("survival"),116.5,97.5,-18.5,180,0));
            shops.put("survival", new Location(Bukkit.getWorld("survival"),147.5,69,29.5,-90,0));
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
            e.getPlayer().teleport(spawns.get(HUB));
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
            if (!from.equals(HUB)) {
                locations.get(from).put(e.getPlayer().getName(), e.getFrom());
            }
            if (!to.equals(HUB)) {
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
    public void onPlayerMove(PlayerMoveEvent e) {
        Location loc = e.getTo();
        if (loc.getWorld().getName().equals("survival") && loc.getBlockY() == 69) {
            if (loc.getX() > 113 && loc.getX() < 120 && loc.getZ() > 75 && loc.getZ() < 82) {
                if (loc.getBlock().isLiquid()) randomTP(e.getPlayer(), e.getTo().getWorld());
            }
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
    
    public void randomTP(Player p, World world) {
        Random rnd = new Random();
        int x = rnd.nextInt(2000)-1000;
        int y;
        int z = rnd.nextInt(2000)-1000;
        Biome biome = world.getBiome(x, z);
        if (biome == Biome.OCEAN || biome == biome.DEEP_OCEAN || biome == Biome.RIVER) return;
        for (int i=0;i<5;i++) {
            Block top = world.getHighestBlockAt(x, z);
            y = top.getY();
            if (top.isLiquid()) {} else {
                if (top.getType().isSolid() || world.getBlockAt(x, --y, z).getType().isSolid()) {
                    p.teleport(new Location(world, x+0.5, ++y, z+0.5));
                    p.sendMessage(ChatColor.DARK_AQUA + "Teleported to the location:");
                    p.sendMessage(ChatColor.DARK_AQUA + "X: " + ChatColor.AQUA + x);
                    p.sendMessage(ChatColor.DARK_AQUA + "Y: " + ChatColor.AQUA + y);
                    p.sendMessage(ChatColor.DARK_AQUA + "Z: " + ChatColor.AQUA + z);
                    return;
                }
            }
            x++;
        }
    }
    
}
