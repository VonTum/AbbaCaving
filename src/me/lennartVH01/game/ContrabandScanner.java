package me.lennartVH01.game;

import java.util.ArrayList;
import java.util.List;





import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ContrabandScanner{
	private ItemStack[] illegalItems;
	
	
	
	public ContrabandScanner(ItemStack[] illegalItems){
		this.illegalItems = illegalItems;
	}
	
	public List<ItemStack> getContraband(Inventory inv){
		List<ItemStack> totalContraband = new ArrayList<ItemStack>();
		for(ItemStack detectionStack:illegalItems){
			for(ItemStack stack:inv){
				if(detectionStack.isSimilar(stack)){
					totalContraband.add(stack);
					break;
				}
			}
		}
		return totalContraband;
	}
	
	public boolean hasContraband(Inventory inv){
		for(ItemStack detectionStack:illegalItems){
			for(ItemStack stack:inv){
				if(detectionStack.isSimilar(stack)){
					return true;
				}
			}
		}
		return false;
	}
}
