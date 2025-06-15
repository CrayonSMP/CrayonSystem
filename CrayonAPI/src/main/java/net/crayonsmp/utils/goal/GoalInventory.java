package net.crayonsmp.utils.goal;

import net.crayonsmp.enums.GoalType;
import org.bukkit.inventory.Inventory;

public class GoalInventory {
    private final Inventory inv;
    public GoalType selectetPlaceholder;
    private final PlayerGoalPlaceholder goodPlaceholder;
    private final PlayerGoalPlaceholder neutralPlaceholder;
    private final PlayerGoalPlaceholder badPlaceholder;

    public Magic selectetPrimaryMagic;
    public Magic selectetSecondaryMagic;



    public GoalInventory(Inventory inv, PlayerGoalPlaceholder goodPlaceholder, PlayerGoalPlaceholder neutralPlaceholder, PlayerGoalPlaceholder badPlaceholder) {
        this.inv = inv;
        this.goodPlaceholder = goodPlaceholder;
        this.neutralPlaceholder = neutralPlaceholder;
        this.badPlaceholder = badPlaceholder;
    }

    public Inventory getInv() {
        return inv;
    }

    public PlayerGoalPlaceholder getGoodPlaceholder() {
        return goodPlaceholder;
    }

    public PlayerGoalPlaceholder getNeutralPlaceholder() {
        return neutralPlaceholder;
    }

    public PlayerGoalPlaceholder getBadPlaceholder() {
        return badPlaceholder;
    }
    public GoalType getSelectetPlaceholder() {
        return selectetPlaceholder;
    }
    public void setSelectetPlaceholder(GoalType selectetPlaceholder) {
        this.selectetPlaceholder = selectetPlaceholder;
    }
    public Magic getSelectetPrimaryMagic() {
        return selectetPrimaryMagic;
    }
    public void setSelectetPrimaryMagic(Magic selectetPrimaryMagic) {
        this.selectetPrimaryMagic = selectetPrimaryMagic;
    }
    public Magic getSelectetSecondaryMagic() {
        return selectetSecondaryMagic;
    }
    public void setSelectetSecondaryMagic(Magic selectetSecondaryMagic) {
        this.selectetSecondaryMagic = selectetSecondaryMagic;
    }
}