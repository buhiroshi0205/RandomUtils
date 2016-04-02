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
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class RandomUtils extends JavaPlugin implements Listener {
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Command sender must be a player!");
                return true;
            }
            Player p = (Player) sender;
            switch (cmd.getName().toLowerCase()) {
                case "spawn":
                    Bukkit.dispatchCommand(sender, "warp spawn");
                    break;
                case "shop":
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
    
    public void randomTP(Player p, World world) {
        int x = randomInRange(500, 2000);
        int y;
        int z = randomInRange(500, 2000);
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
    
    private int randomInRange(int min, int max) {
        Random rnd = new Random();
        int num = rnd.nextInt(max - min) + min;
        if (rnd.nextBoolean()) num *= -1;
        return num;
    }
    
}
