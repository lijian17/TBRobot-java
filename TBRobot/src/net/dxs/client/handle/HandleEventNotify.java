package net.dxs.client.handle;

import net.dxs.client.bean.request.Message;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class HandleEventNotify {

	private HandleMsg mMandleMsg;
	private JSONObject mJObj;
	private static HandleEventNotify instance;

	private HandleEventNotify() {
	}

	public static HandleEventNotify getInstanse() {
		if (instance == null) {
			instance = new HandleEventNotify();
		}
		return instance;
	}

	public void handleMsg(HandleMsg handleMsg, JSONObject jObj) {
		this.mMandleMsg = handleMsg;
		this.mJObj = jObj;
		init();
	}

	private void init() {
		String event_name = mJObj.getJSONObject("content")
				.getJSONObject("param").getJSONObject("service_param")
				.getString("event_name");
		// JSONObject data =
		// jObj.getJSONObject("content").getJSONObject("param")
		// .getJSONObject("service_param").getJSONObject("data");
		JSONObject data = null;
		// battery_info_event
		// audio_play_begin_event
		// audio_play_finish_event
		// {"detail":"audio play end event","service_name":"default@AudioPlayExtModule","type":"status_event","status_event_code":"10024"}
		if ("CustomerInfoEvent".equals(event_name)) {// 处理人脸信息(人来)
			System.out.println("处理人脸信息(人来)");
			handleCustomerInfoEvent(data);
		} else if ("CustomerLeaveEvent".equals(event_name)) {// 处理人离开
			System.out.println("处理人离开");
			handleCustomerLeaveEvent();
		} else if ("CustomerUpdateEvent".equals(event_name)) {// 处理人脸切换(换人)
			System.out.println("处理人脸切换(换人)");
			handleCustomerUpdateEvent(data);
		} else if ("audio_play_begin_event".equals(event_name)) {
			handleAudioPlayBeginEvent(data);
		} else if ("audio_play_finish_event".equals(event_name)) {
			handleAudioPlayFinishEvent(data);
		} else if ("AnswerTypeEvent".equals(event_name)) {
			handleAnswerTypeEvent(data);
		} else if ("FaceEmotionEvent".equals(event_name)) {
			handleFaceEmotionEvent(data);
		} else if ("MoveCruiseChassisEvent".equals(event_name)) {
			handleMoveCruiseChassisEvent(data);
		} else if ("StopMoveChassisEvent".equals(event_name)) {
			handleStopMoveChassisEvent(data);
		} else if ("GoHomeChassisEvent".equals(event_name)) {
			handleGoHomeChassisEvent(data);
		} else if ("BatteryInfoEvent".equals(event_name)) {
			handleBatteryInfoEvent(data);
		} else if ("ChargingInfoEvent".equals(event_name)) {
			handleChargingInfoEvent(data);
		} else if ("CardInfoEvent".equals(event_name)) {// 读卡
			System.out.println("读卡");
			handleCardInfoEvent(data);
		} else if ("PrinterStatusEvent".equals(event_name)) {// 打印机状态事件(缺纸, 正常)
			System.out.println("打印机状态事件(缺纸, 正常)");
			handlePrinterStatusEvent(data);
		} else if ("PrinterTimeoutEvent".equals(event_name)) {// 打印机超时事件
			System.out.println("打印机超时事件");
			handlePrinterTimeoutEvent(data);
		} else if ("SinginResultEvent".equals(event_name)) {
			handleSinginResultEvent(data);
		} else {

		}
	}

	private void handleCustomerInfoEvent(JSONObject data) {
		// TODO Auto-generated method stub

	}

	private void handleCustomerLeaveEvent() {
		// TODO Auto-generated method stub

	}

	private void handleCustomerUpdateEvent(JSONObject data) {
		// TODO Auto-generated method stub

	}

	private void handleAudioPlayBeginEvent(JSONObject data) {
		// TODO Auto-generated method stub

	}

	private void handleAudioPlayFinishEvent(JSONObject data) {
//		String data2 = mJObj.getJSONObject("content").getJSONObject("param")
//				.getJSONObject("service_param").getString("data");
//		JSONObject object = JSONObject.parseObject(data2);
//		String service_name = object.getString("service_name");
		

		String event_name = mJObj.getJSONObject("content").getJSONObject("param")
				.getJSONObject("service_param").getString("event_name");

		if ("audio_play_finish_event".equals(event_name)) {
			System.out.println("开始触摸屏幕");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Message msg = new Message();
			msg.header.type = "31034";
			msg.header.session_id = mMandleMsg.getSession_id();
			msg.header.serial_number = String.format("%010d",
					Integer.parseInt(mMandleMsg.getSerial_number(mJObj)) + 1);
			msg.content.param.service_name = "CustomEvent";
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("service_name", "customer_operation_service");
			jsonObject.put("service_param", new JSONObject());
			msg.content.param.service_param = jsonObject;
			mMandleMsg.sendMsg(msg.toString());
		}
		
		if ("audio_play_finish_event".equals(event_name)) {
			System.out.println("播放");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Message msg = new Message();
			msg.header.type = "31034";
			msg.header.session_id = mMandleMsg.getSession_id();
			msg.header.serial_number = mMandleMsg.getSerial_number(mJObj);
			msg.content.param.service_name = "CustomEvent";
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("service_name", "Play_Custom_Text_Event");
			JSONObject jsonObject2 = new JSONObject();
			jsonObject2.put("text", "快3当期投注已截止请购买下一期");
			jsonObject.put("service_param", jsonObject2);
			
			msg.content.param.service_param = jsonObject;
			mMandleMsg.sendMsg(msg.toString());
		}

//		if ("default@AudioPlayExtModule".equals(service_name)) {
//			Message msg = new Message();
//			msg.header.type = "31034";
//			msg.header.session_id = mMandleMsg.getSession_id();
//			msg.header.serial_number = mMandleMsg.getSerial_number(mJObj);
//			msg.content.param.service_name = "CustomEvent";
//			JSONObject jsonObject = new JSONObject();
//			jsonObject.put("service_name", "Play_Custom_Text_Event");
//			JSONObject jsonObject2 = new JSONObject();
//			jsonObject2.put("text", "快3当期投注已截止请购买下一期");
//			jsonObject.put("service_param", jsonObject2);
//
//			msg.content.param.service_param = jsonObject;
//			mMandleMsg.sendMsg(msg.toString());
//		}

//		if ("default@AudioPlayExtModule".equals(service_name)) {
//			Message msg = new Message();
//			msg.header.type = "31034";
//			msg.header.session_id = mMandleMsg.getSession_id();
//			msg.header.serial_number = String.format("%010d",
//					Integer.parseInt(mMandleMsg.getSerial_number(mJObj)) + 1);
//			msg.content.param.service_name = "CustomEvent";
//			JSONObject jsonObject = new JSONObject();
//			jsonObject.put("service_name", "customer_operation_service");
//			jsonObject.put("service_param", new JSONObject());
//			msg.content.param.service_param = jsonObject;
//			mMandleMsg.sendMsg(msg.toString());
//		}
	}

	private void handleAnswerTypeEvent(JSONObject data) {
		// TODO Auto-generated method stub

	}

	private void handleFaceEmotionEvent(JSONObject data) {
		// TODO Auto-generated method stub

	}

	private void handleMoveCruiseChassisEvent(JSONObject data) {
		// TODO Auto-generated method stub

	}

	private void handleStopMoveChassisEvent(JSONObject data) {
		// TODO Auto-generated method stub

	}

	private void handleGoHomeChassisEvent(JSONObject data) {
		// TODO Auto-generated method stub

	}

	private void handleBatteryInfoEvent(JSONObject data) {
		// TODO Auto-generated method stub

	}

	private void handleChargingInfoEvent(JSONObject data) {
		// TODO Auto-generated method stub

	}

	private void handleCardInfoEvent(JSONObject data) {
		// TODO Auto-generated method stub

	}

	private void handlePrinterStatusEvent(JSONObject data) {
		// TODO Auto-generated method stub

	}

	private void handlePrinterTimeoutEvent(JSONObject data) {
		// TODO Auto-generated method stub

	}

	private void handleSinginResultEvent(JSONObject data) {
		// TODO Auto-generated method stub

	}

}
