package net.crayonsmp.gui;

import net.crayonsmp.managers.GoalManager;
import net.crayonsmp.utils.*;
import net.crayonsmp.utils.serialization.Magic;
import net.crayonsmp.utils.serialization.PlayerGoalPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GoalMenu {

    public static final HashMap<Player, GoalInventory> GOAL_INVENTORIES = new HashMap<>();

    public static void openGoalMenu(Player player) {
        if (player == null) {
            return;
        }
        if (GoalManager.hasPlayerGoalData(player)) {
            return;
        }
        Inventory inv = Bukkit.createInventory(player, 54, "<shift:-37><glyph:menu_goals>");

        PlayerGoalPlaceholder GoodplayerGoalPlaceholder = GoalManager.getPlayerGoalPlaceholder(GoalManager.getRandomGoalByType(GoalType.GOOD));
        PlayerGoalPlaceholder NeutralplayerGoalPlaceholder = GoalManager.getPlayerGoalPlaceholder(GoalManager.getRandomGoalByType(GoalType.NEUTRAL));
        PlayerGoalPlaceholder BadplayerGoalPlaceholder = GoalManager.getPlayerGoalPlaceholder(GoalManager.getRandomGoalByType(GoalType.BAD));

        GoalInventory goalInventory = new GoalInventory(BadplayerGoalPlaceholder, GoodplayerGoalPlaceholder, NeutralplayerGoalPlaceholder, inv);

        GOAL_INVENTORIES.put(player, goalInventory); // Hinzugefügt: Spieler und GoalInventory in die Map

        goalInventory.setSelectedPlaceholder(GoalType.GOOD);
        setDefaultModelData(goalInventory);

        inv.setItem(0, ItemBuilder.of(Material.IRON_NUGGET).setTitle(goalInventory.getPlaceholder(GoalType.GOOD).getGoal().getName()).setCustomModelData(2001).build());
        setSelectetGoal(goalInventory);

        player.openInventory(inv);
    }

    private static ItemStack generateGoalItem(PlayerGoalPlaceholder playerGoalPlaceholder) {
        ItemBuilder itemBuilder = ItemBuilder.of(Material.IRON_NUGGET);
        itemBuilder.setTitle("§r" + playerGoalPlaceholder.getGoal().getName());

        List<String> lore = new ArrayList<>(playerGoalPlaceholder.getGoal().getDescription());

        lore.replaceAll(ChatUtil::format);

        itemBuilder.setLore(lore);
        itemBuilder.setCustomModelData(1000);
        return itemBuilder.build();
    }

    public static ItemStack generateMagicItems(Magic magic, int customModelData) {
        ItemBuilder itemBuilder = ItemBuilder.of(Material.IRON_NUGGET);
        itemBuilder.setTitle("§r" + magic.getName());

        List<String> lore = new ArrayList<>();
        lore.add("Description:");
        magic.getDescription().forEach(lore::add);
        lore.add("Theme:");
        magic.getTheme().forEach(lore::add);

        lore.replaceAll(ChatUtil::format);

        itemBuilder.setLore(lore);
        itemBuilder.setCustomModelData(customModelData);
        return itemBuilder.build();
    }

    public static void setSelectetGoal(GoalInventory goalInventory) {
        setGoalItems(goalInventory.getInv(), goalInventory.getPlaceholder(goalInventory.selectedPlaceholder));
    }

    private static void setGoalItems(Inventory inv, PlayerGoalPlaceholder playerGoalPlaceholders) {
        // Generiere und setze die Haupt-Goal-Items
        for (int i = 18; i < 21; i++) {
            inv.setItem(i, generateGoalItem(playerGoalPlaceholders));
        }
        for (int i = 27; i < 30; i++) {
            inv.setItem(i, generateGoalItem(playerGoalPlaceholders));
        }
        for (int i = 36; i < 39; i++) {
            inv.setItem(i, generateGoalItem(playerGoalPlaceholders));
        }
        for (int i = 45; i < 48; i++) {
            inv.setItem(i, generateGoalItem(playerGoalPlaceholders));
        }

        if (playerGoalPlaceholders.getMagicPrimeryList() != null && playerGoalPlaceholders.getMagicPrimeryList().size() >= 3) {
            inv.setItem(22, generateMagicItems(playerGoalPlaceholders.getMagicPrimeryList().get(0), 1005));
            inv.setItem(24, generateMagicItems(playerGoalPlaceholders.getMagicPrimeryList().get(1), 1006));
            inv.setItem(26, generateMagicItems(playerGoalPlaceholders.getMagicPrimeryList().get(2), 1007));
        } else {
            Bukkit.getLogger().warning("Primary magic list is insufficient for GoalTemplate: " + playerGoalPlaceholders.getGoal().getName());
        }

        if (playerGoalPlaceholders.getMagicSecondaryList() != null && playerGoalPlaceholders.getMagicSecondaryList().size() >= 2) {
            inv.setItem(40, generateMagicItems(playerGoalPlaceholders.getMagicSecondaryList().get(0), 1008));
            inv.setItem(42, generateMagicItems(playerGoalPlaceholders.getMagicSecondaryList().get(1), 1009));
        } else {
            Bukkit.getLogger().warning("Secondary magic list is insufficient for GoalTemplate: " + playerGoalPlaceholders.getGoal().getName());
        }
    }

    public static void resetPrimeryMagics(Inventory inv, PlayerGoalPlaceholder playerGoalPlaceholders) {
        if (playerGoalPlaceholders.getMagicPrimeryList() != null && playerGoalPlaceholders.getMagicPrimeryList().size() >= 3) {
            inv.setItem(22, generateMagicItems(playerGoalPlaceholders.getMagicPrimeryList().get(0), 1005));
            inv.setItem(24, generateMagicItems(playerGoalPlaceholders.getMagicPrimeryList().get(1), 1006));
            inv.setItem(26, generateMagicItems(playerGoalPlaceholders.getMagicPrimeryList().get(2), 1007));
        } else {
            Bukkit.getLogger().warning("Primary magic list is insufficient for GoalTemplate: " + playerGoalPlaceholders.getGoal().getName());
        }
    }

    public static void resetSecondaryMagics(Inventory inv, PlayerGoalPlaceholder playerGoalPlaceholders) {
        if (playerGoalPlaceholders.getMagicSecondaryList() != null && playerGoalPlaceholders.getMagicSecondaryList().size() >= 2) {
            inv.setItem(40, generateMagicItems(playerGoalPlaceholders.getMagicSecondaryList().get(0), 1008));
            inv.setItem(42, generateMagicItems(playerGoalPlaceholders.getMagicSecondaryList().get(1), 1009));
        } else {
            Bukkit.getLogger().warning("Secondary magic list is insufficient for GoalTemplate: " + playerGoalPlaceholders.getGoal().getName());
        }
    }

    public static void setDefaultModelData(GoalInventory goalInventory) {
        Inventory inv = goalInventory.getInv();
        ItemBuilder ironNugget = ItemBuilder.of(Material.IRON_NUGGET);
        String goodName = getName(goalInventory, GoalType.GOOD);
        String neutralName = getName(goalInventory, GoalType.NEUTRAL);
        String badName = getName(goalInventory, GoalType.BAD);

        for (int i = 0; i < 3; i++) {
            inv.setItem(i, ironNugget.clone().setTitle(goodName).setCustomModelData(1000).build());
        }

        for (int i = 3; i < 6; i++) {
            inv.setItem(i, ironNugget.setTitle(neutralName).setCustomModelData(1000).build());
        }

        for (int i = 6; i < 9; i++) {
            inv.setItem(i, ironNugget.setTitle(badName).setCustomModelData(1000).build());
        }

        inv.setItem(0, ironNugget.setTitle(goodName).setCustomModelData(1001).build());
        inv.setItem(3, ironNugget.setTitle(neutralName).setCustomModelData(1002).build());
        inv.setItem(6, ironNugget.setTitle(badName).setCustomModelData(1003).build());
    }

    public static String getName(GoalInventory goalInventory, GoalType type) {
        return goalInventory.getPlaceholder(type).getGoal().getName();
    }
}