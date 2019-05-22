package com.dha.dhabigdata.hadoop.util;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 创建HDFS 工具类
 * 
 * @author wangpz
 *
 */
@Component
public class DhaHdfsUtil {

	@Autowired
	private Configuration dhaHadoopConfiguration;

	/**
	 * 获取HDFS文件系统
	 * 
	 * @return org.apache.hadoop.fs.FileSystem
	 */
	private FileSystem getFileSystem() {
		try {
			return FileSystem.get(dhaHadoopConfiguration);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 关闭HDFS文件系统
	 */
	private void close(FileSystem fileSystem) {
		if (fileSystem != null) {
			try {
				fileSystem.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 判断当前目录是否存在
	 * 
	 * @param path
	 * @return
	 */
	public boolean checkExists(FileSystem fileSystem, String path) {
		try {
			if (fileSystem == null) {
				fileSystem = getFileSystem();
			}
			// 判断当前目录是否存在
			return fileSystem.exists(new Path(path));
		} catch (IOException e) {
			e.printStackTrace();
			// 如果存在就不进行创建
			return false;
		}
	}

	/**
	 * 创建文件夹
	 * 
	 * @param path 文件夹路径
	 * @return
	 */
	public boolean mkdir(String path) {
		FileSystem fileSystem = null;
		fileSystem = getFileSystem();
		// 如果目录已经存在，则直接返回
		if (checkExists(fileSystem, path)) {
			return true;
		} else {
			try {
				// 创建目录
				return fileSystem.mkdirs(new Path(path));
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			} finally {
				close(fileSystem);
			}
		}
	}

	/**
	 * 删除文件夹
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean deleteDir(String path) throws Exception {
		FileSystem fileSystem = null;
		try {
			fileSystem = getFileSystem();
			return fileSystem.delete(new Path(path), true);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			close(fileSystem);
		}
	}

	/**
	 * 创建文件
	 * 
	 * @param path hdfs文件地址(例：/hadoop/abc.txt)
	 * @throws IOException
	 */
	public void createFile(String path) throws IOException {
		FileSystem fileSystem = null;
		fileSystem = getFileSystem();
		try {
			// 创建文件
			if (checkExists(fileSystem, path)) {
			} else {
				fileSystem.create(new Path(path));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(fileSystem);
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param path hdfs文件地址(例：/hadoop/abc.txt)
	 * @throws IOException
	 */
	public void deleteFile(String path) throws IOException {
		FileSystem fileSystem = null;
		fileSystem = getFileSystem();
		try {
			// 创建文件
			if (checkExists(fileSystem, path)) {
			} else {
				fileSystem.delete(new Path(path), true);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(fileSystem);
		}
	}

	/**
	 * 将dst1重命名为dst2，也可以进行文件的移动
	 * 
	 * @param oldpath 旧名
	 * @param newpath 新名
	 */
	public void moveFile(String srcPath, String dstPath) {
		FileSystem fileSystem = null;
		fileSystem = getFileSystem();
		try {
			if (!checkExists(fileSystem, srcPath)) {
				// System.out.println(srcPath + " 文件不存在！");
				return;
			}
			if (checkExists(fileSystem, dstPath)) {
				// System.out.println(dstPath + "已存在！");
				return;
			}
			// 将文件进行重命名，可以起到移动文件的作用
			fileSystem.rename(new Path(srcPath), new Path(dstPath));
			// System.out.println("文件已重命名！");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(fileSystem);
		}
	}

	/**
	 * 上传文件到hdfs
	 * 
	 * @param local
	 * @param dst
	 */
	public void uploadFile(String srcPath, String dstPath) {
		FileSystem fileSystem = null;
		fileSystem = getFileSystem();
		try {
			// 从本地将文件拷贝到HDFS中，如果目标文件已存在则进行覆盖
			fileSystem.copyFromLocalFile(new Path(srcPath), new Path(dstPath));
			// System.out.println("上传成功！");
			// 关闭连接
		} catch (IOException e) {
			// System.out.println("上传失败！");
			e.printStackTrace();
		} finally {
			close(fileSystem);
		}
	}

	/**
	 * 下载文件到本地
	 * 
	 * @param dst
	 * @param local
	 */
	public void downLoadFile(String dst, String local) {
		FileSystem fileSystem = null;
		fileSystem = getFileSystem();
		try {
			if (!fileSystem.exists(new Path(dst))) {
				// System.out.println("文件不存在！");
			} else {
				fileSystem.copyToLocalFile(new Path(dst), new Path(local));
				// System.out.println("下载成功！");
			}
		} catch (IOException e) {
			// System.out.println("下载失败！");
			e.printStackTrace();
		} finally {
			close(fileSystem);
		}
	}

	/**
	 * 查看文件内容 飞
	 * 
	 * @param dst hdfs文件地址（例:/hadoop/abc.txt）
	 * @throws IOException
	 */
	public FSDataInputStream readFile(String dst) {
		FileSystem fileSystem = null;
		fileSystem = getFileSystem();
		try {
			if (fileSystem.exists(new Path(dst))) {
				FSDataInputStream inputstream = fileSystem.open(new Path(dst));
				return inputstream;
			} else {
				// System.out.println("文件不存在");
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			close(fileSystem);
		}
		/*
		 * FSDataInputStream fs=hhhh.readFile("/hadoopTest/123.txt"); InputStreamReader
		 * isr = new InputStreamReader(fs); BufferedReader br=new BufferedReader(isr);
		 * String str =""; while((str=br.readLine())!=null) { System.out.println(str); }
		 */
	}

	/**
	 * 显示目录下所有文件
	 * 
	 * @param dst
	 */
	public FileStatus[] listStatus(String dst) {
		FileSystem fileSystem = null;
		fileSystem = getFileSystem();
		try {
			if (!fileSystem.exists(new Path(dst))) {
				// System.out.println("目录不存在！");
				return null;
			}
			// 文件夹下的所有文件状态
			return fileSystem.listStatus(new Path(dst));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			close(fileSystem);
		}
	}
}
