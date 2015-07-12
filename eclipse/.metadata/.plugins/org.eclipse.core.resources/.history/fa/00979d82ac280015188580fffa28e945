package com.skyrossm.mods.advancedmenus.client.gui.tabs;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraft.client.gui.GuiRenameWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveFormatComparator;
import net.minecraft.world.storage.WorldInfo;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.skyrossm.mods.advancedmenus.AdvancedMenus;

public class GuiTabs extends GuiScreen implements GuiYesNoCallback
{
    private static final Logger logger = LogManager.getLogger();
    protected GuiScreen parent;
    protected String title = "Edit Tabs";
    private int selected;
    private java.util.List tabList;
    private GuiTabs.List serverList;
    private boolean deleting;
    private GuiButton deleteBtn;
    private GuiButton renameBtn;
    private GuiButton addBtn;

    public GuiTabs(GuiScreen p_i1054_1_)
    {
        this.parent = p_i1054_1_;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.serverList = new GuiTabs.List(this.mc);
        this.serverList.registerScrollButtons(4, 5);
        this.loadTabs();
        this.createButtons();
        if(this.tabList.size() == 15){
        	this.addBtn.enabled = false;
        }
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        this.serverList.handleMouseInput();
    }

    private void loadTabs()
    {
        this.tabList = AdvancedMenus.tabs;
        this.selected = -1;
    }

    protected String getTabName(int index)
    {
        ServerTab tab = (ServerTab) this.tabList.get(index);
        return tab.getName();
    }

    public void createButtons()
    {
        this.buttonList.add(this.addBtn = new GuiButton(3, this.width / 2 + 54, this.height - 52, 100, 20, I18n.format("Add Tab", new Object[0])));
        this.buttonList.add(this.renameBtn = new GuiButton(6, this.width / 2 - 154, this.height - 52, 100, 20, I18n.format("Rename", new Object[0])));
        this.buttonList.add(this.deleteBtn = new GuiButton(2, this.width / 2 - 50, this.height - 52, 100, 20, I18n.format("Delete", new Object[0])));
        this.buttonList.add(new GuiButton(0, this.width / 2 + 54, this.height - 28, 100, 20, I18n.format("gui.cancel", new Object[0])));
        this.deleteBtn.enabled = false;
        this.renameBtn.enabled = false;
        this.addBtn.enabled = true;
    }

    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            if (button.id == 2)
            {
                String var2 = this.getTabName(this.selected);

                if (var2 != null)
                {
                    this.deleting = true;
                    GuiYesNo var3 = delete(this, var2, this.selected);
                    this.mc.displayGuiScreen(var3);
                }
            }
            else if (button.id == 3)
            {
                this.mc.displayGuiScreen(new GuiAddTab(this));
            }
            else if (button.id == 6)
            {
                this.mc.displayGuiScreen(new GuiRenameTab(this, this.getTabName(this.selected)));
            }
            else if (button.id == 0)
            {
                this.mc.displayGuiScreen(this.parent);
            }else
            {
                this.serverList.actionPerformed(button);
            }
        }
    }

    public void confirmClicked(boolean result, int id)
    {
        if (this.deleting)
        {
            this.deleting = false;

            if (result)
            {
                AdvancedMenus.removeTab((ServerTab) this.tabList.get(id));
                this.loadTabs();
            }

            this.mc.displayGuiScreen(this);
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.serverList.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 20, 16777215);
        if(this.tabList.size() == 15){
        	this.addBtn.enabled = false;
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public static GuiYesNo delete(GuiYesNoCallback p_152129_0_, String p_152129_1_, int p_152129_2_)
    {
        String var3 = I18n.format("Are you sure you want to delete this tab?", new Object[0]);
        String var4 = p_152129_1_ + "\'" + I18n.format("s servers will be deleted!", new Object[0]);
        String var5 = I18n.format("selectWorld.deleteButton", new Object[0]);
        String var6 = I18n.format("gui.cancel", new Object[0]);
        GuiYesNo var7 = new GuiYesNo(p_152129_0_, var3, var4, var5, var6, p_152129_2_);
        return var7;
    }

    class List extends GuiSlot
    {
        private static final String __OBFID = "CL_00000712";

        public List(Minecraft mcIn)
        {
            super(mcIn, GuiTabs.this.width, GuiTabs.this.height, 32, GuiTabs.this.height - 64, 36);
        }

        protected int getSize()
        {
            return GuiTabs.this.tabList.size();
        }

        protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY)
        {
            GuiTabs.this.selected = slotIndex;
            boolean var5 = GuiTabs.this.selected >= 0 && GuiTabs.this.selected < this.getSize();
            GuiTabs.this.deleteBtn.enabled = var5;
            if(slotIndex == 0 && GuiTabs.this.tabList.size() == 1){
            	GuiTabs.this.deleteBtn.enabled = false;
            }
            GuiTabs.this.renameBtn.enabled = var5;
        }

        protected boolean isSelected(int slotIndex){
            return slotIndex == GuiTabs.this.selected;
        }

        protected int getContentHeight(){
            return GuiTabs.this.tabList.size() * 36;
        }

        protected void drawBackground(){
            GuiTabs.this.drawDefaultBackground();
        }

		protected void drawSlot(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
			GuiTabs.this.drawString(GuiTabs.this.fontRendererObj, GuiTabs.this.getTabName(arg0), arg1 + 2, arg2 + 1, 16777215);
		}
    }
}
