package net.shortninja.staffplus.util.lib;

import net.shortninja.staffplus.IStaffPlus;
import net.shortninja.staffplus.util.lib.hex.Strings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public class Message {

    public final String LONG_LINE = "&m" + Strings.repeat('-', 48);
    //private IPermissionsHandler permission;
    private IStaffPlus staffPlus;

    public Message(IStaffPlus staffPlus) {
        this.staffPlus = staffPlus;
        //permission = staffPlus.getPermissions();
    }

    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public void send(Player player, String message, String prefix) {
        if (player == null || message.isEmpty()) {
            return;
        }

        player.sendMessage(colorize(prefix + " " + message));
    }

    public void send(Player player, String message, String prefix, String permission) {
        if (player == null || message == null || message.isEmpty() || !staffPlus.getPermissions().has(player, permission)) {
            return;
        }

        player.sendMessage(colorize(prefix + " " + message));
    }

    public void send(CommandSender sender, String message, String prefix) {
        if (sender == null || message.isEmpty()) {
            return;
        }

        sender.sendMessage(colorize(prefix + " " + message));
    }

    public void sendConsoleMessage(String message, boolean isError) {
        String prefix = isError ? "&4[Staff+] &c" : "&2[Staff+] &a";

        Bukkit.getServer().getConsoleSender().sendMessage(colorize(prefix + message));
    }

    public void sendGlobalMessage(String message, String prefix) {
        if (message.isEmpty() && !prefix.isEmpty()) {
            return;
        }

        Bukkit.broadcastMessage(colorize(prefix + " " + message));
    }

    public void sendGroupMessage(String message, String permission, String prefix) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            send(player, message, prefix, permission);
        }
    }

    public void sendCollectedMessage(Player player, Collection<String> messages, String prefix) {
        for (String message : messages) {
            send(player, message, prefix);
        }
    }
}