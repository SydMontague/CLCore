package de.craftlancer.core.items;

import de.craftlancer.core.CLCore;
import de.craftlancer.core.command.CommandHandler;

public class CustomItemCommandHandler extends CommandHandler {
    
    private CLCore plugin;
    private CustomItemRegistry registry;
    
    public CustomItemCommandHandler(CLCore plugin, String name, CustomItemRegistry registry) {
        super(plugin, name);
        
        this.plugin = plugin;
        this.registry = registry;
    }
    
    @Override
    public void registerSubCommands() {
        registerSubCommand("add", new CustomItemAddCommand(plugin, registry));
        registerSubCommand("remove", new CustomItemRemoveCommand(plugin, registry));
        registerSubCommand("list", new CustomItemListCommand(plugin, registry));
        registerSubCommand("give", new CustomItemGiveCommand(plugin, registry));
    }
    
}
