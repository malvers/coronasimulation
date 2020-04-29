import mratools.MTools;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Combinizer {

    private String possibilities[];

    public Combinizer() throws FileNotFoundException {

        int cols = 6;
        int rows = 6;

        possibilities = new String[cols];

        String[] allCombis = MyCombinationsWithRepeats.get(rows, cols);

        MTools.println("Create possibilities ...");
        fillRows(cols);

        for (String str : possibilities) {
            MTools.println(str);
        }
        
        MTools.println( "Print combinations ..." );
        for ( String str : allCombis) {
            MTools.println( str );
        }

        PrintWriter pw = new PrintWriter(rows + " x " + cols + ".dat");
        pw.write("// rows: " + rows + " cols: " + cols + " number possibilities: " + allCombis.length + "\n");

        MTools.println("");
        MTools.println("Create variations ...");
        int count = 0;
        for (String string : allCombis) {

            for (int i = 0; i < string.length(); i++) {

                char c = string.charAt(i);
                int v = Integer.parseInt(Character.toString(c));

//                MTools.print(v + " ");
//                MTools.println("" + possibilities[v]);
                count++;
                pw.write(possibilities[v] + "\n");
            }
//            MTools.println("");
            pw.write("\n");
        }
        pw.close();
        MTools.println( "count: " + allCombis.length);
    }

    private void fillRows(int cols) {

        String[] oneRow = new String[cols];
        for (int i = 0; i < cols; i++) {
            oneRow[i] = "";
            for (int j = 0; j < cols; j++) {

                if (i == j) {
                    oneRow[i] += "1";
                } else {
                    oneRow[i] += "0";
                }
            }
            possibilities[i] = oneRow[i];
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

    // main for testing
    public static void main(String[] args) throws FileNotFoundException {
        new Combinizer();
    }
}
