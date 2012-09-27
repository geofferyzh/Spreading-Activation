package multinodeactivation;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;


public class SortReducer extends Reducer<Text, Text, Text, Text> {

        private static final Logger sLogger = Logger.getLogger(SortReducer.class);
                   
        @Override
        public void setup(Context context) throws IOException {
        	Configuration conf = context.getConfiguration(); 
        	
        }
        
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
                                
                               
                // Write pair
                
                //sLogger.info("Emitting pair: [" + key + ", " + strOutValue);
                context.write(null, new Text());
        }
}
