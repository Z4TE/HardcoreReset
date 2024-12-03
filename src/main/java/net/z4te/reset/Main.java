package net.z4te.reset;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;


public final class Main extends JavaPlugin implements Listener {

    String motd = getConfig().getString("motd");
    String deathMessage = getConfig().getString("death-message");
    String displayMotd = motd + deathMessage;

    @Override
    public void onEnable() {
        // Plugin startup logic

        if (motd == null) {
            // Set the default motd
            Bukkit.getServer().setMotd(ChatColor.WHITE + "Edit motd in config.yml");
        } else {
            assert displayMotd != null;
            Bukkit.getServer().setMotd(displayMotd);
        }

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // Execute when a player dies in hardcore mode

        if (getServer().isHardcore()) {
            String cause = event.getDeathMessage();
            Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer(ChatColor.RED + cause));
            if (cause != null) {
                getConfig().set("death-message", cause);
                Bukkit.getServer().setMotd(displayMotd);
                saveConfig();
            }
            // stop
            Bukkit.shutdown();
        }
    }


}
