package net.crayonsmp.utils;

import lombok.Getter;
import lombok.Setter;
import net.crayonsmp.utils.serialization.Magic;
import net.crayonsmp.utils.serialization.PlayerGoalPlaceholder;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class GoalInventory {

    private final Map<GoalType, PlayerGoalPlaceholder> placeholders = new HashMap<>();

    private final Inventory inv;
    public GoalType selectedPlaceholder;
    public Magic selectedPrimaryMagic;
    public Magic selectedSecondaryMagic;

    public GoalInventory(PlayerGoalPlaceholder goodPlaceholder, PlayerGoalPlaceholder neutralPlaceholder, PlayerGoalPlaceholder badPlaceholder, Inventory inv) {
        this.placeholders.put(GoalType.GOOD, goodPlaceholder);
        this.placeholders.put(GoalType.NEUTRAL, neutralPlaceholder);
        this.placeholders.put(GoalType.BAD, badPlaceholder);
        this.inv = inv;
    }

    public PlayerGoalPlaceholder getPlaceholder(GoalType type) {
        return placeholders.get(type);
    }

}
