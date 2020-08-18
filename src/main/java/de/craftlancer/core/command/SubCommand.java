package de.craftlancer.core.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public abstract class SubCommand {
    private String permission = "";
    protected Plugin plugin;
    private boolean console;
    private String description;
    private String[] args;
    
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
    
    public void setDescription(@Nullable String description) {
        this.description = description;
    }
    
    public void addHelpLabel(CommandHandler.HelpLabelManager manager, String subCommandLabel) {
        CommandHandler.HelpLabel helpLabel = new CommandHandler.HelpLabel(manager.getLabel() + " " + subCommandLabel, description, permission, args);
        manager.addHelpLabel(helpLabel);
    }
    
    /**
     * Used to show required/optional arguments in the help component.
     *
     * @param arguments Each argument in the command, in the order used.
     *                  Use brackets [] around an argument to specify a required argument
     *                  Use arrows <> around an argument to specify an optional argument
     */
    public void setArguments(String... arguments) {
        this.args = arguments;
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
