package me.lennartVH01;

import org.bukkit.permissions.Permissible;



public class Permission{
	public static boolean debug = false;
	private String perm;
	private Permission(String perm){this.perm = perm;}
	public boolean has(Permissible p){
		return p.hasPermission(perm) || debug;
	}
	
	public static Permission JOIN_FULL = new Permission("AbbaCaving.joinFull");
	public static Permission JOIN_CLOSED = new Permission("AbbaCaving.joinClosed");
	public static Permission JOIN_WHITELISTED = new Permission("AbbaCaving.joinWhilelisted");
	
	public static Permission BASIC = new Permission("AbbaCaving.basic");
	public static Permission ADMIN = new Permission("AbbaCaving.admin");
}
