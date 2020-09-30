package me.lennartVH01.util;

import net.minecraft.server.v1_16_R2.ChatMessageType;
import net.minecraft.server.v1_16_R2.IChatBaseComponent;
import net.minecraft.server.v1_16_R2.PacketPlayOutChat;

import java.util.UUID;

import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;

public class ChatUtil {
	public static void send(Player player, IChatBaseComponent chat) {
		// still hacky, still no official bukkit support
		PacketPlayOutChat packet = new PacketPlayOutChat(chat, ChatMessageType.CHAT, UUID.randomUUID());
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}
	public static IChatBaseComponent stackToChat(org.bukkit.inventory.ItemStack stack){
		return CraftItemStack.asNMSCopy(stack).C();
	}
	public static IChatBaseComponent fromRawJSON(String json){
		return IChatBaseComponent.ChatSerializer.a(json);
	}
	public static String getName(org.bukkit.inventory.ItemStack stack){
		return CraftItemStack.asNMSCopy(stack).getItem().getName() + ".name";
	}
}
