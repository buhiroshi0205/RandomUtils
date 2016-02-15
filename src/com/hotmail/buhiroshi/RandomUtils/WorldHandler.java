package com.hotmail.buhiroshi.RandomUtils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;

public class WorldHandler {
    private static final Map<String, String> WORLD_NORMALIZE = new HashMap<String, String>();
    static {
        WORLD_NORMALIZE.put("survival_nether", "survival");
    }

    static String getWorldNormalized(String world) {
        if (WORLD_NORMALIZE.containsKey(world))
            return WORLD_NORMALIZE.get(world);
        return world;
    }

    static boolean isWorldSameGroup(String a, String b) {
        return getWorldNormalized(a).equals(getWorldNormalized(b));
    }

    static boolean isWorldSameGroup(World a, World b) {
        return isWorldSameGroup(a.getName(), b.getName());
    }

    private WorldHandler() {} // disable instance creation
}
