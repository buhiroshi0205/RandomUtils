package com.hotmail.buhiroshi.RandomUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class RandomUtils extends JavaPlugin implements Listener {
    
    boolean maintenance = false;
    RamTracker ramtracker = new RamTracker(this);
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            if (!(sender instanceof Player)) {
                if (label.equals("maintenance")) {
                    maintenance = !maintenance;
                    this.getLogger().log(Level.INFO, "Server maintenance status set to {0}!", maintenance);
                } else {
                    sender.sendMessage("Command sender must be a player!");
                }
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
                    String[] check = {"NoodlesDragon", "PilipKim2010", "admin", "beta", "buhiroshi0205", "donor", "dragonslayer105", "elite",
                        "helper", "legendary", "member", "moderator", "novice", "pro", "srmember", "titan", "ultimate", "veteran", "vip"};
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
                case "setcommand":
                    boolean success = false;
                    if (p.hasPermission("RandomUtils.setcommand") && args.length > 0) {
                        Set<Material> lalala = null;
                        Block cmdblock = p.getTargetBlock(lalala, 10);
                        if (cmdblock != null && cmdblock.getType() == Material.COMMAND) {
                            StringBuilder sb = new StringBuilder(args[0]);
                            for (int i=1;i<args.length;i++) {
                                sb.append(' ');
                                sb.append(args[i]);
                            }
                            CommandBlock state = (CommandBlock) cmdblock.getState();
                            state.setCommand(sb.toString());
                            state.update();
                            p.sendMessage("Command set to: \"" + sb.toString() + "\"");
                            success = true;
                        }
                    }
                    if (!success) p.sendMessage("Failed! Are you sure you are looking directly at a command block 10 blocks within line of sight and entered a valid command?");
                    break;
                case "show":
                    ramtracker.show();
                    break;
                case "dump":
                    ramtracker.show();
                    ramtracker.reset();
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
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, ramtracker, 6000, 6000);
        getDataFolder().mkdir();
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
    public void onPlayerLogin(AsyncPlayerPreLoginEvent e) {
        if (!maintenance || e.getName().equals("buhiroshi0205")
                || e.getName().equals("PilipKim2010")
                || e.getName().equals("NoodlesDragon")
                || e.getName().equals("dragonslayer105")) return;
        e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Server under Maintenance!");
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        File origin = new File("plugins" + File.separator + "PerWorldInventory" + File.separator + "data"
                + File.separator + uuid.toString() + File.separator + "survival.json");
        File destination = new File("plugins" + File.separator + "RandomUtils" + File.separator + e.getPlayer().getName() + ".json");
        try {
            Files.copy(origin.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {}
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
