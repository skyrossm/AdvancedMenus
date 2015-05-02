package com.skyrossm.mods.advancedmenus.client.gui.tabs;

public class ServerTab {
	
	public String tabName;
	
	public ServerTab(String n){
		tabName = n;
	}
	
	public String getName(){
		return tabName;
	}
	
	public void setName(String n){
		tabName = n;
	}
}
