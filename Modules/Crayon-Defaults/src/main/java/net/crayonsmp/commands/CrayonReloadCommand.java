package net.crayonsmp.commands;

import lombok.RequiredArgsConstructor;
import net.crayonsmp.CrayonCommand;
import net.crayonsmp.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

@RequiredArgsConstructor
public class CrayonReloadCommand implements CrayonCommand, Runnable {

    private final JavaPlugin plugin;
    int counter = 30;

    @Override
    public String getCommand() {
        return "crayonreload";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("crayon.reloadnexo")) {
            sender.sendMessage(ChatUtil.format("<red>You do not have permission to use this command."));
            return true;
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, (task) -> {
            this.run();

            if (this.counter <= 0) {
                task.cancel();
            }
        }, 0, 20L);

        return true;
    }

    public void run() {
        if (counter-- <= 0) {
            reloadPlugins();
            counter = 30;
        }

        sendCooldownMessage();
    }

    private void sendCooldownMessage() {
        String formatted = "<#b2b2b2>Server reload sequence in: <#ff0040> %s seconds!".formatted(counter);

        if (this.counter % 10 == 0) {
            Bukkit.broadcastMessage(ChatUtil.format(formatted));
        }
    }

    private void reloadPlugins() {
        try {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "cc reload");
            Thread.sleep(1000);
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "meg reload");
            Thread.sleep(1000);
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mm reload");
            Thread.sleep(1000);
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "n rl all");
            Bukkit.broadcastMessage(ChatUtil.format("<#b2b2b2>Plugin reload sequence completed!"));
        } catch (Exception e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to reload crayon", e);
        }
    }
}