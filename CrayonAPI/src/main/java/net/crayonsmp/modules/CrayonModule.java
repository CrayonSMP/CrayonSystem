package net.crayonsmp.modules;

import dev.turingcomplete.textcaseconverter.StandardTextCases;
import dev.turingcomplete.textcaseconverter.StandardWordsSplitters;
import net.crayonsmp.CrayonAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

public abstract class CrayonModule {

    public abstract String getName();

    public abstract String getVersion();

    public Logger getLogger() {
        return Bukkit.getLogger();
    }

    public abstract <API extends JavaPlugin & CrayonAPI> void onEnable(API plugin);

    public String getAuthor() {
        return "";
    }

    public void onLoad(CrayonAPI api) {
    } // oder einfach kein Parameter

    public void onDisable() {
    }

    public PluginCommand registerCommand(String name, JavaPlugin plugin) {
        try {
            Constructor<PluginCommand> pluginCommandConstructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            pluginCommandConstructor.setAccessible(true);
            PluginCommand command = pluginCommandConstructor.newInstance(name, plugin);
            if (Bukkit.getPluginManager() instanceof SimplePluginManager pluginManager) {
                try {
                    Field commandMapField = pluginManager.getClass().getDeclaredField("commandMap");
                    commandMapField.setAccessible(true);
                    CommandMap commandMap = (CommandMap) commandMapField.get(pluginManager);
                    commandMap.register(StandardTextCases.SNAKE_CASE.convert(getName(), StandardWordsSplitters.SPACES), command);
                    return command;
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            return null;
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
