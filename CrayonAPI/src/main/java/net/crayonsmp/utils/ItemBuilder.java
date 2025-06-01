package net.crayonsmp.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ItemBuilder implements Cloneable {

    private final ItemStack item;
    private final ItemMeta itemMeta;

    public ItemBuilder(ItemStack item) {
        this.item = item.clone();
        this.itemMeta = item.getItemMeta();
    }

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.itemMeta = item.getItemMeta();
    }

    public static ItemBuilder of(Material material) {
        return new ItemBuilder(material);
    }

    public ItemBuilder setCount(int Count) {
        item.setAmount(Count);
        return this;
    }

    public ItemBuilder title(Component title) {
        itemMeta.displayName(title);
        return this;
    }

    public ItemBuilder setTitle(String title) {
        itemMeta.setDisplayName(title);
        return this;
    }

    public ItemBuilder lore(Component... lore) {
        return this.setLore(Arrays.toString(lore));
    }

    public ItemBuilder lore(List<Component> lore) {
        itemMeta.lore(lore);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        return this.setLore(Arrays.asList(lore));
    }

    public ItemBuilder setLore(List<String> lore) {
        itemMeta.setLore(lore);
        return this;
    }

    public ItemBuilder setCustomModelData(int CustomModelData) {
        itemMeta.setCustomModelData(CustomModelData);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        itemMeta.setUnbreakable(true);
        return this;
    }

    public ItemBuilder setEnchantments(HashMap<Enchantment, Integer> enchantments) {
        item.addEnchantments(enchantments);
        return this;
    }

    public ItemBuilder setItemFlags(ItemFlag... itemFlags) {
        itemMeta.addItemFlags(itemFlags);
        return this;
    }

    public ItemBuilder setLeatherColor(Color color) {
        if (!(itemMeta instanceof LeatherArmorMeta leatherMeta)) {
            return this;
        }

        leatherMeta.setColor(color);
        return this;
    }

    public ItemBuilder setBookPages(List<String> strings) {
        if (!(itemMeta instanceof BookMeta bookMeta)) {
            return this;
        }

        bookMeta.setPages(strings);
        return this;
    }

    public ItemBuilder setBookTitle(String title) {
        if (!(itemMeta instanceof BookMeta bookMeta)) {
            return this;
        }

        bookMeta.setTitle(title);
        return this;
    }

    public ItemBuilder setBookAuthor(String Author) {
        if (!(itemMeta instanceof BookMeta bookMeta)) {
            return this;
        }

        bookMeta.setAuthor(Author);
        return this;
    }

    public ItemBuilder setHeadPlayer(Player player) {
        if (!(itemMeta instanceof SkullMeta skullMeta)) {
            return this;
        }

        skullMeta.setOwningPlayer(player);
        return this;
    }

    public ItemBuilder setHeadTextures(String name, String value, String signature) {
        if (!(itemMeta instanceof SkullMeta skullMeta)) {
            return this;
        }

        PlayerProfile playerProfile = skullMeta.getPlayerProfile();
        Set<ProfileProperty> properties = playerProfile.getProperties();
        properties.add(new ProfileProperty(name, value, signature));
        playerProfile.setProperties(properties);
        skullMeta.setPlayerProfile(playerProfile);
        return this;
    }

    public ItemBuilder setHideTooltip(Boolean hide) {
        itemMeta.setHideTooltip(hide);
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(itemMeta);
        return item;
    }

    @Override
    public ItemBuilder clone() {
        return new ItemBuilder(this.item);
    }
}