package net.crayonsmp.crafting;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.crayonsmp.PluginProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CustomCrafting {

    public static Map<String, CustomCraftingType<?>> TYPES = new HashMap<>();
    private final String name;

    protected CustomCrafting(String name) {
        this.name = name;
    }

    private static void reloadRecipesInPath(File path) {
        String originalPath = new File(PluginProvider.plugin.getDataFolder(), "recipes").getAbsolutePath();
        for (File file : path.listFiles()) {
            if (file.getName().endsWith(".json")) {
                try (FileReader reader = new FileReader(file)) {
                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
                    PluginProvider.plugin.getLogger().info("Type: " + jsonObject.get("type").getAsString());
                    CustomCraftingType<?> type = TYPES.get(jsonObject.get("type").getAsString());

                    try {
                        type.addCrafting(jsonObject, file.getAbsolutePath().substring(originalPath.length()).substring(1, file.getAbsolutePath().substring(originalPath.length()).lastIndexOf(".")));
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (file.isDirectory()) {
                reloadRecipesInPath(file);
            }
        }
    }

    public static void reloadRecipes() {
        PluginProvider.plugin.getLogger().info("Reloading Custom Crafting Recipes");
        File path = new File(PluginProvider.plugin.getDataFolder(), "recipes");
        if (path.exists()) {
            reloadRecipesInPath(path);
        }
    }

    public static <T extends CustomCrafting> T findBestCustomCrafting(CustomCraftingType<T> type, List<ItemStack> itemStacks) {
        for (T crafting : type.CRAFTINGS) {
            if (crafting.matches(itemStacks)) {
                return crafting;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public abstract boolean matches(List<ItemStack> itemStacks);

    public abstract ItemStack craft(List<ItemStack> itemStacks);

    public abstract void sendRecipe(CommandSender commandSender);

    public static abstract class CustomCraftingType<T extends CustomCrafting> {
        private final List<T> CRAFTINGS = new ArrayList<>();

        public List<T> getCraftings() {
            return CRAFTINGS;
        }

        public T ofName(String name) {
            for (T t : CRAFTINGS) {
                if (t.getName().equals(name)) {
                    return t;
                }
            }
            return null;
        }

        public final void addCrafting(JsonObject object, String name) {
            CRAFTINGS.add(read(object, name));
        }

        public abstract T read(JsonObject object, String name);

        public abstract String getID();
    }
}
