package singlenodeactivation;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Vector;

import org.apache.hadoop.io.Writable;

public class VertexInfo implements Writable {
        
        // Class connection with other node
        protected class Edge {
                public Edge(String node, Float w) {
                        destNode = node;
                        weight = w;
                }
                // private Edge() {}
                public String getNode() { return destNode; }
                public Float getWeight() { return weight; }
                protected String destNode;
                protected Float weight;
        }
        
        protected String nodeid;        // node name    
        protected Float activation;     // activation value of node     
        protected Boolean activated; // node has been already activated?
        protected Vector<Edge> edges; // outgoing connections
        protected Boolean isNetwork; // wether it is a network nodeinfo
        
        //----------------------------------------------------------------------
        //public NodeInfo(String id, Float act, Vector<String> dest, Vector<Float> w) {
        //      nodeid = id;
        //      activation = act;
        //      destNodes = dest;
        //      weights = w;
        //}
        //----------------------------------------------------------------------
        public VertexInfo() {
                nodeid = new String();
                activation = new Float(0.0f);
                edges = new Vector<Edge>();
                activated = false;
                isNetwork = false;
                //destNodes = new Vector<String>();
                //weights = new Vector<Float>();
        }       
        //----------------------------------------------------------------------
        public void setNode(String name) {
                this.nodeid = name;
        }
        //----------------------------------------------------------------------
        public String getNode() {
                return this.nodeid;
        }
        //----------------------------------------------------------------------
        public void setActivation(Float activation) {
                this.activation =  activation;
        }
        //----------------------------------------------------------------------
        public Float getActivation() {
                return this.activation;
        }
        //----------------------------------------------------------------------
        public void setIsNetwork(Boolean val) {
                this.isNetwork = val;
        }
        //----------------------------------------------------------------------
        public Boolean isNetwork() {
                return this.isNetwork;
        }
        //----------------------------------------------------------------------
        public void setActivated(Boolean val) {
                this.activated = val;
        }
        //----------------------------------------------------------------------
        public Boolean getActivated() {
                return this.activated;
        }
        //----------------------------------------------------------------------
        public void addConnection(String name, Float weight) {
                //destNodes.add(name);
                //weights.add(weight);
                edges.add(new Edge(name, weight));
        }
        //----------------------------------------------------------------------
        public int getConnectionsCount() {
                return edges.size();
        }
        //----------------------------------------------------------------------
        public String getNodeAt(int i) {
                //assert(i < connections.size());
                return edges.get(i).getNode();
        }
        //----------------------------------------------------------------------
        public Float getWeightAt(int i) {
                return edges.get(i).getWeight();
        }
        //----------------------------------------------------------------------
        //@Override
        public void readFields(DataInput in) throws IOException {
                // Read node name
                nodeid = "";
                int nodeidCount = in.readInt();
                for(int i=0; i<nodeidCount; ++i) {                      
                        nodeid += in.readChar();
                }
                
                // Read activation value
                activation = in.readFloat();
                
                // Read activated
                activated = in.readBoolean();
                
                isNetwork = in.readBoolean();
                
                // Read destinations
                edges.clear();
                int destCount = in.readInt();
                for(int i=0; i<destCount; ++i) {
                        // Read dest name
                        int destNameCount = in.readInt();
                        String destName = new String();
                        for(int j=0; j<destNameCount; ++j) {
                                destName += in.readChar();
                        }               
                        
                        // Add the new connection
                        edges.add(new Edge(destName, new Float(in.readFloat())));
                }               
                //assert(destNodes.size()==weights.size());                     
        }
        //----------------------------------------------------------------------
        public static VertexInfo read(DataInput in) throws IOException {
                VertexInfo ni = new VertexInfo();
        ni.readFields(in);
        return ni;

        }
        //----------------------------------------------------------------------
        //@Override
        public void write(DataOutput out) throws IOException {
                
                //assert(destNodes.size()==weights.size());
                
                // Write node name
                out.writeInt(nodeid.length());
                out.writeChars(nodeid);
                
                // Write activation value
                out.writeFloat(activation.floatValue());
                
                // Write activated value
                out.writeBoolean(activated.booleanValue());
                
                // Write isNetwork
                out.writeBoolean(isNetwork.booleanValue());
                
                // Write destinations
                int destCount = edges.size();
                out.writeInt(destCount);
                for(int i=0; i<destCount; ++i) {
                        // Write destination name
                        String destName = edges.get(i).getNode();
                        out.writeInt(destName.length());
                        out.writeChars(destName);
                        
                        // Write connection weight
                        out.writeFloat(edges.get(i).getWeight().floatValue());
                }               
        }
        //----------------------------------------------------------------------
        public String toString() {
                String out = new String();
                out = "Id: " + nodeid + " Act: " + activation + " Connections: ";
                for (Edge conn: edges) {
                        out += (conn.getNode() + "(" + conn.getWeight() + ")");
                }
                return out;
        }
}
