package dev.tr7zw.itemswapperplugin;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
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

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "itemswapper:enableshulker");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "itemswapper:swap", this);
    }

    @EventHandler
    public void onChannel(PlayerRegisterChannelEvent event) {
        if (!event.getChannel().equals("itemswapper:enableshulker"))
            return;
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeBoolean(true);
        event.getPlayer().sendPluginMessage(this, "itemswapper:enableshulker", out.toByteArray());
    }

    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        ByteArrayDataInput buf = ByteStreams.newDataInput(message);
        int inventory = buf.readInt();
        int slot = buf.readInt();
        ItemStack shulker = player.getInventory().getContents()[inventory];
        if(shulker == null || shulker.getType() == Material.AIR) {
            return;
        }
        ItemMeta meta = shulker.getItemMeta();
        if(meta instanceof BlockStateMeta bsm && bsm.getBlockState() instanceof ShulkerBox box) {
            ItemStack tmp = box.getInventory().getItem(slot);
            box.getInventory().setItem(slot, player.getInventory().getItemInMainHand());
            player.getInventory().setItemInMainHand(tmp);
            bsm.setBlockState(box);
            shulker.setItemMeta(meta);
        }
    }

}
