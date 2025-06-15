package net.crayonsmp.listeners;

import net.crayonsmp.services.GoalService;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player p = event.getPlayer();

        GoalService.ApplySeconderyEffects(p);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player p = event.getPlayer();
        GoalService.removeAllSecondaryEffects(p);
    }

    @EventHandler
    public static void DamageEvent(EntityDamageEvent e){
        if(e.getEntity() instanceof Player){
            Player p = (Player) e.getEntity();
            if(GoalService.hasPlayerGoalData(p)) {
                // Wind
                if (GoalService.hasPlayerMagicSeconderyType(p, "WIND")) {
                    if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                        double originalDamage = e.getDamage();

                        if (originalDamage > 0) {
                            double newDamage = originalDamage * 0.70;
                            e.setDamage(newDamage);
                        }
                    }
                }
                if (GoalService.hasPlayerMagicSeconderyType(p, "FIRE")) {
                    if (e.getCause() == EntityDamageEvent.DamageCause.FIRE || e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
                        double originalDamage = e.getDamage();

                        if (originalDamage > 0) {
                            double newDamage = originalDamage * 0.70;
                            e.setDamage(newDamage);
                        }
                    }
                }
                if (GoalService.hasPlayerMagicSeconderyType(p, "LAVA")) {
                    if (e.getCause() == EntityDamageEvent.DamageCause.LAVA) {
                        double originalDamage = e.getDamage();

                        if (originalDamage > 0) {
                            double newDamage = originalDamage * 0.70;
                            e.setDamage(newDamage);
                        }
                    }
                }
                if (GoalService.hasPlayerMagicSeconderyType(p, "ICE")) {
                    if (e.getCause() == EntityDamageEvent.DamageCause.FREEZE) {
                        double originalDamage = e.getDamage();

                        if (originalDamage > 0) {
                            double newDamage = originalDamage * 0.70;
                            e.setDamage(newDamage);
                        }
                    }
                }
                if (GoalService.hasPlayerMagicSeconderyType(p, "POISON")) {
                    if (e.getCause() == EntityDamageEvent.DamageCause.POISON) {
                        e.setDamage(0);
                    }
                }

            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        Player p = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) {
            return;
        }

        if(GoalService.hasPlayerGoalData(p)) {
            if (GoalService.hasPlayerMagicSeconderyType(p, "ICE")) {
                Location blockUnderTo = new Location(to.getWorld(), to.getX(), to.getY() - 1, to.getZ());
                Material typeUnderTo = blockUnderTo.getBlock().getType();

                Location blockUnderFrom = new Location(from.getWorld(), from.getX(), from.getY() - 1, from.getZ());
                Material typeUnderFrom = blockUnderFrom.getBlock().getType();

                boolean isOnIceNow = (typeUnderTo == Material.ICE || typeUnderTo == Material.PACKED_ICE || typeUnderTo == Material.BLUE_ICE);
                boolean wasOnIceBefore = (typeUnderFrom == Material.ICE || typeUnderFrom == Material.PACKED_ICE || typeUnderFrom == Material.BLUE_ICE);

                if (isOnIceNow && p.getWalkSpeed() != 1.2F) {
                    p.setWalkSpeed(1.2F);
                }
                else if (!isOnIceNow && wasOnIceBefore && p.getWalkSpeed() == 1.2F) {
                    p.setWalkSpeed(0.2F);
                }
            } else {
                if (p.getWalkSpeed() == 1.2F) {
                    p.setWalkSpeed(0.2F);
                }
            }
        } else {
            if (p.getWalkSpeed() == 1.2F) {
                p.setWalkSpeed(0.2F);
            }
        }
    }

        @EventHandler(priority= EventPriority.HIGH)
        public void onUse(PlayerInteractEvent e) {
            Player ply = e.getPlayer();

            ItemStack item = e.getItem();
            if(item == null){ return; }
            Material type = item.getType();

            if (!isHoe(type)) { return; }

            if (!(e.getAction() == Action.RIGHT_CLICK_BLOCK)) { return; }

            Block clickedBlock = e.getClickedBlock();
            if(clickedBlock == null){ return; }

            //Check it a crop we clicked?
            Material mat = clickedBlock.getType();
            if (!isCrop(mat)) { return; }

            Ageable age = (Ageable) clickedBlock.getBlockData();
            if(!isRipe(mat, age.getAge())) { return; }

            age.setAge(0);
            clickedBlock.setBlockData(age);

            ItemMeta itemMeta = item.getItemMeta();
            if(itemMeta == null){return;}
            dropSeeds(mat, clickedBlock, item);

            item.setItemMeta(itemMeta);
        }

        private boolean isHoe(Material material) {
            switch (material) {
                case WOODEN_HOE:
                case STONE_HOE:
                case IRON_HOE:
                case GOLDEN_HOE:
                case DIAMOND_HOE:
                case NETHERITE_HOE:
                    return true;
                default:
                    return false;
            }
        }

        private boolean isCrop(Material material) {
            switch (material) {
                case WHEAT:
                case CARROTS:
                case POTATOES:
                case BEETROOTS:
                case NETHER_WART:
                    return true;
                default:
                    return false;
            }
        }

        private boolean isRipe(Material material, int age) {
            switch (material) {
                case WHEAT:
                case CARROTS:
                case POTATOES:
                    return age == 7;
                case BEETROOTS:
                    return age == 3;
                case NETHER_WART:
                    return age == 3;
                default:
                    return false;
            }
        }

        private void dropSeeds(Material mat, Block blk, ItemStack hoe) {

            //Add fortune level to max drop.
            int fortune = 0;
                fortune = hoe.getEnchantmentLevel(Enchantment.LOOTING);

            ItemStack drops = new ItemStack(Material.WHEAT_SEEDS, rand(Math.max(1, 1),3 + fortune));
            ItemStack harvest = new ItemStack(Material.WHEAT, rand(1, 3));

            switch (mat) {
                case WHEAT:
                    if(drops.getAmount() > 0) {
                        blk.getWorld().dropItemNaturally(blk.getLocation(), drops);
                    }
                    if(harvest.getAmount() > 0) {
                        blk.getWorld().dropItemNaturally(blk.getLocation(), harvest);
                    }
                    break;
                case CARROTS:
                    drops.setType(Material.CARROT);
                    drops.setAmount(rand(1, 4 + fortune));
                    if(drops.getAmount() > 0) {
                        blk.getWorld().dropItemNaturally(blk.getLocation(), drops);
                    }
                    break;
                case POTATOES:
                    drops.setType(Material.POTATO);
                    drops.setAmount(rand(1, 4 + fortune));
                    if(drops.getAmount() > 0){
                        blk.getWorld().dropItemNaturally(blk.getLocation(), drops);
                    }
                    break;
                case BEETROOTS:
                    drops.setType(Material.BEETROOT_SEEDS);
                    drops.setAmount(rand(1, 3 + fortune));
                    harvest.setType(Material.BEETROOT);
                    harvest.setAmount(rand(1, 4));
                    if(drops.getAmount() > 0) {
                        blk.getWorld().dropItemNaturally(blk.getLocation(), drops);
                    }
                    if(harvest.getAmount() > 0){
                        blk.getWorld().dropItemNaturally(blk.getLocation(), harvest);
                    }
                    break;
                case NETHER_WART:
                    drops.setType(Material.NETHER_WART);
                    drops.setAmount(rand(1, 3 + fortune));
                    if(drops.getAmount() > 0) {
                        blk.getWorld().dropItemNaturally(blk.getLocation(), drops);
                    }
                    break;
            }
        }

        public static int rand(int a, int b) {
            Random random = new Random();
            return a + random.nextInt(b - a + 1);
        }


}
