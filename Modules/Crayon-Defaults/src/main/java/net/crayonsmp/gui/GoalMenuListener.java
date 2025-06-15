package net.crayonsmp.gui;

import net.crayonsmp.enums.GoalType;
import net.crayonsmp.services.GoalService;
import net.crayonsmp.utils.ItemBuilder;
import net.crayonsmp.utils.goal.GoalInventory;
import net.crayonsmp.utils.goal.Magic;
import net.crayonsmp.utils.goal.PlayerGoal;
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
    public void onPlayerJoin(PlayerJoinEvent event){
        Player p = event.getPlayer();
        if(!GoalService.hasPlayerGoalData(p)){
            GoalMenu.openGoalMenu(p);
        }
    }

    @EventHandler
    public void onPlayerCloseInv(InventoryCloseEvent event){
        Player p = (Player) event.getPlayer();

        if(GoalMenu.goalInventories.containsKey(p) && !GoalService.hasPlayerGoalData(p)){
            GoalInventory goalInventory = GoalMenu.goalInventories.get(p);
            if (goalInventory.getSelectetPlaceholder() != null && goalInventory.selectetPrimaryMagic != null && goalInventory.selectetSecondaryMagic != null){
                if (goalInventory.getSelectetPlaceholder().equals(GoalType.GOOD)) {
                    GoalService.addPlayerGoalData(p.getUniqueId().toString(), new PlayerGoal(goalInventory.getGoodPlaceholder().getGoal(), goalInventory.selectetPrimaryMagic, goalInventory.selectetSecondaryMagic));
                    GoalMenu.goalInventories.remove(p);
                    p.sendMessage("You can always look at your goal with /goal");
                }
                else if (goalInventory.getSelectetPlaceholder().equals(GoalType.NEUTRAL)) {
                    GoalService.addPlayerGoalData(p.getUniqueId().toString(), new PlayerGoal(goalInventory.getNeutralPlaceholder().getGoal(), goalInventory.selectetPrimaryMagic, goalInventory.selectetSecondaryMagic));
                    GoalMenu.goalInventories.remove(p);
                    p.sendMessage("You can always look at your goal with /goal");
                }
                else if (goalInventory.getSelectetPlaceholder().equals(GoalType.BAD)) {
                    GoalService.addPlayerGoalData(p.getUniqueId().toString(), new PlayerGoal(goalInventory.getBadPlaceholder().getGoal(), goalInventory.selectetPrimaryMagic, goalInventory.selectetSecondaryMagic));
                    GoalMenu.goalInventories.remove(p);
                    p.sendMessage("You can always look at your goal with /goal");
                }
                } else {
                Bukkit.getScheduler().runTaskLater(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("CrayonCore")), () -> {
                    if (p.isOnline() && GoalMenu.goalInventories.containsKey(p)) {
                        p.openInventory(goalInventory.getInv());
                    }
                }, 1L);
            }
        } else if (GoalMenu.goalInventories.containsKey(p) && GoalService.hasPlayerGoalData(p)) {
            GoalMenu.goalInventories.remove(p);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        Player p = (Player) event.getWhoClicked();
        if(GoalMenu.goalInventories.containsKey(p)){
            if(GoalMenu.goalInventories.get(p).getInv().equals(event.getInventory())){
                GoalInventory Goalinv = GoalMenu.goalInventories.get(p);
                Inventory inv = Goalinv.getInv();

                if(event.getCurrentItem() == null) return;
                if(event.getCurrentItem().getItemMeta() == null) return;

                int clickedSlot = event.getSlot();

                if (clickedSlot >= 0 && clickedSlot <= 2) {
                    if (Goalinv.getSelectetPlaceholder() != GoalType.GOOD) {
                        Goalinv.setSelectetPlaceholder(GoalType.GOOD);
                        GoalMenu.setDefaultModelData(Goalinv); // Resets all to default (unselected)
                        inv.setItem(0, new ItemBuilder().setMaterial(Material.IRON_NUGGET).sethidetooltip(true).setCustomModelData(2001).build());
                        Goalinv.setSelectetPrimaryMagic(null);
                        Goalinv.setSelectetSecondaryMagic(null);
                        GoalMenu.setSelectetGoal(Goalinv); // This will populate the main content
                        p.playSound(p, Sound.UI_BUTTON_CLICK, 1, 1);
                        event.setCancelled(true);
                    }
                }
                else if (clickedSlot >= 3 && clickedSlot <= 5) {
                    if (Goalinv.getSelectetPlaceholder() != GoalType.NEUTRAL) {
                        Goalinv.setSelectetPlaceholder(GoalType.NEUTRAL);
                        GoalMenu.setDefaultModelData(Goalinv); // Resets all to default
                        inv.setItem(3, new ItemBuilder().setMaterial(Material.IRON_NUGGET).sethidetooltip(true).setCustomModelData(2002).build());
                        Goalinv.setSelectetPrimaryMagic(null);
                        Goalinv.setSelectetSecondaryMagic(null);
                        GoalMenu.setSelectetGoal(Goalinv);
                        p.playSound(p, Sound.UI_BUTTON_CLICK, 1, 1);
                        event.setCancelled(true);
                    }
                }
                else if (clickedSlot >= 6 && clickedSlot <= 8) {
                    if (Goalinv.getSelectetPlaceholder() != GoalType.BAD) {
                        Goalinv.setSelectetPlaceholder(GoalType.BAD);
                        GoalMenu.setDefaultModelData(Goalinv); // Resets all to default
                        inv.setItem(6, new ItemBuilder().setMaterial(Material.IRON_NUGGET).sethidetooltip(true).setCustomModelData(2003).build());
                        Goalinv.setSelectetPrimaryMagic(null);
                        Goalinv.setSelectetSecondaryMagic(null);
                        GoalMenu.setSelectetGoal(Goalinv);
                        p.playSound(p, Sound.UI_BUTTON_CLICK, 1, 1);
                        event.setCancelled(true);
                    }
                }
                else if (clickedSlot == 22) {
                    Magic magic = null;
                        switch (Goalinv.getSelectetPlaceholder()){
                            case GOOD:
                                magic = Goalinv.getGoodPlaceholder().getMagicPrimeryList().get(0);
                                GoalMenu.resetPrimeryMagics(Goalinv.getInv(), Goalinv.getGoodPlaceholder());
                                break;
                            case NEUTRAL:
                                magic = Goalinv.getNeutralPlaceholder().getMagicPrimeryList().get(0);
                                GoalMenu.resetPrimeryMagics(Goalinv.getInv(), Goalinv.getNeutralPlaceholder());
                                break;
                            case BAD:
                                magic = Goalinv.getBadPlaceholder().getMagicPrimeryList().get(0);
                                GoalMenu.resetPrimeryMagics(Goalinv.getInv(), Goalinv.getBadPlaceholder());
                                break;
                        }
                        Goalinv.setSelectetPrimaryMagic(magic);
                        inv.setItem(22, GoalMenu.generateMagicItems(magic, 2005));
                        event.setCancelled(true);
                    p.playSound(p, Sound.UI_BUTTON_CLICK, 1, 1);
                }
                else if (clickedSlot == 24) {
                    Magic magic = null;
                    switch (Goalinv.getSelectetPlaceholder()){
                        case GOOD:
                            magic = Goalinv.getGoodPlaceholder().getMagicPrimeryList().get(1);
                            GoalMenu.resetPrimeryMagics(Goalinv.getInv(), Goalinv.getGoodPlaceholder());
                            break;
                        case NEUTRAL:
                            magic = Goalinv.getNeutralPlaceholder().getMagicPrimeryList().get(1);
                            GoalMenu.resetPrimeryMagics(Goalinv.getInv(), Goalinv.getNeutralPlaceholder());
                            break;
                        case BAD:
                            magic = Goalinv.getBadPlaceholder().getMagicPrimeryList().get(1);
                            GoalMenu.resetPrimeryMagics(Goalinv.getInv(), Goalinv.getBadPlaceholder());
                            break;
                    }
                    Goalinv.setSelectetPrimaryMagic(magic);
                    inv.setItem(24, GoalMenu.generateMagicItems(magic, 2006));
                    event.setCancelled(true);
                    p.playSound(p, Sound.UI_BUTTON_CLICK, 1, 1);
                }
                else if (clickedSlot == 26) {
                    Magic magic = null;
                    switch (Goalinv.getSelectetPlaceholder()){
                        case GOOD:
                            magic = Goalinv.getGoodPlaceholder().getMagicPrimeryList().get(2);
                            GoalMenu.resetPrimeryMagics(Goalinv.getInv(), Goalinv.getGoodPlaceholder());
                            break;
                        case NEUTRAL:
                            magic = Goalinv.getNeutralPlaceholder().getMagicPrimeryList().get(2);
                            GoalMenu.resetPrimeryMagics(Goalinv.getInv(), Goalinv.getNeutralPlaceholder());
                            break;
                        case BAD:
                            magic = Goalinv.getBadPlaceholder().getMagicPrimeryList().get(2);
                            GoalMenu.resetPrimeryMagics(Goalinv.getInv(), Goalinv.getBadPlaceholder());
                            break;
                    }
                    Goalinv.setSelectetPrimaryMagic(magic);
                    inv.setItem(26, GoalMenu.generateMagicItems(magic, 2007));
                    event.setCancelled(true);
                    p.playSound(p, Sound.UI_BUTTON_CLICK, 1, 1);
                }
                else if (clickedSlot == 40) {
                    Magic magic = null;
                    switch (Goalinv.getSelectetPlaceholder()){
                        case GOOD:
                            magic = Goalinv.getGoodPlaceholder().getMagicSecondaryList().get(0);
                            GoalMenu.resetSecondaryMagics(Goalinv.getInv(), Goalinv.getGoodPlaceholder());
                            break;
                        case NEUTRAL:
                            magic = Goalinv.getNeutralPlaceholder().getMagicSecondaryList().get(0);
                            GoalMenu.resetSecondaryMagics(Goalinv.getInv(), Goalinv.getNeutralPlaceholder());
                            break;
                        case BAD:
                            magic = Goalinv.getBadPlaceholder().getMagicSecondaryList().get(0);
                            GoalMenu.resetSecondaryMagics(Goalinv.getInv(), Goalinv.getBadPlaceholder());
                            break;
                    }
                    Goalinv.setSelectetSecondaryMagic(magic);
                    inv.setItem(40, GoalMenu.generateMagicItems(magic, 2008));
                    event.setCancelled(true);
                    p.playSound(p, Sound.UI_BUTTON_CLICK, 1, 1);
                }
                else if (clickedSlot == 42) {
                    Magic magic = null;
                    switch (Goalinv.getSelectetPlaceholder()){
                        case GOOD:
                            magic = Goalinv.getGoodPlaceholder().getMagicSecondaryList().get(1);
                            GoalMenu.resetSecondaryMagics(Goalinv.getInv(), Goalinv.getGoodPlaceholder());
                            break;
                        case NEUTRAL:
                            magic = Goalinv.getNeutralPlaceholder().getMagicSecondaryList().get(1);
                            GoalMenu.resetSecondaryMagics(Goalinv.getInv(), Goalinv.getNeutralPlaceholder());
                            break;
                        case BAD:
                            magic = Goalinv.getBadPlaceholder().getMagicSecondaryList().get(1);
                            GoalMenu.resetSecondaryMagics(Goalinv.getInv(), Goalinv.getBadPlaceholder());
                            break;
                    }
                    Goalinv.setSelectetSecondaryMagic(magic);
                    inv.setItem(42, GoalMenu.generateMagicItems(magic, 2009));
                    event.setCancelled(true);
                    p.playSound(p, Sound.UI_BUTTON_CLICK, 1, 1);
                }

                InventoryView openView = p.getOpenInventory();
                if (Goalinv.getSelectetPlaceholder() != null && Goalinv.selectetPrimaryMagic != null && Goalinv.selectetSecondaryMagic != null) {

                    openView.setTitle("<shift:-37><glyph:menu_goals_esc>");
                    p.updateInventory();
                } else {
                    openView.setTitle("<shift:-37><glyph:menu_goals>");
                    p.updateInventory();
                }

                event.setCancelled(true);
                return;
            }

        }


    }
}
