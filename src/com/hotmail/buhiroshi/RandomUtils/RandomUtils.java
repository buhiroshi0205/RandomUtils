package com.hotmail.buhiroshi.RandomUtils;

import java.util.Arrays;
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
    
    private static World WORLD_HUB;
    private static World WORLD_STAFF;
    private static World WORLD_SURVIVAL;

    private static final PlayerLocations locations = new PlayerLocations();
    private static final TeleportLocations spawns = new TeleportLocations();
    private static final TeleportLocations shops = new TeleportLocations();

    private static void setupStatics() {
        WORLD_HUB = Bukkit.getWorld("hub");
        WORLD_STAFF = Bukkit.getWorld("staff");
        WORLD_SURVIVAL = Bukkit.getWorld("survival");
        spawns.addLocation(new Location(WORLD_HUB, 0.5, 65, 0.5, 135, 7));
        spawns.addLocation(new Location(WORLD_STAFF,34.5,113,-48.5,90,0));
        spawns.addLocation(new Location(WORLD_SURVIVAL,116.5,97.5,-18.5,180,0));
        shops.addLocation(new Location(WORLD_SURVIVAL,147.5,69,29.5,-90,0));
    }

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
                    //p.teleport(spawns.getLocationInWorld(WORLD_HUB));
                    Bukkit.dispatchCommand(sender, "warp hub");
                    break;
                case "spawn":
                    //p.teleport(spawns.getLocationInWorld(p.getWorld()));
                    Bukkit.dispatchCommand(sender, "warp spawn");
                    break;
                case "shop":
                    /*Location temp = shops.getLocationInWorld(p.getWorld());
                    if (temp == null) {
                        p.sendMessage(ChatColor.RED + "You are not in a world with a valid admin shop!");
                    } else {
                        p.teleport(temp);
                    }*/
                    Bukkit.dispatchCommand(sender, "warp shop");
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
            setupStatics();
            getDataFolder().mkdir();
            locations.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        try {
            locations.updateAll();
            locations.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        try {
            e.getPlayer().teleport(spawns.getLocationInWorld(WORLD_HUB));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        try {
            Location from = e.getFrom();
            Location to = e.getTo();
            if (from.getWorld().equals(to.getWorld())) return;
            if (WorldHandler.isWorldSameGroup(from.getWorld(), to.getWorld())) return;
            if (!from.getWorld().equals(WORLD_HUB)) {
                locations.updatePlayerLocation(e.getPlayer(), from);
            }
            if (!to.getWorld().equals(WORLD_HUB)) {
                Location temp = locations.getPlayerLocation(to.getWorld(), e.getPlayer());
                if (temp == null) {
                    e.setTo(spawns.getLocationInWorld(e.getTo().getWorld()));
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
            locations.updatePlayerLocation(e.getPlayer());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void randomTP(Player p, World world) {
        Random rnd = new Random();
        int x = rnd.nextInt(4000)-2000;
        int y;
        int z = rnd.nextInt(4000)-2000;
        Biome biome = world.getBiome(x, z);
        if (biome == Biome.OCEAN || biome == Biome.DEEP_OCEAN || biome == Biome.RIVER) return;
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
