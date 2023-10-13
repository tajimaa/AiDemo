package ai;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class NN {
    private int inputSize;
    private int hiddenSize1;
    private int hiddenSize2;
    private int outputSize;

    private double[][] weightsInputHidden1;
    private double[][] weightsHidden1Hidden2;
    private double[][] weightsHidden2Output;

    private double[] biasHidden1;
    private double[] biasHidden2;
    private double[] biasOutput;

    public NN(int inputSize, int hiddenSize1, int hiddenSize2, int outputSize) {
        this.inputSize = inputSize;
        this.hiddenSize1 = hiddenSize1;
        this.hiddenSize2 = hiddenSize2;
        this.outputSize = outputSize;

        weightsInputHidden1 = new double[inputSize][hiddenSize1];
        weightsHidden1Hidden2 = new double[hiddenSize1][hiddenSize2];
        weightsHidden2Output = new double[hiddenSize2][outputSize];

        biasHidden1 = new double[hiddenSize1];
        biasHidden2 = new double[hiddenSize2];
        biasOutput = new double[outputSize];

        initializeWeightsAndBiases();
    }

    private void initializeWeightsAndBiases() {
        Random rand = new Random();

        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < hiddenSize1; j++) {
                weightsInputHidden1[i][j] = rand.nextDouble() - 0.5;
            }
        }

        for (int i = 0; i < hiddenSize1; i++) {
            for (int j = 0; j < hiddenSize2; j++) {
                weightsHidden1Hidden2[i][j] = rand.nextDouble() - 0.5;
            }
        }

        for (int i = 0; i < hiddenSize2; i++) {
            for (int j = 0; j < outputSize; j++) {
                weightsHidden2Output[i][j] = rand.nextDouble() - 0.5;
            }
        }

        Arrays.fill(biasHidden1, 0.0);
        Arrays.fill(biasHidden2, 0.0);
        Arrays.fill(biasOutput, 0.0);
    }

    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    private double[] softmax(double[] x) {
        double[] result = new double[x.length];
        double max = Arrays.stream(x).max().getAsDouble();
        double expSum = 0.0;
        for (int i = 0; i < x.length; i++) {
            result[i] = Math.exp(x[i] - max);
            expSum += result[i];
        }
        for (int i = 0; i < x.length; i++) {
            result[i] /= expSum;
        }
        return result;
    }

    public double[] forward(double[] input) {
        double[] hidden1Input = new double[hiddenSize1];
        double[] hidden1Output = new double[hiddenSize1];

        double[] hidden2Input = new double[hiddenSize2];
        double[] hidden2Output = new double[hiddenSize2];

        double[] outputInput = new double[outputSize];
        double[] output = new double[outputSize];

        for (int j = 0; j < hiddenSize1; j++) {
            hidden1Input[j] = 0.0;
            for (int i = 0; i < inputSize; i++) {
                hidden1Input[j] += input[i] * weightsInputHidden1[i][j];
            }
            hidden1Input[j] += biasHidden1[j];
            hidden1Output[j] = sigmoid(hidden1Input[j]);
        }

        for (int j = 0; j < hiddenSize2; j++) {
            hidden2Input[j] = 0.0;
            for (int i = 0; i < hiddenSize1; i++) {
                hidden2Input[j] += hidden1Output[i] * weightsHidden1Hidden2[i][j];
            }
            hidden2Input[j] += biasHidden2[j];
            hidden2Output[j] = sigmoid(hidden2Input[j]);
        }

        for (int j = 0; j < outputSize; j++) {
            outputInput[j] = 0.0;
            for (int i = 0; i < hiddenSize2; i++) {
                outputInput[j] += hidden2Output[i] * weightsHidden2Output[i][j];
            }
            outputInput[j] += biasOutput[j];
        }

        output = softmax(outputInput);
        return output;
    }

    public void train(double[] input, double[] target, double learningRate) {
        double[] hidden1Input = new double[hiddenSize1];
        double[] hidden1Output = new double[hiddenSize1];

        double[] hidden2Input = new double[hiddenSize2];
        double[] hidden2Output = new double[hiddenSize2];

        double[] outputInput = new double[outputSize];
        double[] output = new double[outputSize];

        for (int j = 0; j < hiddenSize1; j++) {
            hidden1Input[j] = 0.0;
            for (int i = 0; i < inputSize; i++) {
                hidden1Input[j] += input[i] * weightsInputHidden1[i][j];
            }
            hidden1Input[j] += biasHidden1[j];
            hidden1Output[j] = sigmoid(hidden1Input[j]);
        }

        for (int j = 0; j < hiddenSize2; j++) {
            hidden2Input[j] = 0.0;
            for (int i = 0; i < hiddenSize1; i++) {
                hidden2Input[j] += hidden1Output[i] * weightsHidden1Hidden2[i][j];
            }
            hidden2Input[j] += biasHidden2[j];
            hidden2Output[j] = sigmoid(hidden2Input[j]);
        }

        for (int j = 0; j < outputSize; j++) {
            outputInput[j] = 0.0;
            for (int i = 0; i < hiddenSize2; i++) {
                outputInput[j] += hidden2Output[i] * weightsHidden2Output[i][j];
            }
            outputInput[j] += biasOutput[j];
        }

        output = softmax(outputInput);

        double[] outputError = new double[outputSize];
        for (int j = 0; j < outputSize; j++) {
            outputError[j] = output[j] - target[j];
            biasOutput[j] -= learningRate * outputError[j];
            for (int i = 0; i < hiddenSize2; i++) {
                weightsHidden2Output[i][j] -= learningRate * outputError[j] * hidden2Output[i];
            }
        }

        double[] hidden2Error = new double[hiddenSize2];
        for (int i = 0; i < hiddenSize2; i++) {
            hidden2Error[i] = 0.0;
            for (int j = 0; j < outputSize; j++) {
                hidden2Error[i] += outputError[j] * weightsHidden2Output[i][j];
            }
            hidden2Error[i] *= hidden2Output[i] * (1 - hidden2Output[i]);
            biasHidden2[i] -= learningRate * hidden2Error[i];
            for (int k = 0; k < hiddenSize1; k++) {
                weightsHidden1Hidden2[k][i] -= learningRate * hidden2Error[i] * hidden1Output[k];
            }
        }

        double[] hidden1Error = new double[hiddenSize1];
        for (int i = 0; i < hiddenSize1; i++) {
            hidden1Error[i] = 0.0;
            for (int j = 0; j < hiddenSize2; j++) {
                hidden1Error[i] += hidden2Error[j] * weightsHidden1Hidden2[i][j];
            }
            hidden1Error[i] *= hidden1Output[i] * (1 - hidden1Output[i]);
            biasHidden1[i] -= learningRate * hidden1Error[i];
            for (int k = 0; k < inputSize; k++) {
                weightsInputHidden1[k][i] -= learningRate * hidden1Error[i] * input[k];
            }
        }
    }

    static NN nn;
    public static NN getNN() {
        return nn;
    }

    public static void init() {
        int inputSize = 784;
        int hiddenSize1 = 100;
        int hiddenSize2 = 50;
        int outputSize = 10;
        double learningRate = 0.007;

        nn = new NN(inputSize, hiddenSize1, hiddenSize2, outputSize);

        List<String[]> list = CsvManager.readCSV("data copy.csv");
        double[][] target = new double[list.size()][outputSize];
        double[][] input = new double[list.size()][inputSize];
        double[] temp = new double[inputSize + 1];

        for (int i = 0; i < list.size(); i++) {
            temp = Arrays.stream(list.get(i)).mapToDouble(Double::parseDouble).toArray();
            target[i][(int) temp[inputSize]] = 1;
            for (int j = 0; j < inputSize; j++) {
                input[i][j] = temp[j];
            }
        }

        System.out.println("トレーニング開始");
        int batchSize = 32;
        for (int epoch = 0; epoch < 1000; epoch++) {
            if (epoch % 100 == 0) {
                System.out.println("エポック" + epoch + " 完了");
            }

            for (int i = 0; i < input.length; i += batchSize) {
                for (int j = 0; j < batchSize; j++) {
                    if (i + j < input.length) {
                        nn.train(input[i + j], target[i + j], learningRate);
                    }
                }
            }
        }
        
    }

    public void doF(double[] in) {
            double[] res = nn.forward(in);
            for(double d : res) {
                System.out.println("["+d+"]");
            }
            int index = findClosestValueIndex(res, 1);
            System.out.println(+index);
    }

    public static int findClosestValueIndex(double[] array, double targetValue) {
        if (array == null || array.length == 0) {
            
        }

        int closestIndex = 0;
        double closestDifference = Math.abs(targetValue - array[0]);

        for (int i = 1; i < array.length; i++) {
            double difference = Math.abs(targetValue - array[i]);
            if (difference < closestDifference) {
                closestIndex = i;
                closestDifference = difference;
            }
        }

        return closestIndex;
    }
}
