package net.shortninja.staffplus.server.listener.player;

import net.shortninja.staffplus.StaffPlus;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public final class PlayerLogin implements Listener {

<<<<<<< HEAD
	public PlayerLogin() {
		Bukkit.getPluginManager().registerEvents(this, StaffPlus.get());
	}

	@EventHandler
	public void onLogin(PlayerLoginEvent e) { }
=======
    public PlayerLogin() {
        Bukkit.getPluginManager().registerEvents(this, StaffPlus.get());
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
    }
>>>>>>> b2eb803718fc6d2d09f3ef627210b17920278857
}
