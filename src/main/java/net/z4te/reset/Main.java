package net.z4te.reset;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;


public final class Main extends JavaPlugin implements Listener {

    String motd = getConfig().getString("motd");
    String deathMessage = getConfig().getString("death-message");
    String displayMotd = motd + deathMessage;

    ArrayList<String> causeList = new ArrayList<>();

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

            if (isEveryoneInTheEnd() || isEveryoneAlive()){
                Bukkit.broadcastMessage(ChatColor.YELLOW + "[Suspended] " + cause);
                causeList.add(cause + "\n");
            } else {
                causeList.add(cause);
                shutdownSequence(causeList);
            }
        }
    }

    private boolean isEveryoneInTheEnd() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            // Abort unless all players are in the end or dead
            if (player.getWorld().getEnvironment() != World.Environment.THE_END) {
                return false;
            }
        }
        return true;
    }

    private boolean isEveryoneAlive() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            // Abort unless all players are in the end or dead
            if (player.getHealth() == 0) {
                return false;
            }
        }
        return true;
    }

    private void shutdownSequence(ArrayList<String> causes) {

        String causeKickScreen = causes.toString();
        String causeLatest = causes.get(causes.size() - 1);

        Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer(ChatColor.RED + causeKickScreen));
        if (causeKickScreen != null) {
            getConfig().set("death-message", causeLatest);
            Bukkit.getServer().setMotd(displayMotd);
            saveConfig();
        }
        // stop
        Bukkit.shutdown();
    }


}
