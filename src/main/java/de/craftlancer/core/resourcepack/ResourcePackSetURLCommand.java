package de.craftlancer.core.resourcepack;

import de.craftlancer.core.CLCore;
import de.craftlancer.core.command.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class ResourcePackSetURLCommand extends SubCommand {
    
    private ResourcePackManager manager;
    
    public ResourcePackSetURLCommand(CLCore plugin, ResourcePackManager manager) {
        super("clcore.admin", plugin, true);
        
        this.manager = manager;
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Collections.singletonList("URL");
        
        return Collections.emptyList();
    }
    
    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if (!checkSender(sender))
            return "§cYou do not have access to this command.";
        
        if (args.length < 2)
            return "§eYou must enter a url!";
        
        manager.setUrl(args[1]);
        
        return "§aSuccessfully updated url.";
    }
    
    @Override
    public void help(CommandSender sender) {
    
    }
}
