package jobs;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;


//public class SpreadActivatorMapper extends Mapper<LongWritable, Text, Text, Text> {
public class SAMapper extends Mapper<Text, VertexInfo, Text, VertexInfo> {    
               
        private static final Logger sLogger = Logger.getLogger(SAMapper.class);
        //private Text word = new Text();
       
        float threshold = 0.0f;
        boolean loopsEnabled = false;
        float decayFactor = 0.0f;
       
        @Override
        public void setup(Context context) throws IOException {
                // Get configuration
                Configuration conf = context.getConfiguration();
               
                // Read threshold
                threshold = conf.getFloat("algorithm.activation.threshold", 0.2f);      
               
                // Read if loops are enabled
                loopsEnabled = conf.getBoolean("algorithm.loops.enabled", true);
               
                // Get decay factor
                decayFactor = conf.getFloat("algorithm.decay.factor", 0.9f);
        }
       
        public void map(Text key, VertexInfo value, Context context) throws IOException, InterruptedException {
               
                // If not loops enabled and this node has been activated before
                //if(!loopsEnabled && (value.getActivated()==true)) {
                        // Return doing nothing
                        //return;
                //}            
               
                // Calculate if this node is activated
                float outputActivation = 0.0f;
                float activation = value.getActivation().floatValue();
                if(activation > threshold) {
                        // If not activated before or loops are enabled
                        if(!value.getActivated() || loopsEnabled) {
                                outputActivation = activation;
                                value.setActivated(true);
                                       
                                // Get outgoing connections count
                                int connCount = value.getConnectionsCount();
                               
                                // Get fanout behaviour, if enabled divide activation by number of outgoing connections
                                //boolean fanout = conf.getBoolean("algorithm.fanout.enabled", false);
                                //if(fanout) {
                                //      outputActivation /= connCount;
                                //}                                    
               
                                // Emit a record for each out connection
                                for(int i=0; i< connCount; ++i) {
                                        //String valueString = new String();
                                        //valueString = outputActivation * value.weights.get(i) + " " + value.nodeid;
                                       
                                        // Emit ["nodedest", NodeInfo]
                                        VertexInfo outValue = new VertexInfo();
                                        outValue.setNode(value.getNodeAt(i));
                                        outValue.setActivation(outputActivation * value.getWeightAt(i) * decayFactor );
                                        outValue.addConnection(key.toString(), new Float(0.0f)); // hace falta???????
                                        //context.write(new Text(value.destNodes.get(i)) , new Text(valueString));
                                        context.write(new Text(value.getNodeAt(i)), outValue);
                                       
                                        //sLogger.info("Emitting pair: [" + value.getNodeAt(i) + ", " + outValue + "]");
                                }      
                        }
                }
               
                // Emit special pair for network reconstruct
                value.setActivation(activation);
                value.setIsNetwork(true);
                //sLogger.info("Emitting network pair: [" + key + ", " + value + "]");          
                context.write(key, value);
        }
}

