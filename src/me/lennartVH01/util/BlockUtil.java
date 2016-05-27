package me.lennartVH01.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;

public class BlockUtil {
	public static final BlockFace[] SIGN_ATTACH_DIRECTIONS = new BlockFace[]{BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
	
	
	public static boolean isChest(Block block){
		return block.getState() instanceof Chest;
	}
	public static boolean isSign(Block block){
		return block.getState().getData() instanceof org.bukkit.material.Sign;
	}
	
	public static Block getAttachedBlock(Block b){
		return b.getRelative(((org.bukkit.material.Sign) b.getState().getData()).getAttachedFace());
	}
	public static List<org.bukkit.block.Sign> getAttachedSigns(Block block){
		List<org.bukkit.block.Sign> signs = new ArrayList<org.bukkit.block.Sign>(5);
		
		for(BlockFace face:SIGN_ATTACH_DIRECTIONS){
			Block b = block.getRelative(face);
			if(b.getState() instanceof org.bukkit.material.Sign && face == ((org.bukkit.material.Sign) block.getState().getData()).getAttachedFace().getOppositeFace()){
				signs.add((org.bukkit.block.Sign) b.getState());
			}
		}
		return signs;
	}
}
