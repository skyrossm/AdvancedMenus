package com.skyrossm.mods.advancedmenus.client.gui.serverlist;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;

import java.awt.image.BufferedImage;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiListExtended.IGuiListEntry;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Charsets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class SkyListEntryNormal implements IGuiListEntry
{
    private static final Logger logger = LogManager.getLogger();
    private static final ThreadPoolExecutor threadPooler = new ScheduledThreadPoolExecutor(5, (new ThreadFactoryBuilder()).setNameFormat("Server Pinger #%d").setDaemon(true).build());
    private static final ResourceLocation unknownServer = new ResourceLocation("textures/misc/unknown_server.png");
    private static final ResourceLocation selectionTex = new ResourceLocation("textures/gui/server_selection.png");
    private final GuiMultiplayerSky guiMultiplayer;
    private final Minecraft mc;
    private final ServerData serverData;
    private final ResourceLocation resourceLocation;
    private String string1;
    private DynamicTexture texture;
    private long long1;

    protected SkyListEntryNormal(GuiMultiplayerSky guiM, ServerData serverD)
    {
        this.guiMultiplayer = guiM;
        this.serverData = serverD;
        this.mc = Minecraft.getMinecraft();
        this.resourceLocation = new ResourceLocation("servers/" + serverD.serverIP + "/icon");
        this.texture = (DynamicTexture)this.mc.getTextureManager().getTexture(this.resourceLocation);
    }

    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected)
    {
        if (!this.serverData.field_78841_f)
        {
            this.serverData.field_78841_f = true;
            this.serverData.pingToServer = -2L;
            this.serverData.serverMOTD = "";
            this.serverData.populationInfo = "";
            threadPooler.submit(new Runnable()
            {
                private static final String __OBFID = "CL_00000818";
                public void run()
                {
                    try
                    {
                        SkyListEntryNormal.this.guiMultiplayer.getOldServerPinger().ping(SkyListEntryNormal.this.serverData);
                    }
                    catch (UnknownHostException var2)
                    {
                        SkyListEntryNormal.this.serverData.pingToServer = -1L;
                        SkyListEntryNormal.this.serverData.serverMOTD = EnumChatFormatting.DARK_RED + "Can\'t resolve hostname";
                    }
                    catch (Exception var3)
                    {
                        SkyListEntryNormal.this.serverData.pingToServer = -1L;
                        SkyListEntryNormal.this.serverData.serverMOTD = EnumChatFormatting.DARK_RED + "Can\'t connect to server.";
                    }
                }
            });
        }

        boolean var9 = this.serverData.version > 47;
        boolean var10 = this.serverData.version < 47;
        boolean var11 = var9 || var10;
        this.mc.fontRendererObj.drawString(this.serverData.serverName, x + 32 + 3, y + 1, 16777215);
        List var12 = this.mc.fontRendererObj.listFormattedStringToWidth(this.serverData.serverMOTD, listWidth - 32 - 2);

        for (int var13 = 0; var13 < Math.min(var12.size(), 2); ++var13)
        {
            this.mc.fontRendererObj.drawString((String)var12.get(var13), x + 32 + 3, y + 12 + this.mc.fontRendererObj.FONT_HEIGHT * var13, 8421504);
        }

        String var23 = var11 ? EnumChatFormatting.DARK_RED + this.serverData.gameVersion : this.serverData.populationInfo;
        int var14 = this.mc.fontRendererObj.getStringWidth(var23);
        this.mc.fontRendererObj.drawString(var23, x + listWidth - var14 - 15 - 2, y + 1, 8421504);
        byte var15 = 0;
        String var17 = null;
        int var16;
        String var18;

        if (var11)
        {
            var16 = 5;
            var18 = var9 ? "Client out of date!" : "Server out of date!";
            var17 = this.serverData.playerList;
        }
        else if (this.serverData.field_78841_f && this.serverData.pingToServer != -2L)
        {
            if (this.serverData.pingToServer < 0L)
            {
                var16 = 5;
            }
            else if (this.serverData.pingToServer < 150L)
            {
                var16 = 0;
            }
            else if (this.serverData.pingToServer < 300L)
            {
                var16 = 1;
            }
            else if (this.serverData.pingToServer < 600L)
            {
                var16 = 2;
            }
            else if (this.serverData.pingToServer < 1000L)
            {
                var16 = 3;
            }
            else
            {
                var16 = 4;
            }

            if (this.serverData.pingToServer < 0L)
            {
                var18 = "(no connection)";
            }
            else
            {
                var18 = this.serverData.pingToServer + "ms";
                var17 = this.serverData.playerList;
            }
        }
        else
        {
            var15 = 1;
            var16 = (int)(Minecraft.getSystemTime() / 100L + (long)(slotIndex * 2) & 7L);

            if (var16 > 4)
            {
                var16 = 8 - var16;
            }

            var18 = "Pinging...";
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(Gui.icons);
        Gui.drawModalRectWithCustomSizedTexture(x + listWidth - 15, y, (float)(var15 * 10), (float)(176 + var16 * 8), 10, 8, 256.0F, 256.0F);

        if (this.serverData.getBase64EncodedIconData() != null && !this.serverData.getBase64EncodedIconData().equals(this.string1))
        {
            this.string1 = this.serverData.getBase64EncodedIconData();
            this.prepareServerIcon();
            this.guiMultiplayer.getServerList().saveServerList();
        }

        if (this.texture != null)
        {
            this.drawSelected(x, y, this.resourceLocation);
        }
        else
        {
            this.drawSelected(x, y, unknownServer);
        }

        int var19 = mouseX - x;
        int var20 = mouseY - y;

        if (var19 >= listWidth - 15 && var19 <= listWidth - 5 && var20 >= 0 && var20 <= 8)
        {
            this.guiMultiplayer.func_146793_a(var18);
        }
        else if (var19 >= listWidth - var14 - 15 - 2 && var19 <= listWidth - 15 - 2 && var20 >= 0 && var20 <= 8)
        {
            this.guiMultiplayer.func_146793_a(var17);
        }

        if (this.mc.gameSettings.touchscreen || isSelected)
        {
            this.mc.getTextureManager().bindTexture(selectionTex);
            Gui.drawRect(x, y, x + 32, y + 32, -1601138544);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            int var21 = mouseX - x;
            int var22 = mouseY - y;

            if (this.func_178013_b())
            {
                if (var21 < 32 && var21 > 16)
                {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                }
                else
                {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                }
            }

            if (this.guiMultiplayer.func_175392_a(this, slotIndex))
            {
                if (var21 < 16 && var22 < 16)
                {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                }
                else
                {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                }
            }

            if (this.guiMultiplayer.func_175394_b(this, slotIndex))
            {
                if (var21 < 16 && var22 > 16)
                {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                }
                else
                {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                }
            }
        }
    }

    protected void drawSelected(int x, int y, ResourceLocation texture)
    {
        this.mc.getTextureManager().bindTexture(texture);
        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
        GlStateManager.disableBlend();
    }

    private boolean func_178013_b()
    {
        return true;
    }

    private void prepareServerIcon()
    {
        if (this.serverData.getBase64EncodedIconData() == null)
        {
            this.mc.getTextureManager().deleteTexture(this.resourceLocation);
            this.texture = null;
        }
        else
        {
            ByteBuf var2 = Unpooled.copiedBuffer(this.serverData.getBase64EncodedIconData(), Charsets.UTF_8);
            ByteBuf var3 = Base64.decode(var2);
            BufferedImage var1;
            label74:
            {
                try
                {
                    var1 = TextureUtil.readBufferedImage(new ByteBufInputStream(var3));
                    Validate.validState(var1.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
                    Validate.validState(var1.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
                    break label74;
                }
                catch (Exception var8)
                {
                    logger.error("Invalid icon for server " + this.serverData.serverName + " (" + this.serverData.serverIP + ")", var8);
                    this.serverData.setBase64EncodedIconData((String)null);
                }
                finally
                {
                    var2.release();
                    var3.release();
                }

                return;
            }

            if (this.texture == null)
            {
                this.texture = new DynamicTexture(var1.getWidth(), var1.getHeight());
                this.mc.getTextureManager().loadTexture(this.resourceLocation, this.texture);
            }

            var1.getRGB(0, 0, var1.getWidth(), var1.getHeight(), this.texture.getTextureData(), 0, var1.getWidth());
            this.texture.updateDynamicTexture();
        }
    }

    /**
     * Returns true if the mouse has been pressed on this control.
     */
    public boolean mousePressed(int p_148278_1_, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_)
    {
        if (p_148278_5_ <= 32)
        {
            if (p_148278_5_ < 32 && p_148278_5_ > 16 && this.func_178013_b())
            {
                this.guiMultiplayer.selectServer(p_148278_1_);
                this.guiMultiplayer.connectToSelected();
                return true;
            }

            if (p_148278_5_ < 16 && p_148278_6_ < 16 && this.guiMultiplayer.func_175392_a(this, p_148278_1_))
            {
                this.guiMultiplayer.moveDown(this, p_148278_1_, GuiScreen.isShiftKeyDown());
                return true;
            }

            if (p_148278_5_ < 16 && p_148278_6_ > 16 && this.guiMultiplayer.func_175394_b(this, p_148278_1_))
            {
                this.guiMultiplayer.moveUp(this, p_148278_1_, GuiScreen.isShiftKeyDown());
                return true;
            }
        }

        this.guiMultiplayer.selectServer(p_148278_1_);

        if (Minecraft.getSystemTime() - this.long1 < 250L)
        {
            this.guiMultiplayer.connectToSelected();
        }

        this.long1 = Minecraft.getSystemTime();
        return false;
    }

    public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {}

    /**
     * Fired when the mouse button is released. Arguments: index, x, y, mouseEvent, relativeX, relativeY
     */
    public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {}

    public ServerData getServerData()
    {
        return this.serverData;
    }
}
