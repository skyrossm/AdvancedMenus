package com.skyrossm.mods.advancedmenus.client.gui.serverlist;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.skyrossm.mods.advancedmenus.AdvancedMenus;
import com.skyrossm.mods.advancedmenus.client.gui.tabs.GuiTabs;
import com.skyrossm.mods.advancedmenus.client.gui.tabs.ServerTab;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenServerList;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.LanServerDetector;
import net.minecraft.client.network.OldServerPinger;
import net.minecraft.client.resources.I18n;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

public class GuiMultiplayerSky extends GuiScreen implements GuiYesNoCallback
{
    private static final Logger logger = LogManager.getLogger();
    private final OldServerPinger oldServerPinger = new OldServerPinger();
    private GuiScreen parentScreen;
    private SkySelectionList serverListSelector;
    private SkyServerList savedServerList;
    private GuiButton btnEditServer;
    private GuiButton btnSelectServer;
    private GuiButton btnDeleteServer;
    private boolean deletingServer;
    private boolean addingServer;
    private boolean editingServer;
    private String title = "Default";
    private boolean directConnect;
    private String field_146812_y;
    private String currentTab;
    private ServerData selectedServer;
    private LanServerDetector.LanServerList lanServerList;
    private LanServerDetector.ThreadLanServerFind lanServerDetector;
    private boolean initialized;

    public GuiMultiplayerSky(GuiScreen parentScreen)
    {
        this.parentScreen = parentScreen;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();

        if (!this.initialized)
        {
            this.initialized = true;
            currentTab = AdvancedMenus.tabs.get(0).getName();
            this.savedServerList = new SkyServerList(this.mc, currentTab);
            this.savedServerList.loadServerList();
            this.lanServerList = new LanServerDetector.LanServerList();

            try
            {
                this.lanServerDetector = new LanServerDetector.ThreadLanServerFind(this.lanServerList);
                this.lanServerDetector.start();
            }
            catch (Exception var2)
            {
                logger.warn("Unable to start LAN server detection: " + var2.getMessage());
            }

            this.serverListSelector = new SkySelectionList(this, this.mc, this.width, this.height, 32, this.height - 64, 36);
            this.serverListSelector.addServers(this.savedServerList);
        }
        else
        {
            this.serverListSelector.setDimensions(this.width, this.height, 32, this.height - 64);
        }

        this.createButtons();
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        this.serverListSelector.handleMouseInput();
    }

    public void createButtons()
    {
    	AdvancedMenus.tabs.clear();
    	AdvancedMenus.loadTabs();
        this.buttonList.add(this.btnEditServer = new GuiButton(7, this.width / 2 - 154, this.height - 28, 70, 20, I18n.format("selectServer.edit", new Object[0])));
        this.buttonList.add(this.btnDeleteServer = new GuiButton(2, this.width / 2 - 74, this.height - 28, 70, 20, I18n.format("selectServer.delete", new Object[0])));
        this.buttonList.add(this.btnSelectServer = new GuiButton(1, this.width / 2 - 154, this.height - 52, 100, 20, I18n.format("selectServer.select", new Object[0])));
        this.buttonList.add(new GuiButton(4, this.width / 2 - 50, this.height - 52, 100, 20, I18n.format("selectServer.direct", new Object[0])));
        this.buttonList.add(new GuiButton(3, this.width / 2 + 4 + 50, this.height - 52, 100, 20, I18n.format("selectServer.add", new Object[0])));
        this.buttonList.add(new GuiButton(8, this.width / 2 + 4, this.height - 28, 70, 20, I18n.format("selectServer.refresh", new Object[0])));
        this.buttonList.add(new GuiButton(0, this.width / 2 + 4 + 76, this.height - 28, 75, 20, I18n.format("gui.cancel", new Object[0])));
        this.buttonList.add(new GuiButton(9, this.width - 80, 6, 75, 20, I18n.format("Edit Tabs", new Object[0])));
        
        int buttonID = 10;
        int buttonHeight = 34;
        
        for(ServerTab tab : AdvancedMenus.getServerTabList())
        {
        	String name = tab.getName();
        	boolean isTooLong = false;
        	int max = this.width / 8 + 2;
        	
        	while(this.mc.fontRendererObj.getStringWidth(name + "...") > max - 10)
        	{
        		name = name.substring(0, name.length() - 1);
        		isTooLong = true;
        	}
        	
        	if(isTooLong)
        	{
        		name += "...";
        	}
        	
        	this.buttonList.add(new GuiButton(buttonID, 4, buttonHeight, max, 20, I18n.format(name, new Object[0])));
        	buttonID++;
        	buttonHeight += 22;
        }
        
        for(Object o : this.buttonList)
        {
        	GuiButton btn = (GuiButton) o;
        	
        	if(currentTab.isEmpty())
        	{
        		currentTab = AdvancedMenus.tabs.get(0).getName();
        	}
        	
        	String name = currentTab;
        	boolean isTooLong = false;
        	int max = this.width / 8 + 2;
        	
        	while(this.mc.fontRendererObj.getStringWidth(name + "...") > max - 10)
        	{
        		name = name.substring(0, name.length() - 1);
        		isTooLong = true;
        	}
        	
        	if(isTooLong)
        	{
        		name += "...";
        	}
        	
        	if(btn.displayString.equalsIgnoreCase(name))
        	{
        		btn.enabled = false;
        	}
        }
        this.selectServer(this.serverListSelector.getDatInt());
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();

        if (this.lanServerList.getWasUpdated())
        {
            List var1 = this.lanServerList.getLanServers();
            this.lanServerList.setWasNotUpdated();
            this.serverListSelector.addLanServers(var1);
        }

        this.oldServerPinger.pingPendingNetworks();
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);

        if (this.lanServerDetector != null)
        {
            this.lanServerDetector.interrupt();
            this.lanServerDetector = null;
        }

        this.oldServerPinger.clearPendingNetworks();
    }

    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            GuiListExtended.IGuiListEntry var2 = this.serverListSelector.getDatInt() < 0 ? null : this.serverListSelector.getListEntry(this.serverListSelector.getDatInt());

            if (button.id == 2 && var2 instanceof SkyListEntryNormal)
            {
                String var9 = ((SkyListEntryNormal)var2).getServerData().serverName;

                if (var9 != null)
                {
                    this.deletingServer = true;
                    String var4 = I18n.format("selectServer.deleteQuestion", new Object[0]);
                    String var5 = "\'" + var9 + "\' " + I18n.format("selectServer.deleteWarning", new Object[0]);
                    String var6 = I18n.format("selectServer.deleteButton", new Object[0]);
                    String var7 = I18n.format("gui.cancel", new Object[0]);
                    GuiYesNo var8 = new GuiYesNo(this, var4, var5, var6, var7, this.serverListSelector.getDatInt());
                    this.mc.displayGuiScreen(var8);
                }
            }
            else if (button.id == 1)
            {
                this.connectToSelected();
            }
            else if (button.id == 4)
            {
                this.directConnect = true;
                this.mc.displayGuiScreen(new GuiScreenServerList(this, this.selectedServer = new ServerData(I18n.format("selectServer.defaultName", new Object[0]), "")));
            }
            else if (button.id == 3)
            {
                this.addingServer = true;
                this.mc.displayGuiScreen(new GuiAddSkyServer(this, this.selectedServer = new ServerData(I18n.format("selectServer.defaultName", new Object[0]), ""), currentTab));
            }
            else if (button.id == 7 && var2 instanceof SkyListEntryNormal)
            {
                this.editingServer = true;
                ServerData var3 = ((SkyListEntryNormal)var2).getServerData();
                this.selectedServer = new ServerData(var3.serverName, var3.serverIP);
                this.selectedServer.copyFrom(var3);
                this.mc.displayGuiScreen(new GuiAddSkyServer(this, this.selectedServer, currentTab));
            }
            else if (button.id == 0)
            {
                this.mc.displayGuiScreen(this.parentScreen);
            }
            else if (button.id == 8)
            {
                this.refreshServerList();
            }
            else if(button.id >= 10)
            {
            	int index = button.id - 10;
            	button.enabled = false;
            	
            	for(Object o : this.buttonList)
            	{
            		GuiButton btn = (GuiButton) o;
            		
            		if(btn.id >= 10 && btn.id != button.id)
            		{
            			btn.enabled = true;
            		}
            	}	
            	
            	this.currentTab = AdvancedMenus.tabs.get(index).getName();
            	this.savedServerList.saveServerList();
            	this.savedServerList = new SkyServerList(this.mc, currentTab);
                this.serverListSelector.addServers(savedServerList);
                this.serverListSelector.setSelected(-1);
                this.title = currentTab;
            }
            else if(button.id == 9)
            {
            	this.mc.displayGuiScreen(new GuiTabs(this));
            }
        }
    }

    private void refreshServerList()
    {
        this.mc.displayGuiScreen(new GuiMultiplayerSky(this.parentScreen));
    }

    public void confirmClicked(boolean result, int id)
    {
        GuiListExtended.IGuiListEntry var3 = this.serverListSelector.getDatInt() < 0 ? null : this.serverListSelector.getListEntry(this.serverListSelector.getDatInt());

        if (this.deletingServer)
        {
            this.deletingServer = false;

            if (result && var3 instanceof SkyListEntryNormal)
            {
                this.savedServerList.removeServerData(this.serverListSelector.getDatInt());
                this.savedServerList.saveServerList();
                this.serverListSelector.setSelected(-1);
                this.serverListSelector.addServers(this.savedServerList);
            }

            this.mc.displayGuiScreen(this);
        }
        else if (this.directConnect)
        {
            this.directConnect = false;

            if (result)
            {
                this.connectToServer(this.selectedServer);
            }
            else
            {
                this.mc.displayGuiScreen(this);
            }
        }
        else if (this.addingServer)
        {
            this.addingServer = false;

            if (result)
            {
                this.savedServerList.addServerData(this.selectedServer);
                this.savedServerList.saveServerList();
                this.serverListSelector.setSelected(-1);
                this.serverListSelector.addServers(this.savedServerList);
            }

            this.mc.displayGuiScreen(this);
        }
        else if (this.editingServer)
        {
            this.editingServer = false;

            if (result && var3 instanceof SkyListEntryNormal)
            {
                ServerData var4 = ((SkyListEntryNormal)var3).getServerData();
                var4.serverName = this.selectedServer.serverName;
                var4.serverIP = this.selectedServer.serverIP;
                var4.copyFrom(this.selectedServer);
                this.savedServerList.saveServerList();
                this.serverListSelector.addServers(this.savedServerList);
            }

            this.mc.displayGuiScreen(this);
        }
    }

    /**
     * Fired when a key is typed (except F11 who toggle full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        int var3 = this.serverListSelector.getDatInt();
        GuiListExtended.IGuiListEntry var4 = var3 < 0 ? null : this.serverListSelector.getListEntry(var3);

        if (keyCode == 63)
        {
            this.refreshServerList();
        }
        else
        {
            if (var3 >= 0)
            {
                if (keyCode == 200)
                {
                    if (isShiftKeyDown())
                    {
                        if (var3 > 0 && var4 instanceof SkyListEntryNormal)
                        {
                            this.savedServerList.swapServers(var3, var3 - 1);
                            this.selectServer(this.serverListSelector.getDatInt() - 1);
                            this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());
                            this.serverListSelector.addServers(this.savedServerList);
                        }
                    }
                    else if (var3 > 0)
                    {
                        this.selectServer(this.serverListSelector.getDatInt() - 1);
                        this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());

                        if (this.serverListSelector.getListEntry(this.serverListSelector.getDatInt()) instanceof SkyListEntryLanScan)
                        {
                            if (this.serverListSelector.getDatInt() > 0)
                            {
                                this.selectServer(this.serverListSelector.getSize() - 1);
                                this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());
                            }
                            else
                            {
                                this.selectServer(-1);
                            }
                        }
                    }
                    else
                    {
                        this.selectServer(-1);
                    }
                }
                else if (keyCode == 208)
                {
                    if (isShiftKeyDown())
                    {
                        if (var3 < this.savedServerList.countServers() - 1)
                        {
                            this.savedServerList.swapServers(var3, var3 + 1);
                            this.selectServer(var3 + 1);
                            this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());
                            this.serverListSelector.addServers(this.savedServerList);
                        }
                    }
                    else if (var3 < this.serverListSelector.getSize())
                    {
                        this.selectServer(this.serverListSelector.getDatInt() + 1);
                        this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());

                        if (this.serverListSelector.getListEntry(this.serverListSelector.getDatInt()) instanceof SkyListEntryLanScan)
                        {
                            if (this.serverListSelector.getDatInt() < this.serverListSelector.getSize() - 1)
                            {
                                this.selectServer(this.serverListSelector.getSize() + 1);
                                this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());
                            }
                            else
                            {
                                this.selectServer(-1);
                            }
                        }
                    }
                    else
                    {
                        this.selectServer(-1);
                    }
                }
                else if (keyCode != 28 && keyCode != 156)
                {
                    super.keyTyped(typedChar, keyCode);
                }
                else
                {
                    this.actionPerformed((GuiButton)this.buttonList.get(2));
                }
            }
            else
            {
                super.keyTyped(typedChar, keyCode);
            }
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
    	title = currentTab;
        this.field_146812_y = null;
        this.drawDefaultBackground();
        this.serverListSelector.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, I18n.format(this.title, new Object[0]), this.width / 2, 20, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (this.field_146812_y != null)
        {
            this.drawHoveringText(Lists.newArrayList(Splitter.on("\n").split(this.field_146812_y)), mouseX, mouseY);
        }
    }

    public void connectToSelected()
    {
        GuiListExtended.IGuiListEntry var1 = this.serverListSelector.getDatInt() < 0 ? null : this.serverListSelector.getListEntry(this.serverListSelector.getDatInt());

        if (var1 instanceof SkyListEntryNormal)
        {
            this.connectToServer(((SkyListEntryNormal)var1).getServerData());
        }
        else if (var1 instanceof SkyListEntryLanDetected)
        {
            LanServerDetector.LanServer var2 = ((SkyListEntryLanDetected)var1).getLanServer();
            this.connectToServer(new ServerData(var2.getServerMotd(), var2.getServerIpPort()));
        }
    }

    private void connectToServer(ServerData server)
    {
        this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, server));
    }

    public void selectServer(int index)
    {
        this.serverListSelector.setSelected(index);
        GuiListExtended.IGuiListEntry var2 = index < 0 ? null : this.serverListSelector.getListEntry(index);
        this.btnSelectServer.enabled = false;
        this.btnEditServer.enabled = false;
        this.btnDeleteServer.enabled = false;

        if (var2 != null && !(var2 instanceof SkyListEntryLanScan))
        {
            this.btnSelectServer.enabled = true;

            if (var2 instanceof SkyListEntryNormal)
            {
                this.btnEditServer.enabled = true;
                this.btnDeleteServer.enabled = true;
            }
        }
    }

    public OldServerPinger getOldServerPinger()
    {
        return this.oldServerPinger;
    }

    public void func_146793_a(String p_146793_1_)
    {
        this.field_146812_y = p_146793_1_;
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.serverListSelector.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Called when a mouse button is released.  Args : mouseX, mouseY, releaseButton
     */
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY, state);
        this.serverListSelector.mouseReleased(mouseX, mouseY, state);
    }

    public SkyServerList getServerList()
    {
        return this.savedServerList;
    }

    public boolean func_175392_a(SkyListEntryNormal p_175392_1_, int p_175392_2_)
    {
        return p_175392_2_ > 0;
    }

    public boolean func_175394_b(SkyListEntryNormal p_175394_1_, int p_175394_2_)
    {
        return p_175394_2_ < this.savedServerList.countServers() - 1;
    }

    public void moveDown(SkyListEntryNormal serverEntry, int index, boolean replace)
    {
        int var4 = replace ? 0 : index - 1;
        this.savedServerList.swapServers(index, var4);

        if (this.serverListSelector.getDatInt() == index)
        {
            this.selectServer(var4);
        }

        this.serverListSelector.addServers(this.savedServerList);
    }

    public void moveUp(SkyListEntryNormal serverEntry, int index, boolean replace)
    {
        int var4 = replace ? this.savedServerList.countServers() - 1 : index + 1;
        this.savedServerList.swapServers(index, var4);

        if (this.serverListSelector.getDatInt() == index)
        {
            this.selectServer(var4);
        }

        this.serverListSelector.addServers(this.savedServerList);
    }
}
