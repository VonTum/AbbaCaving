package me.lennartVH01.util;

import net.minecraft.server.v1_9_R2.IChatBaseComponent;
import net.minecraft.server.v1_9_R2.PacketPlayOutChat;

import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;

public class ChatUtil {
	public static void send(Player player, IChatBaseComponent chat) {
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(chat));
	}
	public static IChatBaseComponent stackToChat(org.bukkit.inventory.ItemStack stack){
		return CraftItemStack.asNMSCopy(stack).B();
	}
	public static IChatBaseComponent fromRawJSON(String json){
		return IChatBaseComponent.ChatSerializer.a(json);
	}
	public static String getName(org.bukkit.inventory.ItemStack stack){
		return CraftItemStack.asNMSCopy(stack).getItem().getName() + ".name";
	}
}
