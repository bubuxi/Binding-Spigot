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
                Logger.sendMessage(commandSender.getName(), "&6[绑定系统]&c请输入/binding confirm确认绑定");
                Logger.sendMessage(commandSender.getName(), "&6[绑定系统]&c绑定后无法改名,附魔,丢出,和放入箱子");
                return false;
            }
            else if(strings.length==1 &&strings[0].equalsIgnoreCase("confirm")) {
                if(waitingToBind.containsKey(commandSender.getName())&&waitingToBind.get(commandSender.getName())
                        >System.currentTimeMillis()-60000L) {
                    if(plugin.econ.getBalance(commandSender.getName())>plugin.money) {
                        if (Binding.bindCurrentItem((Player) commandSender)) {
                            Logger.sendMessage(commandSender.getName(), "&6[绑定系统]&a绑定成功, 当前物品攻击力+3");
                            plugin.econ.withdrawPlayer(commandSender.getName(), plugin.money);
                            return false;
                        } else {
                            Logger.sendMessage(commandSender.getName(), "&6[绑定系统]&c当前物品无法绑定");
                            return false;
                        }
                    }
                    Logger.sendMessage(commandSender.getName(), "&6[绑定系统]&c金钱不够");
                }
            }
            else if(strings.length==1 &&strings[0].equalsIgnoreCase("remove")) {
                Player sender = (Player)commandSender;
                if(Binding.isBinded(sender.getItemInHand())/*&&Binding.getBinder(sender.getItemInHand())
                        .equals(sender.getName())*/) {
                    sender.setItemInHand(null);
                    Logger.sendMessage(sender, "&6[绑定系统]&a绑定物品删除成功");
                }
            }
            else{
                Logger.sendMessage(commandSender.getName(), "&6[绑定系统]&2帮助信息");
                Logger.sendMessage(commandSender.getName(), "&2/binding help 打开帮助界面");
                Logger.sendMessage(commandSender.getName(), "&2/binding bind 绑定手持物品");
                Logger.sendMessage(commandSender.getName(), "&2/binding confirm 确认绑定手持物品");
                Logger.sendMessage(commandSender.getName(), "&2/binding remove 移除手持绑定物品");
                Logger.sendMessage(commandSender.getName(), "&c绑定物品需要20000金币!");

            }
        }
        return false;
    }
}
