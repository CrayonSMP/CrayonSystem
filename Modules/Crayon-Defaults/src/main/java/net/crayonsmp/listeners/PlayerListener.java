package net.crayonsmp.listeners;

import net.crayonsmp.services.GoalService;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

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


}
