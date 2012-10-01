package multinodeactivation;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;



public class SADriver extends Configured implements Tool {
                                
        private static final Logger sLogger = Logger.getLogger(SADriver.class);
        
        // load initial activaion list for looping        
        public static final String InitialActivationList = "/.../Initial_Activation_List.txt";
        //Collection<String> activationList;
        //Iterator<String> activationListIter;
        String toBeActivated;
        
	    public void loadInitialActivation(String InitialActivationList, int linenum_index) throws IOException {			
		    String line; 
		    int linenum = 0;
		    
		    BufferedReader bufferedReader = new BufferedReader(new FileReader(InitialActivationList));
		    while ((line = bufferedReader.readLine()) != null) {	    	
		    	linenum = linenum + 1;
		    	if (linenum == linenum_index) { 
		    		toBeActivated = line.toString();
		    		break;
		    	}
		    }
		    
		    bufferedReader.close();
		    
	    } 	    
	    
        //----------------------------------------------------------------------
        @Override
        public int run(String[] arg0) throws Exception {
                
                Configuration conf = getConf();
        
                // Add custom config variables to configuration
                conf.addResource("mrspread.xml");
                
                FileSystem hdfs = FileSystem.get(conf);        
                	
                // Get number of iterations
                int maxIterations = conf.getInt("algorithm.iterations",5);
                sLogger.info("ITERATIONS=" + maxIterations);
                
                // Outer Loop "Loop through nodes starts" ...................................
                long time_outer = System.currentTimeMillis();  
                int numOfNodes = 2;
                for(int n_nodes=1; n_nodes <= numOfNodes; ++n_nodes) {
                	
		            // load and set initial activation through jobconf
	                loadInitialActivation(InitialActivationList, n_nodes);
		            conf.setInt("toActivate", Integer.parseInt(toBeActivated));
                
	                int step = 0;           
	                long activatedNodes = 0;               
	                long time_inner = System.currentTimeMillis();  
	                
	                // Inner Loop "Within activation iteration Starts" .......................   
	                do {         
	                        Job job = new Job(conf, "Spreading_Activation_Step_" + step);
	                        job.setJarByClass(SADriver.class);
	                        job.setMapperClass(SAMapper.class);
	                        job.setReducerClass(SAReducer.class);
	                        
	                        job.setOutputKeyClass(Text.class);
	                        job.setOutputValueClass(Text.class);
	                        
	                        job.setMapOutputKeyClass(Text.class);
	                        job.setMapOutputValueClass(VertexInfo.class);
	                        
	                        //NodeInfoInputFormatText.setMaxInputSplitSize(job, 200);
	                        job.setInputFormatClass(VertexInfoInputFormatText.class);
	                                               
	                        // Chain execution, input is output of last step
	                        String inputPath = new String();
	                        String outputPath = new String();
	                        
	                        if(step==0) {
	                        	inputPath = "spreadactivation/output" + step;
	                        	outputPath = "spreadactivation/Loop" + n_nodes + "output/iter" + (step + 1);
	                        }
	                        else {
		                        inputPath = "spreadactivation/Loop" + n_nodes + "output/iter" + step;
		                        outputPath = "spreadactivation/Loop" + n_nodes + "output/iter" + (step + 1);
	                        }
	                        
	                        // Delete output directory if exists
	                        hdfs.delete(new Path(outputPath), true);
	                        
	                        FileInputFormat.addInputPath(job, new Path(inputPath));
	                        // FileInputFormat.setInputPath(job, new Path(inputPath));
	                        FileOutputFormat.setOutputPath(job, new Path(outputPath));                      
	                        
	                        // Execute job
	                        job.waitForCompletion(false);
	                        
	                        // Get activated nodes
	                        //activatedNodes = job.getCounters().findCounter(MrSpreadCounters.Counters.ACTIVATED_NODES).getValue();
	                        activatedNodes = job.getCounters().findCounter("ACTIVATED_NODES", "SACounter").getValue();
	                        sLogger.info("--------------- Step=" + step);
	                        sLogger.info("Number of activated nodes=" + activatedNodes);
	                        
	                        ++step;
	                        
	                        // If reached max number of iterations stop
	                        if(maxIterations != 0 && (step == maxIterations)) break;
	                        
	                } while(activatedNodes > 0);
	                // Inner Loop "Within activation iteration ends" .......................                
                    
	                sLogger.info("------------------------------ Outer loop activation #" + n_nodes + " finished in " + (float)(System.currentTimeMillis() - time_inner)/1000.0f + " s");
	                
	                // --------------------------------------------------------------
	                // Job to Sort the nodes by activation values in descending order
	                // --------------------------------------------------------------
	                
	                Job job = new Job(conf, "Sort by activation value");
	                job.setJarByClass(SortDriver.class);
	                job.setMapperClass(SortMapper.class);
	                job.setReducerClass(SortReducer.class);
	                
	                // add a sortcomparator class to sort in descending order
	                job.setSortComparatorClass(SortFloatComparator.class);
	                
	                job.setOutputKeyClass(Text.class);
	                job.setOutputValueClass(Text.class);
	                
	                job.setMapOutputKeyClass(FloatWritable.class);
	                job.setMapOutputValueClass(Text.class);
	                
	                String inputPath = new String();
	                String outputPath = new String();
	                
	                job.setInputFormatClass(VertexInfoInputFormatText.class);
	                
	                inputPath = "spreadactivation/Loop" + n_nodes + "output/iter" + step;
	                outputPath = "spreadactivation/Loop" + n_nodes + "output/finalsort";
	                FileInputFormat.addInputPath(job, new Path(inputPath));
	                FileOutputFormat.setOutputPath(job, new Path(outputPath));                                 
	                
	                job.waitForCompletion(true);
	                
	                
                } 
                // Outer Loop "Loop through nodes ends" ...................................
                
                sLogger.info("------------------------------ Entire job finished in " + (float)(System.currentTimeMillis() - time_outer)/1000.0f + " s");
                
                return 0;
        }
        //----------------------------------------------------------------------
        public static void main(String[] args) throws Exception {

                int exitCode = ToolRunner.run(new Configuration(), new SADriver(), args);
                System.exit(exitCode);
        }

}
