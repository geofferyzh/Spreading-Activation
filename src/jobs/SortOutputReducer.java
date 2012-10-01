package jobs;

import java.io.IOException;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
//import org.apache.log4j.Logger;

public class SortOutputReducer extends Reducer<FloatWritable, Text, Text, Text> {
        //private final float threshold = 0.5f;
        //private static final Logger sLogger = Logger.getLogger(SortOutputReducer.class);
       
        @Override
        public void setup(Context context) throws IOException {

        }
       
        protected void reduce(FloatWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
               
                // For each incoming activation
                for(Text val : values) {        
                        context.write(val, new Text(key.toString()));
                }
        }
}

