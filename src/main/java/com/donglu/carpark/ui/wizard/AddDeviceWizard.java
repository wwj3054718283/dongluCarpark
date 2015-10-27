package com.donglu.carpark.ui.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.util.StrUtil;


public class AddDeviceWizard extends Wizard implements AbstractWizard{
	AddDeviceModel model;
	private AddDeviceBasicPage page;
	
	public AddDeviceWizard(AddDeviceModel model) {
		this.model=model;
		setWindowTitle("添加固定用户");
		model.setVoice(model.getVolume()+"");
		if (!StrUtil.isEmpty(model.getTcpLabel())) {
			model.setTcpAddress(model.getTcpLabel());
		}
	}

	@Override
	public void addPages() {
		
		page = new AddDeviceBasicPage(model);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		String linkAddress = model.getLinkAddress();
		SingleCarparkCarpark carpark = model.getCarpark();
		String identifire = model.getIdentifire();
		String roadType = model.getRoadType();
		
		
		if (StrUtil.isEmpty(model.getAddress())||StrUtil.isEmpty(model.getName())||StrUtil.isEmpty(model.getIp())||
				StrUtil.isEmpty(roadType)||StrUtil.isEmpty(identifire)||StrUtil.isEmpty(carpark)) {
			page.setErrorMessage("请填写完整信息");
			return false;
		}
		if (model.getType().equals("tcp")) {
			if (model.getTcpAddress()!=null) {
				model.setLinkAddress(model.getTcpAddress()+":10001");
			}else{
				page.setErrorMessage("请填写控制器连接");
				return false;
			}
		}else if(model.getType().equals("485")){
			if (model.getSerialAddress()!=null) {
				model.setLinkAddress(model.getSerialAddress());
			}else{page.setErrorMessage("请填写控制器连接");
				return false;
			}
		}else if(StrUtil.isEmpty(model.getVoice())){
			page.setErrorMessage("请选择语言音量");
			return false;
		}
		if (model.getAdvertise().length()>40) {
			page.setErrorMessage("平时语音不能超过40个汉字");
			return false;
		}
		String voice = model.getVoice();
		
		model.setVolume(Integer.valueOf(voice));
		return true;
	}

	public Object getModel() {
		
		return model;
	}

}