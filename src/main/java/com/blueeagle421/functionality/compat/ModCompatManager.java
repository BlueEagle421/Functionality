package com.blueeagle421.functionality.compat;

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
    public static boolean curiosPresent;

    public static void setupModCompatPreInit() {
        String modid = "";

        try {

            modid = "farmersdelight";
            loadModCompat(modid, () -> FarmersDelightCompat.setupCompat());

            modid = "curios";
            loadModCompat(modid, () -> CurioCompat.setupCompat());

        } catch (Throwable e) {
            printErrorToLogs("classloading " + modid + " pre init, mod compat done afterwards broke");
            e.printStackTrace();
        }
    }

    private static void loadModCompat(String modid, Runnable runnable) {
        try {
            if (ModList.get().isLoaded(modid)) {
                runnable.run();
            }
        }

        catch (Throwable e) {
            printErrorToLogs(modid);
            e.printStackTrace();

        }
    }

    private static void printErrorToLogs(String currentModID) {
        FunctionalityMod.LOGGER
                .error("ERROR [Functionality]: Exception caught when trying to add mod compatibility with %s."
                        .formatted(currentModID));
    }
}
