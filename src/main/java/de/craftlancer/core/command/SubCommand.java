package de.craftlancer.core.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;

public abstract class SubCommand {
    private String permission = "";
    protected Plugin plugin;
    private boolean console;
    
    public SubCommand(String permission, Plugin plugin, boolean console) {
        this.permission = permission;
        this.plugin = plugin;
        this.console = console;
    }
    
    public boolean checkSender(CommandSender sender) {
        if (!(sender instanceof Player) && isConsoleCommand())
            return true;
        
        return getPermission().equals("") || sender.hasPermission(getPermission());
    }
    
    public Plugin getPlugin() {
        return plugin;
    }
    
    public boolean isConsoleCommand() {
        return console;
    }
    
    public String getPermission() {
        return permission;
    }
    
    /**
     * Used to show required/optional arguments in the help component.
     *
     * @return An array of strings to be set as the arguments.
     */
    public abstract String[] getArgs();
    
    /**
     * @return The description
     */
    public abstract String getDescription();
    
    public CommandHandler.HelpLabel getHelpLabel() {
        return new CommandHandler.HelpLabel(getDescription(), permission, getArgs());
    }
    
    /**
     * The code that will be executed when the sub command is called
     *
     * @param sender the sender of the command
     * @param cmd    the root command
     * @param label  the command's label
     * @param args   the arguments provided to the command, they are already processed to support input with space chars
     * @return a String, that will be send to the player, used for error messages. No message will be shown, when this
     * is null
     */
    protected abstract String execute(CommandSender sender, Command cmd, String label, String[] args);
    
    /**
     * Requests a list of possible completions for a command argument
     *
     * @param sender Source of the command
     * @param args   The arguments passed to the command, including final partial argument to be completed and command label
     * @return a list of possible completions for the command argument
     */
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
    
    public abstract void help(CommandSender sender);
}
