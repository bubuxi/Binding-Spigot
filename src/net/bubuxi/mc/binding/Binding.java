package net.bubuxi.mc.binding;

import at.pcgamingfreaks.georgh.MinePacks.MinePacks;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zekunshen on 1/1/16.
 */
public class Binding extends JavaPlugin {

    static String loreName;
    public MinePacks minepacks;
    public double money;
    Economy econ;

    public void onEnable() {
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents( new BInventoryListener(this), this);
        if(Bukkit.getPluginManager().getPlugin("QuickShop")!=null)
            Bukkit.getPluginManager().registerEvents(new QShopListener(), this);
        this.getCommand("binding").setExecutor(new BCommand(this));
        minepacks = (MinePacks) Bukkit.getPluginManager().getPlugin("MinePacks");
        money = getConfig().getDouble("money");
        loreName = getConfig().getString("LoreName");
        setupEconomy();
    }

    public void onDisable() {

    }

    static boolean bindCurrentItem(Player p) {
        ItemStack is = p.getItemInHand();
        if(!isBinded(is)) {
            if (is != null) {
                ItemMeta im = is.getItemMeta();
                if(im!=null) {
                    List<String> lore = new ArrayList<>();
                    if (im.hasLore()) {
                        lore = im.getLore();
                    }
                    lore.add(ChatColor.translateAlternateColorCodes('&', loreName));
                    im.setLore(lore);
                    is.setItemMeta(im);
                    return true;
                }
            }
        }
        return false;
    }
    public static boolean isBinded(ItemStack is) {
        if(is!=null&&is.hasItemMeta()&&is.getItemMeta().hasLore()) {
            for(String s: is.getItemMeta().getLore()) {
                if(s.startsWith(ChatColor.translateAlternateColorCodes('&', loreName))) return true;
            }
        }
        return false;
    }
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    /*static String getBinder(ItemStack is) {
        if(is.hasItemMeta()&&is.getItemMeta().hasLore()) {
            for(String s: is.getItemMeta().getLore()) {
                if(s.startsWith(loreName)) return s.substring(loreName.length());
            }
        }
        return "";
    }*/
}
