package me.lennartVH01;

import org.bukkit.command.CommandSender;



public enum Permission{
	
	JOIN_FULL("AbbaCaving.joinFull"),
	JOIN_CLOSED("AbbaCaving.joinClosed"),
	JOIN_WHITELISTED("AbbaCaving.joinWhilelisted"),
	REGISTER_CHEST("AbbaCaving.registerChest");
	
	private final String permission;
	private Permission(String permission){
		this.permission = permission;
	}
	public static boolean hasPermission(CommandSender sender, Permission perm){
		return hasPermission(sender, perm.permission);
	}
	public static boolean hasPermission(CommandSender sender, String perm){
		return sender.hasPermission(perm);
	}
	public String toString(){
		return permission;
	}
}
