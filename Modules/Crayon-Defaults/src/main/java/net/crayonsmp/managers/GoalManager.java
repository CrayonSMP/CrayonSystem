package net.crayonsmp.managers;

import net.crayonsmp.Main;
import net.crayonsmp.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GoalManager {
    //----------Utility Fields---------
    private static final Pattern MAGIC_PROBABILITY_PATTERN = Pattern.compile("([^ ]+)(?: p=(\\d+))?");
    private static final Random random = new Random();
    //----------Static Data (Temporary)---------
    public static HashMap<String, Goal> registertgoals = new HashMap<>();
//----------Static Data (Temporary)---------
    public static HashMap<String, Magic> registertmagics = new HashMap<>();
    public static HashMap<String, PlayerGoal> PlayerGoalData = new HashMap<>();
//----------Utility Fields---------

    //----------Magic Selection Logic---------
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

        double randomValue = random.nextDouble() * totalProbability;

        double cumulativeProbability = 0;
        String selectedMagicId = null;

        for (final MagicSelectionCandidate candidate : candidates) {
            cumulativeProbability += candidate.probability;
            if (randomValue <= cumulativeProbability) {
                selectedMagicId = candidate.id;
                break;
            }
        }

        if (selectedMagicId == null && !candidates.isEmpty()) {
            selectedMagicId = candidates.get(candidates.size() - 1).id;
            Bukkit.getLogger().log(Level.SEVERE, "FATAL ERROR: Probability selection logic failed to select an ID! Using last candidate. TotalProb=" + totalProbability + ", RandomValue=" + randomValue + ", Candidates=" + candidates.size());
        }


        if (selectedMagicId != null) {
            return getMagicById(selectedMagicId);
        }

        return null;
    }

    //----------Magic Lookup Helper---------
    public static Magic getMagicById(final String id) {
        return registertmagics.get(id);
    }
//----------Magic Selection Logic---------

    public static Goal getRandomGoalByType(GoalType type) {
        if (registertgoals.isEmpty()) {
            Bukkit.getLogger().log(Level.WARNING, "No goals are registered to select from.");
            return null;
        }

        final List<Goal> availableGoals = new ArrayList<>();
        for (Goal goal : registertgoals.values()) {
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
//----------Magic Lookup Helper---------

    //----------Placeholder Creation---------
    public static PlayerGoalPlaceholder getPlayerGoalPlaceholder(Goal goal) {
        if (goal == null) {
            Bukkit.getLogger().log(Level.WARNING, "Attempted to get PlayerGoalPlaceholder for a null GoalTemplate.");
        }

        final HashMap<String, Magic> allAvailableMagics = registertmagics;

        final List<Magic> magicPrimaryList = new ArrayList<>();
        final List<Magic> magicSecondaryList = new ArrayList<>();

        final int numberOfPrimaryMagicsToSelect = 3;
        final int numberOfSecondaryMagicsToSelect = 2;

        final List<String> primaryMagicConfigs = goal.getPrimaryMagicConfigs();
        final Set<String> selectedPrimaryMagicNames = new HashSet<>();
        final int maxRetries = 10;

        for (int i = 0; i < numberOfPrimaryMagicsToSelect; i++) {
            Magic selectedMagic = null;
            int attempts = 0;
            // Versuche, eine einzigartige Magie zu finden
            while (attempts < maxRetries) {
                selectedMagic = selectMagicBasedOnProbability(primaryMagicConfigs, allAvailableMagics);

                if (selectedMagic == null) {
                    // Wenn keine Magie ausgewählt werden konnte, breche ab
                    break;
                }

                // Überprüfe, ob diese Magie bereits in der Liste ist
                if (!selectedPrimaryMagicNames.contains(selectedMagic.getName())) {
                    // Einzigartige Magie gefunden, füge sie hinzu und beende den Versuch
                    magicPrimaryList.add(selectedMagic);
                    selectedPrimaryMagicNames.add(selectedMagic.getName());
                    break; // Schleife beenden, da eine einzigartige Magie gefunden wurde
                } else {
                    // Magie ist bereits ausgewählt, versuche es erneut
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
            // Versuche, eine einzigartige Magie zu finden
            while (attempts < maxRetries) {
                selectedMagic = selectMagicBasedOnProbability(secondaryMagicConfigs, allAvailableMagics);

                if (selectedMagic == null) {
                    // Wenn keine Magie ausgewählt werden konnte, breche ab
                    break;
                }

                // Überprüfe, ob diese Magie bereits in der Liste ist
                if (!selectedSecondaryMagicNames.contains(selectedMagic.getName())) {
                    // Einzigartige Magie gefunden, füge sie hinzu und beende den Versuch
                    magicSecondaryList.add(selectedMagic);
                    selectedSecondaryMagicNames.add(selectedMagic.getName());
                    break; // Schleife beenden, da eine einzigartige Magie gefunden wurde
                } else {
                    // Magie ist bereits ausgewählt, versuche es erneut
                    attempts++;
                    Bukkit.getLogger().log(Level.FINE, "Duplicate secondary magic '" + selectedMagic.getName() + "' selected. Retrying... (Attempt " + attempts + ")");
                }
            }

            if (selectedMagic == null || attempts >= maxRetries) {
                Bukkit.getLogger().log(Level.WARNING, "Could not select a unique secondary magic after " + maxRetries + " attempts for GoalTemplate '" + goal.getConfigKey() + "'. Check config strings and registered magics or available unique magics.");
            }
        }

        return new PlayerGoalPlaceholder(goal, magicPrimaryList, magicSecondaryList);
    }

    public static void addPlayerGoalData(String uuid, PlayerGoal playerGoal) {
        PlayerGoalData.put(uuid, playerGoal); // Keep consistent for in-memory access
        Main.PlayerGoalData.set("playergoals." + uuid, playerGoal); // For persistent storage
        Main.PlayerGoalData.save();

        Player p = Bukkit.getPlayer(UUID.fromString(uuid));

        if(p != null) {
            if (playerGoal.getMagicPrimery().getId().equals("FIRE"))
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "n give fire_scythe 1 " + p.getName());

            if (playerGoal.getMagicPrimery().getId().equals("ICE"))
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "n give ice_katana 1 " + p.getName());

            if (playerGoal.getMagicPrimery().getId().equals("LIGHT"))
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "n give light_greatsword 1 " + p.getName());

            if (playerGoal.getMagicPrimery().getId().equals("DARKNESS"))
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "n give darkness_katana 1 " + p.getName());

            if (playerGoal.getMagicPrimery().getId().equals("LAVA"))
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "n give magma_sledge 1 " + p.getName());

            if (playerGoal.getMagicPrimery().getId().equals("EATH"))
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "n give earth_hammer 1 " + p.getName());

            if (playerGoal.getMagicPrimery().getId().equals("LIGHTNING"))
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "n give mjolnir 1 " + p.getName());

            if (playerGoal.getMagicPrimery().getId().equals("WATER"))
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "n give water_trident 1 " + p.getName());

            if (playerGoal.getMagicPrimery().getId().equals("POISON"))
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "n give poisonous_dagger 1 " + p.getName());

            ApplySeconderyEffects(p);
        }
    }
//----------Placeholder Creation---------

    public static void removePlayerGoalData(String uuid) {
        PlayerGoalData.remove(uuid);
        Main.PlayerGoalData.set("playergoals." + uuid, null);
        Main.PlayerGoalData.save();
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
            // For water breathing, you'll need to handle it with an event listener as there's no direct attribute.
            // This example focuses purely on attributes. You would implement custom logic for water breathing
            // (e.g., cancelling drowning damage, refilling air) in a separate method or event handler.
            // For the "Dolphin's Grace" aspect (movement speed), an attribute can be used.
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