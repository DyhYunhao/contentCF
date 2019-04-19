package step2;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Mapper2 extends Mapper<LongWritable, Text, Text, Text> {
    private Text outKey = new Text();
    private Text outValue = new Text();

    private List<String> cacheList = new ArrayList<String>();

    private DecimalFormat df = new DecimalFormat("0.00");


    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);



        //输入流读入矩阵
        FileReader fr = new FileReader("itemUserScore2");
        BufferedReader br = new BufferedReader(fr);

        String line = null;
        while ((line = br.readLine()) != null){
            cacheList.add(line);
        }

        fr.close();
        br.close();
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String row_matrix1 = value.toString().split("\t")[0];
        String[] column_value_array_matrix1 = value.toString().split("\t")[1].split(",");

        for (String line: cacheList){
            String row_matrix2 = line.toString().split("\t")[0];
            String[] column_value_array_matrix2 = line.toString().split("\t")[1].split(",");

            //相乘
            double result = 0;
            for (String column_value_matrix1: column_value_array_matrix1){
                String column_matrix1 = column_value_matrix1.split("_")[0];
                String vale_matrix1 = column_value_matrix1.split("_")[1];
                for (String column_value_matrix2: column_value_array_matrix2){
                    if (column_value_matrix2.startsWith(column_matrix1 + "_")){
                        String vale_matrix2 = column_value_matrix2.split("_")[1];
                        result += Double.valueOf(vale_matrix1) * Double.valueOf(vale_matrix2);
                    }
                }
            }

            if (result == 0){
                continue;
            }

            outKey.set(row_matrix1);
            outValue.set(row_matrix2 + "_" + df.format(result));
            context.write(outKey, outValue);
        }

    }
}
