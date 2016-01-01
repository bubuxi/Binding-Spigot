package net.bubuxi.mc.binding;

import at.pcgamingfreaks.georgh.MinePacks.MinePacks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zekunshen on 1/1/16.
 */
public class Binding extends JavaPlugin {

    static String loreName = "绑定者:";
    public MinePacks minepacks;

    public void onEnable() {
        Bukkit.getPluginManager().registerEvents( new BInventoryListener(this), this);
        this.getCommand("binding").setExecutor(new BCommand(this));
        minepacks = (MinePacks) Bukkit.getPluginManager().getPlugin("MinePacks");
    }

    public void onDisable() {

    }

    static boolean bindCurrentItem(Player p) {
        ItemStack is = p.getItemInHand();
        if(is!=null) {
            ItemMeta im = is.getItemMeta();
            List<String> lore = new ArrayList<>();
            if (im.hasLore()) {
                lore = im.getLore();
            }
            lore.add(ChatColor.translateAlternateColorCodes('&', loreName + p.getName()));
            im.setLore(lore);
            is.setItemMeta(im);
            return true;
        }
        return false;
    }
    static boolean isBinded(ItemStack is) {
        if(is.hasItemMeta()&&is.getItemMeta().hasLore()) {
            for(String s: is.getItemMeta().getLore()) {
                if(s.startsWith(loreName)) return true;
            }
        }
        return false;
    }
    static String getBinder(ItemStack is) {
        if(is.hasItemMeta()&&is.getItemMeta().hasLore()) {
            for(String s: is.getItemMeta().getLore()) {
                if(s.startsWith(loreName)) return s.substring(loreName.length());
            }
        }
        return "";
    }
}
