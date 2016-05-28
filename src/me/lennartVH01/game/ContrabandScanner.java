package me.lennartVH01.game;

import java.util.ArrayList;
import java.util.List;





import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ContrabandScanner{
	private StackTester[] illegalItems;
	
	
	
	public ContrabandScanner(StackTester[] illegalItems){
		this.illegalItems = illegalItems;
	}
	
	public List<ItemStack> getContraband(Inventory inv){
		List<ItemStack> totalContraband = new ArrayList<ItemStack>();
		for(StackTester tester:illegalItems){
			for(ItemStack stack:inv){
				if(tester.isSimilar(stack)){
					totalContraband.add(stack);
					break;
				}
			}
		}
		return totalContraband;
	}
	
	public boolean hasContraband(Inventory inv){
		for(StackTester tester:illegalItems){
			for(ItemStack stack:inv){
				if(tester.isSimilar(stack)){
					return true;
				}
			}
		}
		return false;
	}
}
