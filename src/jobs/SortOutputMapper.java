package jobs;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;


public class SortOutputMapper extends Mapper<Text, VertexInfo, FloatWritable, Text> {     
                
        private static final Logger sLogger = Logger.getLogger(SortOutputMapper.class);
        FloatWritable zeroFloat;
        
        @Override
        public void setup(Context context) throws IOException {
                // Get configuration
                Configuration conf = context.getConfiguration();
                zeroFloat = new FloatWritable(0.0f);
        }
        
        public void map(Text key, VertexInfo value, Context context) throws IOException, InterruptedException {                   
                
                FloatWritable outkey = new FloatWritable(value.getActivation());
                Text outvalue = new Text(value.getNode());
                if(outkey != zeroFloat) {
                        context.write(outkey, outvalue);
                }
        }
}
