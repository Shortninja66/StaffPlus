package net.shortninja.staffplus.player.attribute.mode;

import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.player.User;
import net.shortninja.staffplus.player.UserManager;
import net.shortninja.staffplus.player.attribute.InventorySerializer;
import net.shortninja.staffplus.player.attribute.mode.handler.VanishHandler;
import net.shortninja.staffplus.player.attribute.mode.handler.VanishHandler.VanishType;
import net.shortninja.staffplus.player.attribute.mode.item.ModeItem;
import net.shortninja.staffplus.player.attribute.mode.item.ModuleConfiguration;
import net.shortninja.staffplus.server.data.config.Messages;
import net.shortninja.staffplus.server.data.config.Options;
import net.shortninja.staffplus.util.MessageCoordinator;
import net.shortninja.staffplus.util.lib.JavaUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


import java.util.*;

public class ModeCoordinator {
    private static Map<UUID, ModeDataVault> modeUsers = new HashMap<UUID, ModeDataVault>();
    private MessageCoordinator message = StaffPlus.get().message;
    private Options options = StaffPlus.get().options;
    public final ModeItem[] MODE_ITEMS =
            {
                    new ModeItem("compass", options.modeCompassItem, options.modeCompassSlot, options.modeCompassEnabled),
                    new ModeItem("randomTeleport", options.modeRandomTeleportItem, options.modeRandomTeleportSlot, options.modeRandomTeleportEnabled),
                    new ModeItem("vanish", options.modeVanishItem, options.modeVanishSlot, options.modeVanishEnabled),
                    new ModeItem("guiHub", options.modeGuiItem, options.modeGuiSlot, options.modeGuiEnabled),
                    new ModeItem("counter", options.modeCounterItem, options.modeCounterSlot, options.modeCounterEnabled),
                    new ModeItem("freeze", options.modeFreezeItem, options.modeFreezeSlot, options.modeFreezeEnabled),
                    new ModeItem("cps", options.modeCpsItem, options.modeCpsSlot, options.modeCpsEnabled),
                    new ModeItem("examine", options.modeExamineItem, options.modeExamineSlot, options.modeExamineEnabled),
                    new ModeItem("follow", options.modeFollowItem, options.modeFollowSlot, options.modeFollowEnabled),
            };
    private Messages messages = StaffPlus.get().messages;
    private UserManager userManager = StaffPlus.get().userManager;
    private VanishHandler vanishHandler = StaffPlus.get().vanishHandler;

    public Set<UUID> getModeUsers() {
        return modeUsers.keySet();
    }

    public boolean isInMode(UUID uuid) {
        return modeUsers.containsKey(uuid);
    }

    public void addMode(Player player) {
        UUID uuid = player.getUniqueId();
        User user = userManager.get(uuid);
        ModeDataVault modeData;
        if(!StaffPlus.get().nineteenPlus)
            modeData = new ModeDataVault(uuid, getContents(player), player.getInventory().getArmorContents(),
                    player.getLocation(), player.getAllowFlight(), player.getGameMode(), user.getVanishType());
        else
            modeData = new ModeDataVault(uuid, getContents(player), player.getInventory().getArmorContents(), player.getInventory().getExtraContents(),
                    player.getLocation(), player.getAllowFlight(), player.getGameMode(), user.getVanishType());
        if (isInMode(player.getUniqueId())) {
            return;
        }
        JavaUtils.clearInventory(player);
        modeUsers.put(uuid, modeData);
        setPassive(player, user);
        message.send(player, messages.modeStatus.replace("%status%", "enabled"), messages.prefixGeneral);
    }

    public void removeMode(Player player) {
        System.out.println(isInMode(player.getUniqueId())+"");
        if (!isInMode(player.getUniqueId())) {
            return;
        }

        unsetPassive(player);
        modeUsers.remove(player.getUniqueId());
        message.send(player, messages.modeStatus.replace("%status%", "disabled"), messages.prefixGeneral);
    }

    private void setPassive(Player player, User user) {
        if (options.modeFlight && !options.modeCreative) {
            player.setAllowFlight(true);
        } else if (options.modeCreative) {
            player.setGameMode(GameMode.CREATIVE);
        }

        runModeCommands(player.getName(), true);
        vanishHandler.addVanish(player, options.modeVanish);

        for (ModeItem modeItem : MODE_ITEMS) {
            if (!modeItem.isEnabled()) {
                continue;
            }

            if (modeItem.getIdentifier().equals("vanish")) {
                modeItem.setItem(user.getVanishType() == options.modeVanish ? options.modeVanishItem : options.modeVanishItemOff);
            }

            player.getInventory().setItem(modeItem.getSlot(), StaffPlus.get().versionProtocol.addNbtString(modeItem.getItem(), modeItem.getIdentifier()));
        }

        for (ModuleConfiguration moduleConfiguration : options.moduleConfigurations.values()) {
            player.getInventory().setItem(moduleConfiguration.getSlot(), StaffPlus.get().versionProtocol.addNbtString(moduleConfiguration.getItem(), moduleConfiguration.getIdentifier()));
        }
    }

    private void unsetPassive(Player player) {
        UUID uuid = player.getUniqueId();
        ModeDataVault modeData = modeUsers.get(uuid);
        InventorySerializer saver = new InventorySerializer(player.getUniqueId());

        if (options.modeOriginalLocation) {
            player.teleport(modeData.getPreviousLocation().setDirection(player.getLocation().getDirection()));
            message.send(player, messages.modeOriginalLocation, messages.prefixGeneral);
        }

        runModeCommands(player.getName(), false);
        getItems(player,saver);
        player.getInventory().setArmorContents(saver.getArmor());
        if(StaffPlus.get().nineteenPlus)
            player.getInventory().setExtraContents(saver.getOffHand());
        saver.deleteFile();
        player.updateInventory();
        player.setAllowFlight(modeData.hasFlight());
        player.setGameMode(modeData.getGameMode());

        if (modeData.getVanishType() == VanishType.NONE) {
            vanishHandler.removeVanish(player);
        } else vanishHandler.addVanish(player, modeData.getVanishType());
    }

    private void runModeCommands(String name, boolean isEnabled) {
        for (String command : isEnabled ? options.modeEnableCommands : options.modeDisableCommands) {
            if (command.isEmpty()) {
                continue;
            }

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", name));
        }
    }

    public static HashMap<String, ItemStack> getContents(Player p) {
        ArrayList<ItemStack> itemStacks = new ArrayList<>();
        HashMap<String, ItemStack> itemHash = new HashMap<>();
        for (int i = 0; i <= 35; i++) {
            if (p.getInventory().getItem(i) != null) {
                itemStacks.add(p.getInventory().getItem(i));
                itemHash.put(String.valueOf(i), p.getInventory().getItem(i));
            }
        }
        return itemHash;
    }

    private void getItems(Player p, InventorySerializer saver){
        HashMap<String, ItemStack> items = saver.getContents();
        for(String num : items.keySet())
            p.getInventory().setItem(Integer.parseInt(num),items.get(num));
    }

}