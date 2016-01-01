package net.bubuxi.mc.binding;

import at.pcgamingfreaks.georgh.MinePacks.Backpack;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
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
                            Logger.info("1");
                            return;
                        }
                    }
                }
                //move to owner's inventory
                else if(e.getDestination() instanceof PlayerInventory) {
                    if(((PlayerInventory)e.getDestination()).getHolder() instanceof Player) {
                        Player dest = (Player)(((PlayerInventory) e.getDestination()).getHolder());
                        if(dest.getName().equals(Binding.getBinder(e.getItem()))) {
                            Logger.info("2");
                            return;
                        }
                    }
                }
                //move to MinePacks
                else if (e.getDestination().getTitle()!=null) {
                    Backpack backpack = plugin.minepacks.DB.getBackpack(e.getDestination().getTitle());
                    if(backpack!=null&&backpack.getOwner().getName().equals(Binding.getBinder(e.getItem()))) {
                        Logger.info("3");
                        return;
                    }
                }
            }
            //move from MinePacks
            else if(e.getSource()!=null&&e.getSource().getTitle()!=null) {
                Backpack backpack = plugin.minepacks.DB.getBackpack(e.getSource().getTitle());
                if(backpack!=null&&backpack.getOwner().getName().equals(Binding.getBinder(e.getItem()))) {
                    Logger.info("4");
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
        if(Binding.isBinded(e.getItemDrop().getItemStack())) {
            if(Binding.getBinder(e.getItemDrop().getItemStack()).equals(e.getPlayer().getName()))
                e.setCancelled(true);
        }
    }
    /*
    attack +3
     */
    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        if(e.getDamager() instanceof Player) {
            Player p = (Player)e.getDamager();
            if(Binding.isBinded(p.getItemInHand())&&Binding.getBinder(p.getItemInHand()).equals(p.getName())) {
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
                if (Binding.isBinded(is) && Binding.getBinder(is).equals(e.getEntity().getName())) {
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

    @EventHandler(priority = EventPriority.HIGH)
    public void onInv(InventoryClickEvent event) {
        Inventory top = event.getView().getTopInventory();
        Inventory bottom = event.getView().getBottomInventory();

        if(bottom.getType() == InventoryType.PLAYER
                &&event.getWhoClicked()instanceof Player){
            Player whoClicked = (Player)event.getWhoClicked();
            if(!whoClicked.isOp()) {
                if (event.getCurrentItem() != null && Binding.isBinded(event.getCurrentItem())) {
                    if(plugin.minepacks.DB.getBackpack(event.getInventory().getTitle())==null) {
                        if (event.getInventory().getTitle().equalsIgnoreCase("container.chest")) {
                            if (event.getRawSlot() > 26) {
                                event.setCancelled(true);
                            }
                        } else if (event.getInventory().getTitle().equalsIgnoreCase("container.chestDouble")) {
                            if (event.getRawSlot() > 53) {
                                event.setCancelled(true);
                            }
                        } else if (event.getInventory().getTitle().equalsIgnoreCase("container.enderchest")) {
                            if (event.getRawSlot() > 26) {
                                event.setCancelled(true);
                            }
                        } else if(event.getInventory().getTitle().equalsIgnoreCase("container.crafting")
                            &&event.getInventory().getType()==InventoryType.CRAFTING) {
                            //do nothing
                        }  else if (event.getInventory().getTitle().equalsIgnoreCase("container.furnace")) {
                            if (event.getRawSlot() > 2) {
                                event.setCancelled(true);
                            }
                        } else if (event.getInventory().getTitle().equalsIgnoreCase("enchant")) {
                            if (event.getRawSlot() > 1) {
                                event.setCancelled(true);
                            }
                        } else if (event.getInventory().getTitle().equalsIgnoreCase("repair")) {
                            if (event.getRawSlot() > 2) {
                                event.setCancelled(true);
                            }
                        } else if (event.getInventory().getTitle().equalsIgnoreCase("container.brewing")) {
                            if (event.getRawSlot() > 3) {
                                event.setCancelled(true);
                            }
                        } else if (event.getInventory().getTitle().equalsIgnoreCase("container.dispenser")) {
                            if (event.getRawSlot() > 8) {
                                event.setCancelled(true);
                            }
                        } else if (event.getInventory().getTitle().equalsIgnoreCase("container.dropper")) {
                            if (event.getRawSlot() > 8) {
                                event.setCancelled(true);
                            }
                        } else if (event.getInventory().getTitle().equalsIgnoreCase("container.hopper")) {
                            if (event.getRawSlot() > 4) {
                                event.setCancelled(true);
                            }
                        } else if (event.getInventory().getTitle().equalsIgnoreCase("container.beacon")) {
                            if (event.getRawSlot() > 0) {
                                event.setCancelled(true);
                            }
                        } else if (event.getInventory().getTitle().equalsIgnoreCase("container.crafting")
                                && event.getInventory().getType() == InventoryType.WORKBENCH) {
                            if (event.getRawSlot() > 9) {
                                event.setCancelled(true);
                            }
                        } else if (event.getInventory().getTitle().equalsIgnoreCase("mob.villager")) {
                            if (event.getRawSlot() > 2) {
                                event.setCancelled(true);
                            }
                        } else {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }


}
