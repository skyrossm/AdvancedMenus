package com.skyrossm.mods.advancedmenus.events;

import com.skyrossm.mods.advancedmenus.client.gui.serverlist.GuiMultiplayerSky;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreenServerList;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventManager {
	@SubscribeEvent
	public void onGuiOpenEvent(GuiOpenEvent event) {
		if(event.gui instanceof GuiMultiplayer) {
			event.gui = new GuiMultiplayerSky(new GuiMainMenu());
		}
	}
}
