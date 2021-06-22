import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.lang.Math;

/**
 * FDR.java
 * Implementation of P-Values adjustment with False Discovery Rate (FDR).
 * The FDR is defined as the proportion of false positives among all significant results and works by estimating some
 * rejection region so that, on average, FDR < alpha.
 * @authors Georgios M. Moschovis (geomos@kth.se)
 */
public class FDR {
    /**
     * Read a specific file including the P-Values of some statistical significant tests and store them in memory.
     * @param filePath The relative path of the P-Values file.
     * @return The respective P-Values.
     */
    public List<Vec2<String, Double>> ReadTargetFile(String filePath) {
        BufferedReader br = null;
        String[] aCurrentLine;
        String sCurrentLine;
        double p_value;

        List<Vec2<String, Double>> p_values = new ArrayList<Vec2<String, Double>>();

        try {
            br = new BufferedReader(new FileReader(filePath));
            while ((sCurrentLine = br.readLine()) != null) {
                // Eliminate multiple whitespaces-unify into one.
                sCurrentLine = sCurrentLine.toLowerCase().trim().replaceAll(" +", " ");

                // Create P-Value ID and cast the actual value into decimal.
                aCurrentLine = sCurrentLine.split(" ");
                if(aCurrentLine[1].contains("e")) {
                    String[] aCurrentLine1 = aCurrentLine[1].split("e");
                    p_value = Double.parseDouble(aCurrentLine1[0]) * Math.pow(10, Integer.parseInt(aCurrentLine1[1]));
                } else p_value = Double.parseDouble(aCurrentLine[1]);

                // Store the respective P-Value.
                p_values.add(new Vec2<String, Double>(aCurrentLine[0], p_value));
            }
        } catch (IOException exc) {
            exc.printStackTrace();
        }

        return p_values;
    }

    /**
     * Adjust P-values with FDR.
     * @param P_values A list of P-values obtained from statistical significant tests.
     * @return The respective adjusted values.
     */
    public List<Vec2<String, Double>> adjust(List<Vec2<String, Double>> P_values) {
        // Sort the P-values in ascending order
        P_values.sort(new DefaultComparator());

        // Count non-statistically significant discoveries
        int falseDiscoveries = 0;
        for(int i = 0; i < P_values.size(); i++)
            if(P_values.get(i).getYValue() < 0.05) falseDiscoveries++;

        // Compute False Discovery Proportions (FDPs)
        double[] FDP = new double[P_values.size()];
        for(int i = 0; i < P_values.size(); i++)
            FDP[i] = P_values.get(i).getYValue() * falseDiscoveries / (i + 1);

        // Compute False Discovery Rates (FDRs)
        List<Vec2<String, Double>> FDR = new ArrayList<Vec2<String, Double>>();
        for(int i = 0; i < P_values.size(); i++)
            FDR.add(new Vec2<String, Double>(P_values.get(i).getTValue(), getMin(FDP, i)));

        return FDR;
    }

    /**
     * Write a specific file with the adjusted P-Values of some statistical significant tests from memory.
     * @param filePath The relative path of the adjusted P-Values file.
     * @param P_values The respective P-Values.
     */
    public void WriteTargetFile(String filePath, List<Vec2<String, Double>> P_values) {

        String content = "";

        try {
            FileWriter myWriter = new FileWriter(filePath);
            for(int i = 0; i < P_values.size(); i++)
                content += P_values.get(i).getTValue() + " " + P_values.get(i).getYValue() + "\n";
            myWriter.write(content);
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get minimum value within an array of numbers.
     * @param array An array of numerical values to get the minimum.
     * @param start The position to start within the array looking for the mimimum value.
     * @return The minimum value in the specified subarray.
     */
    public double getMin(double[] array, int start) {
        double min = Integer.MAX_VALUE;
        for(int i = start; i < array.length; i++)
            if(min > array[i]) min = array[i];

        return min;
    }

    /**
     * Main function (may be adjusted respectively).
     */
    public static void main(String[] args) {
        String base = "C:\\Users\\georg\\Documents\\IdeaProjects\\FalseDiscoveryRate";
        String[] tests = {"Ttests1.txt", "FTests1.txt", "UTests1.txt", "Ttests2.txt", "FTests2.txt", "UTests2.txt"};
        for(int i = 0; i < tests.length; i++) {
            FDR run = new FDR();
            List<Vec2<String, Double>> P_values = run.ReadTargetFile(base + "\\input\\" + tests[i]);
            List<Vec2<String, Double>> adjusted_P_values = run.adjust(P_values);
            run.WriteTargetFile(base + "\\output\\adj_" + tests[i], adjusted_P_values);
        }
    }
}
