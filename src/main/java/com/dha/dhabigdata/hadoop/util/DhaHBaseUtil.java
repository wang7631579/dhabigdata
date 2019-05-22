package com.dha.dhabigdata.hadoop.util;

import java.io.IOException;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.ColumnPrefixFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.protobuf.generated.FilterProtos.CompareFilter;

/**
 * Hbase 基本操作 参照 https://www.itsvse.com/thread-6428-1-1.html 之前用的是V2.0的方法
 * https://blog.csdn.net/m0_38075425/article/details/81287836 V1.0
 * https://www.cnblogs.com/shadowalker/p/7350484.html 常用命令
 * 
 * @author wangpz
 *
 */
@Component
public class DhaHBaseUtil {
	@Autowired
	private Connection hbaseConnection;

	/**
	 * 关闭流
	 * 
	 * @param admin
	 * @param rs
	 * @param table
	 */
	private void close(Admin admin, ResultScanner rs, Table table) {
		if (admin != null) {
			try {
				admin.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (rs != null) {
			rs.close();
		}
		if (table != null) {
			try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 创建表
	 * 
	 * @param tableNameString 表名
	 * @param columnFamily    列簇数组
	 * @return
	 */
	public boolean creatTable(String tableNameString, List<String> columnFamily) {
		Admin admin = null;
		try {
			admin = hbaseConnection.getAdmin();
			TableName tableName = TableName.valueOf(tableNameString);
			if (admin.tableExists(tableName)) {
				System.out.println("表 " + tableNameString + " 已经存在!");
			} else {
				// 如果需要新建的表不存在
				HTableDescriptor hTableDescriptor = new HTableDescriptor(tableName);
				columnFamily.forEach(columnString -> {
					// 列族描述对象
					HColumnDescriptor columnDescriptor = new HColumnDescriptor(columnString);
					// 设置当前数据保存版本信息
					columnDescriptor.setMaxVersions(10);
					// 在数据表中添加一个列族
					hTableDescriptor.addFamily(columnDescriptor);
				});
				// 建表
				admin.createTable(hTableDescriptor);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			close(admin, null, null);
		}
		return true;
	}

	public boolean deleteTable(String tableNameString) {
		Admin admin = null;
		try {
			admin = hbaseConnection.getAdmin();
			if (admin.tableExists(TableName.valueOf(tableNameString))) {
				admin.disableTable(TableName.valueOf(tableNameString));
				admin.deleteTable(TableName.valueOf(tableNameString));
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			close(admin, null, null);
		}
		return true;
	}

	/**
	 * 自定义获取分区splitKeys
	 */
	public byte[][] getSplitKeys(String[] keys) {
		if (keys == null) {
			// 默认为10个分区
			keys = new String[] { "1|", "2|", "3|", "4|", "5|", "6|", "7|", "8|", "9|" };
		}
		byte[][] splitKeys = new byte[keys.length][];
		// 升序排序
		TreeSet<byte[]> rows = new TreeSet<byte[]>(Bytes.BYTES_COMPARATOR);
		for (String key : keys) {
			rows.add(Bytes.toBytes(key));
		}

		Iterator<byte[]> rowKeyIter = rows.iterator();
		int i = 0;
		while (rowKeyIter.hasNext()) {
			byte[] tempRow = rowKeyIter.next();
			rowKeyIter.remove();
			splitKeys[i] = tempRow;
			i++;
		}
		return splitKeys;
	}

	/**
	 * 预分区创建表
	 * 
	 * @param tableName    表名
	 * @param columnFamily 列族名的集合
	 * @param splitKeys    预分期region
	 * @return 是否创建成功
	 */
	public boolean createTableBySplitKeys(String tableName, List<String> columnFamily, byte[][] splitKeys) {
		Admin admin = null;
		try {
			admin = hbaseConnection.getAdmin();
			if (StringUtils.isBlank(tableName) || columnFamily == null || columnFamily.size() == 0) {
				System.out.println("===Parameters tableName|columnFamily should not be null,Please check!===");
				return false;
			}
			// 如果当前表已经建立
			if (admin.tableExists(TableName.valueOf(tableName))) {
				return true;
			} else {
				HTableDescriptor hTableDescriptor = new HTableDescriptor(TableName.valueOf(tableName));
				columnFamily.forEach(columnString -> {
					// 列族描述对象
					HColumnDescriptor columnDescriptor = new HColumnDescriptor(columnString);
					// 设置当前数据保存版本信息
					columnDescriptor.setMaxVersions(10);
					// 在数据表中添加一个列族
					hTableDescriptor.addFamily(columnDescriptor);
				});
				// 建表
				admin.createTable(hTableDescriptor, splitKeys);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			close(admin, null, null);
		}
		return true;
	}

	/**
	 * 按startKey和endKey，分区数获取分区
	 */
	public static byte[][] getHexSplits(String startKey, String endKey, int numRegions) {
		byte[][] splits = new byte[numRegions - 1][];
		BigInteger lowestKey = new BigInteger(startKey, 16);
		BigInteger highestKey = new BigInteger(endKey, 16);
		BigInteger range = highestKey.subtract(lowestKey);
		BigInteger regionIncrement = range.divide(BigInteger.valueOf(numRegions));
		lowestKey = lowestKey.add(regionIncrement);
		for (int i = 0; i < numRegions - 1; i++) {
			BigInteger key = lowestKey.add(regionIncrement.multiply(BigInteger.valueOf(i)));
			byte[] b = String.format("%016x", key).getBytes();
			splits[i] = b;
		}
		return splits;
	}

	/**
	 * 获取table
	 * 
	 * @param tableName 表名
	 * @return Table
	 * @throws IOException IOException
	 */
	private Table getTable(String tableName) throws IOException {
		Table table = hbaseConnection.getTable(TableName.valueOf(tableName));
		return table;
	}

	/**
	 * 获取当前连接下的所有表名
	 * 
	 * @return
	 */
	public List<String> getAllTableNames() {
		List<String> result = new ArrayList<>();
		Admin admin = null;
		try {
			admin = hbaseConnection.getAdmin();
			TableName[] tableNames = admin.listTableNames();
			for (TableName tableName : tableNames) {
				result.add(tableName.getNameAsString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(admin, null, null);
		}
		return result;
	}

	/**
	 * 获取当前表下的所有数据
	 * 
	 * @param tableName 表名
	 * @return
	 */
	public Map<String, Map<String, String>> getResultScanner(String tableName) {
		Scan scan = new Scan();
		return this.queryData(tableName, scan);
	}

	/**
	 * 根据表名 获取当前表下的所有数据
	 * 
	 * @param tableName
	 * @param scan
	 * @return
	 */
	private Map<String, Map<String, String>> queryData(String tableName, Scan scan) {
		// <rowKey,对应的行数据>
		Map<String, Map<String, String>> result = new HashMap<>();
		ResultScanner rs = null;
		// 获取表
		Table table = null;
		try {
			table = getTable(tableName);
			rs = table.getScanner(scan);
			for (Result r : rs) {
				// 每一行数据
				Map<String, String> columnMap = new HashMap<>();
				String rowKey = null;
				for (Cell cell : r.listCells()) {
					if (rowKey == null) {
						rowKey = Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
					}
					columnMap.put(
							Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(),
									cell.getQualifierLength()),
							Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
				}
				if (rowKey != null) {
					result.put(rowKey, columnMap);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(null, rs, table);
		}
		return result;
	}

	/**
	 * 根据startRowKey和stopRowKey遍历查询指定表中的所有数据
	 * 
	 * @param tableName
	 * @param startRowKey
	 * @param stopRowKey
	 * @return
	 */
	public Map<String, Map<String, String>> getResultScanner(String tableName, String startRowKey, String stopRowKey) {
		Scan scan = new Scan();
		if (StringUtils.isNoneBlank(startRowKey) && StringUtils.isNoneBlank(stopRowKey)) {
			scan.setStartRow(Bytes.toBytes(startRowKey));
			scan.setStopRow(Bytes.toBytes(startRowKey));
			// scan.setsta
			// scan.withStartRow(Bytes.toBytes(startRowKey));
			// scan.withStopRow(Bytes.toBytes(stopRowKey));
		}

		return this.queryData(tableName, scan);
	}

	/**
	 * 通过列前缀过滤器查询数据
	 * 
	 * @param tableName
	 * @param prefix
	 * @return
	 */
	public Map<String, Map<String, String>> getResultScannerColumnPrefixFilter(String tableName, String prefix) {
		Scan scan = new Scan();
		if (StringUtils.isNoneBlank(prefix)) {
			Filter filter = new ColumnPrefixFilter(Bytes.toBytes(prefix));
			scan.setFilter(filter);
		}
		return this.queryData(tableName, scan);
	}

	/**
	 * 查询行键中包含特定字符的数据 注：是根据行号进行过滤 和删选 当前查询方法是模糊查询
	 * 
	 * @param tableName
	 * @param keyword
	 * @return
	 */
	public Map<String, Map<String, String>> getResultScannerRowFilter(String tableName, String keyword) {
		Scan scan = new Scan();
		if (StringUtils.isNoneBlank(keyword)) {
			Filter filter = new RowFilter(CompareOp.GREATER_OR_EQUAL, new SubstringComparator(keyword));
			scan.setFilter(filter);
		}
		return this.queryData(tableName, scan);
	}

	/**
	 * 根据tableName和rowKey精确查询一行的数据
	 * 
	 * @param tableName 表名
	 * @param rowKey    行键
	 * @return java.util.Map<java.lang.String,java.lang.String> 返回一行的数据
	 */
	public Map<String, String> getRowData(String tableName, String rowKey) {
		Map<String, String> result = new HashMap<>();
		Get get = new Get(Bytes.toBytes(rowKey));
		// 获取表
		Table table = null;
		try {
			table = getTable(tableName);
			Result hTableResult = table.get(get);
			if (hTableResult != null && !hTableResult.isEmpty()) {
				for (Cell cell : hTableResult.listCells()) {
					result.put(
							Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(),
									cell.getQualifierLength()),
							Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(null, null, table);
		}

		return result;
	}

	/**
	 * 根据tableName、rowKey、familyName、column查询指定单元格的数据
	 * 
	 * @param tableName  表名
	 * @param rowKey     rowKey
	 * @param familyName 列族名
	 * @param columnName 列名
	 * @return java.lang.String
	 */
	public String getColumnValue(String tableName, String rowKey, String familyName, String columnName) {
		String str = null;
		Get get = new Get(Bytes.toBytes(rowKey));
		// 获取表
		Table table = null;
		try {
			table = getTable(tableName);
			Result result = table.get(get);
			if (result != null && !result.isEmpty()) {
				Cell cell = result.getColumnLatestCell(Bytes.toBytes(familyName), Bytes.toBytes(columnName));
				if (cell != null) {
					str = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(null, null, table);
		}
		return str;
	}

	/**
	 * 根据tableName、rowKey、familyName、column查询指定单元格多个版本的数据
	 * 
	 * @author zifangsky
	 * @date 2018/7/4 11:16
	 * @since 1.0.0
	 * @param tableName  表名
	 * @param rowKey     rowKey
	 * @param familyName 列族名
	 * @param columnName 列名
	 * @param versions   需要查询的版本数
	 * @return java.util.List<java.lang.String>
	 */
	public List<String> getColumnValuesByVersion(String tableName, String rowKey, String familyName, String columnName,
			int versions) {
		// 返回数据
		List<String> result = new ArrayList<>(versions);
		// 获取表
		Table table = null;
		try {
			table = getTable(tableName);
			Get get = new Get(Bytes.toBytes(rowKey));
			get.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(columnName));
			// 读取多少个版本
			get.setMaxVersions(versions);
			// get.readVersions(versions);
			Result hTableResult = table.get(get);
			if (hTableResult != null && !hTableResult.isEmpty()) {
				for (Cell cell : hTableResult.listCells()) {
					result.add(Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(null, null, table);
		}

		return result;
	}

	/**
	 * Hbase 插入数据
	 * 
	 * @param tableName  表名
	 * @param rowKey     主键
	 * @param familyName 列簇名
	 * @param columns    列名
	 * @param values     值
	 */
	public void putData(String tableName, String rowKey, String familyName, String[] columns, String[] values) {
		// 获取表
		Table table = null;
		try {
			table = getTable(tableName);
			putData(table, rowKey, tableName, familyName, columns, values);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(null, null, table);
		}
	}

	/**
	 * 为表添加 or 更新数据
	 * 
	 * @author zifangsky
	 * @date 2018/7/3 17:26
	 * @since 1.0.0
	 * @param table      Table
	 * @param rowKey     rowKey
	 * @param tableName  表名
	 * @param familyName 列族名
	 * @param columns    列名数组
	 * @param values     列值得数组
	 */
	private void putData(Table table, String rowKey, String tableName, String familyName, String[] columns,
			String[] values) {
		try {
			// 设置rowkey
			Put put = new Put(Bytes.toBytes(rowKey));
			if (columns != null && values != null && columns.length == values.length) {
				for (int i = 0; i < columns.length; i++) {
					if (columns[i] != null && values[i] != null) {
						put.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(columns[i]), Bytes.toBytes(values[i]));
					} else {
						throw new NullPointerException(
								MessageFormat.format("列名和列数据都不能为空,column:{0},value:{1}", columns[i], values[i]));
					}
				}
			}
			table.put(put);
			table.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置列值
	 * 
	 * @param tableName
	 * @param rowKey
	 * @param familyName
	 * @param column1
	 * @param value1
	 */
	public void setColumnValue(String tableName, String rowKey, String familyName, String column1, String value1) {
		Table table = null;
		try {
			// 获取表
			table = getTable(tableName);
			// 设置rowKey
			Put put = new Put(Bytes.toBytes(rowKey));
			put.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(column1), Bytes.toBytes(value1));
			table.put(put);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(null, null, table);
		}
	}

	/**
	 * 删除指定列
	 * 
	 * @param tableName  表名
	 * @param rowKey     主键
	 * @param familyName 列簇
	 * @param columnName 列名
	 * @return
	 */
	public boolean deleteColumn(String tableName, String rowKey, String familyName, String columnName) {
		Table table = null;
		Admin admin = null;
		try {
			admin = hbaseConnection.getAdmin();

			if (admin.tableExists(TableName.valueOf(tableName))) {
				// 获取表
				table = getTable(tableName);
				Delete delete = new Delete(Bytes.toBytes(rowKey));
				// 设置待删除的列
				delete.addColumns(Bytes.toBytes(familyName), Bytes.toBytes(columnName));
				table.delete(delete);
			}

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			close(admin, null, table);
		}
		return true;
	}

	/**
	 * 删除主键这一行的数据
	 * 
	 * @param tableName
	 * @param rowKey
	 * @return
	 */
	public boolean deleteRow(String tableName, String rowKey) {
		Table table = null;
		Admin admin = null;
		try {
			admin = hbaseConnection.getAdmin();
			if (admin.tableExists(TableName.valueOf(tableName))) {
				// 获取表
				table = getTable(tableName);
				Delete delete = new Delete(Bytes.toBytes(rowKey));
				table.delete(delete);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			close(admin, null, table);
		}
		return true;
	}

	/**
	 * 删除指定的列簇
	 * 
	 * @param tableName
	 * @param columnFamily
	 * @return
	 */
	public boolean deleteColumnFamily(String tableName, String columnFamily) {
		Admin admin = null;
		try {
			admin = hbaseConnection.getAdmin();
			if (admin.tableExists(TableName.valueOf(tableName))) {
				admin.deleteColumn(TableName.valueOf(tableName), Bytes.toBytes(columnFamily));
				// admin.deleteColumnFamily(TableName.valueOf(tableName),
				// Bytes.toBytes(columnFamily));
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			close(admin, null, null);
		}
		return true;
	}

}