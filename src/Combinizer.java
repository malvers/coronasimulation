import mratools.MTools;

public class Combinizer {

    public Combinizer() {

        int cols = 3;
        int rows = 2;
        int numCombis = cols * cols;
//        int data[][] = new int[numCombis][rows];
        String sField[][] = new String[numCombis][cols];

        String[] oneRow = new String[cols];

        fillRow(cols, oneRow);

        MTools.println("\nGo ...\n");

        int count = 0;
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < cols; j++) {
                sField[count][0] = oneRow[i];
                sField[count][1] = oneRow[j];
//                for (int k = 1; k < rows; k++) {
//                    sField[count][k] = oneRow[j+k];
//                }
                count++;
            }
        }

        /// print
        for (int combi = 0; combi < numCombis; combi++) {

            for (int i = 0; i < rows; i++) {
                MTools.println("" + sField[combi][i]);
            }
            MTools.println("");
        }
    }

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
            MTools.println(i + " " + oneRow[i]);
        }
    }

    public static void main(String[] args) {
        new Combinizer();

//        for (int i = 0; i <= 1000; i++) {
//
//            MTools.println(i + " - " + Integer.toString(i, 2));
//        }
    }
}
