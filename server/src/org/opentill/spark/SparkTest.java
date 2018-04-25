package org.opentill.spark;

import java.io.File;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

import com.opentill.main.Config;


public class SparkTest {
	public SparkTest () {
		// Create a Java Spark Context
		/*SparkSession spark = SparkSession
				  .builder()
				  .appName("Java Spark SQL basic example")
				  .config("spark.some.config.option", "some-value")
				  .getOrCreate();*/
		//SparkConf conf = new SparkConf().setAppName("wordCount");
		JavaSparkContext sc = new JavaSparkContext();
		// Load our input data.
		JavaRDD<String> input = sc.textFile(Config.APP_HOME + File.separatorChar + "input.txt");
		if (input.collect().contains("Hello World")) {
			System.out.println("Hello World");
		}
		// Split up into words.
		/*JavaRDD<String> words = input.flatMap(
		new FlatMapFunction<String, String>() {
		public Iterable<String> call(String x) {
		return Arrays.asList(x.split(" "));
		}});
		// Transform into pairs and count.
		JavaPairRDD<String, Integer> counts = words.mapToPair(
		new PairFunction<String, String, Integer>(){
		public Tuple2<String, Integer> call(String x){
			return new Tuple2(x, 1);
		}}).reduceByKey(new Function2<Integer, Integer, Integer>(){ public Integer call(Integer x, Integer y){ return x + y;}}); // Save the word count back out to a text file, causing evaluation. counts.saveAsTextFile(outputFile);
		}*/
	}
}
