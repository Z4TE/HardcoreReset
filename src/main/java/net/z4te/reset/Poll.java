package net.z4te.reset;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.plugin.Plugin;

public class Poll implements CommandExecutor {

    private final Set<Player> yes = new HashSet<>();
    private final Set<Player> no = new HashSet<>();
    private boolean isActive = false;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equalsIgnoreCase("poll")) {

            // プレイヤーのみが投票可能
            if (!(sender instanceof Player)) {
                sender.sendMessage("");
                return true;
            }
            Player player = (Player) sender;

            // すでに投票中の場合は開始しない
            if (isActive) {
                player.sendMessage("");
                return true;
            }

            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "Too few arguments");
                return true;
            }

            if (args[0].equalsIgnoreCase("start")) {

                isActive = true;
                yes.clear();
                no.clear();

                TextComponent message = new TextComponent("");

                TextComponent yes = new TextComponent("[YES]");
                yes.setColor(ChatColor.GREEN);
                yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click here to select YES").create()));
                yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vote yes"));

                TextComponent no = new TextComponent("[NO]");
                no.setColor(ChatColor.RED);
                no.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click here to select NO").create()));
                no.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vote no"));

                message.addExtra(yes);
                message.addExtra(" ");
                message.addExtra(no);

                Bukkit.getOnlinePlayers().forEach(players -> player.spigot().sendMessage(message));

                Bukkit.getScheduler().runTaskLater((Plugin) this, this::endPoll, 20 * 30);
            }

            if (args[0].equalsIgnoreCase("yes")) {
                yes.add(player);
                player.sendMessage("Successfully submitted your vote : [YES]");
            } else {
                no.add(player);
                player.sendMessage("Successfully submitted your vote : [NO]");
            }
            return true;
        }
        return false;
    }

    private void endPoll() {
        isActive = false;

        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        int requiredVotes = (int) Math.ceil((2.0 / 3.0) * onlinePlayers);

        TextComponent resultYes = new TextComponent("[Yes] " + yes.size());
        TextComponent resultNo = new TextComponent("[No] " + no.size());

        if (yes.size() > requiredVotes) {

            resultYes.setColor(ChatColor.WHITE);
            resultYes.setUnderlined(true);
            resultNo.setColor(ChatColor.GRAY);

            String result = resultYes + " > " + resultNo;

            String messageYes = ChatColor.YELLOW + "More than 2/3 of you have agreed to end the game. The server will be shut down.";

            Bukkit.broadcastMessage(result + "\n" + messageYes);
            Main.gameOver = true;
            Bukkit.shutdown();
        }else {

            resultYes.setColor(ChatColor.GRAY);
            resultNo.setColor(ChatColor.WHITE);
            resultNo.setUnderlined(true);

            String result = resultYes + " < " + resultNo;

            String messageNo = ChatColor.YELLOW + "The server shutdown has been rejected.";

            Bukkit.broadcastMessage(result + "\n" + messageNo);
        }

    }

}
