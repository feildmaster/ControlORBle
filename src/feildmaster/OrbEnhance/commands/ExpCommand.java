package feildmaster.OrbEnhance.commands;

import feildmaster.OrbEnhance.plugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExpCommand implements CommandExecutor {
    private plugin Plugin;

    public ExpCommand(plugin p) {
        Plugin = p;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        int size = args.length;
        if(size == 0) return yourLevel(sender);
        if(size > 2) return invalidCommand(sender, label);

        if(!sender.hasPermission("orbEnhance.admin")) return noPermission(sender);

        Player p = null;
        Integer exp = null;
        String format = "Your experience was set to %1$s";
        String format2 = "You have been given %1$s experience";
        String parse = null;

        if(size == 1 && sender instanceof Player) {
            p = (Player)sender;
            parse = args[0];
        } else if (size == 2) {
            p = sender.getServer().getPlayer(args[0]);
            parse = args[1];
        } else
            return invalidCommand(sender, label);

        if(p==null) return playerNotFound(sender);

        int old_exp = p.getExperience();
        int old_lvl = p.getLevel();

        try {
            exp = Integer.parseInt(parse);
        } catch (NumberFormatException n) {
            return notANumber(sender);
        }

        if(parse.startsWith("+")||parse.startsWith("-")) {
            format = String.format(format2, exp.toString());
            exp += p.getTotalExperience();
        } else
            format = String.format(format, exp.toString());

        p.setLevel(0);
        p.setExperience(0);
        p.setTotalExperience(0);
        p.setExperience(exp);

        // Sender console, or player is a different player
        if(!(sender instanceof Player) || !((Player)sender).equals(p))
            p.sendMessage(Plugin.format(ChatColor.YELLOW,format));

        sender.sendMessage(Plugin.format("Player experience changed from "+old_lvl+"/"+old_exp+" to "+p.getLevel()+"/"+p.getExperience()));
        return true;
    }

    private boolean noPermission(CommandSender sender) {
        sender.sendMessage(Plugin.format("You do not have permission to do that!"));
        return true;
    }
    private boolean playerNotFound(CommandSender sender) {
        sender.sendMessage(Plugin.format("Player not found"));
        return true;
    }
    private boolean invalidCommand(CommandSender sender, String label) {
        sender.sendMessage(Plugin.format("Invalid syntax"));
        sender.sendMessage(Plugin.format("/"+label+" [player] [+|-]{exp}"));
        return true;
    }
    private boolean notANumber(CommandSender sender) {
        sender.sendMessage(Plugin.format("Not a number"));
        return true;
    }

    private boolean yourLevel(CommandSender sender) {
        if(sender instanceof Player) {
            Player p = (Player)sender;
            int level = p.getLevel();

            sender.sendMessage(Plugin.format("Your level: "+level));
            sender.sendMessage(Plugin.format("Experience: "+p.getExperience()+"/"+(level*10+10)));
            if(Plugin.showTotal)
                sender.sendMessage(Plugin.format("Total Exp : "+p.getTotalExperience()));
        }
        return true;
    }
}
