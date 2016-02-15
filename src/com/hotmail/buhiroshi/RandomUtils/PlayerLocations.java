package com.hotmail.buhiroshi.RandomUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class PlayerLocations {
    private final Map<String, Map<String, Location>> mapInteral = new HashMap<String, Map<String, Location>>();

    public void load() {
        // no optimization due to low usage
        for (World world : Bukkit.getWorlds()) {
            // read file
            File file = new File("plugins" + File.separator + "RandomUtils" + File.separator + world.getName() + ".csv");
            if (file.exists()) {
                BufferedReader input;
                try {
                    input = new BufferedReader(new FileReader(file));
                    input.readLine();
                    String line;
                    while ((line = input.readLine()) != null) {
                        String parse[] = line.split(",");
                        String playerName;
                        Location loc;
                        if (parse.length == 6) {
                            // v1
                            float[] values = new float[5];
                            for (int i=1; i<6; i++) {
                                values[i-1] = Float.parseFloat(parse[i]);
                            }
                            loc = new Location(world, values[0], values[1], values[2], values[3], values[4]);
                            playerName = parse[0];
                        } else if (parse.length == 7) {
                            // v2
                            playerName = parse[0];
                            world = Bukkit.getWorld(parse[1]);
                            float[] values = new float[5];
                            for (int i=2; i<7; i++) {
                                values[i-2] = Float.parseFloat(parse[i]);
                            }
                            loc = new Location(world, values[0], values[1], values[2], values[3], values[4]);
                        } else
                            continue; // something went terrible
                        updatePlayerLocation(playerName, loc);
                    }
                    input.close();
                } catch (IOException e) {
                    // skip the file in case of read error
                    e.printStackTrace();
                }
            }
        }
    }

    public void save() throws IOException {
        for (Map.Entry<String, Map<String, Location>> worlds: mapInteral.entrySet()) {
            File file = new File("plugins"+File.separator+"RandomUtils"+File.separator+worlds.getKey()+".csv");
            file.createNewFile();
            BufferedWriter output = new BufferedWriter(new FileWriter(file, false));
            output.write("name,world,x,y,z,yaw,pitch");
            for (Map.Entry<String, Location> entry : worlds.getValue().entrySet()) {
                output.newLine();
                output.write(entry.getKey() + ",");
                Location loc = entry.getValue();
                output.write(loc.getWorld().getName());
                output.write("," + String.valueOf(loc.getX()));
                output.write("," + String.valueOf(loc.getY()));
                output.write("," + String.valueOf(loc.getZ()));
                output.write("," + String.valueOf(loc.getYaw()));
                output.write("," + String.valueOf(loc.getPitch()));
            }
            output.close();
        }
    }

    private Map<String, Location> getWorldLocations(World w) {
        return getWorldLocations(w.getName());
    }

    private Map<String, Location> getWorldLocations(String w) {
        String normalized = WorldHandler.getWorldNormalized(w);
        Map<String, Location> ret = mapInteral.get(normalized);
        if (ret == null) {
            // Insert new if none exist
            ret = new HashMap<String, Location>();
            mapInteral.put(normalized, ret);
        }
        return ret;
    }

    public void updateAll() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            updatePlayerLocation(p);
        }
    }

    public void updatePlayerLocation(Player p) {
        updatePlayerLocation(p, p.getLocation());
    }

    public void updatePlayerLocation(Player p, Location l) {
        updatePlayerLocation(p.getName(), l);
    }

    private void updatePlayerLocation(String p, Location l) {
        // INTERNAL
        getWorldLocations(l.getWorld()).put(p, l);
    }

    public Location getPlayerLocation(World w, Player p) {
        return getWorldLocations(w).get(p.getName());
    }
}
