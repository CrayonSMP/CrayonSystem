package net.crayonsmp.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.crayonsmp.PluginProvider;
import net.crayonsmp.crafting.CustomCrafting;
import net.crayonsmp.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class FeywoodTableRecipe extends CustomCrafting {
    private final ItemStack input0;
    private final ItemStack input1;
    private final ItemStack[] outputs;
    public FeywoodTableRecipe(ItemStack input0, ItemStack input1, ItemStack[] outputs, String name){
        super(name);
        this.input1 = input1;
        this.input0 = input0;
        this.outputs = outputs;
    }
    @Override
    public boolean matches(List<ItemStack> itemStacks) {
        PluginProvider.plugin.getLogger().info(itemStacks.get(0).getType().name() + " and input0 is: " + input0.getType().name() + ", " + itemStacks.get(1).getType().name() + " and input1 is: " + input1.getType().name());
        return (itemStacks.get(0) == input0 && itemStacks.get(1) == input1) || (itemStacks.get(1) == input0 && itemStacks.get(0) == input1);
    }

    public ItemStack[] getOutputs() {
        return outputs;
    }

    @Override
    public ItemStack craft(List<ItemStack> itemStacks) {
        return null;
    }

    @Override
    public void sendRecipe(CommandSender commandSender) {
        commandSender.sendMessage(Component.text("Input 0: ").append(input0.displayName()));
        commandSender.sendMessage(Component.text("Input 1: ").append(input1.displayName()));
        commandSender.sendMessage("Outputs:");
        for (ItemStack stack : outputs){
            commandSender.sendMessage(stack.displayName());
        }
    }

    public static class Type extends CustomCraftingType<FeywoodTableRecipe> {
        public static final Type INSTANCE = new Type();
        @Override
        public FeywoodTableRecipe read(JsonObject object, String name) {
            JsonArray inputs = object.getAsJsonArray("inputs");
            ItemStack input0 = ItemBuilder.makeFromID(inputs.get(0).getAsJsonObject().get("item").getAsString());
            ItemStack input1 = ItemBuilder.makeFromID(inputs.get(1).getAsJsonObject().get("item").getAsString());
            JsonArray outputs = object.getAsJsonArray("outputs");
            ItemStack[] possibleOutputs = new ItemStack[outputs.size()];
            for (int i = 0; i<outputs.size(); i++){
                possibleOutputs[i] = ItemBuilder.makeFromID(outputs.get(i).getAsString());
            }
            return new FeywoodTableRecipe(input0,input1,possibleOutputs,name);
        }

        @Override
        public String getID() {
            return "feywood_table";
        }
    }
}
