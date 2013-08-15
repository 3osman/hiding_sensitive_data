
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Scanner;

public class Apriori {

    ArrayList<Transaction> transactions;
    ArrayList<Hashtable<String, Double>> freq;
    double minSupport;
    double conf;
    double correlation;
    int size;
    FileWriter fstream;
    BufferedWriter out;
    FileWriter fstream2;
    BufferedWriter out2;
    FileWriter fstream3;
    BufferedWriter out3;
    int numberOfRulesFiltered = 0;
    int allNumberOfRules = 0;
    int numOffreq = 0;
    ArrayList<int[]> confArr = new ArrayList<int[]>();
    ArrayList<ArrayList<String>> rightItemSet = new ArrayList<ArrayList<String>>();
    ArrayList<ArrayList<String>> subSet = new ArrayList<ArrayList<String>>();

    public Apriori() {
    }

    public Apriori(double minSupport, double conf, double correlation) throws IOException {
        transactions = new ArrayList<Transaction>();
        freq = new ArrayList<Hashtable<String, Double>>();
        this.minSupport = minSupport;
        this.correlation = correlation;
        freq.add(new Hashtable<String, Double>());
        size = 0;
        this.conf = conf;
       
      
        
    }

    public void addTransaction(String str) {
        Hashtable<String, Double> freq1 = freq.get(0);
        transactions.add(new Transaction(str, freq1));
        size++;
    }

    private void clearNonFrequantItemSet() {
        Hashtable<String, Double> freq1 = freq.get(0);
        double supportFreq = minSupport * size;
        Iterator<String> itr = freq1.keySet().iterator();
        while (itr.hasNext()) {
            if (freq1.get(itr.next()) < supportFreq) {
                itr.remove();
            }
        }
    }

    private int getFreq(String[] item1, String[] item2) {
        int freq1 = 0;
        Iterator<Transaction> itr = transactions.iterator();
        while (itr.hasNext()) {
            if (itr.next().contains(item1, item2)) {
                freq1++;
            }
        }
        return freq1;
    }

    public void findFreqentItemSet() throws IOException {
         
        fstream = new FileWriter("a-rules.txt");
        out = new BufferedWriter(fstream);
        print("Min Support = " + minSupport + "       Confidence = " + conf);
        clearNonFrequantItemSet();
        int k = 2;
        while (!freq.get(k - 2).isEmpty()) {
            freq.add(new Hashtable<String, Double>());
            Hashtable<String, Double> freqItemSet = freq.get(k - 1);
            ArrayList<String> items = new ArrayList<String>();
            items.addAll(freq.get(k - 2).keySet());
            for (int i = 0; i < items.size(); i++) {
                for (int j = i + 1; j < items.size(); j++) {
                    String[] item1 = items.get(i).split(",");
                    String[] item2 = items.get(j).split(",");
                    String candidant = getCandidant(item1, item2, k);

                    if (candidant != null) {
                        double frequency = getFreq(item1, item2);
                        double frequencyPerSize = frequency / size;
                        if (frequencyPerSize >= minSupport) {
                            freqItemSet.put(candidant, frequency);

                        }
                    }
                }
            }
            k++;
        }
    }

    public void generateAssociationRules() throws IOException {
        print("----------------- Assosciation Rules---------------------");

        int n = freq.size();
        ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
        for (int i = 0; i < n - 1; i++) {
            Iterator<String> itr = freq.get(i).keySet().iterator();
            while (itr.hasNext()) {
                String s = itr.next();
                String[] itemSet = s.split(",");
                generateAssociationRule(itemSet, list);
                printAssociationRule(itemSet, list);
                list.clear();
            }
        }
        print("Number Of Rules = " + allNumberOfRules);
        print("Number Of Frequent Itemset = " + numOffreq);
        out.close();
    }

    private void printAssociationRule(String[] itemSet, ArrayList<ArrayList<String>> subSets) {

        int n = subSets.size();
        for (int i = 1; i < n - 1; i++) {
            ArrayList<String> rightItemSetLo = new ArrayList<String>();
            ArrayList<String> subSetLo = subSets.get(i);
            rightItemSetLo.clear();
            for (int j = 0; j < itemSet.length; j++) {
                if (!subSetLo.contains(itemSet[j])) {
                    rightItemSetLo.add(itemSet[j]);
                }
            }
            int[] confArrLo = checkConf(subSetLo, rightItemSetLo);
            double checkConf = ((double) confArrLo[0]) / confArrLo[1];
            if (checkConf >= conf) {
                allNumberOfRules++;
                print(subSetLo.toString() + "---->" + rightItemSetLo.toString() + " Support =   " + new DecimalFormat("##.##").format(((confArrLo[0] / (double) size) * 100)) + "%   Confidence = " + (int) (checkConf * 100) + "%");
                boolean filter = filter(confArrLo, rightItemSetLo, subSetLo);
            }
            rightItemSet.add(rightItemSetLo);
            subSet.add(subSetLo);
            confArr.add(confArrLo);
        }

    }

    public void generateRRules() throws IOException {

        fstream2 = new FileWriter("r-rules.txt");
        out2 = new BufferedWriter(fstream2);
        printFilter("Min Support = " + minSupport + "       Confidence = " + conf);
        printRRule();
        printFilter("Number Of Rules = " + numberOfRulesFiltered);
        out2.close();
    }

    private void printRRule() {
        String[] fin = new String[6];
        try {
            String content = new Scanner(new File("a-rules.txt")).useDelimiter("\\Z").next();
            content = content.substring(content.lastIndexOf("----------------- Assosciation Rules---------------------") + 1, content.length() - 1);
            content = content.substring(content.indexOf("\n") + 1, content.lastIndexOf("\n"));
            String[] content2 = content.split("\n");
            String[] strLine = new String[2];
            String[] temp = new String[20];
            for (int k = 0; k < content2.length - 1; k = k + 2) {
                strLine[0] = content2[k];
                strLine[1] = content2[k + 1];
                String temp1 = strLine[0] + " " + strLine[1];
                fin[0] = temp1.substring(0, temp1.lastIndexOf("]") + 1);
                String temp2 = temp1.substring(temp1.lastIndexOf("]") + 1, temp1.length() - 1);
                temp = temp2.split(" ");
                fin[1] = temp[5];
                fin[2] = temp[10];
                fin[3] = temp[13];//lift
                fin[4] = temp[18];//correlation
                fin[5] = temp[27];//chi
                double fy = Double.parseDouble(fin[4]);
                double chi2 = Double.parseDouble(fin[5]);
                double independancy = Double.parseDouble(fin[3]);
                if (independancy > 1 && fy >= correlation && chi2 >= 3.84) {
                    printFilter(fin[0] + " Support =   " + fin[1] + "%   Confidence = " + (fin[2]));
                    printFilter("lift = " + (int) (independancy * 1000) / (double) 1000 + "  Correlation =  " + (int) (fy * 10000) / (double) 10000 + "       ChiSquare = " + (int) (chi2 * 10000) / (double) 10000);
                    numberOfRulesFiltered++;
                }
            }


        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

    }

    private void printFilter(String string) {

        try {
            out2.write(string);
            out2.newLine();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private boolean filter(int[] confArr, ArrayList<String> yList, ArrayList<String> subSet) {
        double pxy = (double) confArr[0] / size;
        double px = (double) confArr[1] / size;
        String[] y = new String[yList.size()];
        yList.toArray(y);
        double py = getFreq(y, new String[0]) / (double) size;
        double independancy = pxy / (px * py);
        double fy = (pxy - (px * py)) / Math.sqrt(px * py * (1 - px) * (1 - py));
        double chi2 = fy * fy * size;
        print("lift = " + (int) (independancy * 1000) / (double) 1000 + "  Correlation =  " + (int) (fy * 10000) / (double) 10000 + "       Chi = " + (int) (chi2 * 10000) / (double) 10000);
        //print("lift = " +independancy  +"  Correlation =  "+ fy+ "       Chi = "+chi2 );
	 /*
        if (independancy > 1 && fy >= correlation && chi2 >= 3.84) {
        double checkConf = ((double) confArr[0]) / confArr[1];
        printFilter(subSet.toString() + "---->" + yList.toString() + " Support =   " + new DecimalFormat("##.##").format(((confArr[0] / (double) size) * 100)) + "%   Confidence = " + (int) (checkConf * 100) + "%");
        printFilter("lift = " + (int) (independancy * 1000) / (double) 1000 + "  Correlation =  " + (int) (fy * 10000) / (double) 10000 + "       ChiSquare = " + (int) (chi2 * 10000) / (double) 10000);
        numberOfRulesFiltered++;
        return true;
        }*/
        return false;
    }

    private int[] checkConf(ArrayList<String> subSet, ArrayList<String> rightItemSet) {

        String[] item1 = new String[subSet.size()];
        String[] item2 = new String[rightItemSet.size()];
        subSet.toArray(item1);
        rightItemSet.toArray(item2);
        int arr[] = new int[2];
        arr[0] = getFreq(item1, item2);
        arr[1] = getFreq(item1, new String[0]);
        return arr;
    }

    private void generateAssociationRule(String[] itemSet, ArrayList<ArrayList<String>> list) {
        generateAssociationRule(itemSet, list, itemSet.length - 1);
    }

    private void generateAssociationRule(String[] itemSet, ArrayList<ArrayList<String>> list, int n) {
        if (n == -1) {
            list.add(new ArrayList<String>());
            return;
        }
        generateAssociationRule(itemSet, list, n - 1);
        int size1 = list.size();
        for (int i = 0; i < size1; i++) {
            ArrayList<String> cloneArray = (ArrayList<String>) list.get(i).clone();
            cloneArray.add(0, itemSet[n]);
            list.add(cloneArray);
        }
    }

    public void printAllFrequentItemSet() {
        int n = freq.size();
        for (int i = 0; i < n - 1; i++) {
            print("-------------size = " + (i + 1) + "---------------");
            Iterator<String> itr = freq.get(i).keySet().iterator();
            while (itr.hasNext()) {
                String s = itr.next();
                Double number = ((freq.get(i).get(s) / (double) size) * 100);
                String formatedNumber = new DecimalFormat("##.##").format(number);
                numOffreq++;
                print(s + "  [Support =  " + formatedNumber + "% ]");
            }
        }
    }

    private void print(String string) {

        try {
            out.write(string);
            out.newLine();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String getCandidant(String[] item1, String[] item2, int k) {
        HashSet<String> set = new HashSet<String>();
        ArrayList<String> list = new ArrayList<String>();

        list.addAll(Arrays.asList(item1));
        list.addAll(Arrays.asList(item2));
        set.addAll(list);
        if (set.size() == k) {
            list.clear();
            Iterator<String> itr = set.iterator();
            while (itr.hasNext()) {
                list.add(itr.next());
            }
            Collections.sort(list);
            return list.toString().replace(", ", ",").replace("[", "").replace("]", "");
        }
        return null;
    }
}
