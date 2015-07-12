package com.skyrossm.mods.advancedmenus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.google.common.io.Files;
import com.skyrossm.mods.advancedmenus.client.gui.tabs.ServerTab;
import com.skyrossm.mods.advancedmenus.events.EventManager;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = AdvancedMenus.MODID, version = AdvancedMenus.VERSION)
public class AdvancedMenus
{
    public static final String MODID = "advancedmenus";
    public static final String VERSION = "1.0";
    
    public static ArrayList<ServerTab> tabs;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        System.out.println("Thank you for using AdvancedMenus version " + AdvancedMenus.VERSION);
        System.out.println("AdvancedMenus Created By: Skyrossm");
        
        tabs = new ArrayList<ServerTab>();
        
        loadTabs();
        
        MinecraftForge.EVENT_BUS.register(new EventManager());
    }
    
    public static ArrayList<ServerTab> getServerTabList()
    {
    	return tabs;
    }
    
    public static void loadTabs()
    {
    	File mcDD = Minecraft.getMinecraft().mcDataDir;
    	File serverFile = new File(mcDD.getPath() + "/servers");
		serverFile.mkdir();
		
    	File tabFile = new File(serverFile.getPath() + "/tabs");
    	File checkFile = new File(serverFile.getPath() + "/true.txt");
    	tabFile.mkdir();
    	
		try 
		{
			if(checkFile.createNewFile())
			{
				File def = new File(tabFile.getPath() + "/Default");
				def.mkdir();
				
				File defDat = new File(def.getPath() + "/servers.dat");
				defDat.createNewFile();
			}
		} 
		catch (IOException e) {}
		
    	int tabCount = 0;
    	
    	for(File f : tabFile.listFiles())
    	{
    		tabs.add(new ServerTab(f.getName()));
    		tabCount++;
    	}
    }

	public static void addTab(ServerTab serverTab) 
	{
		tabs.add(serverTab);
		
		File mcDD = Minecraft.getMinecraft().mcDataDir;
    	File serverFile = new File(mcDD.getPath() + "/servers");
    	File tabFile = new File(serverFile.getPath() + "/tabs");
		File temp = new File(tabFile.getPath() + "/" + serverTab.getName());
		temp.mkdir();
		
		File tempDat = new File(temp.getPath() + "/servers.dat");
		try 
		{
			tempDat.createNewFile();
		} 
		catch (IOException e) {}
	}
	
	public static void replaceTab(ServerTab oldTab, ServerTab newTab) 
	{
		tabs.remove(oldTab);
		
		File mcDD = Minecraft.getMinecraft().mcDataDir;
		File serverFile = new File(mcDD.getPath() + "/servers");
		File tabFile = new File(serverFile.getPath() + "/tabs");
		File oldTemp = new File(tabFile.getPath() + "/" + oldTab.getName());
		File oldTempDat = new File(oldTemp.getPath() + "/servers.dat");
		
		tabs.add(newTab);
		
		File temp = new File(tabFile.getPath() + "/" + newTab.getName());	
		temp.mkdir();
		
		File tempDat = new File(temp.getPath() + "/servers.dat");
		
		try 
		{
			Files.copy(oldTempDat, tempDat);
		} 
		catch (IOException e) {}
		
		deleteDirectory(oldTemp);
	}
	
	public static void removeTab(ServerTab serverTab)
	{
		tabs.remove(serverTab);
		
		File mcDD = Minecraft.getMinecraft().mcDataDir;
    	File serverFile = new File(mcDD.getPath() + "/servers");
    	File tabFile = new File(serverFile.getPath() + "/tabs");
		File temp = new File(tabFile.getPath() + "/" + serverTab.getName());
		
		deleteDirectory(temp);
	}

	public static ServerTab getTabByName(String tab) 
	{
		for(ServerTab s : tabs)
		{
			if(s.getName().equalsIgnoreCase(tab))
			{
				return s;
			}
		}
		return null;
	}
	
	public static boolean deleteDirectory(File path) 
	{
	    if(path.exists()) 
	    {
	        File[] files = path.listFiles();
	        
	        for (int i = 0; i < files.length; i++) 
	        {
	            if (files[i].isDirectory()) 
	            {
	                deleteDirectory(files[i]);
	            } 
	            else 
	            {
	                files[i].delete();
	            }
	        }
	    }
	    return (path.delete());
	}
}
