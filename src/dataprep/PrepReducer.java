package dataprep;

//import java.io.BufferedReader;
//import java.io.FileReader;
import java.io.IOException;
//import java.util.Collection;
//import java.util.HashSet;

import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.filecache.DistributedCache;
//import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
//import org.apache.hadoop.mapreduce.Reducer.Context;


public class PrepReducer extends Reducer<Text, Text, Text, Text> {

	float weight;
	boolean activated;
	float activation;
	// int toActivate;
	
    @Override
    public void setup(Context context) throws IOException {
            // Get configuration
            Configuration conf = context.getConfiguration();         
            weight = conf.getFloat("algorithm.weight.default", 1.0f);     
            activated = conf.getBoolean("algorithm.activated.default", false);
            activation = conf.getFloat("algorithm.activation.default", 0.0f);
    }        
            
	// Reduce task ----------------------------------------------------------------
    public void reduce(Text key, Iterable<Text> values, Context context) 
    		  throws IOException, InterruptedException {
    	  
    	// Reducer input looks like: User1 (App1, App103)
    	//						   App1 (User1, User2, User3)
    	//                           App103 (User1, User5)
    	
    	String outv;
    	outv = activation + " " + activated;

        for (Text value: values) {
        	outv = outv + " " + value + " " + weight;
        }
        
        // Reducer output looks like: User1 1.5 true App1 1.0 App103 1.0
        context.write(key, new Text(outv));

    	
      }
}