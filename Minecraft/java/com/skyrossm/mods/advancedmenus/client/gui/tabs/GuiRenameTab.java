package com.skyrossm.mods.advancedmenus.client.gui.tabs;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;

import org.lwjgl.input.Keyboard;

import com.skyrossm.mods.advancedmenus.AdvancedMenus;

public class GuiRenameTab extends GuiScreen
{
    private final GuiScreen parentScreen;
    private GuiTextField tabName;
    private String tab;
    private boolean exists = false;
    private GuiButton serverResourcePacks;

    public GuiRenameTab(GuiScreen parent, String tabN)
    {
    	tab = tabN;
        this.parentScreen = parent;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        this.tabName.updateCursorCounter();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 18, I18n.format("Rename Tab", new Object[0])));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 18, I18n.format("gui.cancel", new Object[0])));
        this.tabName = new GuiTextField(1, this.fontRendererObj, this.width / 2 - 100, 106, 200, 20);
        this.tabName.setMaxStringLength(128);
        this.tabName.setText(tab);
        ((GuiButton)this.buttonList.get(0)).enabled = this.tabName.getText().length() > 0 && this.tabName.getText().split(":").length > 0;
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            if (button.id == 1)
            {
                mc.displayGuiScreen(this.parentScreen);
            }
            else if (button.id == 0)
            {
            	if(AdvancedMenus.getTabByName(this.tabName.getText()) != null){
            		button.enabled = false;
            		exists = true;
            	}else{
            		AdvancedMenus.replaceTab(AdvancedMenus.getTabByName(this.tab), new ServerTab(this.tabName.getText()));
            		mc.displayGuiScreen(this.parentScreen);
            	}
            }
        }
    }

    /**
     * Fired when a key is typed (except F11 who toggle full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        this.tabName.textboxKeyTyped(typedChar, keyCode);

        if (keyCode == 15)
        {
            this.tabName.setFocused(!this.tabName.isFocused());
        }
        ((GuiButton)this.buttonList.get(0)).enabled = this.tabName.getText().length() > 0 && this.tabName.getText().split(":").length > 0;
        exists = false;
        if (keyCode == 28 || keyCode == 156)
        {
            this.actionPerformed((GuiButton)this.buttonList.get(0));
        }
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.tabName.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, I18n.format("Rename Tab", new Object[0]), this.width / 2, 17, 16777215);
        if(exists){
        	this.drawCenteredString(this.fontRendererObj, I18n.format("That tab already exists!", new Object[0]), this.width / 2, 34, 16777215);
        }
        this.drawString(this.fontRendererObj, I18n.format("Enter Tab Name", new Object[0]), this.width / 2 - 100, 94, 10526880);
        this.tabName.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
