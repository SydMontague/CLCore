package de.craftlancer.core.items;

import de.craftlancer.core.CLCore;
import de.craftlancer.core.command.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CustomItemAddCommand extends SubCommand {
    
    private CustomItemRegistry registry;
    
    public CustomItemAddCommand(CLCore plugin, CustomItemRegistry registry) {
        super("clcore.itemregistry.add", plugin, false);
        this.registry = registry;
    }
    
    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if (!checkSender(sender))
            return "You can't run this command.";
        
        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        
        if (item.getType().isAir())
            return "You must hold an item.";
        if (args.length < 2)
            return "You must specify a key.";
        
        String key = args[1];
        if (!key.matches("[a-zA-Z0-9_]+"))
            return "Key must match [a-zA-Z0-9_]+";
        
        if (registry.addItem(key, item) == null)
            return "Item successfully added.";
        else
            return "Item successfully replaced.";
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Arrays.asList("<key>");
        
        return Collections.emptyList();
    }
    
    @Override
    public void help(CommandSender sender) {
        // TODO Auto-generated method stub
        
    }
}
