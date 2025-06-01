package net.crayonsmp.utils.serialization;


import net.crayonsmp.utils.Goal;

import java.util.List;

public class PlayerGoalPlaceholder {

    private final Goal goal;
    private final List<Magic> magicPrimeryList;
    private final List<Magic> magicSecondaryList;

    public PlayerGoalPlaceholder(Goal goal, List<Magic> MagicPrimeryList, List<Magic> MagicSecondaryList) {
        if (goal == null) {
            throw new IllegalArgumentException("PlayerGoal goal cannot be null.");
        }
        if (MagicPrimeryList == null) {
            throw new IllegalArgumentException("PlayerGoal MagicPrimeryList cannot be null.");
        }
        if (MagicSecondaryList == null) {
            throw new IllegalArgumentException("PlayerGoal MagicSecondaryList cannot be null.");
        }

        this.goal = goal;
        this.magicPrimeryList = MagicPrimeryList;
        this.magicSecondaryList = MagicSecondaryList;
    }

    public Goal getGoal() {
        return goal;
    }
    public List<Magic> getMagicPrimeryList() {
        return magicPrimeryList;
    }
    public List<Magic> getMagicSecondaryList() {
        return magicSecondaryList;
    }
}