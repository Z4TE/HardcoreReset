package net.z4te.reset;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;


public final class Main extends JavaPlugin implements Listener {

    String displayMotd = getConfig().getString("motd") + getConfig().getString("death-message");

    @Override
    public void onEnable() {
        // Plugin startup logic
        assert displayMotd != null;
        Bukkit.getServer().setMotd(displayMotd);

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        String cause = event.getDeathMessage();
        Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer(ChatColor.RED + cause));

        if (getServer().isHardcore()) {
            if (cause != null) {
                getConfig().set("death-message", cause);
                Bukkit.getServer().setMotd(displayMotd);
                saveConfig();
            }
            Bukkit.shutdown();
        }
    }


}
