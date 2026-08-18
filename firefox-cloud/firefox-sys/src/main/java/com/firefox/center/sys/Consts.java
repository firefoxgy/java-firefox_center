package com.firefox.center.sys;

/**
 * 	系统常量
 */
public final class Consts {

	public static class pay{
		//支付码前缀
		public static final String CODE_START_KP ="32";
		public static final int QR_CODE_WIDTH =700;
		public static final int BAR_CODE_WIDTH =254;
		public static final int BAR_CODE_HEIGHT =85;

		public static final String TYPE_KP ="kp";
		public static final String TYPE_WX ="wx";
		public static final String TYPE_ALIPAY ="alipay";
	}

	public static class Jwt{
		public static final String SIGNATUREA_KEY="RtaCkR0zBMl4VA7BW5giDNTjOuvmPeMg[cn.firefox]";
		public static final long EXPIRE = 10 * 24 * 60 * 60 * 1000;
		public static final String CLAIM_OPEN_ID="openid";

	}

	public static class noticeTemplate{
		public static final String NOTICE_FROM_USER ="admin";

		public static final String TEMPLATE_CODE_ROOM ="sys_room_note";
		public static final String TEMPLATE_CODE_REPAIR ="sys_repair_note";
		public static final String TEMPLATE_CODE_DOOR ="sys_door_note";
	}

	public static class Qrcode{
		public static final String DEFAULT_CODE ="/static/qrcode/default.jpg";
	}

	public static class Room{
		public static final int INDEX_TOP_ID =1;
		public static final int INDEX_CENTER_ID =2;
		public static final int ROOM_DEFAULT_ID =3;
		public static final int CAR_DEFAULT_ID =4;
	}

	public static class Api{
		public static final String TOKEN ="x-access-token";
	}

	public static class Dic{
		public static final String REPAIR_CODE ="daily_repair_type";
	}

	public static class order{
		//daily_goods表的停车缴费类型编码
		public static final String CAR_GOODS_CODE ="P001";
		//daily_goods表的停车缴费类型编码
		public static final String EAT_GOODS_BREAKFAST_CODE ="E001";
		public static final String EAT_GOODS_LUNCH_CODE ="E002";
		public static final String EAT_GOODS_SUPPER_CODE ="E003";
		public static final String EAT_GOODS_DINNER_CODE ="E004";

		//停车缴费
		public static final String TYPE_CAR ="car";
		//食堂缴费
		public static final String TYPE_EAT ="eat";
	}

	public static class DaylyShow{
		public static final String base_path ="https://yndailyhis.firefox.cn/";
		//查询云报模范
		public static final String API_USER_QUERY =base_path+"api/employees/list";
		//添加云报模范
		public static final String API_USER_ADD =base_path+"api/employees";
		//上传云报模范图片
		public static final String API_USER_UP_IMG =base_path+"api/upload_images";
	}
}