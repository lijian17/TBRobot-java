package net.dxs.client.handle;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import net.dxs.client.bean.request.Message;
import net.dxs.client.utils.MsgUtils;
import net.dxs.client.utils.WLog;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 消息处理
 * 
 * @author lijian-pc
 * @date 2018-6-6 下午3:56:45
 */
public class HandleMsg implements Runnable {

	private Socket mSocket;
	private OutputStream mOs;
	private String session_id;
	private String serial_number;
	private int switchCount;// 状态切换计数

	public HandleMsg(Socket socket) {
		this.mSocket = socket;
	}

	@Override
	public void run() {
		try {
			InputStream is = mSocket.getInputStream();
			mOs = mSocket.getOutputStream();

			Message msg = new Message();
			msg.header.type = "31018";
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("app_name", "tdrobotterminalapp");
			jsonObject.put("strategy_name", "tdrobotterminalapp");
			msg.content.param.service_param = jsonObject;
			sendMsg(msg.toString());

			BufferedInputStream bufis = new BufferedInputStream(is);
			byte[] by = new byte[8 * 1024];
			byte[] temp = new byte[4];
			while (bufis.read(by) != -1) {
				System.arraycopy(by, 8, temp, 0, 4);
				int bodyLen = MsgUtils.bytes2int(temp);
				handleMsg(new String(by, 16, bodyLen, "gbk").trim());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getSession_id() {
		return session_id;
	}

	public String getSerial_number(JSONObject jObj) {
		return String.format(
				"%010d",
				Integer.parseInt(jObj.getJSONObject("header").getString(
						"serial_number")) + 1);
	}

	/**
	 * 发送消息
	 * 
	 * @param msg
	 */
	public void sendMsg(String msg) {
		WLog.getInstance().saveLog2File_tdos(msg);
		System.out.println("sendMsg--->" + msg);
		try {
			mOs.write(MsgUtils.msgToBuffer(msg));
			mOs.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void emitMessage(String service, JSONObject params) {
		Message msg = new Message();
		msg.header.type = "31020";
		msg.header.session_id = this.session_id;
		msg.header.serial_number = this.serial_number;
		msg.content.param.service_name = service;
		msg.content.param.service_param = params;
		sendMsg(msg.toString());
	}

	public void handleMsg(String msg) {
		WLog.getInstance().saveLog2File_tdos(msg);
		System.out.println("handleMsg--->" + msg);
		JSONObject jObj = JSONObject.parseObject(msg);
		JSONObject content = jObj.getJSONObject("content");
		JSONObject header = jObj.getJSONObject("header");

		String service_name = content.getJSONObject("param").getString(
				"service_name");
		if (this.session_id == null || "".equals(this.session_id)) {
			this.session_id = header.getString("session_id");
		}
		this.serial_number = String.format("%010d",
				Integer.parseInt(header.getString("serial_number")) + 1);

		if ("Init".equals(service_name)) {// 初始化服务
			init(jObj);
		} else if ("SwitchNotify".equals(service_name)) {// 切换通知服务
			switchNotify(jObj);
		} else if ("Switch".equals(service_name)) {// 状态切换服务(切换完成)
			_switch(jObj);
		} else if ("Ready".equals(service_name)) {// 状态准备完毕服务
			ready(jObj);
		} else if ("EventNotify".equals(service_name)) {// 事件通知服务
			eventNotify(jObj);
		} else if ("Heartbeat".equals(service_name)) {// 心跳包
			heartbeat(jObj);
		} else if ("GetData".equals(service_name)) {// 获取数据
			getData(jObj);
		} else if ("SetData".equals(service_name)) {// 设置数据
			setData(jObj);
		} else {

		}
	}

	/**
	 * 初始化服务
	 * 
	 * @param jObj
	 */
	private void init(JSONObject jObj) {
		this.session_id = jObj.getJSONObject("header").getString("session_id");
	}

	/**
	 * 切换通知服务
	 * 
	 * @param jObj
	 */
	private void switchNotify(JSONObject jObj) {
		String state_name = jObj.getJSONObject("content")
				.getJSONObject("param").getJSONObject("service_param")
				.getString("state_name");
		Message msg = new Message();
		msg.header.type = "31016";
		msg.header.session_id = this.session_id;
		msg.header.serial_number = this.serial_number;
		msg.content.param.service_name = "Switch";
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("state_name", state_name);
		msg.content.param.service_param = jsonObject;
		sendMsg(msg.toString());
	}

	private void switchNotify_playCustomTextEvent(JSONObject jObj) {
		Message msg = new Message();
		msg.header.type = "31034";
		msg.header.session_id = getSession_id();
		msg.header.serial_number = getSerial_number(jObj);
		msg.content.param.service_name = "CustomEvent";
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("service_name", "Play_Custom_Text_Event");
		JSONObject jsonObject2 = new JSONObject();
		jsonObject2.put("text", "快3当期投注已截止请购买下一期");
		jsonObject.put("service_param", jsonObject2);

		msg.content.param.service_param = jsonObject;
		sendMsg(msg.toString());
	}

	private void switchNotify_customer(JSONObject jObj) {
		Message msg = new Message();
		msg.header.type = "31034";
		msg.header.session_id = this.session_id;
		msg.header.serial_number = String.format("%010d",
				Integer.parseInt(getSerial_number(jObj)) + 1);
		msg.content.param.service_name = "CustomEvent";
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("service_name", "customer_operation_service");
		jsonObject.put("service_param", new JSONObject());
		msg.content.param.service_param = jsonObject;
		sendMsg(msg.toString());
	}

	/**
	 * 状态切换服务(切换完成)
	 * 
	 * @param jObj
	 */
	private void _switch(JSONObject jObj) {
		String status_code = jObj.getJSONObject("content")
				.getJSONObject("param").getJSONObject("result")
				.getString("status_code");
		if (!"0".equals(status_code)) {
			return;
		}
		Message msg = new Message();
		msg.header.type = "31026";
		msg.header.session_id = this.session_id;
		msg.header.serial_number = this.serial_number;
		msg.content.param.service_name = "Ready";
		JSONObject jsonObject = new JSONObject();
		msg.content.param.service_param = jsonObject;
		sendMsg(msg.toString());
		// 切换屏保
//		handleScreenSaver(jObj);
	}

	/**
	 * 切换屏保
	 * 
	 * @param jObj
	 */
	private void handleScreenSaver(JSONObject jObj) {
		String cur_state = jObj.getJSONObject("content").getJSONObject("param")
				.getJSONObject("result").getJSONObject("detail")
				.getString("cur_state");
		System.out.println("切换屏保：" + jObj.toString());
		
		if("PrepareService".equals(cur_state)){
			
		} else if("WaitingService@TargetSearching@StaticSearching".equals(cur_state)) {
			String service = "SetData";
			JSONObject params = new JSONObject();
			JSONArray data = new JSONArray();

			JSONArray robotStateSetParasArr = new JSONArray();
			JSONObject robotStateSetParasJObj = new JSONObject();
			robotStateSetParasJObj.put("robotState", "OnService");
			robotStateSetParasJObj.put("stateLevel", 0);
			robotStateSetParasArr.add(robotStateSetParasJObj);
			
			
			JSONObject key1 = new JSONObject();
			key1.put("key", "robotStateSet");
			key1.put("value", robotStateSetParasArr);
			data.add(key1);

			params.put("data", data);
			emitMessage(service, params);
		} else {
			switchNotify_customer(jObj);
		}
		

		// this.$logger.info('【info】新平台要切换的服务状态:', stateName)
		// let saver = null
		// let show = null
		// switch (stateName) {
		// // 准备模式
		// case TDOS.STATE.PREPARE:
		// saver = STATUS.DEFAULT
		// show = false
		// // TODO: 与平台服务建立连接成功 1-通讯建立 2-初始化完成 3-签到完成
		// this.$emit('getTDOSStatus', '2S')
		// break
		// // 服务模式
		// case TDOS.STATE.ON:
		// saver = STATUS.DEFAULT
		// show = false
		// this.$logger.info('【debug】----新平台切换服务状态到:', stateName)
		// // 语言切换显示状态
		// this.$bus.$emit('show-language-icon')
		// // /*
		// // TODO：测试平台多轮对话接口 --for debug
		// const serviceName = 'SetData'
		// let robotStateSetParas = [{robotState: 'OnService', stateLevel: 0}]
		// const serviceParam = {
		// data: [
		// {
		// // 页面状态集合
		// key: 'robotStateSet',
		// value: JSON.stringify(robotStateSetParas)
		// }
		// ]
		// }
		// this.emitMessage(serviceName, serviceParam)
		// // */
		// break
		// // 等待模式-本地巡航
		// case TDOS.STATE.WAITING_TARGETSEARCHING_LOCALSEARCHING:
		// saver = STATUS.CRUISE
		// show = true
		// break
		// // 等待模式-定点巡航
		// case TDOS.STATE.WAITING_TARGETSEARCHING_SPOTSEARCHING:
		// saver = STATUS.CRUISE
		// show = true
		// break
		// // 等待模式-静止模式
		// case TDOS.STATE.WAITING_TARGETSEARCHING_STATICSEARCHING:
		// saver = STATUS.DEFAULT
		// show = true
		// break
		// // 等待模式-定点行走
		// case TDOS.STATE.WAITING_TARGETSEARCHING_MOVETOPOINT:
		// saver = STATUS.LEADING
		// show = true
		// break
		// // 挂起模式-上班前
		// case TDOS.STATE.SUSPEND_POWERMANAGER_BEFOREWORK:
		// saver = STATUS.TO_WORK
		// show = true
		// break
		// // 挂起模式-准备充电
		// case TDOS.STATE.SUSPEND_POWERMANAGER_PREPARECHARGING:
		// saver = STATUS.TO_CHARGE
		// show = true
		// break
		// // 挂起模式-正在充电
		// case TDOS.STATE.SUSPEND_POWERMANAGER_ONCHARGING:
		// saver = STATUS.CHARGING
		// show = true
		// break
		// // 挂起模式-充电失败
		// case TDOS.STATE.SUSPEND_POWERMANAGER_FAILEDTOCHARGE:
		// saver = STATUS.CHARGE_FAILED
		// show = true
		// break
		// default:
		// saver = STATUS.DEFAULT
		// show = true
		// break
		// }
		// this.changeSaver(saver)
		// this.showOrHideSaver(show)
	}

	/**
	 * 状态准备完毕服务
	 * 
	 * @param jObj
	 */
	private void ready(JSONObject jObj) {
		// TODO 待平台服务状态准备完成后，进行终端机构信息的获取，获取成功则进入应用
		switchCount = switchCount + 1;
		if (switchCount == 2) {
//			getSignResult();
		}
	}

	/**
	 * 处理平台签到结果获取
	 */
	private void getSignResult() {
		String service = "GetData";
		JSONObject params = new JSONObject();
		JSONArray data = new JSONArray();

		JSONObject key1 = new JSONObject();
		key1.put("key", "signin_orgid");// 机构号
		data.add(key1);

		JSONObject key2 = new JSONObject();
		key2.put("key", "signin_termid");// 终端号
		data.add(key2);

		JSONObject key3 = new JSONObject();
		key3.put("key", "electricity");// 电量
		data.add(key3);

		JSONObject key4 = new JSONObject();
		key4.put("key", "is_charging");// 充电桩：桩充|线充
		data.add(key4);

		params.put("data", data);
		emitMessage(service, params);
	}

	/**
	 * 事件通知服务
	 * 
	 * @param jObj
	 */
	private void eventNotify(JSONObject jObj) {
		HandleEventNotify.getInstanse().handleMsg(this, jObj);
	}

	/**
	 * 心跳包
	 * 
	 * @param jObj
	 */
	private void heartbeat(JSONObject jObj) {
		System.out.println("心跳包:" + MsgUtils.getTime());
		WLog.getInstance().saveLog2File_tdos("心跳包:" + MsgUtils.getTime());
	}

	/**
	 * 获取数据
	 * 
	 * @param jObj
	 */
	private void getData(JSONObject jObj) {
		String status_code = jObj.getJSONObject("content")
				.getJSONObject("param").getJSONObject("result")
				.getString("status_code");
		if ("0".equals(status_code)) {
			// 获取查询结果成功，--签到成功
			handleGetData(jObj);
		} else {
			// TODO 与平台服务建立连接成功(1-通讯建立 2-初始化完成 3-签到完成)
		}
	}

	private void handleGetData(JSONObject jObj) {
		JSONArray data = jObj.getJSONObject("content").getJSONObject("param")
				.getJSONObject("result").getJSONObject("detail")
				.getJSONArray("data");

		String signin_orgid = null;
		String signin_termid = null;
		int electricity = 0;
		int is_charging = 99;

		for (int i = 0, len = data.size(); i < len; i++) {
			JSONObject v = data.getJSONObject(i);
			String key = v.getString("key");
			String value = v.getString("value");
			if ("signin_orgid".equals(key)) {
				signin_orgid = value;
			} else if ("signin_termid".equals(key)) {
				signin_termid = value;
			} else if ("electricity".equals(key)) {
				electricity = Integer.parseInt(value.trim());
			} else if ("is_charging".equals(key)) {
				is_charging = Integer.parseInt(value.trim());
			}
		}

		if (!MsgUtils.isEmpty(signin_orgid) && !MsgUtils.isEmpty(signin_termid)) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("signin_orgid", signin_orgid);
			jsonObject.put("signin_termid", signin_termid);
			System.out.println(jsonObject.toString());
		}
		if (electricity != 0) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("electricity", electricity);
			jsonObject.put("is_charging", is_charging);
			System.out.println(jsonObject.toString());
		}
		if (is_charging != 99) {
			// 更新充电状态
			System.out.println(is_charging);
		}
	}

	/**
	 * 设置数据
	 * 
	 * @param jObj
	 */
	private void setData(JSONObject jObj) {
		String status_code = jObj.getJSONObject("content")
				.getJSONObject("param").getJSONObject("result")
				.getString("status_code");
		if ("0".equals(status_code)) {
			// 设置数据成功
			handleSetData(jObj);
		} else {
			// TODO 与平台服务建立连接成功(1-通讯建立 2-初始化完成 3-签到完成)
			WLog.getInstance().saveLog2File_tdos("设置平台数据失败");
		}
	}

	private void handleSetData(JSONObject jObj) {
		String service = "GetData";
		JSONObject params = new JSONObject();
		JSONArray data = new JSONArray();

		JSONObject key1 = new JSONObject();
		key1.put("key", "robotStateSet");
		data.add(key1);

		params.put("data", data);
		emitMessage(service, params);
	}

}
