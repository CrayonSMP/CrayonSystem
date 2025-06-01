package net.crayonsmp.gui;

import net.crayonsmp.managers.GoalManager;
import net.crayonsmp.utils.*;
import net.crayonsmp.utils.serialization.Magic;
import net.crayonsmp.utils.serialization.PlayerGoal;
import net.crayonsmp.utils.serialization.PlayerGoalPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import java.util.Objects;

public class GoalMenuListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!GoalManager.hasPlayerGoalData(p)) {
            GoalMenu.openGoalMenu(p);
        }
    }

    @EventHandler
    public void onPlayerCloseInv(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if (!GoalMenu.GOAL_INVENTORIES.containsKey(p) || GoalManager.hasPlayerGoalData(p)) {
            if (GoalMenu.GOAL_INVENTORIES.containsKey(p) && GoalManager.hasPlayerGoalData(p)) {
                GoalMenu.GOAL_INVENTORIES.remove(p);
            }
            return;
        }

        GoalInventory goalInventory = GoalMenu.GOAL_INVENTORIES.get(p);

        if (goalInventory.selectedPlaceholder == null || goalInventory.selectedPrimaryMagic == null || goalInventory.selectedSecondaryMagic == null) {
            Bukkit.getScheduler().runTaskLater(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("CrayonCore")), () -> {
                if (p.isOnline() && GoalMenu.GOAL_INVENTORIES.containsKey(p)) {
                    player.openInventory(goalInventory.getInv());
                }
            }, 1L);
            return;
        }

        GoalManager.addPlayerGoalData(p.getUniqueId().toString(), new PlayerGoal(goalInventory.getPlaceholder(goalInventory.selectedPlaceholder).getGoal(), goalInventory.selectedPrimaryMagic, goalInventory.selectedSecondaryMagic));
        GoalMenu.GOAL_INVENTORIES.remove(p);
        player.sendMessage("You can always look at your goal with /goal");
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!GoalMenu.GOAL_INVENTORIES.containsKey(player) || !GoalMenu.GOAL_INVENTORIES.get(player).getInv().equals(event.getInventory())) {
            return;
        }

        GoalInventory goalinv = GoalMenu.GOAL_INVENTORIES.get(player);
        Inventory inv = goalinv.getInv();

        if (event.getCurrentItem() == null) {
            return;
        }
        if (event.getCurrentItem().getItemMeta() == null) {
            return;
        }

        int clickedSlot = event.getSlot();

        if (clickedSlot >= 0 && clickedSlot <= 2) {
            if (goalinv.getSelectedPlaceholder() != GoalType.GOOD) {

                inv.setItem(0, new ItemBuilder().of(Material.IRON_NUGGET).setHideTooltip(true).setCustomModelData(2001).build());

                doGoalAction(goalinv, player, GoalType.GOOD);

                event.setCancelled(true);
            }
        } else if (clickedSlot >= 3 && clickedSlot <= 5) {
            if (goalinv.getSelectedPlaceholder() != GoalType.NEUTRAL) {
                inv.setItem(3, new ItemBuilder().of(Material.IRON_NUGGET).setHideTooltip(true).setCustomModelData(2002).build());

                doGoalAction(goalinv, player, GoalType.NEUTRAL);

                event.setCancelled(true);
            }
        } else if (clickedSlot >= 6 && clickedSlot <= 8) {
            if (goalinv.getSelectedPlaceholder() != GoalType.BAD) {

                inv.setItem(6, new ItemBuilder().of(Material.IRON_NUGGET).setHideTooltip(true).setCustomModelData(2003).build());
                doGoalAction(goalinv, player, GoalType.BAD);

                event.setCancelled(true);
            }
        } else if (clickedSlot == 22) {
            PlayerGoalPlaceholder placeholder = goalinv.getPlaceholder(goalinv.getSelectedPlaceholder());
            Magic magic = placeholder.getMagicPrimeryList().getFirst();
            GoalMenu.resetPrimeryMagics(goalinv.getInv(), placeholder);

            goalinv.setSelectedPrimaryMagic(magic);
            inv.setItem(22, GoalMenu.generateMagicItems(magic, 2005));

            player.playSound(player, Sound.UI_BUTTON_CLICK, 1, 1);

            event.setCancelled(true);
        } else if (clickedSlot == 24) {
            PlayerGoalPlaceholder placeholder = goalinv.getPlaceholder(goalinv.getSelectedPlaceholder());
            Magic magic = placeholder.getMagicPrimeryList().get(1);
            GoalMenu.resetPrimeryMagics(goalinv.getInv(), placeholder);

            goalinv.setSelectedPrimaryMagic(magic);
            inv.setItem(24, GoalMenu.generateMagicItems(magic, 2006));

            player.playSound(player, Sound.UI_BUTTON_CLICK, 1, 1);

            event.setCancelled(true);
        } else if (clickedSlot == 26) {
            PlayerGoalPlaceholder placeholder = goalinv.getPlaceholder(goalinv.getSelectedPlaceholder());
            Magic magic = placeholder.getMagicPrimeryList().get(2);
            GoalMenu.resetPrimeryMagics(goalinv.getInv(), placeholder);

            goalinv.setSelectedPrimaryMagic(magic);
            inv.setItem(26, GoalMenu.generateMagicItems(magic, 2007));

            player.playSound(player, Sound.UI_BUTTON_CLICK, 1, 1);

            event.setCancelled(true);
        } else if (clickedSlot == 40) {
            PlayerGoalPlaceholder placeholder = goalinv.getPlaceholder(goalinv.getSelectedPlaceholder());
            Magic magic = placeholder.getMagicSecondaryList().getFirst();
            GoalMenu.resetPrimeryMagics(goalinv.getInv(), placeholder);

            goalinv.setSelectedSecondaryMagic(magic);
            inv.setItem(40, GoalMenu.generateMagicItems(magic, 2008));

            player.playSound(player, Sound.UI_BUTTON_CLICK, 1, 1);

            event.setCancelled(true);
        } else if (clickedSlot == 42) {
            PlayerGoalPlaceholder placeholder = goalinv.getPlaceholder(goalinv.getSelectedPlaceholder());
            Magic magic = placeholder.getMagicSecondaryList().get(1);
            GoalMenu.resetPrimeryMagics(goalinv.getInv(), placeholder);

            goalinv.setSelectedSecondaryMagic(magic);
            inv.setItem(42, GoalMenu.generateMagicItems(magic, 2009));

            player.playSound(player, Sound.UI_BUTTON_CLICK, 1, 1);

            event.setCancelled(true);
        }

        InventoryView openView = player.getOpenInventory();
        if (goalinv.selectedPlaceholder != null && goalinv.selectedPrimaryMagic != null && goalinv.selectedSecondaryMagic != null) {
            openView.setTitle("<shift:-37><glyph:menu_goals_esc>");

            player.updateInventory();
        } else {
            openView.setTitle("<shift:-37><glyph:menu_goals>");

            player.updateInventory();
        }

        event.setCancelled(true);
    }

    private static void doGoalAction(GoalInventory goalinv, Player player, GoalType bad) {
        goalinv.setSelectedPlaceholder(bad);

        GoalMenu.setDefaultModelData(goalinv); // Resets all to default

        goalinv.setSelectedPrimaryMagic(null);
        goalinv.setSelectedSecondaryMagic(null);

        GoalMenu.setSelectetGoal(goalinv);

        player.playSound(player, Sound.UI_BUTTON_CLICK, 1, 1);
    }
}
