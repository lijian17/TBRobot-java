package net.dxs.client.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * 写日志
 * 
 * @author lijian-pc
 * @date 2016-11-22 上午10:15:16
 */
public class WLog {
	/** 日志开关：true:开启；false:关闭 */
	private static final boolean flag = true;
	/** 日志加密开关：true:开启；false:关闭 */
	private static final boolean flag_encrypt = false;
	/** 单个日志文件大小(单位：Kb；配置1024即为1M) */
	private static final int logFileSize = 1024;
	/** 日志保存时间(单位：天) */
	private static final int logSaveTime = 30;

	private static WLog instance;

	private String DCARD_PATH;
	/** 当前日期 */
	private String currentData = null;

	private WLog() {
		if (!flag) {
			return;
		}
		if (!createFolder("wlog")) {
			return;
		}

		// 扫描日志目录，清理过期日志文件
		if (!clearLogFile(DCARD_PATH, logSaveTime)) {
			// 暂不做处理
		}
	}

	public static WLog getInstance() {
		if (instance == null) {
			instance = new WLog();
		}
		return instance;
	}

	/**
	 * 保存日志到文件中（devState）
	 * 
	 * @param log
	 *            日志内容
	 */
	public void saveLog2File_myLog(String log) {
		saveLog2File("myLog", log);
	}

	/**
	 * 保存日志到文件中（BMP）
	 * 
	 * @param log
	 *            日志内容
	 */
	public void saveLog2File_BMP(String log) {
		saveLog2File("BMP", log);
	}

	/**
	 * 保存日志到文件中（printer）
	 * 
	 * @param log
	 *            日志内容
	 */
	public void saveLog2File_printer(String log) {
		saveLog2File("printer", log);
	}

	/**
	 * 保存日志到文件中（webView）
	 * 
	 * @param log
	 *            日志内容
	 */
	public void saveLog2File_webView(String log) {
		saveLog2File("webView", log);
	}

	/**
	 * 保存日志到文件中（tdos）
	 * 
	 * @param log
	 *            日志内容
	 */
	public void saveLog2File_tdos(String log) {
		saveLog2File("tdos", log);
	}

	/**
	 * 保存日志到文件中
	 * 
	 * @param name
	 *            日志文件名
	 * @param log
	 *            日志内容
	 */
	public void saveLog2File(String name, String log) {
		if (!flag) {
			return;
		}
		if (!haveSDCard()) {
			return;
		}

		FileWriter fw = null;
		BufferedWriter bufw = null;
		try {
			fw = new FileWriter(getFileObj(name, 0, logFileSize * 1024), true);
			bufw = new BufferedWriter(fw);

			log = getTime() + log;
			if (flag_encrypt) {
				log = DES3.encode(log);
			}

			bufw.write(log);
			bufw.newLine();
			bufw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fw != null) {
					fw.close();
				}
				if (bufw != null) {
					bufw.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	/**
	 * 获得一个输出文件对象（当该文件大小大于指定大小时自动累加）
	 * 
	 * @param name
	 *            文件前缀名
	 * @param index
	 *            当前文件编号（当出现多个文件时）
	 * @param maxFileSize
	 *            单个日志文件大小
	 * @return
	 */
	private File getFileObj(String name, int index, long maxFileSize) {
		File file = new File(DCARD_PATH + getDate_current() + File.separator,
				name + "_" + getDate_current() + "_" + index + ".log");
		long fileSize = file.length();
		if (fileSize >= maxFileSize) {
			return getFileObj(name, index + 1, maxFileSize);
		}
		return file;
	}

	/**
	 * 获取当前日期（性能优化版）<br>
	 * 成员变量保存（有直接返回，无创建后返回并保存至成员变量）
	 */
	private String getDate_current() {
		if (currentData == null) {
			currentData = getCorrectDate(0);
		}
		return currentData;
	}

	private String getTime() {
		long time = System.currentTimeMillis();
		SimpleDateFormat format = new SimpleDateFormat(
				"[yyyy-MM-dd HH:mm:ss.SSS] ");
		Date date = new Date(time);
		String t = format.format(date);
		return t;
	}

	@SuppressWarnings("unused")
	private String getDate() {
		long time = System.currentTimeMillis();
		SimpleDateFormat format = new SimpleDateFormat("_yyyyMMdd");
		Date date = new Date(time);
		String t = format.format(date);
		return t;
	}

	/**
	 * 是否有SD卡
	 * 
	 * @return
	 */
	private boolean haveSDCard() {
		return true;
	}

	/**
	 * 创建文件夹(有则不创建，无则创建)
	 * 
	 * @param name
	 *            文件夹名称
	 * @return true:创建成功或者已经存在;false:系统没有SDCard
	 */
	public boolean createFolder(String name) {
		boolean bResult = false;

		if (haveSDCard()) {
			File directory = new File(name);
			DCARD_PATH = directory.getAbsolutePath() + File.separator;
			String dir = DCARD_PATH + getDate_current() + File.separator;
			File file = new File(dir);
			// 如果目录不存在则创建之
			if (!file.exists() && !file.isDirectory()) {
				file.mkdirs();
			}
			System.out.println("SDCARD_PATH--->" + dir);
			bResult = true;
		} else {
			bResult = false;
		}

		return bResult;
	}

	/**
	 * 获取一个纠正后的时间（格式：yyyyMMdd；例：20170523）
	 * 
	 * @param _day
	 *            由当天向前推进的天数
	 * @return
	 */
	private String getCorrectDate(int _day) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, _day);
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String t = format.format(calendar.getTime());
		return t;
	}

	/**
	 * 清理过期日志文件
	 * 
	 * @param _logDir
	 *            日志目录
	 * @param _logSaveTime
	 *            日志保留时长（保留最近多少天的日志文件）
	 * @return
	 */
	private boolean clearLogFile(String _logDir, int _logSaveTime) {
		// 日志保留时长必须大于等于1天
		if (_logSaveTime <= 0) {
			return false;
		}
		boolean bResult = false;

		// 计算出一个要被保留的日志目录数组
		ArrayList<String> arrayList = new ArrayList<String>(_logSaveTime);
		for (int i = 0; i < _logSaveTime; i++) {
			arrayList.add(getCorrectDate(-i));
		}

		File dir = new File(_logDir);
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				if (!arrayList.contains(files[i].getName())) {
					bResult = removeDir(files[i]);
				}
			} else {
				bResult = files[i].delete();
			}
		}

		return bResult;
	}

	/**
	 * 删除目录
	 * 
	 * @param dir
	 *            要被删除的目录
	 * @return
	 */
	private boolean removeDir(File dir) {
		boolean bResult = false;
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				bResult = removeDir(files[i]);
			} else {
				bResult = files[i].delete();
			}
		}
		bResult = dir.delete();
		return bResult;
	}
}
