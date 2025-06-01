package net.crayonsmp.utils.config;


import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigUtil {

    public static Map<String, CrayonConfig> cachemap = new HashMap<>();
    private final Plugin plugin;

    public ConfigUtil(Plugin plugin) {
        this.plugin = plugin;
    }

    public static CrayonConfig getConfig(String name, Plugin plugin) {
        if (cachemap.get(name) != null) {
            return cachemap.get(name);
        }

        CrayonConfig crayonConfig = new CrayonConfig(new File(plugin.getDataFolder(), name + ".yml"), name);
        cachemap.put(name, crayonConfig);
        return crayonConfig;
    }

    public static void clearAllCache() {
        cachemap.clear();
    }

    public static void saveALL() {
        cachemap.forEach((a, b) -> {
            try {
                b.save();
            } catch (Exception e) {
                System.out.println("Error saving config: " + a);
                e.printStackTrace();
            }
        });
    }

    public void reloadALL() {
        saveALL();
        clearAllCache();

        for (String configName : plugin.getDataFolder().list()) {
            if (configName.endsWith(".yml")) {
                getConfig(configName.replace(".yml", ""), plugin);
            }
        }
    }
}