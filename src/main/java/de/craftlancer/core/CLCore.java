package de.craftlancer.core;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import de.craftlancer.core.conversation.ConvoCommand;

public class CLCore extends JavaPlugin {
    @Override
    public void onEnable() {
        ConfigurationSerialization.registerClass(RectangleArea.class);
        ConfigurationSerialization.registerClass(CuboidArea.class);
        ConfigurationSerialization.registerClass(Point2D.class);
        ConfigurationSerialization.registerClass(Point3D.class);
        ConfigurationSerialization.registerClass(MassChestInventory.class);
        ConfigurationSerialization.registerClass(BlockStructure.class);
        
        getCommand("convo").setExecutor(new ConvoCommand());
    }
}
