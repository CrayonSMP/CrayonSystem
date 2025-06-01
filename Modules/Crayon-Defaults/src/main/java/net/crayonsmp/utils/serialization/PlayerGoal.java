package net.crayonsmp.utils.serialization;

import lombok.RequiredArgsConstructor;
import net.crayonsmp.utils.Goal;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@RequiredArgsConstructor
@SerializableAs("CrayonPlayerGoal")
public class PlayerGoal implements ConfigurationSerializable {

    @NotNull
    private final Goal goal;

    @NotNull
    private final Magic magicPrimery;

    @NotNull
    private final Magic magicSecondary;

    public PlayerGoal(Map<String, Object> map) {
        this.goal = (Goal) map.get("goal"); // Cast to Goal
        this.magicPrimery = (Magic) map.get("magicPrimery"); // Cast to Magic
        this.magicSecondary = (Magic) map.get("magicSecondary"); // Cast to Magic
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new java.util.LinkedHashMap<>();
        map.put("goal", goal); // Bukkit will automatically serialize Goal because it's ConfigurationSerializable
        map.put("magicPrimery", magicPrimery); // Bukkit will automatically serialize Magic
        map.put("magicSecondary", magicSecondary); // Bukkit will automatically serialize Magic
        return map;
    }

    public Goal getGoal() {
        return goal;
    }

    public Magic getMagicPrimery() {
        return magicPrimery;
    }

    public Magic getMagicSecondary() {
        return magicSecondary;
    }
}