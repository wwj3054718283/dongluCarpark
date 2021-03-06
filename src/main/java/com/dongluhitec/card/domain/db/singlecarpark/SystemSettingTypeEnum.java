package com.dongluhitec.card.domain.db.singlecarpark;

public enum SystemSettingTypeEnum {
	车位满是否允许临时车入场("false"),
	车位满是否允许免费车入场("false"),
	车位满是否允许储值车入场("false"),
	临时车入场是否确认("false"),
	临时车零收费是否自动出场("false"),
	固定车入场是否确认("false"),
	固定车出场确认("false"),
	数据库备份位置("d:\\carpark.bak"),
	图片保存位置(System.getProperty("user.dir")),
	图片保存多少天("30"),
	是否自动删除图片("false"),
	是否允许无牌车进("false"),
	同一车牌识别间隔("1"), 出场确认放行("false"),
	固定车到期变临时车("true"),固定车到期所属停车场限制("false"),
	固定车非所属停车场停留收费("false"), 固定车非所属停车场停留时间("15"),固定车到期提醒("false"),
	双摄像头识别间隔("0"),
	左下监控("false"),
	右下监控("false"),
	固定车车位满作临时车计费("false"),自动识别出场车辆类型("false"),进场允许修改车牌("false"),
	进场允许手动入场("false"),
	储值车提醒金额("60"),储值车进出场限制金额("20"),启用集中收费("false"),集中收费延迟出场时间("15"),集中收费时允许岗亭收费("false"),
	临时车通道限制("false"),
	启用车牌报送("false"),
	车位数显示方式("0"),
	免费原因("其他原因"),
	固定车提醒时间(""),停车场重复计费("false"),保存遥控开闸记录("false"),
	启用CJLAPP支付("false"),
	退出时需要密码("false"),
	访客车进场次数用完不能随便出("false"),
	固定车车牌匹配字符数("7"),
	绑定车辆允许场内换车("true"),绑定车辆场内换车时间("30"),换车时间内车辆无限制("true"),
	同一账号只能在一个地方登录("true"),
	收费口无人值守("false"),
	固定车转临时车弹窗提示("true"),
	显示指定停留时间的场内车("false"),
	
	DateBase_version("1.0.0.22"),软件版本("1.0.0.22"),发布时间("2017-09-13 17:00:00"), 更新文件夹("jar,native"), 自动下载车牌("false"), 允许设备限时("false"), 访客车名称("访客车"), 
	;
	
	private String defaultValue;
	
	SystemSettingTypeEnum(String defaultValue){
		this.defaultValue=defaultValue;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public boolean getBooleanValue(){
		try {
			return Boolean.valueOf(defaultValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
