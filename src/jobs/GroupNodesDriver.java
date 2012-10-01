package jobs;

//import mappers.SpreadActivatorMapper;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;


public class GroupNodesDriver extends Configured implements Tool {

        @Override
        public int run(String[] arg0) throws Exception {
Configuration conf = getConf();
               
                Job job = new Job(conf, "GroupNodes");
                job.setJarByClass(GroupNodesDriver.class);
                job.setMapperClass(GroupNodesMapper.class);
                job.setReducerClass(GroupNodesReducer.class);
               
                job.setOutputKeyClass(Text.class);
                job.setOutputValueClass(Text.class);
               
                job.setInputFormatClass(VertexInfoInputFormatText.class);
               
                FileInputFormat.addInputPath(job, new Path("input"));
                FileOutputFormat.setOutputPath(job, new Path("output"));
               
                job.waitForCompletion(true);
               
                return 0;
        }

}

