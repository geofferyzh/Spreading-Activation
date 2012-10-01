package jobs;

import java.io.IOException;
//import java.util.StringTokenizer;

//import jobs.ImportInputFile;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
//import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.log4j.Logger;


public class SAReducer extends Reducer<Text, VertexInfo, Text, Text> {
        //private final float threshold = 0.5f; 
        private static final Logger sLogger = Logger.getLogger(SAReducer.class);
        
        float threshold = 0.0f;
        
        @Override
        public void setup(Context context) throws IOException {
                threshold = context.getConfiguration().getFloat("algorithm.activation.threshold", 2.0f);
        }
        
        protected void reduce(Text key, Iterable<VertexInfo> values, Context context) throws IOException, InterruptedException {
                                
                //sLogger.debug("Processing key: " + key.toString());
                //sLogger.debug("Processing values: ");
                
                
                //sLogger.debug("THRESHOLD=" + threshold);
                
                Float totalActivation = new Float(0.0f);
                
                String strOutNodes = new String();
                String strOutValue = new String();
                
                boolean activated = false;
                boolean activationValue = false;
                float networkActivation = 0.0f;
                int outNodesCount = 0;
                
                // For each incoming activation
                for(VertexInfo val : values) {    
                        //sLogger.info("Value: " + val);
                        //context.write(key, new Text(val.toString()));
                        
                        // Pair with activation info
                        Float activation = val.getActivation();
                        
                        if(!val.isNetwork()) {
                                //sLogger.info("Adding activation: " + activation);
                                totalActivation += activation;
                                activationValue = true;
                        }
                        // Pair with network info
                        else {
                                activated = val.getActivated();
                                networkActivation = val.getActivation();
                                for(int i=0; i<val.getConnectionsCount(); ++i) {
                                        //sLogger.info("Adding network info: " + (val.getNodeAt(i) + " " + val.getWeightAt(i) + " "));
                                        strOutNodes += (val.getNodeAt(i) + " " + val.getWeightAt(i) + " ");
                                        ++outNodesCount;
                                }
                        }
                        
                        // Upper bound (REVISAR)
                        //if(totalActivation > 1.0f) {
                        //      totalActivation = 1.0f;
                        //}                             
                }       
                
                // If node is above threshold and has outgoing connections
                if(totalActivation > threshold && outNodesCount > 0 && !activated) {
                        // Increment number of activated nodes for next iteration
                        // context.getCounter(MrSpreadCounters.Counters.ACTIVATED_NODES).increment(1);
                	    context.getCounter("ACTIVATED_NODES", "SACounter").increment(1);
                }
                
                // If no activation from normal pairs or already activated, get the network activation
                if(!activationValue || activated) {
                        totalActivation = networkActivation;
                }
                
                // Convert activated to string
                String strActivated = new String();
                if(activated) strActivated = "true";
                else strActivated = "false";
                
                // Write pair
                strOutValue = key + " " + totalActivation + " " + strActivated + " " + strOutNodes;
                //sLogger.info("Emitting pair: [" + key + ", " + strOutValue);
                context.write(null, new Text(strOutValue));
        }
}
