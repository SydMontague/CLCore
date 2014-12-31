package de.craftlancer.core.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.craftlancer.core.Utils;

public abstract class SubCommandHandler extends SubCommand
{
    private Map<String, SubCommand> commands = new HashMap<String, SubCommand>();
    
    public SubCommandHandler(String permission, Plugin plugin, boolean console)
    {
        super(permission, plugin, console);
    }
    
    @Override
    protected String onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {        
        SubCommand command = commands.get(args[0]);
        args = Utils.removeFirstElement(args);
        
        return command != null ? command.onCommand(sender, cmd, label, args) : execute(sender, cmd, label, args);
    }
    
    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args)
    {
        help(sender);
        return null;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args)
    {
        switch (args.length)
        {
            case 0:
                return null;
            case 1:
                List<String> l = Utils.getMatches(args[0], commands.keySet());
                for (String str : l)
                    if (!sender.hasPermission(commands.get(str).getPermission()))
                        l.remove(l);
                return l;
            default:
                SubCommand command = commands.get(args[0]);
                args = Utils.removeFirstElement(args);
                
                return command == null ? null : command.onTabComplete(sender, args);
        }
    }
    
    @Override
    public abstract void help(CommandSender sender);
    
    public void registerSubCommand(String name, SubCommand command, String... alias)
    {
        Validate.notNull(command, "SubCommand can't be null!");
        Validate.notEmpty(name, "SubCommandname can't be empty!");
        Validate.isTrue(!commands.containsKey(name), "Command " + name + " is already defined");
        for (String a : alias)
            Validate.isTrue(!commands.containsKey(a), "Command " + a + " is already defined");
        
        commands.put(name, command);
        for (String s : alias)
            commands.put(s, command);
    }
    
    protected Map<String, SubCommand> getCommands()
    {
        return commands;
    }
}
