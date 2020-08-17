package de.craftlancer.core.items;

import de.craftlancer.core.CLCore;
import de.craftlancer.core.command.CommandHandler;

public class CustomItemCommandHandler extends CommandHandler {

    public CustomItemCommandHandler(CLCore plugin, String label, CustomItemRegistry registry) {
        super(plugin, label);
        
        registerSubCommand("add", new CustomItemAddCommand(plugin, getLabel() + "add", this, registry));
        registerSubCommand("remove", new CustomItemRemoveCommand(plugin, getLabel() + "remove", this, registry));
        registerSubCommand("list", new CustomItemListCommand(plugin, getLabel() + "list", this, registry));
        registerSubCommand("give", new CustomItemGiveCommand(plugin, getLabel() + "give", this, registry));
    }
    
}
