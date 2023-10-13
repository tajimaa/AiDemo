package ai.note;

import java.util.Arrays;

import ai.CsvManager;
import ai.NN;

class DataSender {
    private static double[][] result = new double[0][784];
    private static double[][] target = new double[0][1];

    public static void sendData(int[][] pixels, String targetText) {

        double[] newRow = new double[784];
        int index = 0;
        
        for (int x = 0; x < pixels[0].length; x++) {
            for (int y = 0; y < pixels.length; y++, index++) {
                newRow[index] = pixels[y][x];
                System.out.print(pixels[y][x]);
            }
            System.out.println();
        }

        result = Arrays.copyOf(result, result.length + 1);
        result[result.length - 1] = newRow;

        double[] newTarget = { Double.parseDouble(targetText) };
        target = Arrays.copyOf(target, target.length + 1);
        target[target.length - 1] = newTarget;

        NN nn = NN.getNN();
        nn.doF(newRow);

    }

    public static void update() {
        CsvManager.writeLearningData("data.csv", result, target);
    }
    public static void read() {
        CsvManager.readCSV("data.csv");
    }
}
