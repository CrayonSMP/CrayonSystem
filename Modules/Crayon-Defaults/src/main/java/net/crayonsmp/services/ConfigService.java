package net.crayonsmp.services;

import net.crayonsmp.CrayonDefault;
import net.crayonsmp.enums.GoalType;
import net.crayonsmp.utils.config.SConfig;
import net.crayonsmp.utils.goal.Goal;
import net.crayonsmp.utils.goal.Magic;
import net.crayonsmp.utils.goal.PlayerGoal;

import java.util.HashMap;
import java.util.List;

public class ConfigService {
    public static void registergoalconfig() {
        SConfig config = CrayonDefault.goalConfig;
        SConfig playergoaldata = CrayonDefault.playerGoalData;
        if (!config.getFile().isFile()) {
            config.setDefault("goals.good.good_eternal_spring.name", "Eternal Spring Sanctuary");
            config.setDefault("goals.good.good_eternal_spring.description", List.of(
                    "Cultivate a hidden oasis where spring reigns eternal",
                    "flowers never fade, and pure waters flow, offering refuge",
                    "and healing to all peaceful creatures. Requires nurturing",
                    "rare flora and redirecting ancient water sources."));
            config.setDefault("goals.good.good_eternal_spring.magics.primary", List.of(
                    "WATER p=40",
                    "EARTH p=30",
                    "LIGHT p=20",
                    "WIND p=10"));
            config.setDefault("goals.good.good_eternal_spring.magics.secondary", List.of(
                    "WATER",
                    "EARTH",
                    "WIND"));

            config.setDefault("magics.earth.name", "Earth");
            config.setDefault("magics.earth.id", "EARTH");
            config.setDefault("magics.earth.description", List.of(
                    "<#9b5f5f>Earth magic draws power from stone",
                    "<#9b5f5f>dirt, and minerals to create walls",
                    "<#9b5f5f>tremors, and shields."));
            config.setDefault("magics.water.theme", List.of(
                    "<#a9cfdd>Earth magic is brown,",
                    "<#a9cfdd>gray, or mossy green."));

            config.save();
        }

        HashMap<String, Goal> goals = new HashMap<>();
        config.getConfigurationSection("goals.good").getKeys(false).forEach(key -> {
            Goal goal = new Goal(key, GoalType.GOOD, config.getString("goals.good." + key + ".name"), config.getString("goals.good." + key + ".id"), config.getStringList("goals.good." + key + ".description"), config.getStringList("goals.good." + key + ".magics.primary"), config.getStringList("goals.good." + key + ".magics.secondary"));
            goals.put(goal.getID(), goal);
        });
        config.getConfigurationSection("goals.neutral").getKeys(false).forEach(key -> {
            Goal goal = new Goal(key, GoalType.NEUTRAL, config.getString("goals.neutral." + key + ".name"), config.getString("goals.neutral." + key + ".id"), config.getStringList("goals.neutral." + key + ".description"), config.getStringList("goals.neutral." + key + ".magics.primary"), config.getStringList("goals.neutral." + key + ".magics.secondary"));
            goals.put(goal.getID(), goal);
        });
        config.getConfigurationSection("goals.bad").getKeys(false).forEach(key -> {
            Goal goal = new Goal(key, GoalType.BAD, config.getString("goals.bad." + key + ".name"), config.getString("goals.bad." + key + ".id"), config.getStringList("goals.bad." + key + ".description"), config.getStringList("goals.bad." + key + ".magics.primary"), config.getStringList("goals.bad." + key + ".magics.secondary"));
            goals.put(goal.getID(), goal);
        });

        GoalService.RegistertGoals = goals;

        HashMap<String, Magic> magics = new HashMap<>();
        config.getConfigurationSection("magics").getKeys(false).forEach(key -> {
            Magic magic = new Magic(config.getString("magics." + key + ".id"), config.getString("magics." + key + ".name"), config.getStringList("magics." + key + ".description"), config.getStringList("magics." + key + ".theme"));
            magics.put(magic.getId(), magic);
        });

        GoalService.RegistertMagics = magics;

        HashMap<String, PlayerGoal> playergoals = new HashMap<>();
        playergoaldata.getConfigurationSection("playergoals.").getKeys(false).forEach(key -> {
            PlayerGoal playerGoal = playergoaldata.getObject("playergoals." + key, PlayerGoal.class);
            playergoals.put(key, playerGoal);
        });

        GoalService.PlayerGoalData = playergoals;

    }
}
