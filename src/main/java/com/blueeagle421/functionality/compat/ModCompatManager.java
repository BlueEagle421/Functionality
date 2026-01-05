package com.blueeagle421.functionality.compat;

import java.util.LinkedHashMap;
import java.util.Map;

import com.blueeagle421.functionality.FunctionalityMod;

//This code is inspired by Nether Depths Upgrade by Scouter456
//https://github.com/Scouter456/Nether_Depths_Upgrade/tree/nether_depths_upgrade_1.20

/**
 * The following code falls under GNU Lesser General Public License v3.0
 *
 * @Author TelepathicGrunt
 * Taken from https://github.com/TelepathicGrunt/Bumblezone/blob/1.19.2-Forge/src/main/java/com/telepathicgrunt/the_bumblezone/modcompat/ModChecker.java
 */

import net.minecraftforge.fml.ModList;

public class ModCompatManager {

    public static boolean farmersDelightPresent;

    private static final Map<String, Runnable> PRE_INIT_COMPAT = new LinkedHashMap<>();
    private static final Map<String, Runnable> COMMON_SETUP_COMPAT = new LinkedHashMap<>();

    static {
        // Pre-init compat
        PRE_INIT_COMPAT.put("farmersdelight", FarmersDelightCompat::setupCompat);
    }

    public static void setupModCompatPreInit() {
        runCompatMap(PRE_INIT_COMPAT, "pre init");
    }

    public static void setupModCompatCommonSetup() {
        runCompatMap(COMMON_SETUP_COMPAT, "common setup");
    }

    private static void runCompatMap(Map<String, Runnable> compatMap, String phase) {
        for (Map.Entry<String, Runnable> entry : compatMap.entrySet()) {
            String modid = entry.getKey();

            try {
                if (ModList.get().isLoaded(modid)) {
                    entry.getValue().run();
                }
            } catch (Throwable e) {
                printErrorToLogs("classloading " + modid + " " + phase);
                e.printStackTrace();
            }
        }
    }

    private static void printErrorToLogs(String currentModID) {
        FunctionalityMod.LOGGER
                .error("ERROR [Functionality]: Exception caught when trying to add mod compatibility with %s."
                        .formatted(currentModID));
    }
}
