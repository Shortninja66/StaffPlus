package net.shortninja.staffplus.server.command.cmd.mode;

import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.player.attribute.mode.handler.FreezeHandler;
import net.shortninja.staffplus.server.data.config.Messages;
import net.shortninja.staffplus.server.data.config.Options;
import net.shortninja.staffplus.util.MessageCoordinator;
import net.shortninja.staffplus.util.PermissionHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class FreezeCmd extends BukkitCommand {
    private PermissionHandler permission = StaffPlus.get().permission;
    private MessageCoordinator message = StaffPlus.get().message;
    private Options options = StaffPlus.get().options;
    private Messages messages = StaffPlus.get().messages;
    private FreezeHandler freezeHandler = StaffPlus.get().freezeHandler;

    public FreezeCmd(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!permission.has(sender, options.permissionFreeze)) {
            message.send(sender, messages.noPermission, messages.prefixGeneral);
            return true;
        }

        if (args.length >= 2 && permission.isOp(sender)) {
            Player targetPlayer = Bukkit.getPlayer(args[0]);
            String option = args[1];

            if (targetPlayer != null) {
                if (option.equalsIgnoreCase("enable")) {
                    freezeHandler.addFreeze(sender, targetPlayer, true);
                } else if (option.equalsIgnoreCase("disable")) {
                    freezeHandler.removeFreeze(sender, targetPlayer, true);
                } else
                    message.send(sender, messages.invalidArguments.replace("%usage%", getName() + " &7" + getUsage()), messages.prefixGeneral);
            } else message.send(sender, messages.playerOffline, messages.prefixGeneral);
        } else if (args.length == 1) {
            Player targetPlayer = Bukkit.getPlayer(args[0]);

            if (targetPlayer != null) {
                toggleFreeze(targetPlayer, sender);
            } else message.send(sender, messages.playerOffline, messages.prefixGeneral);
        } else message.send(sender, messages.playerOffline, messages.prefixGeneral);

        return true;
    }

    private void toggleFreeze(Player player, CommandSender sender) {
        if (freezeHandler.isFrozen(player.getUniqueId())) {
            freezeHandler.removeFreeze(sender, player, true);
        } else {
            freezeHandler.addFreeze(sender, player, true);
        }
    }
}