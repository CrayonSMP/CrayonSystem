package net.crayonsmp.gui.widgets;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class OutputSlot extends Slot {
    private final ItemStack item;
    public OutputSlot(ItemStack item, Consumer<InventoryClickEvent> onClicked) {
        super(onClicked);
        this.item = item;
    }

    @Override
    public boolean onClicked(InventoryClickEvent e) {
        super.onClicked(e);
        return e.getCursor().isEmpty();
    }

    @Override
    public ItemStack getDefault() {
        return item;
    }

    @Override
    public boolean isOutput() {
        return true;
    }
}
