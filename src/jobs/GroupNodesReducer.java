package jobs;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.log4j.Logger;


public class GroupNodesReducer extends Reducer<Text, VertexInfo, Text, Text> {
       
private static final Logger sLogger = Logger.getLogger(GroupNodesReducer.class);
       
        protected void reduce(Text key, Iterable<VertexInfo> values, Context context) throws IOException, InterruptedException {
               
                String strValue = new String();
                for(VertexInfo node : values) {
                       
                }
                context.write(key, new Text(strValue));
        }

}

