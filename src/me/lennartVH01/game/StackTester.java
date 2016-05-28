package me.lennartVH01.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;


public class StackTester{
	private static final Pattern testableStackPattern = Pattern.compile("^(\\w+)(?:\\((\\d+)\\))?(?:=(\\d+))?$"); // material_name(damage)=amount
	
	private final List<Predicate<ItemStack>> tests = new ArrayList<Predicate<ItemStack>>();
	private final int value;
	private final ItemStack stack;
	
	public StackTester(String input) throws InvalidConfigurationException{
		Matcher matcher = testableStackPattern.matcher(input);
		if(matcher.matches()){
			
			//type will always be defined, as per definition
			final Material type = Material.getMaterial(matcher.group(1));
			tests.add((stack) -> type == stack.getType());
			
			stack = new ItemStack(type);
			
			if(matcher.group(2) != null && !matcher.group(2).isEmpty()){
				final short damage = Short.parseShort(matcher.group(2));
				tests.add((stack) -> damage == stack.getDurability());
				stack.setDurability(damage);
			}
			if(matcher.group(3) != null && !matcher.group(3).isEmpty()){
				value = Integer.parseInt(matcher.group(3));
			}else{
				value = 0;
			}
		}else{
			throw new InvalidConfigurationException();
		}
	}
	public StackTester(Map<String, Object> input){
		Object value;
		
		value = input.get("type");
		if(value != null && value instanceof String){
			final Material type = Material.getMaterial((String) value);
			tests.add((stack) -> type == stack.getType());
		}
		
		value = input.get("damage");
		if(value != null && value instanceof Number){
			final short damage = ((Number) value).shortValue();
			tests.add((stack) -> damage == stack.getDurability());
		}
		
		//TODO Other item tags here
		
		
		value = input.get("value");
		if(value != null && value instanceof Number){
			this.value = (int) value;
		}else{
			this.value = 0;
		}
		
		stack = ItemStack.deserialize(input);
	}
	public boolean isSimilar(ItemStack stack){
		if(stack == null)
			return false;
		for(Predicate<ItemStack> test:tests){
			if(!test.test(stack))
				return false;
		}
		return true;
	}
	public int getValue(){return value;}
	public ItemStack asItemStack(){return stack;}
}
