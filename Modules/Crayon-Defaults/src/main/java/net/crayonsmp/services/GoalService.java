package net.crayonsmp.services;

import net.crayonsmp.CrayonDefault;
import net.crayonsmp.enums.GoalType;
import net.crayonsmp.utils.goal.Goal;
import net.crayonsmp.utils.goal.Magic;
import net.crayonsmp.utils.goal.PlayerGoal;
import net.crayonsmp.utils.goal.PlayerGoalPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GoalService {
    private static final Pattern MAGIC_PROBABILITY_PATTERN = Pattern.compile("([^ ]+)(?: p=(\\d+))?");
    private static final Random random = new Random();
    public static HashMap<String, Goal> RegistertGoals = new HashMap<>();
    public static HashMap<String, Magic> RegistertMagics = new HashMap<>();
    public static HashMap<String, PlayerGoal> PlayerGoalData = new HashMap<>();

    private static Magic selectMagicBasedOnProbability(final List<String> magicStrings, final HashMap<String, Magic> allAvailableMagics) {
        if (magicStrings == null || magicStrings.isEmpty() || allAvailableMagics == null || allAvailableMagics.isEmpty()) {
            return null;
        }

        final List<MagicSelectionCandidate> candidates = new ArrayList<>();
        int totalProbability = 0;

        for (final String magicString : magicStrings) {
            if (magicString == null || magicString.trim().isEmpty()) continue;
            final Matcher matcher = MAGIC_PROBABILITY_PATTERN.matcher(magicString.trim());
            if (matcher.matches()) {
                final String id = matcher.group(1).trim();
                int probability = 100;

                if (matcher.group(2) != null) {
                    try {
                        probability = Integer.parseInt(matcher.group(2));
                    } catch (NumberFormatException e) {
                        Bukkit.getLogger().log(Level.WARNING, "Invalid probability number format in Magic string: '" + magicString + "'. Using default probability 100.");
                    }
                }

                if (probability > 0) {
                    candidates.add(new MagicSelectionCandidate(id, probability));
                    totalProbability += probability;
                } else {
                    Bukkit.getLogger().log(Level.WARNING, "Magic '" + id + "' has a non-positive probability (" + probability + ") in string: '" + magicString + "'. Skipping this candidate.");
                }
            } else {
                Bukkit.getLogger().log(Level.WARNING, "Invalid format for a magic string during probability selection: '" + magicString + "'. Expected format 'MAGIC_ID' or 'MAGIC_ID p=PROBABILITY'.");
            }
        }

        if (candidates.isEmpty() || totalProbability <= 0) {
            return null;
        }

        double RandomValue = random.nextDouble() * totalProbability;

        double cumulativeProbability = 0;
        String SelectedMagicId = null;

        for (final MagicSelectionCandidate candidate : candidates) {
            cumulativeProbability += candidate.probability;
            if (RandomValue <= cumulativeProbability) {
                SelectedMagicId = candidate.id;
                break;
            }
        }

        if (SelectedMagicId == null && !candidates.isEmpty()) {
            SelectedMagicId = candidates.get(candidates.size() - 1).id;
            Bukkit.getLogger().log(Level.SEVERE, "FATAL ERROR: Probability selection logic failed to select an ID! Using last candidate. TotalProb=" + totalProbability + ", RandomValue=" + RandomValue + ", Candidates=" + candidates.size());
        }


        if (SelectedMagicId != null) {
            return getMagicById(SelectedMagicId);
        }

        return null;
    }

    public static Magic getMagicById(final String id) {
        return RegistertMagics.get(id);
    }

    public static Goal getGoalById(final String id) {
        return RegistertGoals.get(id);
    }

    public static Goal getRandomGoalByType(GoalType type) {
        if (RegistertGoals.isEmpty()) {
            Bukkit.getLogger().log(Level.WARNING, "No goals are registered to select from.");
            return null;
        }

        final List<Goal> availableGoals = new ArrayList<>();
        for (Goal goal : RegistertGoals.values()) {
            if (goal.getGoalType() == type) {
                availableGoals.add(goal);
            }
        }

        if (availableGoals.isEmpty()) {
            Bukkit.getLogger().log(Level.WARNING, "No goals found for GoalType: " + type.name());
            return null;
        }

        return availableGoals.get(random.nextInt(availableGoals.size()));
    }

    public static PlayerGoalPlaceholder getPlayerGoalPlaceholder(Goal goal) {
        if (goal == null) {
            Bukkit.getLogger().log(Level.WARNING, "Attempted to get PlayerGoalPlaceholder for a null GoalTemplate.");
        }

        final HashMap<String, Magic> AllAvailableMagics = RegistertMagics;

        final List<Magic> MagicPrimaryList = new ArrayList<>();
        final List<Magic> MagicSecondaryList = new ArrayList<>();

        final int numberOfPrimaryMagicsToSelect = 3;
        final int numberOfSecondaryMagicsToSelect = 2;

        final List<String> primaryMagicConfigs = goal.getPrimaryMagicConfigs();
        final Set<String> selectedPrimaryMagicNames = new HashSet<>();
        final int maxRetries = 10;

        for (int i = 0; i < numberOfPrimaryMagicsToSelect; i++) {
            Magic selectedMagic = null;
            int attempts = 0;
            while (attempts < maxRetries) {
                selectedMagic = selectMagicBasedOnProbability(primaryMagicConfigs, AllAvailableMagics);

                if (selectedMagic == null) {
                    break;
                }

                if (!selectedPrimaryMagicNames.contains(selectedMagic.getName())) {
                    MagicPrimaryList.add(selectedMagic);
                    selectedPrimaryMagicNames.add(selectedMagic.getName());
                    break;
                } else {
                    attempts++;
                    Bukkit.getLogger().log(Level.FINE, "Duplicate primary magic '" + selectedMagic.getName() + "' selected. Retrying... (Attempt " + attempts + ")");
                }
            }

            if (selectedMagic == null || attempts >= maxRetries) {
                Bukkit.getLogger().log(Level.WARNING, "Could not select a unique primary magic after " + maxRetries + " attempts for GoalTemplate '" + goal.getConfigKey() + "'. Check config strings and registered magics or available unique magics.");
            }
        }

        final List<String> secondaryMagicConfigs = goal.getSecondaryMagicConfigs();
        final Set<String> selectedSecondaryMagicNames = new HashSet<>();

        for (int i = 0; i < numberOfSecondaryMagicsToSelect; i++) {
            Magic selectedMagic = null;
            int attempts = 0;
            while (attempts < maxRetries) {
                selectedMagic = selectMagicBasedOnProbability(secondaryMagicConfigs, AllAvailableMagics);

                if (selectedMagic == null) {
                    break;
                }

                if (!selectedSecondaryMagicNames.contains(selectedMagic.getName())) {
                    MagicSecondaryList.add(selectedMagic);
                    selectedSecondaryMagicNames.add(selectedMagic.getName());
                    break;
                } else {
                    attempts++;
                    Bukkit.getLogger().log(Level.FINE, "Duplicate secondary magic '" + selectedMagic.getName() + "' selected. Retrying... (Attempt " + attempts + ")");
                }
            }

            if (selectedMagic == null || attempts >= maxRetries) {
                Bukkit.getLogger().log(Level.WARNING, "Could not select a unique secondary magic after " + maxRetries + " attempts for GoalTemplate '" + goal.getConfigKey() + "'. Check config strings and registered magics or available unique magics.");
            }
        }

        return new PlayerGoalPlaceholder(goal, MagicPrimaryList, MagicSecondaryList);
    }

    public static void addPlayerGoalData(String uuid, PlayerGoal playerGoal) {
        PlayerGoalData.put(uuid, playerGoal);
        CrayonDefault.playerGoalData.set("playergoals." + uuid, playerGoal);
        CrayonDefault.playerGoalData.save();

        Player p = Bukkit.getPlayer(UUID.fromString(uuid));

        if(p != null) {
            String magicPrimaryId = playerGoal.getMagicPrimery().getId();
            String command = "n give ";

            switch (magicPrimaryId) {
                case "FIRE":
                    command += "fire_scythe 1 ";
                    break;
                case "ICE":
                    command += "ice_katana 1 ";
                    break;
                case "LIGHT":
                    command += "light_greatsword 1 ";
                    break;
                case "DARKNESS":
                    command += "darkness_katana 1 ";
                    break;
                case "LAVA":
                    command += "magma_sledge 1 ";
                    break;
                case "EATH": // Note: You have "EATH" here, not "EARTH". Is that intentional?
                    command += "earth_hammer 1 ";
                    break;
                case "LIGHTNING":
                    command += "mjolnir 1 ";
                    break;
                case "WATER":
                    command += "water_trident 1 ";
                    break;
                case "POISON":
                    command += "poisonous_dagger 1 ";
                    break;
                case "WIND":
                    command += "wind_fan 1 ";
                    break;
                default:
                    System.out.println("Unknown MagicPrimary ID: " + magicPrimaryId);
                    return;
            }

            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command + p.getName());
            ApplySeconderyEffects(p);
        }
    }

    public static void removePlayerGoalData(String uuid) {
        PlayerGoalData.remove(uuid);
        CrayonDefault.playerGoalData.set("playergoals." + uuid, null);
        CrayonDefault.playerGoalData.save();
    }

    public static boolean hasPlayerGoalData(Player p) {
        return PlayerGoalData.containsKey(p.getUniqueId().toString());
    }

    public static boolean hasPlayerMagicPrimeryType(Player player, String ID){
        if(hasPlayerGoalData(player)){
            return PlayerGoalData.get(player.getUniqueId().toString()).getMagicPrimery().getId().equals(ID);
        }
        return false;
    }
    public static boolean hasPlayerMagicSeconderyType(Player player, String ID){
        if(hasPlayerGoalData(player)){
            return PlayerGoalData.get(player.getUniqueId().toString()).getMagicSecondary().getId().equals(ID);
        }
        return false;
    }

    private static class MagicSelectionCandidate {
        final String id;
        final int probability;

        MagicSelectionCandidate(final String id, final int probability) {
            this.id = id;
            this.probability = probability;
        }
    }



    private static final Map<UUID, Map<String, UUID>> activeAttributeModifiers = new HashMap<>();

    public static void ApplySeconderyEffects(Player player) {
        if (!hasPlayerGoalData(player)) {
            removeAllSecondaryEffects(player);
            return;
        }

        UUID playerUUID = player.getUniqueId();
        activeAttributeModifiers.putIfAbsent(playerUUID, new HashMap<>());

        if (hasPlayerMagicSeconderyType(player, "WATER")) {
            applyAttribute(player, "WATER_MOVEMENT", Attribute.WATER_MOVEMENT_EFFICIENCY, 0.3, AttributeModifier.Operation.ADD_NUMBER);
        } else {
            removeAttribute(player, "WATER_MOVEMENT", Attribute.WATER_MOVEMENT_EFFICIENCY);
        }

        if (hasPlayerMagicSeconderyType(player, "EARTH")) {
            applyAttribute(player, "EARTH_RESISTANCE_ARMOR", Attribute.ARMOR_TOUGHNESS, 20.0, AttributeModifier.Operation.ADD_NUMBER);
        } else {
            removeAttribute(player, "EARTH_RESISTANCE_ARMOR", Attribute.ARMOR_TOUGHNESS);
        }

        if (hasPlayerMagicSeconderyType(player, "LIGHTNING")) {
            applyAttribute(player, "LIGHTNING_SPEED", Attribute.MOVEMENT_SPEED, 0.05, AttributeModifier.Operation.ADD_NUMBER);
        } else {
            removeAttribute(player, "LIGHTNING_SPEED", Attribute.MOVEMENT_SPEED);
        }
    }

    public static void applyAttribute(Player player, String modifierIdentifier, Attribute attribute, double amount, AttributeModifier.Operation operation) {
        UUID playerUUID = player.getUniqueId();
        Map<String, UUID> playerModifiers = activeAttributeModifiers.get(playerUUID);

        removeAttribute(player, modifierIdentifier, attribute);

        UUID modifierUUID = UUID.randomUUID();
        AttributeModifier modifier = new AttributeModifier(modifierUUID, modifierIdentifier, amount, operation);

        player.getAttribute(attribute).addModifier(modifier);
        playerModifiers.put(modifierIdentifier, modifierUUID);
    }

    public static void removeAttribute(Player player, String modifierIdentifier, Attribute attribute) {
        UUID playerUUID = player.getUniqueId();
        Map<String, UUID> playerModifiers = activeAttributeModifiers.get(playerUUID);

        if (playerModifiers != null && playerModifiers.containsKey(modifierIdentifier)) {
            UUID modifierUUID = playerModifiers.get(modifierIdentifier);
            AttributeModifier modifierToRemove = new AttributeModifier(modifierUUID, modifierIdentifier, 0, AttributeModifier.Operation.ADD_NUMBER);
            player.getAttribute(attribute).removeModifier(modifierToRemove);
            playerModifiers.remove(modifierIdentifier);
        }
    }

    public static void removeAllSecondaryEffects(Player player) {
        UUID playerUUID = player.getUniqueId();
        Map<String, UUID> playerModifiers = activeAttributeModifiers.get(playerUUID);

        if (playerModifiers != null) {
            new HashMap<>(playerModifiers).forEach((key, uuid) -> {
                switch (key) {
                    case "WATER_MOVEMENT":
                        removeAttribute(player, key, Attribute.MOVEMENT_SPEED);
                        break;
                    case "EARTH_RESISTANCE_ARMOR":
                        removeAttribute(player, key, Attribute.ARMOR_TOUGHNESS);
                        break;
                    case "LIGHTNING_SPEED":
                        removeAttribute(player, key, Attribute.MOVEMENT_SPEED);
                        break;
                }
            });
            activeAttributeModifiers.remove(playerUUID);
        }
    }
}