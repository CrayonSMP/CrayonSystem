package net.crayonsmp.gui;

import net.crayonsmp.enums.GoalType;
import net.crayonsmp.services.GoalService;
import net.crayonsmp.utils.ChatUtil;
import net.crayonsmp.utils.ItemBuilder;
import net.crayonsmp.utils.goal.GoalInventory;
import net.crayonsmp.utils.goal.Magic;
import net.crayonsmp.utils.goal.PlayerGoalPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GoalMenu {
    public static HashMap<Player, GoalInventory> goalInventories = new HashMap<>();

    public static void openGoalMenu(Player player) {
        if (player == null) return;
        if (GoalService.hasPlayerGoalData(player)) return;
        Inventory inv = Bukkit.createInventory(player, 54, "<shift:-37><glyph:menu_goals>");

        PlayerGoalPlaceholder GoodplayerGoalPlaceholder = GoalService.getPlayerGoalPlaceholder(GoalService.getRandomGoalByType(GoalType.GOOD));
        PlayerGoalPlaceholder NeutralplayerGoalPlaceholder = GoalService.getPlayerGoalPlaceholder(GoalService.getRandomGoalByType(GoalType.NEUTRAL));
        PlayerGoalPlaceholder BadplayerGoalPlaceholder = GoalService.getPlayerGoalPlaceholder(GoalService.getRandomGoalByType(GoalType.BAD));

        GoalInventory goalInventory = new GoalInventory(inv, GoodplayerGoalPlaceholder, NeutralplayerGoalPlaceholder, BadplayerGoalPlaceholder);
        //----------------------

        goalInventory.setSelectetPlaceholder(GoalType.GOOD);
        setDefaultModelData(goalInventory); // Resets all to default (unselected)
        inv.setItem(0, new ItemBuilder().setMaterial(Material.IRON_NUGGET).setTitle("§r" + goalInventory.getGoodPlaceholder().getGoal().getName()).setCustomModelData(2001).build());
        setSelectetGoal(goalInventory); // This will populate the main content based on the selected goal type

        goalInventories.put(player, goalInventory);
        player.openInventory(inv); // Inventar jetzt öffnen
    }

    private static ItemStack generateGoalItem(PlayerGoalPlaceholder playerGoalPlaceholder) {
        ItemBuilder itemBuilder = new ItemBuilder();
        itemBuilder.setMaterial(Material.IRON_NUGGET);
        itemBuilder.setTitle("§r" + playerGoalPlaceholder.getGoal().getName());

        List<String> lore = new ArrayList<>(playerGoalPlaceholder.getGoal().getDescription());

        lore.replaceAll(ChatUtil::format);

        itemBuilder.setLore(lore);
        itemBuilder.setCustomModelData(1000);
        return itemBuilder.build();
    }

    public static ItemStack generateMagicItems(Magic magic, int customModelData){
        ItemBuilder itemBuilder = new ItemBuilder();
        itemBuilder.setMaterial(Material.IRON_NUGGET);
        itemBuilder.setTitle("§r" + magic.getName());

        List<String> lore = new ArrayList<>();
        lore.add("Description:"); // Beispiel: Fett und die Standardfarbe
        magic.getDescription().forEach(lore::add); // Füge Beschreibungszeilen hinzu
        lore.add("Theme:"); // Beispiel: Fett und die Standardfarbe
        magic.getTheme().forEach(lore::add); // Füge Themenzeilen hinzu

        lore.replaceAll(ChatUtil::format);

        itemBuilder.setLore(lore);
        itemBuilder.setCustomModelData(customModelData);
        return itemBuilder.build();
    }

    public static void setSelectetGoal(GoalInventory goalInventory){
        if(goalInventory.getSelectetPlaceholder().equals(GoalType.GOOD)){
            setGoalItems(goalInventory.getInv(), goalInventory.getGoodPlaceholder());
        } else if(goalInventory.getSelectetPlaceholder().equals(GoalType.NEUTRAL)){
            setGoalItems(goalInventory.getInv(), goalInventory.getNeutralPlaceholder());
        } else if(goalInventory.getSelectetPlaceholder().equals(GoalType.BAD)){
            setGoalItems(goalInventory.getInv(), goalInventory.getBadPlaceholder());
        }
    }


    private static void setGoalItems(Inventory inv, PlayerGoalPlaceholder playerGoalPlaceholders){
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

    public static void resetPrimeryMagics(Inventory inv, PlayerGoalPlaceholder playerGoalPlaceholders){
        if (playerGoalPlaceholders.getMagicPrimeryList() != null && playerGoalPlaceholders.getMagicPrimeryList().size() >= 3) {
            inv.setItem(22, generateMagicItems(playerGoalPlaceholders.getMagicPrimeryList().get(0), 1005));
            inv.setItem(24, generateMagicItems(playerGoalPlaceholders.getMagicPrimeryList().get(1), 1006));
            inv.setItem(26, generateMagicItems(playerGoalPlaceholders.getMagicPrimeryList().get(2), 1007));
        } else {
            Bukkit.getLogger().warning("Primary magic list is insufficient for GoalTemplate: " + playerGoalPlaceholders.getGoal().getName());
        }
    }
    public static void resetSecondaryMagics(Inventory inv, PlayerGoalPlaceholder playerGoalPlaceholders){
        if (playerGoalPlaceholders.getMagicSecondaryList() != null && playerGoalPlaceholders.getMagicSecondaryList().size() >= 2) {
            inv.setItem(40, generateMagicItems(playerGoalPlaceholders.getMagicSecondaryList().get(0), 1008));
            inv.setItem(42, generateMagicItems(playerGoalPlaceholders.getMagicSecondaryList().get(1), 1009));
        } else {
            Bukkit.getLogger().warning("Secondary magic list is insufficient for GoalTemplate: " + playerGoalPlaceholders.getGoal().getName());
        }
    }

    public static void setDefaultModelData(GoalInventory ginv){
        Inventory inv = ginv.getInv();
        for (int i = 0; i < 3; i++) {
            inv.setItem(i, new ItemBuilder().setMaterial(Material.IRON_NUGGET).setTitle("§r" + ginv.getGoodPlaceholder().getGoal().getName()).setCustomModelData(1000).build());
        }

        for (int i = 3; i < 6; i++) {
            inv.setItem(i, new ItemBuilder().setMaterial(Material.IRON_NUGGET).setTitle("§r" + ginv.getNeutralPlaceholder().getGoal().getName()).setCustomModelData(1000).build());
        }

        for (int i = 6; i < 9; i++) {
            inv.setItem(i, new ItemBuilder().setMaterial(Material.IRON_NUGGET).setTitle("§r" + ginv.getBadPlaceholder().getGoal().getName()).setCustomModelData(1000).build());
        }

        inv.setItem(0, new ItemBuilder().setMaterial(Material.IRON_NUGGET).setTitle("§r" + ginv.getGoodPlaceholder().getGoal().getName()).setCustomModelData(1001).build());
        inv.setItem(3, new ItemBuilder().setMaterial(Material.IRON_NUGGET).setTitle("§r" + ginv.getNeutralPlaceholder().getGoal().getName()).setCustomModelData(1002).build());
        inv.setItem(6, new ItemBuilder().setMaterial(Material.IRON_NUGGET).setTitle("§r" + ginv.getBadPlaceholder().getGoal().getName()).setCustomModelData(1003).build());
    }
}