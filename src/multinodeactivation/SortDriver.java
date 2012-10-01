package multinodeactivation;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;





public class SortDriver extends Configured implements Tool {
                                
        //----------------------------------------------------------------------
        @Override
        public int run(String[] arg0) throws Exception {
                
                Configuration conf = getConf();
        
                // Add custom config variables to configuration
                conf.addResource("mrspread.xml");
                
                Job job = new Job(conf, "Sort by activation value");
                job.setJarByClass(SortDriver.class);
                job.setMapperClass(SortMapper.class);
                job.setReducerClass(SortReducer.class);
                //job.setPartitionerClass(SortPartitioner.class);
                //job.setGroupingComparatorClass(SortNaturalKeyGroupingComparator.class);
                job.setSortComparatorClass(SortFloatComparator.class);
                
                job.setOutputKeyClass(Text.class);
                job.setOutputValueClass(Text.class);
                
                job.setMapOutputKeyClass(FloatWritable.class);
                job.setMapOutputValueClass(Text.class);
                
                String inputPath = new String();
                String outputPath = new String();
                
                job.setInputFormatClass(VertexInfoInputFormatText.class);
                
                inputPath = "spreadactivation/Loop1output/iter4";
                outputPath = "spreadactivation/Loop1output/finalsort";
                FileInputFormat.addInputPath(job, new Path(inputPath));
                FileOutputFormat.setOutputPath(job, new Path(outputPath));                                 
                
                job.waitForCompletion(true);
                return 0;
        
        }
                
        //----------------------------------------------------------------------
        public static void main(String[] args) throws Exception {

                int exitCode = ToolRunner.run(new Configuration(), new SortDriver(), args);
                System.exit(exitCode);
        }

}
