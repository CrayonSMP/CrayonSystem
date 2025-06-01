package net.crayonsmp.commands;

import net.crayonsmp.CrayonCommand;
import net.crayonsmp.managers.GoalManager;
import net.crayonsmp.utils.ChatUtil;
import net.crayonsmp.utils.Goal;
import net.crayonsmp.utils.serialization.Magic;
import net.crayonsmp.utils.serialization.PlayerGoal;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GoalCommand implements CrayonCommand {

    @Override
    public String getCommand() {
        return "goal";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You must be a player to use this command!");
            return true;
        }

        String playerUUID = player.getUniqueId().toString();

        PlayerGoal playerGoal = GoalManager.PlayerGoalData.get(playerUUID);

        if (playerGoal == null) {
            player.sendMessage("§cYou do not have a goal assigned yet. Please rejoin.");
            return true;
        }

        Goal goal = playerGoal.getGoal();

        if (goal == null) {
            player.sendMessage("§cYour assigned goal data is incomplete. Please contact an administrator.");
            return true;
        }

        Magic primaryMagic = playerGoal.getMagicPrimery();
        Magic secondaryMagic = playerGoal.getMagicSecondary();
        String color = switch (goal.getGoalType()) {
            case BAD -> "<#a63851>";
            case GOOD -> "<#92b460>";
            case NEUTRAL -> "<#455f7f>";
        };

        player.sendMessage(ChatUtil.format(color + "--- Your Current Goal ---"));
        player.sendMessage(ChatUtil.format("<#f3d6ac>Goal: <#b4b4b4>" + goal.getName()));

        switch (goal.getGoalType()) {
            case BAD:
                player.sendMessage(ChatUtil.format("<#f3d6ac>Type: <glyph:bad_glyph_crayon>"));
                break;
            case GOOD:
                player.sendMessage(ChatUtil.format("<#f3d6ac>Type: <glyph:good_glyph_crayon>"));
                break;
            case NEUTRAL:
                player.sendMessage(ChatUtil.format("<#f3d6ac>Type: <glyph:neutral_glyph_crayon>"));
                break;
        }

        player.sendMessage(ChatUtil.format("<#cb9e83>Description: <#b4b4b4>"));
        for (String line : goal.getDescription()) {
            player.sendMessage("§f" + line);
        }

        player.sendMessage("");

        if (primaryMagic != null) {
            player.sendMessage(ChatUtil.format("<#cb9e83>Primary Magic: <#b4b4b4>" + primaryMagic.getName() + " <glyph:book_glyph_crayon>"));
        } else {
            player.sendMessage(ChatUtil.format("<#cb9e83>Primary Magic: §cNot assigned or invalid."));
        }

        if (secondaryMagic != null) {
            player.sendMessage(ChatUtil.format("<#cb9e83>Secondary Magic: <#b4b4b4>" + secondaryMagic.getName() + " <glyph:ampule_glyph_crayon>"));
        } else {
            player.sendMessage(ChatUtil.format("<#cb9e83>Secondary Magic: §cNot assigned or invalid."));
        }
        player.sendMessage(ChatUtil.format(color + "-------------------------"));

        return true;
    }
}