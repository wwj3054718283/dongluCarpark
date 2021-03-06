package com.donglu.carpark.service;

import com.donglu.carpark.service.impl.WebServiceImpl;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.google.inject.ImplementedBy;

@ImplementedBy(WebServiceImpl.class)
public interface WebService {
	/**
	 * 上传月租信息到云平台
	 * @param u
	 * @return
	 */
	boolean sendUser(SingleCarparkUser u);
	/**
	 * 上传进场信息到云平台
	 * @param out
	 * @return
	 */
	public boolean sendInHistory(SingleCarparkInOutHistory in);
	/**
	 * 上传出场信息到云平台
	 * @param out
	 * @return
	 */
	public boolean sendOutHistory(SingleCarparkInOutHistory out);
	/**
	 * 上传停车场信息到云平台
	 * @return
	 */
	public boolean sendCarparkInfo(SingleCarparkCarpark carpark);
	/**
	 *  锁车
	 * @param plateNO 车牌
	 * @param status 1锁车，2解锁
	 * @return
	 */
	public boolean lockCar(String plateNO,int status);
	/**
	 * 获取锁车信息
	 * @return
	 */
	public boolean getLockCarInfo();
	
	void init();
}
