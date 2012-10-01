package jobs;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;



public class SADriver extends Configured implements Tool {
                                
        private static final Logger sLogger = Logger.getLogger(SADriver.class);
        
        //----------------------------------------------------------------------
        @Override
        public int run(String[] arg0) throws Exception {
                
                Configuration conf = getConf();
        
                // Merge our custom config variables
                conf.addResource("mrspread.xml");
                
                // Get number of iterations
                int maxIterations = conf.getInt("algorithm.iterations",4);
                sLogger.info("ITERATIONS=" + maxIterations);
                
                FileSystem hdfs = FileSystem.get(conf);
                
                int step = 0;           
                long activatedNodes = 0;
                // long activatedNodesLast = 0;
                
                long time = System.currentTimeMillis();         
                
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

                        inputPath = "output" + step;
                        outputPath = "output" + (step + 1);
                        
                        // Delete output directory if exists
                        hdfs.delete(new Path(outputPath), true);
                        
                        FileInputFormat.addInputPath(job, new Path(inputPath));
                        // FileInputFormat.setInputPath(job, new Path(inputPath));
                        FileOutputFormat.setOutputPath(job, new Path(outputPath));                      
                        
                        // Execute job
                        job.waitForCompletion(true);
                        
                        // Get activated nodes
                        //activatedNodes = job.getCounters().findCounter(MrSpreadCounters.Counters.ACTIVATED_NODES).getValue();
                        activatedNodes = job.getCounters().findCounter("ACTIVATED_NODES", "SACounter").getValue();
                        sLogger.info("Step=" + step);
                        sLogger.info("Number of activated nodes=" + activatedNodes);
                        
                        ++step;
                        
                        // If reached max number of iterations stop
                        if(maxIterations != 0 && (step == maxIterations)) break;
                        
                } while(activatedNodes > 0);
                
                sLogger.info("Job finished in " + (float)(System.currentTimeMillis() - time)/1000.0f + " s");
                
                return 0;
        }
        //----------------------------------------------------------------------
        public static void main(String[] args) throws Exception {

                int exitCode = ToolRunner.run(new Configuration(), new SADriver(), args);
                System.exit(exitCode);
        }

}
