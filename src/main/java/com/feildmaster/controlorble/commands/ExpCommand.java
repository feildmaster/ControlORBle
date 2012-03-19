package com.feildmaster.controlorble.commands;

import com.feildmaster.lib.expeditor.Editor;
import com.feildmaster.controlorble.JavaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExpCommand implements CommandExecutor {
    private JavaPlugin plugin;

    public ExpCommand(JavaPlugin p) {
        plugin = p;
        plugin.debug("Registering /exp commands");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equals("exp-reload")) {
            return onReloadCommand(sender);
        }

        int size = args.length;
        if(size == 0) return yourLevel(sender);
        if(size > 2) return invalidCommand(sender, label);

        if(!sender.hasPermission("orbEnhance.admin")) return noPermission(sender);

        Player p1 = null;
        Integer exp = null;
        String format = "Your experience was set to %1$s";
        String format2 = "You have been given %1$s experience";
        String parse = null;

        if(size == 1 && sender instanceof Player) {
            p1 = (Player)sender;
            parse = args[0];
        } else if (size == 2) {
            p1 = sender.getServer().getPlayer(args[0]);
            parse = args[1];
        } else {
            return invalidCommand(sender, label);
        }

        if(p1==null) return playerNotFound(sender);

        Editor p2 = new Editor(p1);

        int old_exp = p2.getExp();
        int old_lvl = p2.getLevel();

        try {
            String n = parse;
            if(parse.startsWith("+")) {
                n = parse.substring(1);
            }
            exp = Integer.parseInt(n);
        } catch (NumberFormatException n) {
            return notANumber(sender);
        }

        if(parse.startsWith("+")||parse.startsWith("-")) {
            format = String.format(format2, exp.toString());
            // Is this supposed to be here...?
            //sender.sendMessage("Exp: "+ exp + " + " + p2.getTotalExp(true) + " = " + (exp+p2.getTotalExp()));
            exp += p2.getTotalExp(true);
        } else {
            format = String.format(format, exp.toString());
        }

        p2.setExp(exp);

        // Sender console, or player is a different player
        if(!(sender instanceof Player) || !((Player)sender).equals(p1)) {
            p1.sendMessage(plugin.format(ChatColor.YELLOW,format));
        }

        sender.sendMessage(plugin.format("Player experience changed from "+old_lvl+"/"+old_exp+" to "+p2.getLevel()+"/"+p2.getExp()));
        return true;
    }

    private boolean onReloadCommand(CommandSender sender) {
        if(!sender.hasPermission("orbEnhance.admin")) return noPermission(sender);
        plugin.getConfig().load();
        plugin.checkConfig();
        sender.sendMessage(plugin.format("Reload Complete"));
        return true;
    }

    private boolean noPermission(CommandSender sender) {
        sender.sendMessage(plugin.format("You do not have permission to do that!"));
        return true;
    }
    private boolean playerNotFound(CommandSender sender) {
        sender.sendMessage(plugin.format("Player not found"));
        return true;
    }
    private boolean invalidCommand(CommandSender sender, String label) {
        sender.sendMessage(plugin.format("Invalid syntax"));
        sender.sendMessage(plugin.format("/"+label+" [player] [+|-]{exp}"));
        return true;
    }
    private boolean notANumber(CommandSender sender) {
        sender.sendMessage(plugin.format("Not a number"));
        return true;
    }

    private boolean yourLevel(CommandSender sender) {
        if(sender instanceof Player) {
            Editor p = new Editor((Player)sender);
            int level = p.getLevel();

            sender.sendMessage(plugin.format("Your level: "+level));
            sender.sendMessage(plugin.format("Experience: "+p.getExp()+"/"+p.getExpToLevel()));

            if(plugin.getConfig().getBoolean("config.showTotal")) {
                sender.sendMessage(plugin.format("Total Exp : "+p.getTotalExp(true)));
            }
        } else
            invalidCommand(sender, "exp");
        return true;
    }
}
