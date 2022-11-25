package dev.tr7zw.itemswapperplugin;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class ItemSwapperPlugin extends JavaPlugin implements Listener, PluginMessageListener {

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
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "itemswapper:disable");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "itemswapper:swap", this);
    }

    @EventHandler
    public void onChannel(PlayerRegisterChannelEvent event) {
        if (!blockModUsage && event.getChannel().equals("itemswapper:enableshulker")) {
            sendShulkerSupportPacket(event.getPlayer(), true);
        }
        if (blockModUsage && event.getChannel().equals("itemswapper:disable")) {
            sendDisableModPacket(event.getPlayer(), true);
        }
    }

    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        ByteArrayDataInput buf = ByteStreams.newDataInput(message);
        int inventory = buf.readInt();
        int slot = buf.readInt();
        ItemStack shulker = player.getInventory().getContents()[inventory];
        if (shulker == null || shulker.getType() == Material.AIR) {
            return;
        }
        ItemMeta meta = shulker.getItemMeta();
        if (meta instanceof BlockStateMeta bsm && bsm.getBlockState() instanceof ShulkerBox box) {
            ItemStack tmp = box.getInventory().getItem(slot);
            box.getInventory().setItem(slot, player.getInventory().getItemInMainHand());
            player.getInventory().setItemInMainHand(tmp);
            bsm.setBlockState(box);
            shulker.setItemMeta(meta);
        }
    }

    public void sendShulkerSupportPacket(Player p, boolean enabled) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeBoolean(enabled);
        p.sendPluginMessage(this, "itemswapper:enableshulker", out.toByteArray());
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
