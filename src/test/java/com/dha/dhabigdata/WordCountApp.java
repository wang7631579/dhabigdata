package com.dha.dhabigdata;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
 
public class WordCountApp {	
	private static String HADOOP_HOME_DIR = "D:\\hadoop-common-2.7.5-bin-master";
	/**我定义一个内部类MyMapper继承Mapper类
	 * 泛型解释：LongWritable是大数据里的类型对应java中的Long类型
	 *         Text对应java里的String类型，所以Mapper泛型前2个就是LongWritable, Text
	 * 逻辑解释：由于我们做的是单词计数，文件中的单词是下面2行
	 *         hello  you
	 *         hello  me
	 * 所以 ，根据上面
	 * 步骤1.1，则   <k1,v1>是<0, hello	you>,<10,hello	me> 形式   
	 * 文件的读取原则：<每行起始字节数，行内容>，所以第一行起始字节是0，内容是hello you
	 *             第二行起始字节是10，内容是hello me，从而得出k1,v1
	 * 步骤1.2：如果我们要实现计数，我们可以把上面的形式通过下面的map函数转换成这样
	 * <k2,v2>--->  <hello,1><you,1><hello,1><me,1>
	 * 于是Mapper泛型后2个就是Text，LongWritable
	 *可以理解泛型前2个为输入的map类型，后2个为输出的map类型
	 */
	public static class MyMapper extends Mapper<LongWritable, Text, Text, LongWritable>{		
		//定义一个k2,v2
		Text k2 = new Text(); 
		LongWritable v2 = new LongWritable();
		@Override
		//下面的key就是从文件中读取的k1,value就是v1，map函数就是在执行步骤1.2
		protected void map(LongWritable key, Text value,
				Mapper<LongWritable, Text, Text, LongWritable>.Context context)
				throws IOException, InterruptedException {
			String[] words = value.toString().split(",");
			for (String word : words) {
				//word表示每一行中的每个单词，即k2
				k2.set(word);
				v2.set(1L);	//没排序分组前每个单词都是1个，由于是Long类型所以加L			
				context.write(k2, v2);//写出
			}
		}
	}
	//步骤1.3:对输出的所有的k2、v2进行分区去执行MapperTask
	//步骤1.4：shuffle-排序后的结果是<hello,1><hello,1><me,1><you,1>
	//        分组后的结果是<hello,{1,1}><me,{1}><you,{1}>
    //1.3和1.4,1.5是hadoop自动帮我们做的，我们做的就是上面写的map函数的输出逻辑
	
	/**
	 * 下面这个MyReducer函数是输出<k3,v3>的函数，逻辑要我们自己写。
	 * 传入的参数是上面得到的<hello,{1,1}><me,{1}><you,{1}>
	 * 把这些map分给不同的ReducerTask去完成最后
	 * 输出为<k3,v3>是<hello, 2>,<me, 1>,<you, 1>
	 */
	public static class MyReducer extends Reducer<Text, LongWritable, Text, LongWritable>{
		LongWritable v3 = new LongWritable();
		@Override
		//传入的数据形如<hello,{1,1}>，V的值是个集合，所以这里Iterable<LongWritable>
		protected void reduce(Text k2, Iterable<LongWritable> v2s,
				Reducer<Text, LongWritable, Text, LongWritable>.Context context)
				throws IOException, InterruptedException {
			long count = 0L;
			for (LongWritable v2 : v2s) {
				count += v2.get();
			}
			v3.set(count);
			//k2就是k3,都是一个单词
			context.write(k2, v3);
		}
	}
	public static void deleteOutDir(Configuration conf, String OUT_DIR)
			throws IOException, URISyntaxException {
		FileSystem fileSystem = FileSystem.get(new URI(OUT_DIR), conf);
		if(fileSystem.exists(new Path(OUT_DIR))){
			fileSystem.delete(new Path(OUT_DIR), true);
		}
	}
	/**
	 * 上面我们把map，reduce都写完了，下面我们把它们合在一起，运转起来
	 */
	public static void main(String[] args) throws Exception {
		//加载驱动
		System.setProperty("hadoop.home.dir", HADOOP_HOME_DIR);
		//System.load("D:/hadoop-common-2.2.0-bin-master/bin/hadoop.dll");
		Configuration conf = new Configuration();
		
		
		
		//获取job,告诉他需要加载那个类
		Job job = Job.getInstance(conf, WordCountApp.class.getSimpleName());
		
		
		
		//如果文件达成jar包在hadoop运行必须做这个设置
		job.setJarByClass(WordCountApp.class);
		
		
		//获取文件数据
		FileInputFormat.setInputPaths(job, new Path("hdfs://192.168.1.123:9000/hello.txt"));
		
		
		//通过TextInputFormat把读到的数据处理成<k1,v1>形式
		job.setInputFormatClass(TextInputFormat.class);
		//job中加入Mapper，同时MyMapper类接受<k1,v1>作为参数传给类中map函数进行数据处理
		job.setMapperClass(MyMapper.class);
		//设置输出的<k2,v2>的数据类型
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(LongWritable.class);
		//job中加入Reducer,Reducer自动接收处理好的map数据
		job.setReducerClass(MyReducer.class);
		//设置输出的<k3,v3>的数据类型
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);
		
		
		//设置输出目录文件out1
		String OUT_DIR = "hdfs://192.168.1.123:9000/output";
		FileOutputFormat.setOutputPath(job, new Path(OUT_DIR));
		job.setOutputFormatClass(TextOutputFormat.class);
		//如果这个文件存在则删除，如果文件存在不删除会报错。
		deleteOutDir(conf, OUT_DIR);
		//把处理好的<k3,v3>的数据写入文件
		//https://blog.csdn.net/congcong68/article/details/42043093
		job.waitForCompletion(true);
		
		System.out.println("--- END");
	}
}
