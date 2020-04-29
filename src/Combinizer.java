import mratools.MTools;

import java.util.ArrayList;
import java.util.List;

public class Combinizer {

    public Combinizer() {

        int cols = 3;
        int rows = 3;
        int max = Math.max(rows, cols);
        int numCombis = max * max;
        int data[] = new int[cols];
        String sField[][] = new String[numCombis][max];

//        test();

        String[] oneRow = new String[cols];
        fillRow(cols, oneRow);

        MTools.println("Permutations");

        printPermutn(oneRow[0], "");

//        MTools.println( "" );
//        printPermutn(oneRow[1], "");
//        MTools.println("");
//        printPermutn(oneRow[2], "");

//        int count = 0;
//        List<List<String>> list = getAllCombinations(Arrays.asList(oneRow));
//        for (List<String> arr : list) {
//            for (String s : arr) {
//                count++;
//                System.out.println(s);
//            }
//            System.out.println();
//        }
//        MTools.println("count: " + count);
    }

    static void printPermutn(String str, String ans) {

        // If string is empty
        if (str.length() == 0) {
            System.out.print(ans + " ");
            return;
        }

        for (int i = 0; i < str.length(); i++) {

            // ith character of str
            char ch = str.charAt(i);

            // Rest of the string after excluding
            // the ith character
            String ros = str.substring(0, i) + str.substring(i + 1);

            // Recurvise call
            printPermutn(ros, ans + ch);
        }
    }

    static void per(String a, int start) {
        //bse case;
        if (a.length() == start) {
            System.out.println(a);
        }
        char[] ca = a.toCharArray();
        //swap
        for (int i = start; i < ca.length; i++) {
            char t = ca[i];
            ca[i] = ca[start];
            ca[start] = t;
            per(new String(ca), start + 1);
        }
    }//per


    private void fillRow(int cols, String[] oneRow) {

        for (int i = 0; i < cols; i++) {
            oneRow[i] = "";
            for (int j = 0; j < cols; j++) {

                if (i == j) {
                    oneRow[i] += "1";
                } else {
                    oneRow[i] += "0";
                }
            }
            MTools.println(oneRow[i]);
        }
    }

    public List<List<String>> getAllCombinations(List<String> elements) {

        List<List<String>> combinationList = new ArrayList<List<String>>();
        for (long i = 1; i < Math.pow(2, elements.size()); i++) {
            List<String> list = new ArrayList<String>();
            for (int j = 0; j < elements.size(); j++) {
                if ((i & (long) Math.pow(2, j)) > 0) {
                    list.add(elements.get(j));
                }
            }
            combinationList.add(list);
        }
        return combinationList;
    }

    //    private static int m[] = new int[100];
//    static void move(int line) {
//
//        MTools.println("" + m[line]);
//        m[line]++;
//        if (m[line] == 100) {
//            m[line] = 0;
//            move(line + 1);
//        }
//    }
    public static void main(String[] args) {

//        new Combinizer();

        String a = "123";
        per(a, 0);

    }
}
