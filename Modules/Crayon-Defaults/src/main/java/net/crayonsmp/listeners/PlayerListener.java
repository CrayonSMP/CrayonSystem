package net.crayonsmp.listeners;

import net.crayonsmp.managers.GoalManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public static void DamageEvent(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player player)) {
            return;
        }
        if (!GoalManager.hasPlayerGoalData(player)) {
            return;
        }

        // Wind
        if (GoalManager.hasPlayerMagicSeconderyType(player, "WIND")) {
            if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                double originalDamage = e.getDamage();

                if (originalDamage > 0) {
                    double newDamage = originalDamage * 0.70;
                    e.setDamage(newDamage);
                }
            }
        }
        if (GoalManager.hasPlayerMagicSeconderyType(player, "FIRE")) {
            if (e.getCause() == EntityDamageEvent.DamageCause.FIRE || e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
                double originalDamage = e.getDamage();

                if (originalDamage > 0) {
                    double newDamage = originalDamage * 0.70;
                    e.setDamage(newDamage);
                }
            }
        }
        if (GoalManager.hasPlayerMagicSeconderyType(player, "LAVA")) {
            if (e.getCause() == EntityDamageEvent.DamageCause.LAVA) {
                double originalDamage = e.getDamage();

                if (originalDamage > 0) {
                    double newDamage = originalDamage * 0.70;
                    e.setDamage(newDamage);
                }
            }
        }
        if (GoalManager.hasPlayerMagicSeconderyType(player, "ICE")) {
            if (e.getCause() == EntityDamageEvent.DamageCause.FREEZE) {
                double originalDamage = e.getDamage();

                if (originalDamage > 0) {
                    double newDamage = originalDamage * 0.70;
                    e.setDamage(newDamage);
                }
            }
        }
        if (GoalManager.hasPlayerMagicSeconderyType(player, "POISON")) {
            if (e.getCause() == EntityDamageEvent.DamageCause.POISON) {
                e.setDamage(0);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        GoalManager.applySeconderyEffects(p);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        GoalManager.removeAllSecondaryEffects(p);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) {
            return;
        }

        if (!GoalManager.hasPlayerGoalData(p)) {
            if (p.getWalkSpeed() == 1.2F) {
                player.setWalkSpeed(0.2F);
            }
            return;
        }
        if (!GoalManager.hasPlayerMagicSeconderyType(p, "ICE")) {
            if (p.getWalkSpeed() == 1.2F) {
                player.setWalkSpeed(0.2F);
            }
            return;
        }

        Location blockUnderTo = new Location(to.getWorld(), to.getX(), to.getY() - 1, to.getZ());
        Material typeUnderTo = blockUnderTo.getBlock().getType();

        Location blockUnderFrom = new Location(from.getWorld(), from.getX(), from.getY() - 1, from.getZ());
        Material typeUnderFrom = blockUnderFrom.getBlock().getType();

        boolean isOnIceNow = (typeUnderTo == Material.ICE || typeUnderTo == Material.PACKED_ICE || typeUnderTo == Material.BLUE_ICE);
        boolean wasOnIceBefore = (typeUnderFrom == Material.ICE || typeUnderFrom == Material.PACKED_ICE || typeUnderFrom == Material.BLUE_ICE);

        if (isOnIceNow && player.getWalkSpeed() != 1.2F) {
            player.setWalkSpeed(1.2F);
        } else if (!isOnIceNow && wasOnIceBefore && player.getWalkSpeed() == 1.2F) {
            player.setWalkSpeed(0.2F);
        }
    }
}
