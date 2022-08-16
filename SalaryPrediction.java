import java.io.*;
import java.util.*;

public class SalaryPrediction {
    // reading the data leaving the column names
    ArrayList<double[]> readData (String  fileLocation) throws IOException {
        ArrayList<double[]> data = new ArrayList<double[]>();
        BufferedReader br = new BufferedReader(new FileReader(fileLocation));
        String row = null;
        int rowcount = 0;

        while ((row = br.readLine()) != null) {
            if (rowcount > 0) {
                String[] rowStringArray = row.split(",");
                double[] rowArray = new double[rowStringArray.length];

                for (int i = 0; i < rowArray.length; i++) {
                    rowArray[i] = Double.parseDouble(rowStringArray[i]);
                }

                data.add(rowArray);
            }

            rowcount++;
        }

        br.close();
        return data;
    }

    // reading only the column names
    String[] readColumnNames (String fileLocation) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileLocation));

        String row = br.readLine();
        br.close();

        return row.split(",");
    }

    // print the data table
    void printData (String[] columnNames, ArrayList<double[]> data) {
        System.out.println(Arrays.toString(columnNames));

        for (int i = 0; i < data.size(); i++) {
            System.out.println(Arrays.toString(data.get(i)));
        }
    }

    // model training
    double[] fit (ArrayList<double[]> data, int iterations, double learingRate) {
        double slope = 0;
        double intercept = 0;

        for (int i = 0; i < iterations; i++) {
            double slopeDerivative = calculatePartialDerivative(data, slope, intercept, false);
            double interceptDerivative = calculatePartialDerivative(data, slope, intercept, true);

            slope = slope - learingRate * slopeDerivative;
            intercept = intercept - learingRate * interceptDerivative;
        }

        return new double[] {slope, intercept};
    }

    // mean squared error
    double mse (ArrayList<double[]> data,double predictedSalary) {
        double sum = 0;

        for (int i = 0; i < data.size(); i++) {
            sum += data.get(i)[1] - predictedSalary;
        }

        return sum / data.size();
    }

    // calc partial derivative
    double calculatePartialDerivative (ArrayList<double[]> data, double slope, double intercept, boolean isIntercept) {
        double sum = 0;
        double sumForLoss = 0;

        for (int i = 0; i < data.size(); i++) {
            double[] row = data.get(i);
            double predictedSalary = (slope * row[0]) + intercept;

            if (isIntercept) {
                sum += 2 * (predictedSalary - row[1]);
            } else {
                sum += 2 * row[0] * (predictedSalary - row[1]);
            }

            sumForLoss = sumForLoss + row[1] - predictedSalary;
        }

        // System.out.println(sumForLoss / data.size());
        return sum / data.size();
    }

    // predicting salary for given experience
    double predictSalary (double[] trainResults, double experience) {
        return (trainResults[0] * experience) + trainResults[1];
    }

    // calculating r-squared score
    double r2Score (ArrayList<double[]> data, double[] trainResults) {
        double salarySum = 0;

        for (int i = 0; i < data.size(); i++) {
            salarySum += data.get(i)[1];
        }

        double salaryMean = salarySum / data.size();

        double sst = 0;

        for (int i = 0; i < data.size(); i++) {
            sst += Math.pow((data.get(i)[1] - salaryMean), 2);
        }

        double ssr = 0;

        for (int i = 0; i < data.size(); i++) {
            ssr += Math.pow((predictSalary(trainResults, data.get(i)[0]) - salaryMean), 2);
        }

        return ssr / sst;
    }

    public static void main(String[] args) throws IOException {
        SalaryPrediction sp = new SalaryPrediction();
        Scanner s = new Scanner(System.in);

        // loading the data from csv file and storing it in 2d array
        ArrayList<double[]> data = sp.readData("Salary_Data.csv");

        // training the model
        double[] results = sp.fit(data, 3625, 0.01);

        // r-squared score
        System.out.println("R-squared: " + sp.r2Score(data, results));

        String choice = "yes";

        while (choice.equals("yes")) {
            System.out.println("Enter your experience: ");

            int experience = s.nextInt();
            s.nextLine();

            System.out.println("You are expected to get a salary of " + Math.round(sp.predictSalary(results, experience)));

            System.out.println("Estimate again with different experience - type yes (or) no: ");
            choice = s.nextLine();
        }

        s.close();
    }
}
