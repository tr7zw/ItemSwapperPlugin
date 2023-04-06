package dev.tr7zw.itemswapperplugin;

import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

public class CommandRefill implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        ByteArrayDataInput buf = ByteStreams.newDataInput(message);
        int targetSlot = buf.readInt();
        ItemStack target = player.getInventory().getContents()[targetSlot];
        if (target == null || target.getType() == Material.AIR) {
            return;
        }
        int space = target.getMaxStackSize() - target.getAmount();
        if(space <= 0) {
            // nothing to do
            return;
        }
        for(int i = 0; i < player.getInventory().getContents().length; i++) {
            ItemStack shulker = player.getInventory().getContents()[i];
            if (shulker != null && shulker.getType() != Material.AIR && shulker.getItemMeta() instanceof BlockStateMeta bsm && bsm.getBlockState() instanceof ShulkerBox box) {
                Inventory content = box.getInventory();
                if(content != null && !content.isEmpty()) {
                    boolean boxChanged = false;
                    for(int entry = 0; entry < content.getSize(); entry++) {
                        ItemStack boxItem = content.getItem(entry);
                        if(boxItem != null && boxItem.isSimilar(target)) {
                            // same, use to restock
                            int amount = Math.min(space, boxItem.getAmount());
                            target.setAmount(target.getAmount() + amount);
                            boxItem.setAmount(boxItem.getAmount() - amount);
                            space -= amount;
                            boxChanged = true;
                            if(space <= 0) {
                                break;
                            }
                        }
                    }
                    if(boxChanged) {
                        bsm.setBlockState(box);
                        shulker.setItemMeta(bsm);
                    }
                }
            }
        }
    }

}
