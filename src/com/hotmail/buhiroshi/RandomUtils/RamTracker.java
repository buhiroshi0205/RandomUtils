package com.hotmail.buhiroshi.RandomUtils;

import java.util.logging.Level;
import org.bukkit.plugin.java.JavaPlugin;

public class RamTracker implements Runnable {
    
    long[] max = new long[11];
    long[] min = new long[11];
    long[] sum = new long[11];
    int[] numrecords = new int[11];
    
    JavaPlugin main;
    
    public RamTracker(JavaPlugin main) {
        this.main = main;
    }
    
    @Override
    public void run() {
        long mem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        int players = main.getServer().getOnlinePlayers().size();
        if (players > 10) players = 10;
        if (mem > max[players]) max[players] = mem;
        if (min[players] == 0 || mem < min[players]) min[players] = mem;
        sum[players] += mem;
        numrecords[players]++;
    }
    
    public void reset() {
        max = new long[11];
        min = new long[11];
        sum = new long[11];
        numrecords = new int[11];
    }
    
    public void show() {
        for (int i=0;i<11;i++) {
            if (numrecords[i] != 0) {
                main.getLogger().log(Level.INFO, "----- Players={0}: min={1}, max={2}, mean={3} -----",
                        new Object[]{i, (int)(min[i]/1048576), (int)(max[i]/1048576), (int)(sum[i]/numrecords[i]/1048576)});
            }
        }
    }
    
}
