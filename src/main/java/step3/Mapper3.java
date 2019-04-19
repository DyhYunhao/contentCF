package step3;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Mapper3 extends Mapper<LongWritable, Text, Text, Text> {
    private Text outKey = new Text();
    private Text outValue = new Text();

    private List<String> cacheList = new ArrayList<String>();

    private DecimalFormat df = new DecimalFormat("0.00");


    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);

        //输入流读入矩阵
        FileReader fr = new FileReader("itemUserScore1");
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
        String row_matrix1 = value.toString().split("      ")[0];
        String[] column_value_array_matrix1 = value.toString().split("      ")[1].split(",");

        //计算左矩阵行的空间距离
        double denominator1 = 0;
        for (String column_value: column_value_array_matrix1){
            String score = column_value.split("_")[1];
            denominator1 += Double.valueOf(score) * Double.valueOf(score);
        }
        denominator1 = Math.sqrt(denominator1);

        for (String line: cacheList){
            String row_matrix2 = line.toString().split("\t")[0];
            String[] column_value_array_matrix2 = line.toString().split("\t")[1].split(",");

            //计算右矩阵行的空间距离
            double denominator2 = 0;
            for (String column_value: column_value_array_matrix2){
                String score = column_value.split("_")[1];
                denominator2 += Double.valueOf(score) * Double.valueOf(score);
            }
            denominator2 = Math.sqrt(denominator2);

            //相乘
            double numerator  = 0;
            for (String column_value_matrix1: column_value_array_matrix1){
                String column_matrix1 = column_value_matrix1.split("_")[0];
                String vale_matrix1 = column_value_matrix1.split("_")[1];
                for (String column_value_matrix2: column_value_array_matrix2){
                    if (column_value_matrix2.startsWith(column_matrix1 + "_")){
                        String vale_matrix2 = column_value_matrix2.split("_")[1];
                        numerator += Double.valueOf(vale_matrix1) * Double.valueOf(vale_matrix2);
                    }
                }
            }

            double cos = numerator / (denominator1 * denominator2);
            if (cos == 0){
                continue;
            }

            outKey.set(row_matrix2);
            outValue.set(row_matrix1 + "_" + df.format(cos));
            context.write(outKey, outValue);
        }

    }
}
