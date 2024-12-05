package net.z4te.reset;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Objects;


public final class Main extends JavaPlugin implements Listener {
    public static boolean gameOver = false;

    String motd = getConfig().getString("motd");
    String deathMessage = getConfig().getString("death-message");
    String displayMotd = motd + deathMessage;

    ArrayList<String> causeList = new ArrayList<>();

    @Override
    public void onEnable() {
        // Plugin startup logic

        if (motd == null) {
            getConfig().addDefault("motd","Edit motd in config.yml");
            getConfig().addDefault("death-message", "The most recent death message will appear here");
        } else {
            assert displayMotd != null;
            Bukkit.getServer().setMotd(displayMotd);
        }
        getServer().getPluginManager().registerEvents(this, this);
        Objects.requireNonNull(getCommand("poll")).setExecutor(new Poll());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        if (gameOver) {
            String causeKickScreen = String.join("\n", causeList);
            String causeLatest = causeList.get(causeList.size() - 1);

            Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer(ChatColor.RED + causeKickScreen));

            getConfig().set("death-message", causeLatest);
            Bukkit.getServer().setMotd(displayMotd);
            saveConfig();
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        // プレイヤーがハードコアモードで死んだ場合に実行
        if (getServer().isHardcore()) {
            String cause = event.getDeathMessage();

            // エンドで死んだ場合はstopしない
            if (event.getEntity().getWorld().getEnvironment() == World.Environment.THE_END){
                Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(ChatColor.YELLOW + "[Suspended] " + cause));
                causeList.add(cause);
            } else {
                causeList.add(cause);
                gameOver = true;
                Bukkit.shutdown();
            }
        }
    }
}
