package net.crayonsmp.managers;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class DatapackManager {

    private final JavaPlugin plugin;
    private final Logger logger;
    private final File sourceDatapacksFolder;

    private final Map<World, List<File>> copiedDatapacks = new HashMap<>();

    public DatapackManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();

        this.sourceDatapacksFolder = new File(plugin.getDataFolder(), "/datapacks");
    }

    public void setup() {
        logger.info("Initializing DatapackManager...");

        if (!sourceDatapacksFolder.exists()) {
            sourceDatapacksFolder.mkdirs();
            logger.info("Plugin datapack source folder created: " + sourceDatapacksFolder.getAbsolutePath());
            logger.info("Place your .zip datapacks inside this folder.");
            return;
        }

        if (!sourceDatapacksFolder.isDirectory()) {
            logger.severe("Plugin datapack source path is not a directory: " + sourceDatapacksFolder.getAbsolutePath());
            return;
        }

        File[] datapackFiles = sourceDatapacksFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".zip"));

        if (datapackFiles == null || datapackFiles.length == 0) {
            logger.info("No .zip datapacks found in plugin source folder: " + sourceDatapacksFolder.getAbsolutePath());
            return;
        }

        logger.info("Found " + datapackFiles.length + " .zip datapacks in plugin source folder.");

        int copiedCount = 0;
        for (World world : Bukkit.getWorlds()) {
            File worldDatapacksFolder = new File(world.getWorldFolder(), "datapacks");

            if (!worldDatapacksFolder.exists()) {
                worldDatapacksFolder.mkdirs();
            }

            if (!worldDatapacksFolder.isDirectory()) {
                logger.severe("World datapacks path is not a directory for world '" + world.getName() + "': " + worldDatapacksFolder.getAbsolutePath());
                continue;
            }

            List<File> copiedInThisWorld = new ArrayList<>();
            for (File datapackFile : datapackFiles) {
                File destinationFile = new File(worldDatapacksFolder, datapackFile.getName());

                try {
                    Files.copy(datapackFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    copiedInThisWorld.add(destinationFile); // Zum Löschen vormerken
                    copiedCount++;
                    logger.info("Copied datapack '" + datapackFile.getName() + "' to world '" + world.getName() + "'.");
                } catch (IOException e) {
                    logger.severe("Failed to copy datapack '" + datapackFile.getName() + "' to world '" + world.getName() + "': " + e.getMessage());
                    e.printStackTrace();
                }
            }
            if (!copiedInThisWorld.isEmpty()) {
                copiedDatapacks.put(world, copiedInThisWorld);
            }
        }

        if (copiedCount > 0) {
            logger.info("Finished copying " + copiedCount + " datapack file(s). Triggering server reload...");
        } else {
            logger.info("No datapacks were copied to any world.");
        }
    }

    /**
     * Bereinigt die kopierten Datapacks beim Herunterfahren/Deaktivieren.
     */
    public void cleanup() {
        logger.info("Cleaning up copied datapacks...");

        int deletedCount = 0;
        List<Map.Entry<World, List<File>>> entries = new ArrayList<>(copiedDatapacks.entrySet());

        for (Map.Entry<World, List<File>> entry : entries) {
            World world = entry.getKey();
            List<File> filesToDelete = entry.getValue();

            if (Bukkit.getWorld(world.getUID()) == null) {
                logger.warning("World '" + world.getName() + "' is no longer loaded, cannot clean up datapacks in its folder.");
                continue; // Überspringe diese Welt
            }

            for (File file : filesToDelete) {
                if (file.exists()) {
                    if (file.delete()) {
                        deletedCount++;
                         logger.info("Deleted datapack '" + file.getName() + "' from world '" + world.getName() + "'.");
                    } else {
                        logger.warning("Failed to delete datapack '" + file.getName() + "' from world '" + world.getName() + "'. It might need manual removal.");
                    }
                } else {
                     logger.info("Datapack file '" + file.getName() + "' in world '" + world.getName() + "' not found during cleanup.");
                }
            }
        }

        copiedDatapacks.clear(); // Referenzen löschen
        logger.info("Cleanup finished. " + deletedCount + " datapack file(s) attempted to delete.");
    }
}