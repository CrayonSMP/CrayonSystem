package net.crayonsmp.utils;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.*;

@Getter
@ToString
@SerializableAs("CrayonGoal")
public class Goal implements ConfigurationSerializable {

    private final String configKey;
    private final GoalType goalType;
    private final String name;
    private final String ID;
    private List<String> description;
    private List<String> primaryMagicConfigs;
    private List<String> secondaryMagicConfigs;

    public Goal(String configKey, GoalType goalType, String name, String ID, List<String> description, List<String> primaryMagicConfigs, List<String> secondaryMagicConfigs) {
        Preconditions.checkNotNull(configKey);
        Preconditions.checkState(!configKey.trim().isEmpty(), "configKey must not be empty");
        Preconditions.checkNotNull(goalType, "GoalType cannot be null.");

        this.name = (name != null) ? name.trim() : "";
        this.ID = ID.trim();

        this.configKey = configKey.trim();
        this.goalType = goalType;

        this.description = (description != null) ? Collections.unmodifiableList(new ArrayList<>(description)) : Collections.emptyList();
        this.primaryMagicConfigs = (primaryMagicConfigs != null) ? Collections.unmodifiableList(new ArrayList<>(primaryMagicConfigs)) : Collections.emptyList();
        this.secondaryMagicConfigs = (secondaryMagicConfigs != null) ? Collections.unmodifiableList(new ArrayList<>(secondaryMagicConfigs)) : Collections.emptyList();
    }

    // --- Getter-Methoden ---

    public Goal(Map<String, Object> map) {
        this(
                (String) map.get("configKey"),
                GoalType.valueOf((String) map.get("goalType")),
                (String) map.get("name"),
                (String) map.get("ID"),
                (List<String>) map.get("description"),
                (List<String>) map.get("primaryMagicConfigs"),
                (List<String>) map.get("secondaryMagicConfigs")
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Goal that = (Goal) o;
        // Templates sind gleich, wenn configKey und goalType übereinstimmen
        return configKey.equals(that.configKey) && goalType == that.goalType;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(goalType);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new java.util.LinkedHashMap<>();
        map.put("configKey", configKey);
        map.put("goalType", goalType.name()); // Store enum as its name string
        map.put("name", name);
        map.put("ID", ID);
        map.put("description", new ArrayList<>(description));
        map.put("primaryMagicConfigs", new ArrayList<>(primaryMagicConfigs));
        map.put("secondaryMagicConfigs", new ArrayList<>(secondaryMagicConfigs));
        return map;
    }
}