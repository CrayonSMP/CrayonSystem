package net.crayonsmp.gui.widgets;

import net.crayonsmp.gui.InventoryWidget;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Slot implements InventoryWidget {
    private final Consumer<InventoryClickEvent> onClick;
    public Slot(Consumer<InventoryClickEvent> onClick){
        this.onClick = onClick;
    }

    @Override
    public boolean onClicked(InventoryClickEvent e) {
        onClicked.accept(e);
        return true;
    }
    public boolean isOutput(){
        return false;
    }
}
