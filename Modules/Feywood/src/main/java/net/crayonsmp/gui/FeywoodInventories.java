package net.crayonsmp.gui;

import net.crayonsmp.PluginProvider;
import net.crayonsmp.crafting.CustomCrafting;
import net.crayonsmp.gui.widgets.Button;
import net.crayonsmp.gui.widgets.OutputSlot;
import net.crayonsmp.gui.widgets.Slot;
import net.crayonsmp.recipes.FeywoodTableRecipe;
import net.crayonsmp.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class FeywoodInventories {
    public static Inventory feywoodTable(){
        InventoryBuilder builder = new InventoryBuilder();
        builder.setRowAmount(6);
        builder.setTitle(Component.text("Feywood Table"));
        // Adding Buttons
        builder.addWidget(5,0,new Button(new ItemBuilder().setMaterial(Material.STICK).setCustomModelData(2990001).build(), button -> {
            button.getIcon().getItemMeta().setCustomModelData(2990002);
            PluginProvider.plugin.getLogger().info("Clicked Button in Inventory");
        }));

        // Adding Input Slots
        builder.addWidget(7,1,new Slot(FeywoodInventories::updateFeywoodTable));
        builder.addWidget(7,4,new Slot(FeywoodInventories::updateFeywoodTable));
        return builder.build();
    }
    private static void updateFeywoodTable(InventoryClickEvent e){
        if (e.getInventory().getHolder() instanceof BuiltInventoryHolder holder){
            InventoryBuilder builder = holder.getBuilder();
            builder.clearWidgets(OutputSlot.class);
            FeywoodTableRecipe recipe = CustomCrafting.findBestCustomCrafting(FeywoodTableRecipe.Type.INSTANCE,List.of(e.getInventory().getItem(16) != null ? e.getInventory().getItem(16) : e.getSlot() == 16 ? e.getCursor() : ItemStack.empty(),e.getInventory().getItem(43) != null ? e.getInventory().getItem(43) : e.getSlot() == 43 ? e.getCursor() : ItemStack.empty()));
            if (recipe == null){
                return;
            }
            PluginProvider.plugin.getLogger().info("Found Recipe matching inputs!");
            int i = 0;
            for (ItemStack output : recipe.getOutputs()){
                builder.addWidget(Math.floorMod(i,5),Math.floorDiv(i,5),new OutputSlot(output,FeywoodInventories::craftItem));
                i++;
            }
        }
    }

    private static void craftItem(InventoryClickEvent e) {
        e.getInventory().setItem(16, ItemStack.empty());
        e.getInventory().setItem(43,ItemStack.empty());
    }
}
