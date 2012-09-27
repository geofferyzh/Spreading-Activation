package dataprep;

import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
//import org.apache.hadoop.fs.FileSystem;
//import org.apache.hadoop.fs.Path;
//import org.apache.hadoop.filecache.DistributedCache;


import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;


public class PrepDriver extends Configured implements Tool {
                
	    // public static final String InitialActivationList = "/home/training/workspace/SA_Shaohua/data/Initial_Activation_List.txt";
		// public static final String HDFS_Activation_List = "/user/shazhang/HDFS_Activation_List.txt";
		
		  
        @Override
        public int run(String[] args) throws Exception {
                
        		Configuration conf = getConf();
        		// JobConf conf = new JobConf(new Configuration(), PrepDriver.class);
                
                // Add Distributed Cache 
        		// FileSystem fs = FileSystem.get(conf);
    		    // Path hdfsPath = new Path(HDFS_Activation_List);
    		    
    		    // fs.copyFromLocalFile(false, true, new Path(InitialActivationList),hdfsPath); // Upload to HDFS
    		    // DistributedCache.addCacheFile(hdfsPath.toUri(), conf); // Add file to distributed cache
        
                // Merge custom config variables
                conf.addResource("mrspread.xml");
   
                Job job = new Job(conf, "Data Preparation");
                job.setJarByClass(PrepDriver.class);
                job.setOutputKeyClass(Text.class);
                job.setOutputValueClass(Text.class);
                job.setMapperClass(PrepMapper.class);
                job.setReducerClass(PrepReducer.class);                       
                
                FileInputFormat.addInputPath(job, new Path(args[0]));
                FileOutputFormat.setOutputPath(job, new Path(args[1]));                      
                        
                // Execute job
                job.waitForCompletion(true);
                       
                return 0;
        }
        //----------------------------------------------------------------------
        public static void main(String[] args) throws Exception {
                int exitCode = ToolRunner.run(new Configuration(), new PrepDriver(), args);
                System.exit(exitCode);
        }

}
