package net.crayonsmp.commands;

import net.crayonsmp.CrayonCommand;
import net.crayonsmp.gui.GoalMenu;
import net.crayonsmp.managers.GoalManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RemoveGoalCommand implements CrayonCommand {

    @Override
    public String getCommand() {
        return "removegoal";
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (!sender.hasPermission("crayon.goalremove")) {
            sender.sendMessage("You do not have permission to use this command!");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("Usage: /goalremove <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage("That player is not online.");
            return true;
        }

        GoalManager.removePlayerGoalData(target.getUniqueId().toString());

        target.sendMessage("Your goal has been removed.");

        GoalMenu.openGoalMenu(target);

        sender.sendMessage("Removed goal for player " + target.getName() + ".");
        return false;
    }
}
