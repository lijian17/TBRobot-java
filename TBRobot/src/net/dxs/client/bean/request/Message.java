package net.dxs.client.bean.request;

import net.dxs.client.utils.MsgUtils;

import com.alibaba.fastjson.JSONObject;

/**
 * 消息实体
 * 
 * <pre>
 * 
 * </per>
 * @author lijian-pc
 * @date 2018-6-7 上午9:42:16
 */
public class Message {
	public header header = new header();
	public net.dxs.client.bean.request.Message.content content = new content();

	public static class header {
		public String tag = "tdos";
		public String version = "1.0.0.1";
		public String type = "31020";
		public String time_stamp = MsgUtils.getTime();
		public String session_id = "0";
		public String serial_number = "0";
	}

	public static class content {
		public String from = "127.0.0.1:6001";
		public String to = "127.0.0.1:6002";
		public String verb = "DoService";
		public param param = new param();
	}

	public static class param {
		public String service_name = "Init";
		public Object service_param = null;
	}

	@Override
	public String toString() {
		JSONObject jObj = new JSONObject();
		jObj.put("header", this.header);
		jObj.put("content", this.content);
		return jObj.toString();
	}
}
