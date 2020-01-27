package de.craftlancer.core;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import de.craftlancer.core.conversation.ConvoCommand;
import de.craftlancer.core.items.CustomItemRegistry;
import de.craftlancer.core.legacy.MassChestInventory;
import de.craftlancer.core.structure.BlockStructure;
import de.craftlancer.core.structure.CuboidArea;
import de.craftlancer.core.structure.Point2D;
import de.craftlancer.core.structure.Point3D;
import de.craftlancer.core.structure.RectangleArea;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class CLCore extends JavaPlugin {
    
    private static CLCore instance;
    
    /* Vault */
    private Economy econ = null;
    private Permission perms = null;
    private Chat chat = null;
    
    private CustomItemRegistry itemRegistry;
    
    @Override
    public void onEnable() {
        instance = this;
        
        ConfigurationSerialization.registerClass(RectangleArea.class);
        ConfigurationSerialization.registerClass(CuboidArea.class);
        ConfigurationSerialization.registerClass(Point2D.class);
        ConfigurationSerialization.registerClass(Point3D.class);
        ConfigurationSerialization.registerClass(MassChestInventory.class);
        ConfigurationSerialization.registerClass(BlockStructure.class);
        
        getCommand("convo").setExecutor(new ConvoCommand());
        
        itemRegistry = new CustomItemRegistry(this);
        setupChat();
        setupEconomy();
        setupPermissions();
    }
    
    @Override
    public void onDisable() {
        itemRegistry.save();
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
    
    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }
    
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
    
    public CustomItemRegistry getItemRegistry() {
        return itemRegistry;
    }
    
    public Economy getEconomy() {
        return econ;
    }
    
    public Permission getPermissions() {
        return perms;
    }
    
    public Chat getChat() {
        return chat;
    }
    
    public static CLCore getInstance() {
        return instance;
    }
    
}
