package net.crayonsmp.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.ItemStack;

public interface InventoryWidget {

    boolean onClicked(InventoryClickEvent e);

    default ItemStack getDefault() {
        return null;
    }
}
