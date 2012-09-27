package singlenodeactivation;

// import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.util.LineReader;
import org.apache.log4j.Logger;


public class VertexInfoInputFormatText extends FileInputFormat<Text,VertexInfo> {
        // Reads a text file with network information and creates key-value pairs <text, VertexInfo>
       
        @Override
        public RecordReader<Text,VertexInfo> createRecordReader(InputSplit is, TaskAttemptContext tac)  {          
                return new NodeInfoRecordReader();
        }
        //----------------------------------------------------------------------
        public static class NodeInfoRecordReader extends RecordReader<Text,VertexInfo> {
               
                private static final Logger sLogger = Logger.getLogger(NodeInfoRecordReader.class);
               
                private Text key;
                private VertexInfo value;
                private long start;
                private long end;
                // private BufferedReader br;
                private FSDataInputStream fsin;
                private LineReader lr;
            //----------------------------------------------------------------------
                @Override
                public void initialize(InputSplit is, TaskAttemptContext context)
                                throws IOException, InterruptedException {
                       
                FileSplit fileSplit= (FileSplit) is;
                start = fileSplit.getStart();
                end = start + fileSplit.getLength();
                Path file = fileSplit.getPath();
                FileSystem fs = file.getFileSystem(context.getConfiguration());
                fsin = fs.open(fileSplit.getPath());
               
                fsin.seek(start);
               
                lr = new LineReader(fsin);
               
                //br = new BufferedReader(fsin);
                }
                //----------------------------------------------------------------------
                @Override
                public void close() throws IOException {
                        fsin.close();                  
                }
                //----------------------------------------------------------------------
                @Override
                public Text getCurrentKey() throws IOException,
                                InterruptedException {
                        return key;
                }
                //----------------------------------------------------------------------
                @Override
                public VertexInfo getCurrentValue() throws IOException, InterruptedException {
                        return value;
                }
                //----------------------------------------------------------------------
                @Override
                public float getProgress() throws IOException, InterruptedException {
                        return (fsin.getPos() - start) / (float) (end - start);
                }              
                //----------------------------------------------------------------------
                @Override
                public boolean nextKeyValue() throws IOException, InterruptedException {
                       
                        // Read a line from input
                        Text line = new Text();
                        String strLine = new String();
                        // boolean uri = false;
                        // boolean genid = false;
                       
                       
//                        char firstChar, secondChar;
//                        do {
//                                int nbytes = lr.readLine(line);
//                                if(nbytes==0) return false;
//                                strLine = line.toString();
//                                firstChar = strLine.charAt(0);
//                                uri = (firstChar == '<');
//                                if(strLine.length()>1) {
//                                        secondChar = strLine.charAt(1);
//                                        genid = (firstChar == '_' && secondChar == ':');
//                                }                      
//                                else {
//                                        genid = false;
//                                }
//                        } while(!(uri || genid)); // APA

                        int nbytes = lr.readLine(line);
                        if(nbytes==0) return false;
                        strLine = line.toString();
                        
                        sLogger.info("RecordReader read:" + strLine);
                       
                        // Tokenize line
                        StringTokenizer tokenizer = new StringTokenizer(strLine);
                       
                        // Create and set current value
                        value = new VertexInfo();        
                       
                        // Get node id and activation value
                        value.setNode(new String(tokenizer.nextToken()));
                        String strActivation = tokenizer.nextToken();                  
                        value.setActivation(new Float(strActivation));  
                       
                        // Get activated boolean
                        String strActivated = tokenizer.nextToken();
                        if(strActivated.equals("true")) {
                                value.setActivated(true);
                        }
                        else {
                                value.setActivated(false);
                        }
                       
                        // Fetch destination nodes and weights
                        while (tokenizer.hasMoreTokens()) {                            
                                //value.destNodes.add(tokenizer.nextToken());
                                //value.weights.add(new Float(tokenizer.nextToken()));
                                value.addConnection(tokenizer.nextToken(), new Float(tokenizer.nextToken()));
                        }
                       
                        // Set current key
                        key = new Text(value.getNode());
                       
                        return true;
                }              
        }
}

