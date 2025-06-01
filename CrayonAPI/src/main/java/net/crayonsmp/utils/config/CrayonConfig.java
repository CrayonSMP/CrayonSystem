package net.crayonsmp.utils.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

public class CrayonConfig extends YamlConfiguration {

    private final File file;
    private final String name;

    public CrayonConfig(File file, String name) {
        super();
        this.file = file;
        this.name = name;
        try {
            super.load(file);
        } catch (FileNotFoundException ignored) {
        } catch (IOException | InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
        }
    }

    public void setDefault(String a, Object b) {
        if (!isSet(a)) {
            set(a, b);
            save();
        }
    }

    public void save() {
        try {
            super.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        file.delete();
        ConfigUtil.cachemap.remove(name);
    }

    public File getFile() {
        return file;
    }
}
