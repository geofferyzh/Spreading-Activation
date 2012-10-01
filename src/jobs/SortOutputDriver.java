package jobs;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;



public class SortOutputDriver extends Configured implements Tool {
                                
        private static final Logger sLogger = Logger.getLogger(SortOutputDriver.class);
        
        //----------------------------------------------------------------------
        @Override
        public int run(String[] arg0) throws Exception {
                
                Configuration conf = getConf();
        
                // Merge our custom config variables
                conf.addResource("mrspread.xml");
                
                FileSystem hdfs = FileSystem.get(conf);
                
                        
                
                Job job = new Job(conf, "SortOutput");
                job.setJarByClass(SortOutputDriver.class);
                job.setMapperClass(SortOutputMapper.class);
                job.setReducerClass(SortOutputReducer.class);
                
                job.setOutputKeyClass(Text.class);
                job.setOutputValueClass(Text.class);
                
                job.setMapOutputKeyClass(FloatWritable.class);
                job.setMapOutputValueClass(Text.class);
                
                job.setInputFormatClass(VertexInfoInputFormatText.class);
                
                // Chain execution, input is output of last step
                String inputPath = new String();
                String outputPath = new String();
                inputPath = arg0[0];
                outputPath = "sortedoutput";
                
                // Delete output directory if exists
                hdfs.delete(new Path(outputPath), true);
                
                FileInputFormat.addInputPath(job, new Path(inputPath));
                FileOutputFormat.setOutputPath(job, new Path(outputPath));
                
                long time = System.currentTimeMillis(); 
                
                // Execute job
                job.waitForCompletion(true);
                
                sLogger.info("Job finished in " + (float)(System.currentTimeMillis() - time)/1000.0f + " s");
                
                return 0;
        }
        //----------------------------------------------------------------------
        public static void main(String[] args) throws Exception {

                int res = ToolRunner.run(new Configuration(), new SortOutputDriver(), args);
                System.exit(res);
        }

}
