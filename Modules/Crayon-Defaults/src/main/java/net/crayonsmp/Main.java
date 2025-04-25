package net.crayonsmp;

import net.crayonsmp.commands.ModulesCommand;
import org.bukkit.plugin.Plugin;

public class Main implements CrayonModule {

    @Override
    public String getName() {
        return "Crayon-Defaults";
    }

    @Override
    public String getAuthor() {
        return "Terrocraft, Villagerzock";
    }

    @Override
    public void onLoad(CrayonAPI core) {
        core.getLogger().info("Crayon-Defaults loaded!");
    }

    @Override
    public <API extends Plugin & CrayonAPI> void onEnable(API plugin) {
        registerCommand("modules",plugin).setExecutor(new ModulesCommand(plugin));
    }
}
