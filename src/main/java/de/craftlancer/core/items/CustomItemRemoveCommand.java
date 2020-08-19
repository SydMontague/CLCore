package de.craftlancer.core.items;

import de.craftlancer.core.CLCore;
import de.craftlancer.core.Utils;
import de.craftlancer.core.command.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CustomItemRemoveCommand extends SubCommand {
    
    private CustomItemRegistry registry;
    
    public CustomItemRemoveCommand(CLCore plugin, CustomItemRegistry registry) {
        super("clcore.itemregistry.remove", plugin, true);
        this.registry = registry;
    }
    
    @Override
    public String[] getArgs() {
        return new String[0];
    }
    
    @Override
    public String getDescription() {
        return null;
    }
    
    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if (!checkSender(sender))
            return "You can't run this command.";
        
        if (args.length < 2)
            return "You must specify a key.";
        
        String key = args[1];
        if (!key.matches("[a-zA-Z0-9_]+"))
            return "Key must match [a-zA-Z0-9_]+";
        if (!registry.hasItem(key))
            return "This key is not in use.";
        
        if (registry.removeItem(key))
            return "Item successfully removed.";
        else
            return "Item couldn't be removed.";
    }
    
    @Override
    public void help(CommandSender sender) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Utils.getMatches(args[1], registry.getKeys());
        
        
        return super.onTabComplete(sender, args);
    }
    
}
