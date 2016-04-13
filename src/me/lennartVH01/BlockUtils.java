package me.lennartVH01;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;

public class BlockUtils {
	public static boolean isChest(Block block){
		return block.getState() instanceof Chest;
	}
	public static boolean isSign(Block block){
		return block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN;
	}
	public static Block getAttachedBlock(Block b){
		return b.getRelative(((org.bukkit.material.Sign) b.getState().getData()).getAttachedFace());
	}
}
