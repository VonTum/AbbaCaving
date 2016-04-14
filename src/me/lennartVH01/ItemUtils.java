package me.lennartVH01;

import java.util.Map;


public class ItemUtils {
	public static boolean isMapInMap(Map<String, Object> testMap, Map<String, Object> elderMap){
		for(String key:elderMap.keySet()){
			Object o = testMap.get(key);
			if(o == null){
				return false;
			}
			
			if(o instanceof Map<?, ?>){
				
			}
		}
		return true;
	}
}
