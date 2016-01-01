package net.bubuxi.mc.binding;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Created by zekunshen on 1/1/16.
 */
public class BCommand implements CommandExecutor {

    private Binding plugin;
    private HashMap<String, Long> waitingToBind;

    BCommand(Binding b) {
        plugin = b;
        waitingToBind = new HashMap<>();
    }

    /*
    commands:   /binding help
                /binding bind
                /binding confirm
                /binding unbind
     */
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(command.getName().equalsIgnoreCase("binding")&&commandSender instanceof Player) {
            if(strings.length==1 &&strings[0].equalsIgnoreCase("bind")) {
                waitingToBind.put(commandSender.getName(), System.currentTimeMillis());
                Logger.sendMessage(commandSender.getName(), "&4请输入/binding confirm确认绑定");
                Logger.sendMessage(commandSender.getName(), "&4绑定后无法改名,附魔,丢出,和放入箱子");
                return false;
            }
            else if(strings.length==1 &&strings[0].equalsIgnoreCase("confirm")) {
                if(waitingToBind.containsKey(commandSender.getName())&&waitingToBind.get(commandSender.getName())
                        >System.currentTimeMillis()-60000L) {
                    Binding.bindCurrentItem((Player) commandSender);
                }
            }
            else if(strings.length==1 &&strings[0].equalsIgnoreCase("unbind")) {

            }
        }
        return false;
    }
}
