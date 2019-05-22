package com.dha.dhabigdata;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.dha.dhabigdata.hadoop.util.DhaHdfsUtil;
import com.dha.dhabigdata.mapper.mysql.UserMapper;
import com.dha.dhabigdata.mapper.otherdb.OtherUserMapper;



@RunWith(SpringRunner.class)
@SpringBootTest
public class DhabigdataApplicationTests {
	
	private static String driverName = "org.apache.hive.jdbc.HiveDriver";
    private static String url = "jdbc:hive2://hdpcomprs:10000/db_comprs";
    private static String user = "root";
    private static String password = "123456";
    private static Connection conn = null;
    private static Statement stmt = null;
    private static ResultSet rs = null;



	@Autowired
	UserMapper userMapper;

	@Autowired
	OtherUserMapper deptMapper;

	@Autowired
	Configuration hadoopConf;

	@Autowired
	DhaHdfsUtil hdfsUtils;
	
	@Autowired
	JdbcTemplate hiveJdbcTemplate;

	@Test
	@SuppressWarnings("all")
	public void contextLoads() {
		Map map = new HashMap<String, String>();
		map.put("code", "10001");
		List<Map> aa = userMapper.getUsersByUserCode(map);
		List<Map> bb = deptMapper.getUsersByUserCode(map);
		System.out.println(aa);
		System.out.println(bb);
	}

	@Test
	public void testHDFS() throws Exception {
		// 其他方法我就不测试了 都好用 完美
		hdfsUtils.mkdir("/20190513Test");
	}

	
	/**
	 * // https://blog.csdn.net/congcong68/article/details/42043093
	 * 参照
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMR() throws Exception {
		// 加载驱动
		// System.setProperty("hadoop.home.dir", HADOOP_HOME_DIR);
		// System.load("D:/hadoop-common-2.2.0-bin-master/bin/hadoop.dll");
		// Configuration conf = new Configuration();

		// 获取job,告诉他需要加载那个类
		Job job = Job.getInstance(hadoopConf, WordCountApp.class.getSimpleName());

		// 如果文件达成jar包在hadoop运行必须做这个设置
		job.setJarByClass(WordCountApp.class);

		// 获取文件数据
		FileInputFormat.setInputPaths(job, new Path("hdfs://192.168.1.123:9000/hello.txt"));
		// 通过TextInputFormat把读到的数据处理成<k1,v1>形式
		job.setInputFormatClass(TextInputFormat.class);
		// job中加入Mapper，同时MyMapper类接受<k1,v1>作为参数传给类中map函数进行数据处理
		job.setMapperClass(WordCountApp.MyMapper.class);
		// 设置输出的<k2,v2>的数据类型
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(LongWritable.class);
		// job中加入Reducer,Reducer自动接收处理好的map数据
		job.setReducerClass(WordCountApp.MyReducer.class);
		// 设置输出的<k3,v3>的数据类型
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);

		// 设置输出目录文件out1
		String OUT_DIR = "hdfs://192.168.1.123:9000/output";
		FileOutputFormat.setOutputPath(job, new Path(OUT_DIR));
		job.setOutputFormatClass(TextOutputFormat.class);
		// 如果这个文件存在则删除，如果文件存在不删除会报错。
		WordCountApp.deleteOutDir(hadoopConf, OUT_DIR);
		// 把处理好的<k3,v3>的数据写入文件
		
		job.waitForCompletion(true);

		System.out.println("--- END");
	}

	
	
	
	@Test
	@SuppressWarnings("all")
	public void testHive() throws Exception {
		//hiveJdbcTemplate.exec
		//hiveJdbcTemplate.execute("create database hive_jdbc_test11111");
		//hiveJdbcTemplate.execute("use dha2019");
		List<Map<String,Object>> rows = hiveJdbcTemplate.queryForList("select * from table01"); 
		System.out.println("-------------------------");
		System.out.println(rows.size());
	}
	
	@Autowired
	com.dha.dhabigdata.hadoop.util.DhaHBaseUtil dhaHBaseUtil;
	
	@Test
	@SuppressWarnings("all")
	public void testHbase() throws Exception {
		System.out.println("---begin");
		String tableNameString = "hbaseTest2019";
		List<String> columnFamily = Arrays.asList("c01", "c02");
		List<String> columnValuesByVersion = dhaHBaseUtil.getColumnValuesByVersion("hbaseTest2019", "001", "c01",
				"name", 3);
		System.out.println("---end");

	}
	
	
}
