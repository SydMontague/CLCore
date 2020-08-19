package de.craftlancer.core.command;

import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class SubCommandHandler extends SubCommand {
    private Map<String, SubCommand> commands = new HashMap<>();
    private int depth;
    private List<CommandHandler.HelpLabelManager> helpLabelManagers = new ArrayList<>();
    
    public SubCommandHandler(String permission, Plugin plugin, boolean console, int depth) {
        super(permission, plugin, console);
        Validate.isTrue(depth >= 1, "SubCommandHandler depth can't be smaller than 0!");
        this.depth = depth;
    }
    
    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length <= getDepth() || !commands.containsKey(args[getDepth()]))
            if (commands.containsKey("help"))
                return commands.get("help").execute(sender, cmd, label, args);
            else {
                help(sender);
                return null;
            }
        else
            return commands.get(args[getDepth()]).execute(sender, cmd, label, args);
    }
    
    protected int getDepth() {
        return depth;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        switch (args.length - depth) {
            case 0:
                return Collections.emptyList();
            case 1:
                return commands.keySet().stream()
                        .filter(a -> a.startsWith(args[args.length - 1]))
                        .filter(a -> sender.hasPermission(commands.get(a).getPermission()))
                        .collect(Collectors.toList());
            default:
                if (!commands.containsKey(args[depth]))
                    return Collections.emptyList();
                else
                    return commands.get(args[depth]).onTabComplete(sender, args);
        }
    }
    
    @Override
    public abstract void help(CommandSender sender);
    
    public void setHelpLabels() {
        getCommands().forEach((name, command) -> {
            if (command instanceof SubCommandHandler) {
                Validate.isTrue(((SubCommandHandler) command).getDepth() == getDepth() + 1, "The depth of a SubCommandHandler must be the depth of the previous Handler + 1!");
                helpLabelManagers.forEach(helpLabelManager -> {
                    helpLabelManager.addLabel(name);
                    ((SubCommandHandler) command).addHelpLabelManager(helpLabelManager);
                    ((SubCommandHandler) command).setHelpLabels();
                });
            } else {
                helpLabelManagers.forEach(helpLabelManager -> {
                    CommandHandler.HelpLabel label = command.getHelpLabel();
                    label.setLabel(helpLabelManager.getLabel() + " " + name);
                    helpLabelManager.addHelpLabel(label);
                });
            }
        });
    }
    
    public void registerSubCommand(String name, SubCommand command, String... alias) {
        Validate.notNull(command, "SubCommand can't be null!");
        Validate.notEmpty(name, "SubCommandname can't be empty!");
        Validate.isTrue(!commands.containsKey(name), "Command " + name + " is already defined");
        for (String a : alias)
            Validate.isTrue(!commands.containsKey(a), "Command " + a + " is already defined");
        
        commands.put(name, command);
        for (String s : alias)
            commands.put(s, command);
    }
    
    protected Map<String, SubCommand> getCommands() {
        return commands;
    }
    
    public void addHelpLabelManager(CommandHandler.HelpLabelManager manager) {
        helpLabelManagers.add(manager);
    }
    
    @Override
    public String[] getArgs() {
        return new String[0];
    }
    
    @Override
    public String getDescription() {
        return null;
    }
}
