package net.crayonsmp;


import de.bluecolored.bluemap.api.BlueMapAPI;
import dev.turingcomplete.textcaseconverter.StandardTextCases;
import dev.turingcomplete.textcaseconverter.StandardWordsSplitters;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public interface CrayonModule {
    String getName();
    default String getAuthor(){return "";}
    default void OnBlueMapEnabled(BlueMapAPI blueMapAPI){}
    default void onLoad(CrayonAPI api) {} // oder einfach kein Parameter
    <API extends Plugin & CrayonAPI> void onEnable(API plugin);
    default void onDisable() {}
    default PluginCommand registerCommand(String name, Plugin plugin) {
        try {
            Constructor<PluginCommand> pluginCommandConstructor = PluginCommand.class.getDeclaredConstructor(String.class,Plugin.class);
            pluginCommandConstructor.setAccessible(true);
            PluginCommand command = pluginCommandConstructor.newInstance(name,plugin);
            plugin.getLogger().info("Type: " + Bukkit.getPluginManager().getClass().getCanonicalName());
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
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }
}
