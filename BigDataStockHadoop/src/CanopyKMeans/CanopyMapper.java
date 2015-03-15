package CanopyKMeans;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class CanopyMapper extends Mapper<LongWritable, Text, Vector, Vector> {

	static ArrayList<Vector> centers = new ArrayList<Vector>();;

	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		Stock stock = new Stock(value);

		if (centers.size() == 0) {
			centers.add(new Vector(stock));
			context.write(new Vector(stock), new Vector(stock));
		} else {
			boolean isInCluster = false;
			Vector bestCenter = null;
			double bestDistance = Double.MAX_VALUE;
			double currDistance;
			
			for (Vector currCenter : centers) {
				currDistance = currCenter.measureDistance(stock);
				
				if (bestCenter == null) {
					bestDistance = currDistance;
					bestCenter = currCenter;
				}
				if (currCenter.measureDistance(stock) <= Integer
						.parseInt(context.getConfiguration().get("radios"))) {

					if (currDistance < bestDistance) {
						isInCluster = true;
						bestDistance = currDistance;
						bestCenter = currCenter;
					}
				}
			}
			if (!isInCluster) {
				centers.add(new Vector(stock));
				bestCenter = stock;
			}
			context.write(new Vector(bestCenter), new Vector(stock));
		}
	}

}
