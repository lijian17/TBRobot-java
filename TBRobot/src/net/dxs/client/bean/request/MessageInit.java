package net.dxs.client.bean.request;

import net.dxs.client.utils.MsgUtils;

import com.alibaba.fastjson.JSONObject;

/**
 * 初始化服务
 * 
 * <pre>
 * 
 * </per>
 * @author lijian-pc
 * @date 2018-6-7 上午9:42:16
 */
public class MessageInit {
	public header header = new header();
	public net.dxs.client.bean.request.MessageInit.content content = new content();

	public static class header {
		public String tag = "tdos";
		public String version = "1.0.0.1";
		public String type = "31018";
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

	static class param {
		public String service_name = "Init";
		public service_param service_param = new service_param();
	}

	static class service_param {
		public String app_name = "tdrobotterminalapp";
		public String strategy_name = "tdrobotterminalapp";
	}

	@Override
	public String toString() {
		JSONObject jObj = new JSONObject();
		jObj.put("header", this.header);
		jObj.put("content", this.content);
		return jObj.toString();
	}
}
