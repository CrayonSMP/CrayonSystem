package net.crayonsmp.commands;

import net.crayonsmp.services.GoalService;
import net.crayonsmp.utils.ChatUtil;
import net.crayonsmp.utils.goal.Goal;
import net.crayonsmp.utils.goal.Magic;
import net.crayonsmp.utils.goal.PlayerGoal;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GoalCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String [] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to use this command!");
            return true; // Return true as the command was handled, just not by console
        }
        Player player = (Player) sender;

        String playerUUID = player.getUniqueId().toString();

        PlayerGoal playerGoal = GoalService.PlayerGoalData.get(playerUUID);

        if (playerGoal == null) {
            player.sendMessage("§cYou do not have a goal assigned yet. Please rejoin.");
            return true; // Command handled
        }

        Goal goal = playerGoal.getGoal();
        Magic primaryMagic = playerGoal.getMagicPrimery(); // Note: Method name 'getMagicPrimery' from your PlayerGoal.java
        Magic secondaryMagic = playerGoal.getMagicSecondary();
        String color = null;
        switch (goal.getGoalType()) {
            case BAD:
                color = "<#a63851>";
                break;
            case GOOD:
                color = "<#92b460>";
                break;
            case NEUTRAL:
                color = "<#455f7f>";
                break;
        }
        player.sendMessage(ChatUtil.format(color + "--- Your Current Goal ---"));
        if (goal != null) { // Defensive check, though Goal should ideally not be null if playerGoal is valid
            player.sendMessage(ChatUtil.format("<#f3d6ac>Goal: <#b4b4b4>" + goal.getName()));
            switch (goal.getGoalType()){
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
        } else {
            player.sendMessage("§cYour assigned goal data is incomplete. Please contact an administrator.");
        }

        if (primaryMagic != null) { // Defensive check for magic being null
            player.sendMessage(ChatUtil.format("<#cb9e83>Primary Magic: <#b4b4b4>" + primaryMagic.getName() + " <glyph:book_glyph_crayon>"));
        } else {
            player.sendMessage(ChatUtil.format("<#cb9e83>Primary Magic: §cNot assigned or invalid."));
        }

        if (secondaryMagic != null) { // Defensive check for magic being null
            player.sendMessage(ChatUtil.format("<#cb9e83>Secondary Magic: <#b4b4b4>" + secondaryMagic.getName() + " <glyph:ampule_glyph_crayon>"));
        } else {
            player.sendMessage(ChatUtil.format("<#cb9e83>Secondary Magic: §cNot assigned or invalid."));
        }
        player.sendMessage(ChatUtil.format(color + "-------------------------"));

        return true; // Command successfully handled
    }
}