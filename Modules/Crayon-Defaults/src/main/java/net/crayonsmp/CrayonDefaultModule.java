package net.crayonsmp;

import it.sauronsoftware.cron4j.Scheduler;
import lombok.Getter;
import net.crayonsmp.commands.*;
import net.crayonsmp.gui.GoalMenuListener;
import net.crayonsmp.listeners.DebugListener;
import net.crayonsmp.listeners.PlayerListener;
import net.crayonsmp.managers.ConfigManager;
import net.crayonsmp.managers.DatapackManager;
import net.crayonsmp.modules.CrayonModule;
import net.crayonsmp.utils.Goal;
import net.crayonsmp.utils.serialization.Magic;
import net.crayonsmp.utils.serialization.PlayerGoal;
import net.crayonsmp.utils.config.ConfigUtil;
import net.crayonsmp.utils.config.CrayonConfig;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.stream.Stream;

public class CrayonDefaultModule extends CrayonModule {

    public static CrayonConfig goalConfig;
    public static CrayonConfig playerGoalData;

    @Getter
    public static JavaPlugin plugin;

    public DatapackManager datapackManager;

    @Override
    public void onLoad(CrayonAPI core) {
        core.getLogger().info("Crayon-Defaults loaded!");
    }

    @Override
    public <API extends JavaPlugin & CrayonAPI> void onEnable(API plugin) {
        CrayonDefaultModule.plugin = plugin;

        ConfigurationSerialization.registerClass(Goal.class);
        ConfigurationSerialization.registerClass(Magic.class);
        ConfigurationSerialization.registerClass(PlayerGoal.class);

        goalConfig = ConfigUtil.getConfig("goalconfig", plugin);
        playerGoalData = ConfigUtil.getConfig("playergoaldata", plugin);

        CrayonReloadCommand reloadCommand = new CrayonReloadCommand(plugin);

        Stream.of(
                new ModulesCommand(plugin),
                new DebugCommand(plugin),
                new GoalCommand(),
                new SetGoalCommand(),
                new RemoveGoalCommand(),
                reloadCommand
        ).forEach(executor -> registerCommand(executor.getCommand(), plugin).setExecutor(executor));

        PluginManager pluginManager = plugin.getServer().getPluginManager();
        Stream.of(
                new DebugListener(),
                new GoalMenuListener(),
                new PlayerListener()
        ).forEach(listener -> pluginManager.registerEvents(listener, plugin));

        datapackManager = new DatapackManager(plugin);
        datapackManager.setup();

        ConfigManager.registergoalconfig();

        this.scheduleDailyTasks(reloadCommand);
    }

    private void scheduleDailyTasks(CrayonReloadCommand reloadCommand) {
        Scheduler scheduler = new Scheduler();

        scheduler.schedule("0 7,13,17,22 * * *", reloadCommand);

        scheduler.start();
    }

    @Override
    public void onDisable() {
        datapackManager.cleanup();
    }

    @Override
    public String getName() {
        return "Crayon-Defaults";
    }

    @Override
    public String getVersion() {
        return "0.0.1";
    }

    @Override
    public String getAuthor() {
        return "Terrocraft, Villagerzock";
    }
}
