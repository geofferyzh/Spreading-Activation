package multinodeactivation;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.log4j.Logger;
import org.apache.hadoop.io.FloatWritable;


public class SortReducer extends Reducer<FloatWritable, Text, Text, Text> {

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