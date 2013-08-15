import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

public class Borders {

	List<List<String>> frequentItems = null;
	List<List<String>> removedItems = null;
	
	public Borders(List<List<String>> frequentItems, List<List<String>> removedItems) {
		this.frequentItems = frequentItems;
		int size = frequentItems.size();
		for(int i = 0; i < size; i++)
			Collections.sort(frequentItems.get(i));
		this.removedItems = removedItems == null? new ArrayList<List<String>>() : removedItems;
	}

	public List<List<String>> getPositiveBorders() {
		List<List<String>> tempList = new ArrayList<List<String>>(frequentItems);
		class CompareWithLength implements Comparator<List<String>> {
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.Comparator#compare(java.lang.Object,
			 * java.lang.Object)
			 */
			@Override
			public int compare(List<String> o1, List<String> o2) {
				int compVal;
				if (o1.size() < o2.size())
					compVal = 1;
				else if (o1.size() == o2.size())
					compVal = 0;
				else
					compVal = -1;
				return compVal;
			}
		}

		Collections.sort(tempList, new CompareWithLength());
		ArrayList<List<String>> deleteItemList = new ArrayList<List<String>>();

		for (int i = 0; i < tempList.size() && tempList.get(i).size() > 1; i++) {
			for (int j = i + 1; j < tempList.size(); j++) {
				if (PNUtilities.isSubset(tempList.get(j), tempList.get(i)))
					deleteItemList.add(tempList.get(j));
			}
		}
		tempList.removeAll(deleteItemList);

		return tempList;
	}

	public List<List<String>> getNegativeBorders() {
		List<List<String>> tempList = new ArrayList<List<String>>(frequentItems);
		class CompareWithLength implements Comparator<List<String>> {
			/* (non-Javadoc)
			 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
			 */
			@Override
			public int compare(List<String> o1, List<String> o2) {
				int compVal;
				if (o1.size() < o2.size())
					compVal = -1;
				else if (o1.size() == o2.size())
					compVal = 0;
				else
					compVal = 1;
				return compVal;
			}
		}
		Collections.sort(tempList, new CompareWithLength());
		
		int minLength = tempList.get(0).size();
		int maxLength = tempList.get(tempList.size() - 1).size();
		
		LinkedHashSet<List<String>> eligibleForNBorder = new LinkedHashSet<List<String>>();
		
		
		Map<Integer, List<List<String>>> lengthSeparatedMap = new HashMap<Integer, List<List<String>>>();
		for(int currentLength = minLength, j = 0; currentLength <= maxLength; currentLength++) {
			
			List<List<String>> temp = new ArrayList<List<String>>();
			while(j < tempList.size() && tempList.get(j).size() == currentLength){
				temp.add(tempList.get(j));
				j++;
			}
			if(temp.size() == 0)
				maxLength = currentLength;
			
			lengthSeparatedMap.put(currentLength, temp);
		}
		
		for(int currentLength = minLength; currentLength < maxLength; currentLength++) {
			List<List<String>> temp = lengthSeparatedMap.get(currentLength);
			for(int i = 0; i < temp.size() - 1; i++){
				for(int j = i + 1; j < temp.size(); j++) {
					List<String> newItem = PNUtilities.compineTwoItems(temp.get(i), temp.get(j));
					if(newItem != null ){
						Collections.sort(newItem);
						if(!lengthSeparatedMap.get(currentLength + 1).contains(newItem))
							eligibleForNBorder.add(newItem);
					}
						
				}
			}
		}
		
		LinkedHashSet<List<String>> tempSet = new LinkedHashSet<List<String>>(eligibleForNBorder);
		for(List<String> item : tempSet) {
			if(!isNegativeItem(item))
				eligibleForNBorder.remove(item);
		}
		for(List<String> item : removedItems){
			if(item.size() == 1)
				eligibleForNBorder.add(item);
		}
		return new ArrayList<List<String>>(eligibleForNBorder);
	}

	private boolean isNegativeItem(List<String> item) {
		List<List<String>> subitems = PNUtilities.getAllSubsets(item);
		return frequentItems.containsAll(subitems);
	}

}
