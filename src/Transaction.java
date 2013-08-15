
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;

public class Transaction {

    HashSet<String> items;

    public Transaction(String str, Hashtable<String, Double> freq) {
        items = new HashSet<String>();
        String[] itemsArr = str.split(",");
        for (String s : itemsArr) {
            items.add(s);
            if (freq.containsKey(s)) {
                freq.put(s, freq.get(s) + 1);
            } else {
                freq.put(s, 1.0);
            }
        }
    }

    public boolean contains(String s) {
        return items.contains(s);
    }

    public int size() {
        return items.size();
    }

    public boolean contains(String[] item1, String[] item2) {

        ArrayList<String> list = new ArrayList<String>(Arrays.asList(item1));

        list.addAll(Arrays.asList(item2));
        return items.containsAll(list);
    }
}
