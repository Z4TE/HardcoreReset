package net.z4te.reset;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import org.bukkit.World;
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

        // プレイヤーがハードコアモードで死んだ場合に実行
        if (getServer().isHardcore()) {
            String cause = event.getDeathMessage();

            // 死者がエンドにいた場合はstopしない
            if (event.getEntity().getWorld().getEnvironment() == World.Environment.THE_END){
                Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(ChatColor.YELLOW + "[Suspended] " + cause));
                causeList.add(cause);
            } else {
                causeList.add(cause);
                shutdownSequence(causeList);
            }
        }
    }

    private void shutdownSequence(ArrayList<String> causes) {

        String causeKickScreen = String.join("\n", causes);
        String causeLatest = causes.get(causes.size() - 1);

        Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer(ChatColor.RED + causeKickScreen));

        getConfig().set("death-message", causeLatest);
        Bukkit.getServer().setMotd(displayMotd);
        saveConfig();

        // stop
        Bukkit.shutdown();
    }


}
