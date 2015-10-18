package com.hotmail.buhiroshi.RandomUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Methods {
    
    static Map<String, Location> getLocationData(String world) throws Exception{
        Map<String, Location> temp = new HashMap<String, Location>();
        File file = new File("plugins" + File.separator + "RandomUtils" + File.separator + world + ".csv");
        if (file.exists()) {
            BufferedReader input = new BufferedReader(new FileReader(file));
            input.readLine();
            String line;
            while ((line = input.readLine()) != null) {
                String parse[] = line.split(",");
                float[] values = new float[5];
                for (int i=1; i<6; i++) {
                    values[i-1] = Float.parseFloat(parse[i]);
                }
                Location loc = new Location(Bukkit.getWorld(world), values[0], values[1], values[2], values[3], values[4]);
                temp.put(parse[0], loc);
            }
            input.close();
        }
        return temp;
    }
    
    static void saveLocationData(String name, Map<String, Location> map) throws Exception {
        File file = new File("plugins"+File.separator+"RandomUtils"+File.separator+name+".csv");
        file.createNewFile();
        BufferedWriter output = new BufferedWriter(new FileWriter(file, false));
        output.write("name,x,y,z,yaw,pitch");
        for (Map.Entry<String, Location> entry : map.entrySet()) {
            output.newLine();
            output.write(entry.getKey() + ",");
            Location loc = entry.getValue();
            output.write(String.valueOf(loc.getX()));
            output.write("," + String.valueOf(loc.getY()));
            output.write("," + String.valueOf(loc.getZ()));
            output.write("," + String.valueOf(loc.getYaw()));
            output.write("," + String.valueOf(loc.getPitch()));
        }
        output.close();
    }
    
}
