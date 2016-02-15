package com.hotmail.buhiroshi.RandomUtils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;

public class TeleportLocations {
    private final Map<String, Location> mapInteral = new HashMap<String, Location>();

    public void addLocation(Location l) {
        mapInteral.put(WorldHandler.getWorldNormalized(l.getWorld().getName()), l);
    }

    public Location getLocationInWorld(World w) {
        return mapInteral.get(WorldHandler.getWorldNormalized(w.getName()));
    }
}
