package me.lennartVH01;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.lennartVH01.game.CollectionInfo;
import me.lennartVH01.util.ChatUtil;

public class CollectionHandler {
	private static Map<Player, List<CollectionInfo>> itemsLeftToBeCollected = new HashMap<Player, List<CollectionInfo>>();
	
	private static void addItemToList(List<CollectionInfo> curList, CollectionInfo newItem){
		for(CollectionInfo cur : curList){
			if(cur.item.equals(newItem.item)){
				cur.count += newItem.count;
				return;
			}
		}
		curList.add(newItem);
	}
	
	public static void pushItemsForCollection(Player player, List<CollectionInfo> newItemsForCollection){
		if(itemsLeftToBeCollected.containsKey(player)){
			List<CollectionInfo> items = itemsLeftToBeCollected.get(player);
			for(CollectionInfo currentlyAdding : newItemsForCollection){
				addItemToList(items, currentlyAdding);
			}
		}else{
			itemsLeftToBeCollected.put(player, newItemsForCollection);
		}
		printCollectableItems(player);
	}
	
	// returns true if all items could be given
	private static void give(Player player, CollectionInfo item){
		while(item.count > 0){
			int numberToGive = Math.min(item.count, item.item.getMaxStackSize());
			ItemStack newItemStack = item.item.clone();
			newItemStack.setAmount(numberToGive);
			HashMap<Integer, ItemStack> couldntStore = player.getInventory().addItem(newItemStack);
			
			if(!couldntStore.isEmpty()){
				int leftOver = couldntStore.get(0).getAmount();
				int given = numberToGive - leftOver;
				item.count -= given;
				return;
			}else{
				item.count -= numberToGive;
			}
		}
	}
	
	public static boolean collect(Player player){
		if(itemsLeftToBeCollected.containsKey(player)){
			List<CollectionInfo> items = itemsLeftToBeCollected.get(player);
			
			
			for(int i = 0; i < items.size();){
				give(player, items.get(i));
				if(items.get(i).count == 0){
					items.remove(i);
				}else{
					i++;
				}
			}
			if(items.isEmpty()){
				itemsLeftToBeCollected.remove(player);
				player.sendMessage(Messages.allItemsCollected);
			}else{
				printCollectableItems(player);
			}
			
			return true;
		}else{
			return false;
		}
	}
	
	private static void printCollectableItems(Player player){
		if(itemsLeftToBeCollected.containsKey(player)){
			List<CollectionInfo> items = itemsLeftToBeCollected.get(player);
			player.sendMessage(Messages.pickupCollected);
			
			for(CollectionInfo i : items){
				ChatUtil.sendItemStack(player, i.item, "- " + i.count + "x ");
			}
		}
	}
}
