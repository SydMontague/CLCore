package de.craftlancer.core;

import java.awt.Point;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/*
 * ItemStack: <Material> <Amount> <Data> <Name> <Lore>
 * 
 * ItemStack:
 *   Material: material
 *   Data: Data
 *   Amount: amount
 *   Name:
 *   Enchants:
 *   Lore:
 *   
 */
public class Utils
{
    public static Direction getPlayerDirection(Player player)
    {
        int facing = Math.abs(((Math.round((player.getLocation().getYaw()) / 90)) + 4) % 4);
        
        switch (facing)
        {
            case 0:
                return Direction.SOUTH;
            case 1:
                return Direction.WEST;
            case 2:
                return Direction.NORTH;
            case 3:
                return Direction.EAST;
            default:
                throw new RuntimeException("Illegal facing value! Must be between 0 and 3 but was " + facing);
        }
    }
    
    // TODO tests
    public static String getItemString(ItemStack i)
    {
        return (i == null) ? "" : i.getType().name() + " " + i.getAmount();
    }
    
    // TODO tests
    public static String getLocationString(Location loc)
    {
        return (loc == null) ? "" : loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " " + loc.getWorld().getName();
    }
    
    // TODO tests
    public static Location parseLocation(String loc)
    {
        if (loc == null)
            return null;
        
        String coords[] = loc.split(" ", 4);
        
        if (coords.length != 4)
            return null;
        
        for (int i = 0; i <= 2; i++)
            if (!coords[i].matches("-?[0-9]+"))
                return null;
        
        if (Bukkit.getServer().getWorld(coords[3]) == null)
            return null;
        
        int x = Integer.parseInt(coords[0]);
        int y = Integer.parseInt(coords[1]);
        int z = Integer.parseInt(coords[2]);
        
        return new Location(Bukkit.getServer().getWorld(coords[3]), x, y, z);
    }
    
    // TODO tests
    public static boolean isLocationString(String loc)
    {
        if (loc == null)
            return false;
        
        String coords[] = loc.split(" ", 4);
        
        if (coords.length != 4)
            return false;
        
        for (int i = 0; i <= 2; i++)
            if (!coords[i].matches("-?[0-9]+"))
                return false;
        
        if (Bukkit.getServer().getWorld(coords[3]) == null)
            return false;
        
        return true;
    }
    
    public static Point parsePointString(String posi)
    {
        String[] cmp = posi.split(" ");
        int x = Integer.parseInt(cmp[0]);
        int y = Integer.parseInt(cmp[1]);
        
        return new Point(x, y);
    }
    
    public static String parsePointWorld(String posi)
    {
        String[] cmp = posi.split(" ");
        return cmp[2];
    }
    
    // TODO tests
    public static ItemStack parseItem(String k)
    {
        if (k == null)
            return null;
        
        String value[] = k.split(" ", 2);
        
        if (value.length != 2)
            return null;
        
        for (int i = 0; i < 2; i++)
            if (!value[i].matches("[0-9]"))
                return null;
        
        Material mat = Material.getMaterial(value[0]);
        int amount = Integer.parseInt(value[1]);
        
        if (mat == null)
            return null;
        
        return new ItemStack(mat, amount);
    }
    
    // TODO tests
    public static boolean isItemStackString(String k)
    {
        if (k == null)
            return false;
        
        String value[] = k.split(" ", 2);
        
        if (value.length != 2)
            return false;
        
        if (!value[1].matches("[0-9]"))
            return false;
        
        return true;
    }
    
    public static <T> boolean arrayContains(T[] a, T o)
    {
        if (a != null && a.length != 0)
            for (T ob : a)
                if (ob.equals(o))
                    return true;
        
        return false;
    }
    
    // TODO tests
    /**
     * Get all values of a String Collection which start with a given String
     * 
     * @param value the given String
     * @param list the Collection
     * @return a List of all matches
     */
    public static List<String> getMatches(String value, Collection<String> list)
    {
        List<String> result = new LinkedList<String>();
        
        for (String str : list)
            if (str.startsWith(value))
                result.add(str);
        
        return result;
    }
    
    // TODO tests
    /**
     * Get all values of a String array which start with a given String
     * 
     * @param value the given String
     * @param list the array
     * @return a List of all matches
     */
    public static List<String> getMatches(String value, String[] list)
    {
        List<String> result = new LinkedList<String>();
        
        for (String str : list)
            if (str.startsWith(value))
                result.add(str);
        
        return result;
    }
    
    public static boolean isInt(String string)
    {
        try
        {
            Integer.parseInt(string);
            return true;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }
    
    // TODO tests
    public static int getExp(double level)
    {
        int exp = 0;
        
        double nlevel = Math.ceil(level);
        
        if (nlevel - 32 > 0)
            exp += (1 - (nlevel - level)) * (65 + (nlevel - 32) * 7);
        else if (nlevel - 16 > 0)
            exp += (1 - (nlevel - level)) * (17 + (level - 16) * 3);
        else
            exp += (1 - (nlevel - level)) * 17;
        
        level = Math.ceil(level - 1);
        
        while (level > 0)
        {
            if (level - 32 > 0)
                exp += 65 + (level - 32) * 7;
            else if (level - 16 > 0)
                exp += 17 + (level - 16) * 3;
            else
                exp += 17;
            
            level--;
        }
        
        return exp;
    }
    
    // TODO tests
    public static double getLevel(int exp)
    {
        double i = 0;
        boolean stop = true;
        
        while (stop)
        {
            i++;
            
            if (i - 32 > 0)
            {
                if (exp - (65 + (i - 32) * 7) > 0)
                    exp = (int) (exp - (65 + (i - 32) * 7));
                else
                    stop = false;
            }
            else if (i - 16 > 0)
            {
                if (exp - (17 + (i - 16) * 3) > 0)
                    exp = (int) (exp - (17 + (i - 16) * 3));
                else
                    stop = false;
            }
            else if (exp > 17)
                exp -= 17;
            else
                stop = false;
        }
        
        if (exp != 0)
            if (i - 32 > 0)
                i += exp / (65 + (i - 32) * 7);
            else if (i - 16 > 0)
                i += exp / (17 + (i - 16) * 3);
            else
                i += exp / 17D;
        
        if (exp == 0)
            i--;
        
        i--;
        return i;
    }
    
    // TODO tests
    public static String getTimeString(long time)
    {
        long h = time / (60 * 60 * 1000);
        long min = (time / (60 * 1000)) % 60;
        long s = (time / (1000)) % 60;
        
        return h + "h " + min + "min " + s + "s";
    }
    
    // TODO tests
    public static String timeToString(long time)
    {
        long h = time / 3600;
        long min = (time - h * 3600) / 60;
        long s = time - h * 3600 - min * 60;
        
        return h + "h " + min + "min " + s + "s";
    }

    public static String[] removeFirstElement(String[] args)
    {
        if(args == null || args.length <= 1)
            return new String[0];

        return Arrays.copyOfRange(args, 1, args.length);
    }
}
