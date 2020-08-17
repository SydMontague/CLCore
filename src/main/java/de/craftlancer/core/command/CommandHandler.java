package de.craftlancer.core.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import sun.misc.Perf;

import javax.annotation.Nonnull;

// TODO implement usage of HelpMap and HelpTopic via HelpMapFactory
// TODO externalize common code between CommandHandler and SubCommandHandler
public abstract class CommandHandler implements TabExecutor {
    private static final String MESSAGE_LINE = ChatColor.DARK_GRAY + "+-------------------------------------------+";
    private static final String NEW_LINE = "\n";
    
    private Map<String, SubCommand> commands = new HashMap<>();
    private List<HelpLabel> helpLabels = new ArrayList<>();
    private Plugin plugin;
    private String label;
    
    public CommandHandler(Plugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Required constructor for help component
     */
    public CommandHandler(Plugin plugin, String label) {
        this.plugin = plugin;
        this.label = label;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        args = CommandUtils.parseArgumentStrings(args);
        
        String message;
        
        if (args.length == 0 || !commands.containsKey(args[0])) {
            sender.spigot().sendMessage(getHelpComponent(sender));
            return false;
        }
        else
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
    
    public void registerSubCommand(String name, SubCommand command, String... alias) {
        Validate.notNull(command, "Command can't be null!");
        Validate.notEmpty(name, "Commandname can't be empty!");
        Validate.isTrue(!commands.containsKey(name), "Command " + name + " is already defined");
        for (String a : alias)
            Validate.isTrue(!commands.containsKey(a), "Command " + a + " is already defined");
        
        if (command instanceof SubCommandHandler)
            Validate.isTrue(((SubCommandHandler) command).getDepth() == 1, "The depth of a SubCommandHandler must be the depth of the previous Handler + 1!");
        
        commands.put(name, command);
        for (String s : alias)
            commands.put(s, command);
    }
    
    private BaseComponent[] getHelpComponent(CommandSender sender) {
        ComponentBuilder componentBuilder = new ComponentBuilder();
    
        componentBuilder.append(MESSAGE_LINE);
        componentBuilder.append(NEW_LINE);
        componentBuilder.append(getPluginColor() + "  " + plugin.getDescription().getName() + ChatColor.GRAY + " " + plugin.getDescription().getVersion());
        componentBuilder.append(NEW_LINE);
        componentBuilder.append(NEW_LINE);
        helpLabels.stream().filter(label -> !label.getPermission().equals("") && sender.hasPermission(label.getPermission()))
                .forEach(label -> componentBuilder.append(label.getComponents()));
        componentBuilder.append(NEW_LINE);
        componentBuilder.append(MESSAGE_LINE);
        
        return componentBuilder.create();
    }
    
    protected Plugin getPlugin() {
        return plugin;
    }
    
    protected Map<String, SubCommand> getCommands() {
        return commands;
    }
    
    public String getLabel() {
        return label + " ";
    }
    
    public void addHelpLabel(@Nonnull HelpLabel label) {
        helpLabels.add(label);
    }
    
    public String getPluginColor() {
        return ChatColor.GOLD + "" + ChatColor.BOLD;
    }
    
    public static class HelpLabel {
        private String label;
        private String permission;
        private String description;
    
        /**
         * Creates a label for a subcommand.
         * @param label the label of the subcommand
         * @param permission the permission of the subcommand (use "" for no permission)
         * @param description the description of the subcommand (use null for no description)
         */
        public HelpLabel(String label, String permission, String description) {
            this.label = label;
            this.permission = permission;
            this.description = description;
        }
        
        public BaseComponent[] getComponents() {
            ComponentBuilder componentBuilder = new ComponentBuilder();
    
            componentBuilder.append(ChatColor.GRAY + "  - " + ChatColor.GOLD + "/" + label + (description == null ? "" : ChatColor.GRAY + " - " + ChatColor.YELLOW + description));
            componentBuilder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder(ChatColor.YELLOW + "Click to suggest command " + ChatColor.GOLD + "/" + label).create()));
            componentBuilder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + label + " "));
            componentBuilder.append(NEW_LINE);
            
            return componentBuilder.create();
        }
    
        public String getPermission() {
            return permission;
        }
    }
}
