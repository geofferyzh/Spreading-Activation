package multinodeactivation;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;


//public class SpreadActivatorMapper extends Mapper<LongWritable, Text, Text, Text> {
public class SortMapper extends Mapper<Text, Text, Text, Text> {    
               
        private static final Logger sLogger = Logger.getLogger(SortMapper.class);
            	
        @Override
        public void setup(Context context) throws IOException {
                // Get configuration
                Configuration conf = context.getConfiguration(); 
                
                               
        }
           	
        // Map Task ---------------------------------------------------------------------------       
        public void map(Text key, Text value, Context context) throws IOException, InterruptedException {
               
                
                sLogger.info("");          
                context.write(key, value);
        }
}

