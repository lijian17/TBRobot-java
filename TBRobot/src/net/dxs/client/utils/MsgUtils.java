package net.dxs.client.utils;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MsgUtils {

	/**
	 * number转byte数组
	 * 
	 * @param n
	 * @return
	 */
	private static byte[] toLH(int n) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) (n & 0xff);
		bytes[1] = (byte) (n >> 8 & 0xff);
		bytes[2] = (byte) (n >> 16 & 0xff);
		bytes[3] = (byte) (n >> 24 & 0xff);
		return bytes;
	}

	/**
	 * 高位在前，低位在后
	 * 
	 * @param num
	 * @return
	 */
	public static byte[] int2bytes(int num) {
		byte[] result = new byte[4];
		result[0] = (byte) ((num >>> 24) & 0xff);// 说明一
		result[1] = (byte) ((num >>> 16) & 0xff);
		result[2] = (byte) ((num >>> 8) & 0xff);
		result[3] = (byte) ((num >>> 0) & 0xff);
		return result;
	}

	/**
	 * 高位在前，低位在后
	 * 
	 * @param bytes
	 * @return
	 */
	public static int bytes2int2(byte[] bytes) {
		int result = 0;
		if (bytes.length == 4) {
			int a = (bytes[0] & 0xff) << 24;// 说明二
			int b = (bytes[1] & 0xff) << 16;
			int c = (bytes[2] & 0xff) << 8;
			int d = (bytes[3] & 0xff);
			result = a | b | c | d;
		}
		return result;
	}

	/**
	 * 高位在前，低位在后
	 * 
	 * @param bytes
	 * @return
	 */
	public static int bytes2int(byte[] bytes) {
		int result = 0;
		if (bytes.length == 4) {
			int a = (bytes[0] & 0xff) << 0;
			int b = (bytes[1] & 0xff) << 8;
			int c = (bytes[2] & 0xff) << 16;
			int d = (bytes[3] & 0xff) << 24;
			result = a | b | c | d;
		}
		return result;
	}

	private static byte[] byteMergerAll(byte[]... values) {
		int length_byte = 0;
		for (int i = 0; i < values.length; i++) {
			length_byte += values[i].length;
		}
		byte[] all_byte = new byte[length_byte];
		int countLength = 0;
		for (int i = 0; i < values.length; i++) {
			byte[] b = values[i];
			System.arraycopy(b, 0, all_byte, countLength, b.length);
			countLength += b.length;
		}
		return all_byte;
	}

	/**
	 * 获取格式化时间
	 * 
	 * @return
	 */
	public static String getTime() {
		long time = System.currentTimeMillis();
		SimpleDateFormat format = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss SSS");
		Date date = new Date(time);
		String t = format.format(date);
		return t;
	}

	/**
	 * 格式化消息格式
	 * 
	 * @param str
	 * @return
	 */
	public static byte[] msgToBuffer(String str) {
		try {
			return byteMergerAll(toLH(0), toLH(0), toLH(str.length()), toLH(0),
					str.getBytes("gbk"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean isEmpty(String str) {
		if (str == null || "".equals(str)) {
			return true;
		}
		return false;
	}
}
