package net.crayonsmp;

import net.crayonsmp.interfaces.CrayonModule;
import net.crayonsmp.recipes.FeywoodTableRecipe;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Feywood implements CrayonModule {
    @Override
    public String getName() {
        return "Feywood Module";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getAuthor() {
        return "Feywood Inc.";
    }

    @Override
    public <API extends Plugin & CrayonAPI> void onEnable(API plugin) {
    }

    @Override
    public void addCraftingTypes() {
        addCustomCraftingType("feywood_table",new FeywoodTableRecipe.Type());
    }
    @Override
    public String getID() {
        return "feywood";
    }
}