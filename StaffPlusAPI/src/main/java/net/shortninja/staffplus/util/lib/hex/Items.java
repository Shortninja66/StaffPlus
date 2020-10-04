package net.shortninja.staffplus.util.lib.hex;

import net.shortninja.staffplus.util.lib.JavaUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

import java.util.*;

/**
 * @author TigerHix, Shortninja
 */

public final class Items {
    private Items() {
    }

    public static BookBuilder bookBuilder() {
        return new BookBuilder();
    }

    public static ItemStackBuilder builder() {
        return new ItemStackBuilder();
    }

    public static ItemStack createColoredArmor(Armor armor, Color color, String name) {
        ItemStack leatherArmor = new ItemStack(armor.getMaterial());
        LeatherArmorMeta meta = (LeatherArmorMeta) leatherArmor.getItemMeta();
        meta.setColor(color);
        if (name != null)
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        leatherArmor.setItemMeta(meta);
        return leatherArmor;
    }

    public static ItemStack createColoredArmor(Armor armor, Color color) {
        return createColoredArmor(armor, color,
                "&l" + WordUtils.capitalize(armor.name().toLowerCase()));
    }

    public static ItemStack createSkull(String name) {
        String[] tmp = Bukkit.getVersion().split("MC: ");
        String version = tmp[tmp.length - 1].substring(0, 4);
        ItemStack skull;
        if (JavaUtils.parseMcVer(version) < 13) {
            skull = new ItemStack(Material.valueOf("SKULL_ITEM"), 1);
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            skullMeta.setOwner(name);
        } else {
            skull = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            skullMeta.setOwner(name);
            skull.setItemMeta(skullMeta);
        }
        return skull;
    }

    public static ItemStackBuilder editor(ItemStack itemStack) {
        return new ItemStackBuilder(itemStack);
    }

    public enum Armor {

        HELMET(Material.LEATHER_HELMET), CHESTPLATE(Material.LEATHER_CHESTPLATE), LEGGINGS(
                Material.LEATHER_LEGGINGS), BOOTS(Material.LEATHER_BOOTS);

        private Material material;

        private Armor(Material material) {
            this.material = material;
        }

        public Material getMaterial() {
            return material;
        }

    }

    public static class ItemStackBuilder {

        private final ItemStack itemStack;

        ItemStackBuilder() {
            itemStack = new ItemStack(Material.QUARTZ);
            setName("");
            setLore(new ArrayList<String>());
        }

        public ItemStackBuilder(ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        public ItemStackBuilder setItemMeta(ItemMeta meta) {
            this.itemStack.setItemMeta(meta);
            return this;
        }

        public ItemStackBuilder setMaterial(Material material) {
            itemStack.setType(material);
            return this;
        }

        public ItemStackBuilder changeAmount(int change) {
            itemStack.setAmount(itemStack.getAmount() + change);
            return this;
        }

        public ItemStackBuilder setAmount(int amount) {
            itemStack.setAmount(amount);
            return this;
        }

        public ItemStackBuilder setData(short data) {
            itemStack.setDurability(data);
            return this;
        }

        public ItemStackBuilder setData(MaterialData data) {
            itemStack.setData(data);
            return this;
        }

        public ItemStackBuilder setEnchantments(
                HashMap<Enchantment, Integer> enchantments) {
            for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
                itemStack.removeEnchantment(enchantment);
            }
            itemStack.addUnsafeEnchantments(enchantments);
            return this;
        }

        public ItemStackBuilder addEnchantment(Enchantment enchantment,
                                               int level) {
            itemStack.addUnsafeEnchantment(enchantment, level);
            return this;
        }

        public ItemStackBuilder setName(String name) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(name.equals("") ? " " : Strings.format(name));
            itemStack.setItemMeta(itemMeta);
            return this;
        }

        public ItemStackBuilder addBlankLore() {
            addLore(" ");
            return this;
        }

        public ItemStackBuilder addLineLore() {
            return addLineLore(20);
        }

        public ItemStackBuilder addLineLore(int length) {
            addLore("&8&m&l" + Strings.repeat('-', length));
            return this;
        }

        public ItemStackBuilder addLore(String... lore) {
            List<String> coloredLore = new ArrayList<String>();
            String[] newLore = new String[lore.length];

            for (String string : lore) {
                coloredLore.add(ChatColor.translateAlternateColorCodes('&', string));
            }

            lore = coloredLore.toArray(newLore);

            ItemMeta itemMeta = itemStack.getItemMeta();
            List<String> original = itemMeta.getLore();
            if (original == null) original = new ArrayList<String>();
            Collections.addAll(original, Strings.format(lore));
            itemMeta.setLore(original);
            itemStack.setItemMeta(itemMeta);
            return this;
        }

        public ItemStackBuilder addLore(List<String> lore) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            List<String> original = itemMeta.getLore();
            if (original == null) original = new ArrayList<String>();
            original.addAll(Strings.format(lore));
            itemMeta.setLore(original);
            itemStack.setItemMeta(itemMeta);
            return this;
        }

        public ItemStackBuilder setLore(String... lore) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setLore(Strings.format(Arrays.asList(lore)));
            itemStack.setItemMeta(itemMeta);
            return this;
        }

        public ItemStackBuilder setLore(List<String> lore) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setLore(Strings.format(lore));
            itemStack.setItemMeta(itemMeta);
            return this;
        }

        public ItemStack build() {
            return itemStack;
        }
    }

    public static class BookBuilder {
        private final ItemStack itemStack = new ItemStack(Material.WRITTEN_BOOK);
        private BookMeta meta;

        BookBuilder() {
            meta = (BookMeta) itemStack.clone().getItemMeta();
            meta.setTitle(Strings.format("&lWritten Book"));
            meta.setAuthor("Hex Framework");
        }

        public BookBuilder setTitle(String title) {
            meta.setTitle(Strings.format(title));
            return this;
        }

        public BookBuilder setAuthor(String author) {
            meta.setAuthor(Strings.format(author));
            return this;
        }

        public BookBuilder addPage(String... lines) {
            StringBuilder builder = new StringBuilder();
            for (String line : lines) {
                builder.append(
                        Strings.format(((line == null || line.isEmpty()) ? " "
                                : line))).append("\n");
            }
            meta.addPage(builder.toString());
            return this;
        }

        public ItemStack build() {
            ItemStack itemStack = this.itemStack.clone();
            itemStack.setItemMeta(meta);
            return itemStack;
        }
    }
}