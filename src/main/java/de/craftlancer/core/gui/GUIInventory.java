package de.craftlancer.core.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Deprecated
/**
 * Deprecated. Use de.craftlancer.core.menu.Menu
 */
public class GUIInventory implements InventoryHolder, Listener {
    
    private final Inventory inventory;
    private Map<Integer, Map<ClickType, Consumer<Player>>> clickActions = new HashMap<>();
    
    public GUIInventory(Plugin plugin, String title, int rows) {
        if (rows <= 0 || rows > 6)
            throw new IllegalArgumentException("Number of rows must be 1 and 6 (inclusive)");
        
        this.inventory = Bukkit.createInventory(this, rows * 9, title);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    public GUIInventory(Plugin plugin, int rows) {
        if (rows <= 0 || rows > 6)
            throw new IllegalArgumentException("Number of rows must be 1 and 6 (inclusive)");
        
        this.inventory = Bukkit.createInventory(this, rows * 9);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    public GUIInventory(Plugin plugin, String title) {
        this(plugin, title, 6);
    }
    
    public GUIInventory(Plugin plugin) {
        this(plugin, 6);
    }
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    
    public void fill(ItemStack item) {
        for (int i = 0; i < inventory.getSize(); i++)
            inventory.setItem(i, item != null ? item.clone() : null);
    }
    
    public void setItem(int slotId, ItemStack item) {
        if (slotId < 0 || slotId >= 54)
            return;
        
        inventory.setItem(slotId, item != null ? item.clone() : null);
    }
    
    public void setClickAction(int slotId, @Nonnull Runnable action) {
        setClickAction(slotId, action, null);
    }
    
    public void setClickAction(int slotId, @Nonnull Runnable action, ClickType clickType) {
        setClickAction(slotId, a -> action.run(), clickType);
    }
    
    public void setClickAction(int slotId, @Nonnull Consumer<Player> action) {
        setClickAction(slotId, action, null);
    }
    
    public void setClickAction(int slotId, @Nonnull Consumer<Player> action, ClickType clickType) {
        if (slotId < 0 || slotId >= 54)
            return;
        
        clickActions.computeIfAbsent(slotId, HashMap::new).put(clickType, action);
    }
    
    @EventHandler
    public void onInteract(InventoryClickEvent event) {
        InventoryAction action = event.getAction();
        
        if (event.getView().getTopInventory() == inventory)
            switch (action) {
                case COLLECT_TO_CURSOR:
                case MOVE_TO_OTHER_INVENTORY:
                case HOTBAR_MOVE_AND_READD:
                case HOTBAR_SWAP:
                    event.setCancelled(true);
                    break;
                default:
                    break;
            }
        
        if (event.getClickedInventory() != inventory)
            return;
        
        event.setCancelled(true);
        Map<ClickType, Consumer<Player>> map = clickActions.getOrDefault(event.getSlot(), Collections.emptyMap());
        map.getOrDefault(event.getClick(), map.getOrDefault(null, a -> {
        })).accept((Player) event.getWhoClicked());
    }
    
    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory() == inventory || event.getInventory() == inventory)
            event.setCancelled(true);
    }
}
