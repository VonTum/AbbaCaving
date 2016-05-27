package me.lennartVH01.game;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


public class TesterStackFactory{
	/*private static final Pattern testableStackPattern = Pattern.compile("(\\w+)\\((\\d+)\\)"); // material_name(damage): amount
	
	public static StackTestable[] deserialize(List<Object> inputList){
		StackTestable[] testStacks = new StackTestable[inputList.size()];
		
		for(int i = 0; i < inputList.size(); i++){
			Object entry = inputList.get(i);

			String name;
			Object data = null;
			if(entry instanceof String){
				testStacks[i] = new MaterialTestStack(Material.getMaterial((String) entry));
				continue;
			}else if(entry instanceof Map){
				if(((Map) entry).size() == 1){
					Map.Entry itemEntry = (Map.Entry) ((Map) entry).entrySet().iterator().next();
					name = (String) itemEntry.getKey();
					data = itemEntry.getValue();
				}else{
					continue;
				}
			}else{
				continue;
			}
			
			
			short damage = -1;
			int amount = -1;
			Matcher match = testableStackPattern.matcher(name);
			Material material = Material.getMaterial(match.group(1));
			if(match.group(2) != null){
				damage = Short.parseShort(match.group(2));
			}
			if(data == null || data instanceof Integer){
				amount = (Integer) data;
			}else if(data instanceof Map){
				
			}
		}
	}
	
	
	private interface StackTestable{
		public boolean compare(ItemStack testStack);
		public ItemStack asItemStack();
	}
	private static class MaterialTestStack implements StackTestable{
		private final Material material;
		private final ItemStack stack;
		public MaterialTestStack(Material material){
			this(material, 0);
		}
		public MaterialTestStack(Material material, int amount){
			this.material = material;
			this.stack = new ItemStack(material, amount);
		}
		
		@Override public boolean compare(ItemStack testStack){
			return material == testStack.getType();
		}
		@Override public ItemStack asItemStack(){return stack;}
	}
	private static class MaterialDamageTestStack implements StackTestable{
		private final Material material;
		private final short damage;
		private final ItemStack stack;
		public MaterialDamageTestStack(Material material, short damage){
			this(material, damage, 0);
		}
		public MaterialDamageTestStack(Material material, short damage, int amount){
			this.material = material;
			this.damage = damage;
			this.stack = new ItemStack(material, amount, damage);
		}
		
		@Override public boolean compare(ItemStack testStack){
			return material == testStack.getType() && testStack.getDurability() == damage;
		}
		@Override public ItemStack asItemStack(){return stack;}
	}
	private static class ItemStackTestStack implements StackTestable{
		private final ItemStack stack;
		public ItemStackTestStack(ItemStack testStack){
			this.stack = testStack;
		}
		
		@Override public boolean compare(ItemStack testStack){
			return stack.isSimilar(testStack);
		}
		@Override public ItemStack asItemStack(){return stack;}
	}*/
}
