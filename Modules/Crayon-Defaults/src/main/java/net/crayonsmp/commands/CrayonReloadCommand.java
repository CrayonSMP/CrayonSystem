package net.crayonsmp.commands;

import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.api.NexoItems;
import lombok.RequiredArgsConstructor;
import net.crayonsmp.services.HttpService;
import net.crayonsmp.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class CrayonReloadCommand implements CommandExecutor, Runnable {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender.hasPermission("crayon.reloadnexo")) {
            run();
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
        }
        return true;
    }

    @Override
    public void run() {
        new BukkitRunnable() {
            int counter = 30;

            @Override
            public void run() {
                if (counter > 0) {
                    if (counter == 30) {
                        Bukkit.broadcastMessage(ChatUtil.format("<#b2b2b2>Server reload sequence in: <#ff0040>" + counter + " seconds!"));
                    } else if (counter == 20) {
                        Bukkit.broadcastMessage(ChatUtil.format("<#b2b2b2>Server reload sequence in: <#ff0040>" + counter + " seconds!"));
                    } else if (counter <= 10) {
                        Bukkit.broadcastMessage(ChatUtil.format("<#b2b2b2>Server reload sequence in: <#ff0040>" + counter + " seconds!"));
                    }
                    counter--;
                } else {
                    Bukkit.broadcastMessage(ChatUtil.format("<#b2b2b2>Initiating reload sequence now!"));

                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "cc reload");
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "meg reload");
                        }
                    }.runTaskLater(Bukkit.getPluginManager().getPlugin("CrayonCore"), 20L * 2);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mm reload");
                        }
                    }.runTaskLater(Bukkit.getPluginManager().getPlugin("CrayonCore"), 20L * 3);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "n rl all");
                            Bukkit.broadcastMessage(ChatUtil.format("<#b2b2b2>Plugin reload sequence completed!"));
                        }
                    }.runTaskLater(Bukkit.getPluginManager().getPlugin("CrayonCore"), 20L * 5);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Collection<? extends Player> onlinePlayersCollection = Bukkit.getOnlinePlayers();

                            List<Player> onlinePlayersList = new ArrayList<>(onlinePlayersCollection);
                            try {
                                HttpService.sendStatsUpdate(NexoItems.items().size(), NexoBlocks.blockIDs().length, NexoFurniture.furnitureIDs().length, onlinePlayersList);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }.runTaskLater(Bukkit.getPluginManager().getPlugin("CrayonCore"), 20L * 7);

                    this.cancel();
                }
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("CrayonCore"), 0L, 20L);
    }
}