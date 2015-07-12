package com.skyrossm.mods.advancedmenus.client.gui.serverlist;

import com.google.common.collect.Lists;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SkyServerList
{
    private static final Logger logger = LogManager.getLogger();

    /** The Minecraft instance. */
    private final Minecraft mc;
    private static String currentTab;

    /** List of ServerData instances. */
    private final List servers = Lists.newArrayList();

    public SkyServerList(Minecraft mcIn, String curTab)
    {
        this.mc = mcIn;
        this.currentTab = curTab;
        this.loadServerList();
    }

    /**
     * Loads a list of servers from servers.dat, by running ServerData.getServerDataFromNBTCompound on each NBT compound
     * found in the "servers" tag list.
     */
    public void loadServerList()
    {
        try
        {
            this.servers.clear();
            NBTTagCompound var1 = CompressedStreamTools.read(new File(this.mc.mcDataDir, "/servers/tabs/" + currentTab + "/servers.dat"));

            if (var1 == null)
            {
                return;
            }

            NBTTagList var2 = var1.getTagList("servers", 10);

            for (int var3 = 0; var3 < var2.tagCount(); ++var3)
            {
                this.servers.add(ServerData.getServerDataFromNBTCompound(var2.getCompoundTagAt(var3)));
            }
        }
        catch (Exception var4)
        {
            logger.error("Couldn\'t load server list", var4);
        }
    }

    /**
     * Runs getNBTCompound on each ServerData instance, puts everything into a "servers" NBT list and writes it to
     * servers.dat.
     */
    public void saveServerList()
    {
        try
        {
            NBTTagList var1 = new NBTTagList();
            Iterator var2 = this.servers.iterator();

            while (var2.hasNext())
            {
                ServerData var3 = (ServerData)var2.next();
                var1.appendTag(var3.getNBTCompound());
            }

            NBTTagCompound var5 = new NBTTagCompound();
            var5.setTag("servers", var1);
            CompressedStreamTools.safeWrite(var5, new File(this.mc.mcDataDir, "/servers/tabs/" + currentTab + "/servers.dat"));
        }
        catch (Exception var4)
        {
            logger.error("Couldn\'t save server list", var4);
        }
    }

    /**
     * Gets the ServerData instance stored for the given index in the list.
     */
    public ServerData getServerData(int p_78850_1_)
    {
        return (ServerData)this.servers.get(p_78850_1_);
    }

    /**
     * Removes the ServerData instance stored for the given index in the list.
     */
    public void removeServerData(int p_78851_1_)
    {
        this.servers.remove(p_78851_1_);
    }

    /**
     * Adds the given ServerData instance to the list.
     */
    public void addServerData(ServerData p_78849_1_)
    {
        this.servers.add(p_78849_1_);
    }

    /**
     * Counts the number of ServerData instances in the list.
     */
    public int countServers()
    {
        return this.servers.size();
    }

    /**
     * Takes two list indexes, and swaps their order around.
     */
    public void swapServers(int index1, int index2)
    {
        ServerData var3 = this.getServerData(index1);
        this.servers.set(index1, this.getServerData(index2));
        this.servers.set(index2, var3);
        this.saveServerList();
    }

    public void replaceServer(int index, ServerData serverData)
    {
        this.servers.set(index, serverData);
    }

    public static void refresh(ServerData serverData)
    {
        SkyServerList newServerList = new SkyServerList(Minecraft.getMinecraft(), SkyServerList.currentTab);
        newServerList.loadServerList();

        for (int var2 = 0; var2 < newServerList.countServers(); ++var2)
        {
            ServerData var3 = newServerList.getServerData(var2);

            if (var3.serverName.equals(serverData.serverName) && var3.serverIP.equals(serverData.serverIP))
            {
                newServerList.replaceServer(var2, serverData);
                break;
            }
        }

        newServerList.saveServerList();
    }
}
