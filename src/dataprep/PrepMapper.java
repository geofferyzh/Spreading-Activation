package dataprep;
//import java.io.BufferedReader;
//import java.io.FileReader;
import java.io.IOException;
//import java.util.Collection;
//import java.util.HashSet;
import java.util.StringTokenizer;


import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;
//import org.apache.hadoop.mapreduce.Mapper.Context;

// import org.apache.hadoop.mapreduce.Reducer.Conte

public class PrepMapper extends Mapper<LongWritable, Text, Text, Text> { 

		
		
		// Map Task ------------------------------------------------------------------------------------------
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        	// Input looks like: User1 App1,App103
        	StringTokenizer tokenizer_tab = new StringTokenizer(value.toString(),"\t");            
            String outkey = tokenizer_tab.nextToken();        
            String applist = tokenizer_tab.nextToken();
            
        	//String[] s = value.toString().split("\t");
        	//String outkey = s[0];
        	//String applist= s[1];
        	
            StringTokenizer tokenizer_comma = new StringTokenizer(applist,",");
            while (tokenizer_comma.hasMoreTokens()) {     
            	// Output looks like: 
            	// User1 App1
            	// User1 App103
            	// App1 User1
            	// App103 User1
            	String nextapp = tokenizer_comma.nextToken();
            	context.write(new Text(outkey), new Text(nextapp));
            	context.write(new Text(nextapp), new Text(outkey));
            }
        }
}