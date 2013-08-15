import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;


public class PNUtilities {


	public static boolean isSubset(List<String> subItem, List<String> superItem){
		return superItem.containsAll(subItem);
	}
	
	public static List<String> compineTwoItems(List<String> item1, List<String> item2) {
		if(item1.size() != item2.size()) return null;
		
		HashSet<String> newItem = new HashSet<String>();
		newItem.addAll(item1);
		newItem.addAll(item2);
		
        if(newItem.size() == item1.size() + 1) {
        	return new ArrayList<String>(newItem);
        }else{
        	return null;
        }
	}
	
	
	public static List<List<String>> getAllSubsets(List<String> item){
		
		HashMap<Integer, List<List<String>>> map = new HashMap<Integer, List<List<String>>>();
		List<List<String>> level1 = new ArrayList<List<String>>();
		for(String str : item) {
			List<String> level1Item = new ArrayList<String>();
			level1Item.add(str);
			level1.add(level1Item);
		}
		map.put(1, level1);
		LinkedHashSet<List<String>> subsets = new LinkedHashSet<List<String>>(map.get(1));
		int size = map.get(1).size();
		for(int i = 1; i < size - 1; i++){
			List<List<String>> temp = map.get(i);
			List<List<String>> temp2 = new ArrayList<List<String>>();
			for(int j = 0; j < temp.size() - 1; j++){
				for(int k = j + 1; k < temp.size(); k++) {
					List<String> newItem = compineTwoItems(temp.get(j), temp.get(k));
					if(newItem != null){
						Collections.sort(newItem);
						subsets.add(newItem);
						temp2.add(newItem);
					}
				}
			}
			map.put(i + 1, temp2);
		}
		
		return new ArrayList<List<String>>(subsets);
	}

	public static boolean isEqualedSets(List<String> item1, List<String> item2) {
            if(item1.size() != item2.size())
                return false;
            
            boolean result = true;
            for(String element : item1){
                if(!item2.contains(element)) {
                    result = false;
                }
            }
            return result;
        }
	
}
