package net.shortninja.staffplus.player.attribute.mode.handler;

import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.server.data.config.Messages;
import net.shortninja.staffplus.server.data.config.Options;
import net.shortninja.staffplus.util.MessageCoordinator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CpsHandler {
    private static Map<UUID, Integer> currentTests = new HashMap<UUID, Integer>();
    private MessageCoordinator message = StaffPlus.get().message;
    private Options options = StaffPlus.get().options;
    private Messages messages = StaffPlus.get().messages;

    public boolean isTesting(UUID uuid) {
        return currentTests.containsKey(uuid);
    }

    public void updateCount(UUID uuid) {
        int count = currentTests.get(uuid);

        currentTests.put(uuid, count + 1);
    }

    public void startTest(final CommandSender sender, final Player targetPlayer) {
        currentTests.put(targetPlayer.getUniqueId(), 0);
        message.send(sender, messages.cpsStart.replace("%target%", targetPlayer.getName()).replace("%seconds%", Integer.toString((int) options.modeCpsTime / 20)), messages.prefixGeneral);

        new BukkitRunnable() {
            @Override
            public void run() {
                stopTest(sender, targetPlayer);
            }
        }.runTaskLater(StaffPlus.get(), options.modeCpsTime);
    }

    public void stopTest(CommandSender sender, Player targetPlayer) {
        UUID uuid = targetPlayer.getUniqueId();
        if (uuid == null)
            return;
        int cps = (int) (currentTests.get(uuid) / (options.modeCpsTime / 20));
        String message = cps > options.modeCpsMax ? messages.cpsFinishMax : messages.cpsFinishNormal;

        this.message.send(sender, message.replace("%player%", targetPlayer.getName()).replace("%cps%", Integer.toString(cps)), messages.prefixGeneral);
        currentTests.remove(uuid);
    }
}