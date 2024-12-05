package net.z4te.reset;


import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TabCompleter implements org.bukkit.command.TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            if (args[0].length() == 0) {
                return Arrays.asList("start", "yes" ,"no");
            } else {
                if ("start".startsWith(args[0]) && "yes".startsWith(args[0])  && "no".startsWith(args[0])) {
                    return Arrays.asList("start", "yes" ,"no");
                } else if ("start".startsWith(args[0])) {
                    return Collections.singletonList("start");
                } else if ("yes".startsWith(args[0])) {
                    return Collections.singletonList("yes");
                } else if ("no".startsWith(args[0])) {
                    return Collections.singletonList("no");
                }
            }
        }
        return null;
    }
}
