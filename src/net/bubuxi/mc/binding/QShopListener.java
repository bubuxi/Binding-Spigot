package net.bubuxi.mc.binding;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.maxgamer.QuickShop.Shop.ShopCreateEvent;

/**
 * Created by zekunshen on 1/9/16.
 */
public class QShopListener implements Listener{
    @EventHandler
    public void onCreate(ShopCreateEvent event) {
        if(Binding.isBinded(event.getShop().getItem())) {
            event.setCancelled(true);
        }
    }
}
