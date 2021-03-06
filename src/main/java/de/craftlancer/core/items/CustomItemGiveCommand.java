package de.craftlancer.core.items;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.craftlancer.core.CLCore;
import de.craftlancer.core.Utils;
import de.craftlancer.core.command.SubCommand;

public class CustomItemGiveCommand extends SubCommand {

    private CustomItemRegistry registry;
    
    public CustomItemGiveCommand(CLCore plugin, CustomItemRegistry registry) {
        super("clcore.itemregistry.give", plugin, true);
        this.registry = registry;
    }
    
    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if(!checkSender(sender))
            return "You can't run this command.";
        
        if(args.length < 3)
            return "You must specify a player and an item name.";
        
        Player player = Bukkit.getPlayer(args[1]);
        Optional<ItemStack> item = registry.getItem(args[2]);
        int amount = args.length >= 4 ? Utils.parseIntegerOrDefault(args[3], 1) : 1;
        
        if(player == null)
            return "Player not found.";
        if(!item.isPresent())
            return "Item not found.";
        
        ItemStack localItem = item.get();
        localItem.setAmount(amount);
        player.getInventory().addItem(localItem).forEach((a,b) -> player.getWorld().dropItem(player.getLocation(), b));
        return String.format("Item %s given to player %s.", args[2], player.getName());
    }
    
    @Override
    public void help(CommandSender sender) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if(args.length == 2)
            return Utils.getMatches(args[1], Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
        if(args.length == 3)
            return Utils.getMatches(args[2], registry.getKeys());
        
        return super.onTabComplete(sender, args);
    }
}
