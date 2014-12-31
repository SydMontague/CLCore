package de.craftlancer.core.command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public abstract class SubCommand
{
    private String permission = "";
    private Plugin plugin;
    private boolean console;
    
    public SubCommand(String permission, Plugin plugin, boolean console)
    {
        this.permission = permission;
        this.plugin = plugin;
        this.console = console;
    }
    
    public boolean checkSender(CommandSender sender)
    {
        if (!(sender instanceof Player) && isConsoleCommand())
            return true;
        
        if (sender.hasPermission(getPermission()))
            return true;
        
        return false;
    }
    
    public Plugin getPlugin()
    {
        return plugin;
    }
    
    public boolean isConsoleCommand()
    {
        return console;
    }
    
    public String getPermission()
    {
        return permission;
    }
    
    /**
     * The code that will be executed when the sub command is called
     * 
     * @param sender the sender of the command
     * @param cmd the root command
     * @param label the command's label
     * @param args the arguments provided to the command, they are already processed to support input with space chars
     *              also already handled args (by higher handlers) have been removed
     * @return a String, that will be send to the player, used for error messages. No message will be shown, when this is null
     */
    protected abstract String execute(CommandSender sender, Command cmd, String label, String[] args);
    
    protected String onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return execute(sender, cmd, label, args);
    }
    
    protected List<String> onTabComplete(CommandSender sender, String[] args)
    {
        return null;
    }
    
    public abstract void help(CommandSender sender);
}
