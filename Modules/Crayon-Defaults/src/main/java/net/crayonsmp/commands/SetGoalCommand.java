package net.crayonsmp.commands;

import net.crayonsmp.CrayonCommand;
import net.crayonsmp.managers.GoalManager;
import net.crayonsmp.utils.Goal;
import net.crayonsmp.utils.serialization.Magic;
import net.crayonsmp.utils.serialization.PlayerGoal;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetGoalCommand implements CrayonCommand {

    @Override
    public String getCommand() {
        return "setgoal";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length != 4) {
            sender.sendMessage("Usage: /goalset <player> <goalId> <primaryMagicId> <secondaryMagicId>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("Player '" + args[0] + "' not found.");
            return true;
        }
        if (!sender.hasPermission("crayon.goalset")) {
            sender.sendMessage("You do not have permission to use this command!");
            return true;
        }

        Goal goal = GoalManager.registertgoals.get(args[1]);
        Magic primaryMagic = GoalManager.getMagicById(args[2]);
        Magic secondaryMagic = GoalManager.getMagicById(args[3]); // Corrected to args[3]

        if (goal == null) {
            sender.sendMessage("Error: Goal '" + args[1] + "' not found.");
            sender.sendMessage("all registert goals:");
            GoalManager.registertgoals.forEach((key, value) -> sender.sendMessage(key));
            return true;
        }
        if (primaryMagic == null) {
            sender.sendMessage("Error: Primary Magic '" + args[2] + "' not found.");
            return true;
        }
        if (secondaryMagic == null) {
            sender.sendMessage("Error: Secondary Magic '" + args[3] + "' not found.");
            return true;
        }

        GoalManager.addPlayerGoalData(target.getUniqueId().toString(), new PlayerGoal(goal, primaryMagic, secondaryMagic));
        target.sendMessage("Your goal has been set to " + args[1]);
        sender.sendMessage("Set goal for player " + target.getName() + " to '" + goal.getName() + "' with primary magic '" + primaryMagic.getName() + "' and secondary magic '" + secondaryMagic.getName() + "'.");
        return true;
    }
}
