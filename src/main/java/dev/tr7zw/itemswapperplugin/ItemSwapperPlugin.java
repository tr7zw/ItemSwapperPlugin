package dev.tr7zw.itemswapperplugin;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class ItemSwapperPlugin extends JavaPlugin implements Listener {

    public static ItemSwapperPlugin instance;
    private boolean blockModUsage = false;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        FileConfiguration config = this.getConfig();
        config.options().copyDefaults(true);
        config.addDefault("blockModUsage", false);
        this.saveConfig();
        this.blockModUsage = config.getBoolean("blockModUsage", false);
        Bukkit.getPluginManager().registerEvents(this, this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "itemswapper:enableshulker");
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "itemswapper:enablerefill");
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "itemswapper:disable");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "itemswapper:swap", new CommandSwap());
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "itemswapper:refill", new CommandRefill());
    }

    @EventHandler
    public void onChannel(PlayerRegisterChannelEvent event) {
        if (!blockModUsage && event.getChannel().equals("itemswapper:enableshulker")) {
            sendShulkerSupportPacket(event.getPlayer(), true);
        }
        if (!blockModUsage && event.getChannel().equals("itemswapper:enablerefill")) {
            sendRefillSupportPacket(event.getPlayer(), true);
        }
        if (blockModUsage && event.getChannel().equals("itemswapper:disable")) {
            sendDisableModPacket(event.getPlayer(), true);
        }
    }

    public void sendShulkerSupportPacket(Player p, boolean enabled) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeBoolean(enabled);
        p.sendPluginMessage(this, "itemswapper:enableshulker", out.toByteArray());
    }
    
    public void sendRefillSupportPacket(Player p, boolean enabled) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeBoolean(enabled);
        p.sendPluginMessage(this, "itemswapper:enablerefill", out.toByteArray());
    }

    public void sendDisableModPacket(Player p, boolean disabled) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeBoolean(disabled);
        p.sendPluginMessage(this, "itemswapper:disable", out.toByteArray());
    }

    public boolean isBlockModUsage() {
        return blockModUsage;
    }

    public void setBlockModUsage(boolean blockModUsage) {
        this.blockModUsage = blockModUsage;
    }

}
