package multinodeactivation;

import java.io.*;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

// Groups values based on the natural key
public class SortFloatComparator extends WritableComparator {

	//Constructor.
	 
	protected SortFloatComparator() {
		super(FloatWritable.class, true);
	}
	
	@SuppressWarnings("rawtypes")

	@Override
	public int compare(WritableComparable w1, WritableComparable w2) {
		FloatWritable k1 = (FloatWritable)w1;
		FloatWritable k2 = (FloatWritable)w2;
		
		//int comp = k1.compareTo(k2);

		//if(0 == comp) {
			//comp = -1 * k1.compareTo(k2);
		//}
		return -1 * k1.compareTo(k2);
	}
}
