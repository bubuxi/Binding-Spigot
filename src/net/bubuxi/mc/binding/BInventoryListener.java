package net.bubuxi.mc.binding;

import at.pcgamingfreaks.georgh.MinePacks.Backpack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zekunshen on 1/1/16.
 */
public class BInventoryListener implements Listener {

    private Binding plugin;
    private HashMap<Player, List<ItemStack>> returnItems;
    private HashMap<String, Boolean> cache;

    BInventoryListener(Binding b) {
        plugin = b;
        returnItems = new HashMap<>();
    }

    /*
    control moving of binded item between inventory
     */
    @EventHandler
    public void onInventory(InventoryMoveItemEvent e) {
        if(e.getItem()!=null&&Binding.isBinded(e.getItem())) {
            if(e.getDestination()!=null) {
                //op move item to its inventory
                if (e.getDestination() instanceof PlayerInventory) {
                    if (((PlayerInventory) e.getDestination()).getHolder() instanceof Player) {
                        Player dest = (Player) (((PlayerInventory) e.getDestination()).getHolder());
                        if (dest.isOp()) {
                            return;
                        }
                    }
                }
                //move to owner's inventory
                else if(e.getDestination() instanceof PlayerInventory) {
                    if(((PlayerInventory)e.getDestination()).getHolder() instanceof Player) {
                        Player dest = (Player)(((PlayerInventory) e.getDestination()).getHolder());
                        //if(dest.getName().equals(Binding.getBinder(e.getItem()))) {
                            return;
                        //}
                    }
                }
                //move to MinePacks
                else if (e.getDestination().getTitle()!=null) {
                    Backpack backpack = plugin.minepacks.DB.getBackpack(e.getDestination().getTitle());
                    if(backpack!=null/*&&backpack.getOwner().getName().equals(Binding.getBinder(e.getItem()))*/) {
                        return;
                    }
                }
            }
            //move from MinePacks
            else if(e.getSource()!=null&&e.getSource().getTitle()!=null) {
                Backpack backpack = plugin.minepacks.DB.getBackpack(e.getSource().getTitle());
                if(backpack!=null/*&&backpack.getOwner().getName().equals(Binding.getBinder(e.getItem()))*/) {
                    return;
                }
            }
            e.setCancelled(true);
        }
    }

    /*
    make it impossible to throw one's binded item
     */
    @EventHandler
    public void onThrowItem(PlayerDropItemEvent e) {
        if(!e.getPlayer().isOp()) {
            if (Binding.isBinded(e.getItemDrop().getItemStack())) {
                //if(Binding.getBinder(e.getItemDrop().getItemStack()).equals(e.getPlayer().getName())) {
                e.setCancelled(true);
                Logger.sendMessage(e.getPlayer(), "&6[绑定系统]&c绑定物品无法丢出,如果要删除请手持物品输入/binding remove删除");
                //}
            }
        }
    }
    /*
    attack +3
     */
    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        if(e.getDamager() instanceof Player) {
            Player p = (Player)e.getDamager();
            if(Binding.isBinded(p.getItemInHand())/*&&Binding.getBinder(p.getItemInHand()).equals(p.getName())*/) {
                e.setDamage(e.getDamage()+3);
            }
        }
    }

    /*
    make it impossible to drop one's binded item
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent e) {
        if(!e.getKeepInventory()) {
            int counter = 0;
            boolean flag = false;
            List<ItemStack> il = new ArrayList<>();
            for(Iterator<ItemStack> isi= e.getDrops().iterator(); isi.hasNext();) {

                ItemStack is = isi.next();
                Logger.info("iterating");
                if (Binding.isBinded(is) /*&& Binding.getBinder(is).equals(e.getEntity().getName())*/) {
                    Logger.info("found");
                    //e.getEntity().getInventory().setItem(counter++,is.clone());
                    //e.getEntity().getInventory().addItem(is.clone());
                    il.add(is.clone());
                    isi.remove();
                    flag = true;
                }
            }
            if(flag) {
                returnItems.put(e.getEntity(), il);
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        if(returnItems.containsKey(e.getPlayer())) {
            for(ItemStack is: returnItems.get(e.getPlayer())) {
                e.getPlayer().getInventory().addItem(is);
            }
            returnItems.remove(e.getPlayer());
        }
    }

    @EventHandler
    public void onPutArmor(PlayerArmorStandManipulateEvent event) {
        if(event.getArmorStandItem()!=null&&Binding.isBinded(event.getArmorStandItem())) {
            event.setCancelled(true);
        }
        else if(event.getPlayerItem()!=null&&Binding.isBinded(event.getPlayerItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPutInFrame(PlayerInteractEntityEvent event) {
        if(event.getRightClicked().getType()== EntityType.ITEM_FRAME) {
            if (Binding.isBinded(event.getPlayer().getItemInHand())) {
                event.setCancelled(true);
            }
            if(Binding.isBinded(((ItemFrame)event.getRightClicked()).getItem())) {
                event.setCancelled(true);
            }
        }
    }

/*    @EventHandler
    public void blah(PlayerInteractEntityEvent event){

        Player player = event.getPlayer();

        Entity e = event.getRightClicked();

        if(e instanceof ItemFrame){

            player.sendMessage("You right clicked an item frame!");

        }

    }*/





    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInv(InventoryClickEvent event) {
        Inventory top = event.getView().getTopInventory();
        Inventory bottom = event.getView().getBottomInventory();
        if(event.getAction()==InventoryAction.HOTBAR_SWAP||event.getAction()==InventoryAction.HOTBAR_MOVE_AND_READD) {
            Player p = (Player)event.getWhoClicked();
            if(event.getHotbarButton()<p.getInventory().getSize()&&event.getHotbarButton()>-1
                    &&Binding.isBinded(p.getInventory().getItem(event.getHotbarButton()))) {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
            }


        }

        if(bottom.getType() == InventoryType.PLAYER
                &&event.getWhoClicked()instanceof Player){
            Player whoClicked = (Player)event.getWhoClicked();
            if(!whoClicked.isOp()) {
                if (event.getCurrentItem() != null && Binding.isBinded(event.getCurrentItem())) {
                    if(plugin.minepacks.DB.getBackpack(event.getInventory().getTitle())==null) {
                        if (event.getInventory().getTitle().equalsIgnoreCase("container.chest")) {
                            if (event.getRawSlot() > 26) {
                                if(needCancel("Chest")) {
                                    event.setCancelled(true);
                                    event.setResult(Event.Result.DENY);
                                }
                            }
                        } else if (event.getInventory().getTitle().equalsIgnoreCase("container.chestDouble")) {
                            if (event.getRawSlot() > 53) {
                                if(needCancel("DoubleChest")) {
                                    event.setCancelled(true);
                                    event.setResult(Event.Result.DENY);
                                }
                            }
                        }
                        else if (event.getInventory().getTitle().equalsIgnoreCase("container.enderchest")) {
                            if (event.getRawSlot() > 26) {
                                if(needCancel("EnderChest")) {
                                    event.setCancelled(true);
                                    event.setResult(Event.Result.DENY);
                                }
                            }
                        }
                        else if(event.getInventory().getTitle().equalsIgnoreCase("container.crafting")
                            &&event.getInventory().getType()==InventoryType.CRAFTING) {
                            if (event.getRawSlot() > 5) {
                                if(needCancel("Crafing")) {
                                    event.setCancelled(true);
                                    event.setResult(Event.Result.DENY);
                                }
                            }
                        }
                        else if (event.getInventory().getTitle().equalsIgnoreCase("container.furnace")) {
                            if (event.getRawSlot() > 2) {
                                if(needCancel("Furnace")) {
                                    event.setCancelled(true);
                                    event.setResult(Event.Result.DENY);
                                }
                            }
                        } else if (event.getInventory().getTitle().equalsIgnoreCase("enchant")) {
                            if (event.getRawSlot() > 1) {
                                if(needCancel("Enchant")) {
                                    event.setCancelled(true);
                                    event.setResult(Event.Result.DENY);
                                }
                            }
                        } else if (event.getInventory().getTitle().equalsIgnoreCase("repair")) {
                            if (event.getRawSlot() > 2) {
                                if(needCancel("Repair")) {
                                    event.setCancelled(true);
                                    event.setResult(Event.Result.DENY);
                                }
                            }
                        } else if (event.getInventory().getTitle().equalsIgnoreCase("container.brewing")) {
                            if (event.getRawSlot() > 3) {
                                if(needCancel("Brewing")) {
                                    event.setCancelled(true);
                                    event.setResult(Event.Result.DENY);
                                }
                            }
                        } else if (event.getInventory().getTitle().equalsIgnoreCase("container.dispenser")) {
                            if (event.getRawSlot() > 8) {
                                if(needCancel("Dispenser")) {
                                    event.setCancelled(true);
                                    event.setResult(Event.Result.DENY);
                                }
                            }
                        } else if (event.getInventory().getTitle().equalsIgnoreCase("container.dropper")) {
                            if (event.getRawSlot() > 8) {
                                if(needCancel("Dropper")) {
                                    event.setCancelled(true);
                                    event.setResult(Event.Result.DENY);
                                }
                            }
                        } else if (event.getInventory().getTitle().equalsIgnoreCase("container.hopper")) {
                            if (event.getRawSlot() > 4) {
                                if(needCancel("Hopper")) {
                                    event.setCancelled(true);
                                    event.setResult(Event.Result.DENY);
                                }
                            }
                        } else if (event.getInventory().getTitle().equalsIgnoreCase("container.beacon")) {
                            if (event.getRawSlot() > 0) {
                                if(needCancel("Beacon")) {
                                    event.setCancelled(true);
                                    event.setResult(Event.Result.DENY);
                                }
                            }
                        } else if (event.getInventory().getTitle().equalsIgnoreCase("container.crafting")
                                && event.getInventory().getType() == InventoryType.WORKBENCH) {
                            if (event.getRawSlot() > 9) {
                                if(needCancel("Workbench")) {
                                    event.setCancelled(true);
                                    event.setResult(Event.Result.DENY);
                                }
                            }
                        } else if (event.getInventory().getTitle().equalsIgnoreCase("mob.villager")) {
                            if (event.getRawSlot() > 1) {
                                if(needCancel("Villager")) {
                                    event.setCancelled(true);
                                    event.setResult(Event.Result.DENY);
                                }
                            }
                        } else {
                            if(needCancel("Other")) {
                                event.setCancelled(true);
                                event.setResult(Event.Result.DENY);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean needCancel(String str) {
        if(!cache.containsKey(str)) {
            cache.put(str, !plugin.getConfig().getBoolean("Container.".concat(str)));
        }
        return cache.get(str);
    }


}
