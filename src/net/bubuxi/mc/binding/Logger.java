package net.bubuxi.mc.binding;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by zekunshen on 1/1/16.
 */
public class Logger {
    public static int debugLevel = -1;

    public static void sendMessage(Player player, String msg) {
        String [] list = msg.split(";");
        for(String s : list) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
        }
    }
    public static void sendMessage(String name, String msg) {
        Bukkit.getPlayer(name).sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

    public static void debug(String msg, int level) {
        if(level<=debugLevel) {
            System.out.println("Level-"+level+": "+msg);
        }
    }

    public static void warning(String msg) {
        Bukkit.getLogger().warning(msg);
    }

    public static void info(String msg) {
        Bukkit.getLogger().info(msg);
    }
}
