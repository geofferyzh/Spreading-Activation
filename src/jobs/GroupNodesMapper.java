package jobs;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.log4j.Logger;


public class GroupNodesMapper extends Mapper<Text, VertexInfo, Text, VertexInfo> {
        
        private static final Logger sLogger = Logger.getLogger(GroupNodesMapper.class);
        
        public void map(Text key, VertexInfo value, Context context) throws IOException, InterruptedException {
                
                context.write(key, value);              
        }

}
