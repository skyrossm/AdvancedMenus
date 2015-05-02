package com.skyrossm.mods.advancedmenus.client.gui.serverlist;

import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.network.LanServerDetector;

import com.google.common.collect.Lists;

public class SkySelectionList extends GuiListExtended
{
    private final GuiMultiplayerSky owner;
    private final List serverList = Lists.newArrayList();
    private final List lanServers = Lists.newArrayList();
    private final GuiListExtended.IGuiListEntry lanScanEntry = new SkyListEntryLanScan();
    private int selected = -1;

    public SkySelectionList(GuiMultiplayerSky guiM, Minecraft mcIn, int p_i45049_3_, int p_i45049_4_, int p_i45049_5_, int p_i45049_6_, int p_i45049_7_)
    {
        super(mcIn, p_i45049_3_, p_i45049_4_, p_i45049_5_, p_i45049_6_, p_i45049_7_);
        this.owner = guiM;
    }

    /**
     * Gets the IGuiListEntry object for the given index
     */
    public GuiListExtended.IGuiListEntry getListEntry(int index)
    {
        if (index < this.serverList.size())
        {
            return (GuiListExtended.IGuiListEntry)this.serverList.get(index);
        }
        else
        {
            index -= this.serverList.size();

            if (index == 0)
            {
                return this.lanScanEntry;
            }
            else
            {
                --index;
                return (GuiListExtended.IGuiListEntry)this.lanServers.get(index);
            }
        }
    }

    protected int getSize()
    {
        return this.serverList.size() + 1 + this.lanServers.size();
    }

    public void setSelected(int index)
    {
        this.selected = index;
    }

    /**
     * Returns true if the element passed in is currently selected
     */
    protected boolean isSelected(int slotIndex)
    {
        return slotIndex == this.selected;
    }

    public int getDatInt()
    {
        return this.selected;
    }

    public void addServers(SkyServerList savedServerList)
    {
        this.serverList.clear();

        for (int var2 = 0; var2 < savedServerList.countServers(); ++var2)
        {
            this.serverList.add(new SkyListEntryNormal(this.owner, savedServerList.getServerData(var2)));
        }
    }

    public void addLanServers(List lans)
    {
        this.lanServers.clear();
        Iterator var2 = lans.iterator();

        while (var2.hasNext())
        {
            LanServerDetector.LanServer var3 = (LanServerDetector.LanServer)var2.next();
            this.lanServers.add(new SkyListEntryLanDetected(this.owner, var3));
        }
    }

    protected int getScrollBarX()
    {
        return super.getScrollBarX() + 30;
    }

    /**
     * Gets the width of the list
     */
    public int getListWidth()
    {
        return super.getListWidth() + 85;
    }
}
