package com.donglu.carpark.ui.view.speed.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemSetting;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import org.eclipse.swt.widgets.Group;

public class OverSpeedSettingWizardPage extends WizardPage {
	private Text txt_fixDay;
	private Text txt_fixSize;
	private Text txt_tempDay;
	private Text txt_tempSize;
	private Text txt_tempBlackDay;
	private Button btn_start;
	private Text txt_overSpeedSmsTempCode;
	private Text txt_nomalStart;
	private Text txt_nomalEnd;
	private Text txt_seriousSize;
	private Text txt_seriousTempCarBlackDay;
	private Text txt_seriousCarSendSms;
	private Button btn_seriousFixCarCancel;

	protected OverSpeedSettingWizardPage() {
		super("超速车辆设置");
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		composite.setLayout(new GridLayout(1, false));
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(new GridLayout(1, false));
		composite_1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		
		btn_start = new Button(composite_1, SWT.CHECK);
		btn_start.setText("启用车辆车速统计");
		
		Group group = new Group(composite_1, SWT.NONE);
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		group.setText("普通超速");
		
		Composite composite_5 = new Composite(group, SWT.NONE);
		composite_5.setLayout(new GridLayout(4, false));
		composite_5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_9 = new Label(composite_5, SWT.NONE);
		label_9.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_9.setText("限速");
		
		txt_nomalStart = new Text(composite_5, SWT.BORDER);
		txt_nomalStart.setText("36");
		GridData gd_txt_nomalStart = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_txt_nomalStart.widthHint = 50;
		txt_nomalStart.setLayoutData(gd_txt_nomalStart);
		
		Label label_10 = new Label(composite_5, SWT.NONE);
		label_10.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_10.setText("到");
		
		txt_nomalEnd = new Text(composite_5, SWT.BORDER);
		txt_nomalEnd.setText("44");
		GridData gd_txt_nomalEnd = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_txt_nomalEnd.widthHint = 50;
		txt_nomalEnd.setLayoutData(gd_txt_nomalEnd);
		
		Composite composite_4 = new Composite(group, SWT.NONE);
		composite_4.setLayout(new GridLayout(2, false));
		
		Label label_8 = new Label(composite_4, SWT.NONE);
		label_8.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_8.setText("固定车超速后发送短信");
		
		txt_overSpeedSmsTempCode = new Text(composite_4, SWT.BORDER);
		txt_overSpeedSmsTempCode.setToolTipText("短信模板编号");
		GridData gd_txt_overSpeedSmsTempCode = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txt_overSpeedSmsTempCode.widthHint = 100;
		txt_overSpeedSmsTempCode.setLayoutData(gd_txt_overSpeedSmsTempCode);
		
		Composite composite_2 = new Composite(group, SWT.NONE);
		composite_2.setLayout(new GridLayout(5, false));
		
		Label label = new Label(composite_2, SWT.NONE);
		label.setText("固定车");
		
		txt_fixDay = new Text(composite_2, SWT.BORDER);
		GridData gd_txt_fixDay = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_txt_fixDay.widthHint = 30;
		txt_fixDay.setLayoutData(gd_txt_fixDay);
		
		Label label_1 = new Label(composite_2, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("天内超速");
		
		txt_fixSize = new Text(composite_2, SWT.BORDER);
		GridData gd_txt_fixSize = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_txt_fixSize.widthHint = 30;
		txt_fixSize.setLayoutData(gd_txt_fixSize);
		
		Label label_2 = new Label(composite_2, SWT.NONE);
		label_2.setText("次自动取消授权");
		
		Composite composite_3 = new Composite(group, SWT.NONE);
		composite_3.setLayout(new GridLayout(7, false));
		
		Label label_3 = new Label(composite_3, SWT.NONE);
		label_3.setText("临时车");
		
		txt_tempDay = new Text(composite_3, SWT.BORDER);
		GridData gd_txt_tempDay = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_txt_tempDay.widthHint = 30;
		txt_tempDay.setLayoutData(gd_txt_tempDay);
		
		Label label_4 = new Label(composite_3, SWT.NONE);
		label_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_4.setText("天内超速");
		
		txt_tempSize = new Text(composite_3, SWT.BORDER);
		GridData gd_text_3 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text_3.widthHint = 30;
		txt_tempSize.setLayoutData(gd_text_3);
		
		Label label_5 = new Label(composite_3, SWT.NONE);
		label_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_5.setText("次自动拉黑");
		
		txt_tempBlackDay = new Text(composite_3, SWT.BORDER);
		GridData gd_text_4 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text_4.widthHint = 30;
		txt_tempBlackDay.setLayoutData(gd_text_4);
		
		Label label_6 = new Label(composite_3, SWT.NONE);
		label_6.setText("天");
		
		Group group_1 = new Group(composite_1, SWT.NONE);
		group_1.setLayout(new GridLayout(1, false));
		group_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		group_1.setText("严重超速");
		
		Composite composite_6 = new Composite(group_1, SWT.NONE);
		composite_6.setLayout(new GridLayout(2, false));
		composite_6.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		
		Label label_11 = new Label(composite_6, SWT.NONE);
		label_11.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_11.setText("限速");
		
		txt_seriousSize = new Text(composite_6, SWT.BORDER);
		txt_seriousSize.setText("45");
		GridData gd_txt_seriousSize = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txt_seriousSize.widthHint = 50;
		txt_seriousSize.setLayoutData(gd_txt_seriousSize);
		
		Composite composite_7 = new Composite(group_1, SWT.NONE);
		composite_7.setLayout(new GridLayout(3, false));
		composite_7.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label_12 = new Label(composite_7, SWT.NONE);
		label_12.setText("临时车严重超速拉黑");
		
		txt_seriousTempCarBlackDay = new Text(composite_7, SWT.BORDER);
		GridData gd_txt_seriousTempCarBlackDay = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_txt_seriousTempCarBlackDay.widthHint = 50;
		txt_seriousTempCarBlackDay.setLayoutData(gd_txt_seriousTempCarBlackDay);
		
		Label label_13 = new Label(composite_7, SWT.NONE);
		label_13.setText("天");
		
		btn_seriousFixCarCancel = new Button(group_1, SWT.CHECK);
		btn_seriousFixCarCancel.setText("固定车严重超速取消授权");
		
		Composite composite_8 = new Composite(group_1, SWT.NONE);
		composite_8.setLayout(new GridLayout(2, false));
		
		Label label_7 = new Label(composite_8, SWT.NONE);
		label_7.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_7.setText("固定车严重超速发送短信");
		
		txt_seriousCarSendSms = new Text(composite_8, SWT.BORDER);
		GridData gd_txt_seriousCarSendSms = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txt_seriousCarSendSms.widthHint = 100;
		txt_seriousCarSendSms.setLayoutData(gd_txt_seriousCarSendSms);
		init();
	}
	private void init() {
		CarparkDatabaseServiceProvider sp = getWizard().getSp();
		SingleCarparkSystemSetting setting = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.启用测速系统.name());
		btn_start.setSelection(Boolean.valueOf(setting.getSettingValue()));
		setting = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.固定车超速自动删除.name());
		String[] split = setting.getSettingValue().split("-");
		txt_fixDay.setText(split[0]);
		txt_fixSize.setText(split[1]);
		
		setting = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.临时车超速自动拉黑.name());
		split = setting.getSettingValue().split("-");
		txt_tempDay.setText(split[0]);
		txt_tempSize.setText(split[1]);
		txt_tempBlackDay.setText(split[2]);
		
		setting = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.普通超速速度.name());
		split = setting.getSettingValue().split("-");
		txt_nomalStart.setText(split[0]);
		txt_nomalEnd.setText(split[1]);
		
		setting = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.固定车严重超速取消授权.name());
		btn_seriousFixCarCancel.setSelection(Boolean.valueOf(setting.getSettingValue()));
		
		
		setText(txt_overSpeedSmsTempCode, SystemSettingTypeEnum.固定车超速发送短信);
		setText(txt_seriousCarSendSms, SystemSettingTypeEnum.固定车严重超速发送短信);
		setText(txt_seriousSize, SystemSettingTypeEnum.严重超速速度);
		setText(txt_seriousTempCarBlackDay, SystemSettingTypeEnum.临时车严重超速拉黑);
	}
	
	public void setText(Text txt,SystemSettingTypeEnum systemSettingTypeEnum) {
		CarparkDatabaseServiceProvider sp = getWizard().getSp();
		SingleCarparkSystemSetting setting = sp.getCarparkService().findSystemSettingByKey(systemSettingTypeEnum.name());
		txt.setText(setting.getSettingValue());
	}
	
	protected void save() {
		CarparkDatabaseServiceProvider sp = getWizard().getSp();
		SingleCarparkSystemSetting setting = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.启用测速系统.name());
		setting.setSettingValue(btn_start.getSelection()+"");
		sp.getCarparkService().saveSystemSetting(setting);
		
		setting = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.固定车超速自动删除.name());
		setting.setSettingValue(txt_fixDay.getText()+"-"+txt_fixSize.getText());
		sp.getCarparkService().saveSystemSetting(setting);
		
		setting = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.临时车超速自动拉黑.name());
		setting.setSettingValue(txt_tempDay.getText()+"-"+txt_tempSize.getText()+"-"+txt_tempBlackDay.getText());
		sp.getCarparkService().saveSystemSetting(setting);
		
		setting = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.普通超速速度.name());
		setting.setSettingValue(txt_nomalStart.getText()+"-"+txt_nomalEnd.getText());
		sp.getCarparkService().saveSystemSetting(setting);
		
		setting = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.固定车严重超速取消授权.name());
		setting.setSettingValue(String.valueOf(btn_seriousFixCarCancel.getSelection()));
		sp.getCarparkService().saveSystemSetting(setting);
		
		
		getText(txt_overSpeedSmsTempCode, SystemSettingTypeEnum.固定车超速发送短信);
		getText(txt_overSpeedSmsTempCode, SystemSettingTypeEnum.固定车超速发送短信);
		getText(txt_seriousCarSendSms, SystemSettingTypeEnum.固定车严重超速发送短信);
		getText(txt_seriousSize, SystemSettingTypeEnum.严重超速速度);
		getText(txt_seriousTempCarBlackDay, SystemSettingTypeEnum.临时车严重超速拉黑);
	}
	public void getText(Text txt,SystemSettingTypeEnum systemSettingTypeEnum) {
		CarparkDatabaseServiceProvider sp = getWizard().getSp();
		SingleCarparkSystemSetting setting = sp.getCarparkService().findSystemSettingByKey(systemSettingTypeEnum.name());
		setting.setSettingValue(txt.getText());
		sp.getCarparkService().saveSystemSetting(setting);
	}

	@Override
	public OverSpeedSettingWizard getWizard() {
		return (OverSpeedSettingWizard) super.getWizard();
	}
}