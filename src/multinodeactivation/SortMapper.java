package multinodeactivation;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer.Context;



//public class SpreadActivatorMapper extends Mapper<LongWritable, Text, Text, Text> {
public class SortMapper extends Mapper<Text, VertexInfo, FloatWritable, Text> {    
               
	    @Override
	    public void setup(Context context) throws IOException {
	
	    }            	
        // Map Task ---------------------------------------------------------------------------       
        public void map(Text key, VertexInfo value, Context context) throws IOException, InterruptedException {
                FloatWritable mapoutkey = new FloatWritable(value.getActivation());
                Text mapoutvalue = new Text(value.getNode());
    
                context.write(mapoutkey, mapoutvalue);
        }
}

