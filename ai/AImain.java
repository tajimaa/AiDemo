package ai;

import java.util.List;

import ai.note.Note;

public class AImain {
    public static void main(String[] args) {
       
        List<String[]> list = CsvManager.readCSV("data.csv");
        double[][] target = new double[list.size()][1];
        double[][] input = new double[list.size()][784];
        
        for(int i=0; i<list.size(); i++) {
            for(int j=0; j<784; j++){
                input[i][j] = Double.parseDouble(list.get(i)[j]);
            }
            target[i][0] = Double.parseDouble(list.get(i)[784]);
        }
        
        NN.init();

        Note.note(); 
    }
}
