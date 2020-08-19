package de.craftlancer.core.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// TODO implement usage of HelpMap and HelpTopic via HelpMapFactory
// TODO externalize common code between CommandHandler and SubCommandHandler
public abstract class CommandHandler implements TabExecutor {
    
    private HelpLabelManager manager;
    private Map<String, SubCommand> commands = new HashMap<>();
    private Plugin plugin;
    
    public CommandHandler(Plugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        args = CommandUtils.parseArgumentStrings(args);
        
        String message;
        
        if (args.length == 0 || !commands.containsKey(args[0])) {
            if (commands.containsKey("help"))
                message = commands.get("help").execute(sender, cmd, label, args);
            else {
                if (manager == null)
                    setManager();
                sender.spigot().sendMessage(manager.getHelpComponent(sender, label, this));
                return true;
            }
        } else
            message = commands.get(args[0]).execute(sender, cmd, label, args);
        
        if (message != null)
            sender.sendMessage(message);
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        switch (args.length) {
            case 0:
                return Collections.emptyList();
            case 1:
                return commands.keySet().stream().filter(a -> a.startsWith(args[0])).filter(a -> commands.get(a).getPermission().isEmpty() || sender.hasPermission(commands.get(a).getPermission()))
                        .collect(Collectors.toList());
            default:
                if (!commands.containsKey(args[0]))
                    return Collections.emptyList();
                else
                    return commands.get(args[0]).onTabComplete(sender, args);
        }
    }
    
    protected void setManager() {
        this.manager = new HelpLabelManager();
        
        getCommands().forEach((name, command) -> {
            if (command instanceof SubCommandHandler) {
                Validate.isTrue(((SubCommandHandler) command).getDepth() == 1, "The depth of a SubCommandHandler must be the depth of the previous Handler + 1!");
                ((SubCommandHandler) command).addHelpLabelManager(manager);
                ((SubCommandHandler) command).setHelpLabels();
            } else {
                CommandHandler.HelpLabel label = command.getHelpLabel();
                label.setLabel(manager.getLabel() + " " + name);
                manager.addHelpLabel(label);
            }
        });
    }
    
    public void registerSubCommand(String name, SubCommand command, String... alias) {
        Validate.notNull(command, "Command can't be null!");
        Validate.notEmpty(name, "Commandname can't be empty!");
        Validate.isTrue(!commands.containsKey(name), "Command " + name + " is already defined");
        for (String a : alias)
            Validate.isTrue(!commands.containsKey(a), "Command " + a + " is already defined");
        
        commands.put(name, command);
        for (String s : alias)
            commands.put(s, command);
    }
    
    protected Plugin getPlugin() {
        return plugin;
    }
    
    protected Map<String, SubCommand> getCommands() {
        return commands;
    }
    
    public String getPluginColor() {
        return ChatColor.DARK_PURPLE + "" + ChatColor.BOLD;
    }
    
    public String getRequiredArgumentColors() {
        return ChatColor.DARK_PURPLE + "";
    }
    
    public String getOptionalArgumentsColor() {
        return ChatColor.DARK_GREEN + "";
    }
    
    public String getCommandLabelColor() {
        return ChatColor.GOLD + "";
    }
    
    public String getDescriptionColor() {
        return ChatColor.YELLOW + "";
    }
    
    public class HelpLabelManager {
        private final String MESSAGE_LINE = ChatColor.DARK_GRAY + "+-------------------------------------------+";
        private final String NEW_LINE = "\n";
        
        private List<HelpLabel> helpLabels = new ArrayList<>();
        private String label;
        
        public HelpLabelManager() {
            this.label = "{command.handler.label}";
        }
        
        public void addLabel(String label) {
            this.label += label;
        }
        
        public String getLabel() {
            return label;
        }
        
        public void addHelpLabel(HelpLabel helpLabel) {
            helpLabels.add(helpLabel);
        }
        
        private BaseComponent[] getHelpComponent(CommandSender sender, String commandHandlerLabel, CommandHandler handler) {
            ComponentBuilder componentBuilder = new ComponentBuilder();
            
            componentBuilder.append(MESSAGE_LINE);
            componentBuilder.append(NEW_LINE);
            componentBuilder.append(NEW_LINE);
            componentBuilder.append(getPluginColor() + "  " + plugin.getDescription().getName() + ChatColor.GRAY + " " + plugin.getDescription().getVersion());
            componentBuilder.append(NEW_LINE);
            componentBuilder.append(NEW_LINE);
            //If there are any args and the user can actually view the commands, display the arguments legend
            if (helpLabels.stream().anyMatch(label -> label.getArgs() != null && label.getArgs().length > 0)
                    && helpLabels.stream().anyMatch(label -> label.getPermission() == null || label.getPermission().equals("") || sender.hasPermission(label.getPermission()))) {
                componentBuilder.append(ChatColor.GRAY + "  * " + getRequiredArgumentColors() + "<> " + ChatColor.GRAY + "- required arguments")
                        .append(NEW_LINE)
                        .append(ChatColor.GRAY + "  * " + getOptionalArgumentsColor() + "[] " + ChatColor.GRAY + "- optional arguments")
                        .append(NEW_LINE)
                        .append(NEW_LINE);
            }
            //display commands that player can see, replace the {command.handler.label} with the command group label
            helpLabels.stream().filter(label -> label.getPermission() == null || label.getPermission().equals("") || sender.hasPermission(label.getPermission()))
                    .forEach(label -> {
                        label.setLabel(label.getLabel().replace("{command.handler.label}", commandHandlerLabel));
                        componentBuilder.append(label.getComponents(handler));
                    });
            //If there are any commands being displayed, add another line at the end
            if (helpLabels.stream().anyMatch(label -> label.getPermission() == null || label.getPermission().equals("") || sender.hasPermission(label.getPermission())))
                componentBuilder.append(NEW_LINE);
            componentBuilder.append(MESSAGE_LINE);
            
            return componentBuilder.create();
        }
    }
    
    public static class HelpLabel {
        private static final String NEW_LINE = "\n";
        private String label;
        private String permission;
        private String description;
        private String[] args;
        
        public HelpLabel(String description, String permission, String[] args) {
            this.permission = permission;
            this.description = description;
            this.args = args;
        }
        
        public BaseComponent[] getComponents(CommandHandler handler) {
            ComponentBuilder componentBuilder = new ComponentBuilder()
                    .append(ChatColor.GRAY + "  - " + handler.getCommandLabelColor() + "/" + label)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(handler.getDescriptionColor() + "Click to suggest command " + handler.getCommandLabelColor() + "/" + label).create()))
                    .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + label + " "));
            if (args != null && args.length > 0)
                componentBuilder.append(getArgsComponent(handler));
            if (description != null)
                componentBuilder.append(NEW_LINE + "      " + handler.getDescriptionColor() + description);
            componentBuilder.append(NEW_LINE);
            
            return componentBuilder.create();
        }
        
        private BaseComponent[] getArgsComponent(CommandHandler handler) {
            ComponentBuilder builder = new ComponentBuilder();
            for (String string : args) {
                if ((!string.contains("<") && !string.contains(">")) && (!string.contains("[") && !string.contains("]")))
                    throw new IllegalArgumentException("Given arguments from label " + label + " do not include required set of '[]' or '<>'. See 'addHelpLabel' method javadocs for more info.");
                
                builder.append(" ")
                        .append(string.contains("[") ? handler.getOptionalArgumentsColor() + string : handler.getRequiredArgumentColors() + string);
            }
            
            return builder.create();
        }
        
        public String getLabel() {
            return label;
        }
        
        public String getDescription() {
            return description;
        }
        
        public String[] getArgs() {
            return args;
        }
        
        public void setLabel(String label) {
            this.label = label;
        }
        
        public String getPermission() {
            return permission;
        }
    }
}
