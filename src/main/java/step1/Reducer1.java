package step1;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class Reducer1 extends Reducer<Text, Text, Text, Text> {
    private Text outKey = new Text();
    private Text outValue = new Text();

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
//        super.reduce(key, values, context);
        StringBuilder sb = new StringBuilder();
        for (Text text: values){
            sb.append(text).append(",");
        }
        String line = null;
        if (sb.toString().endsWith(",")){
            line = sb.substring(0, sb.length() - 1);
        }

        outKey.set(key);
        assert line != null;
        outValue.set(line);

        context.write(outKey, outValue);
    }
}
