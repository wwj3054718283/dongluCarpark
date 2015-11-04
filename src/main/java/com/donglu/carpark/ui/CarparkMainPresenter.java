package com.donglu.carpark.ui;

import java.awt.Canvas;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.SWTResourceManager;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.model.SearchErrorCarModel;
import com.donglu.carpark.server.imgserver.FileuploadSend;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkInOutServiceI;
import com.donglu.carpark.service.CountTempCarChargeI;
import com.donglu.carpark.service.impl.CountTempCarChargeImpl;
import com.donglu.carpark.ui.common.App;
import com.donglu.carpark.ui.view.InOutHistoryPresenter;
import com.donglu.carpark.ui.view.SearchErrorCarPresenter;
import com.donglu.carpark.ui.wizard.AddDeviceModel;
import com.donglu.carpark.ui.wizard.AddDeviceWizard;
import com.donglu.carpark.ui.wizard.ChangeUserWizard;
import com.donglu.carpark.ui.wizard.ReturnAccountWizard;
import com.donglu.carpark.ui.wizard.SearchHistoryByHandWizard;
import com.donglu.carpark.ui.wizard.model.ChangeUserModel;
import com.donglu.carpark.ui.wizard.model.ReturnAccountModel;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.LPRInOutType;
import com.dongluhitec.card.domain.LinkProtocolEnum;
import com.dongluhitec.card.domain.LinkTypeEnum;
import com.dongluhitec.card.domain.db.Device;
import com.dongluhitec.card.domain.db.Link;
import com.dongluhitec.card.domain.db.LinkStyleEnum;
import com.dongluhitec.card.domain.db.SerialDeviceAddress;
import com.dongluhitec.card.domain.db.singlecarpark.CarTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkReturnAccount;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.hardware.device.WebCameraDevice;
import com.dongluhitec.card.hardware.service.BasicHardwareService;
import com.dongluhitec.card.hardware.xinluwei.XinlutongJNA;
import com.dongluhitec.card.mapper.BeanUtil;
import com.dongluhitec.card.ui.util.FileUtils;
import com.google.common.io.Files;
import com.google.common.util.concurrent.Uninterruptibles;
import com.google.inject.Inject;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class CarparkMainPresenter {
	private Logger LOGGER = LoggerFactory.getLogger(CarparkMainPresenter.class);
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Inject
	private CommonUIFacility commonui;
	@Inject
	private XinlutongJNA xinlutongJNA;

	@Inject
	private WebCameraDevice webCameraDevice;
	@Inject
	private BasicHardwareService hardwareService;

	@Inject
	private InOutHistoryPresenter inOutHistoryPresenter;
	@Inject
	private SearchErrorCarPresenter searchErrorCarPresenter;
	
	private CountTempCarChargeI countTempCarCharge;

	// 保存设备的进出口信息
	Map<String, String> mapDeviceType;

	// 保存设备的界面信息
	Map<CTabItem, String> mapDeviceTabItem;
	// 保存设备的信息
	Map<String, SingleCarparkDevice> mapIpToDevice;
	// 保存设置信息
	private Map<SystemSettingTypeEnum, String> mapSystemSetting;

	private CarparkMainModel model;

	private CarparkMainApp view;

	private App app;
	public void setCarNo() {

	}

	/**
	 * 删除一个设备tab页
	 * 
	 * @param selection
	 */
	protected void deleteDeviceTabItem(CTabItem selection) {
		if (selection != null) {
			String ip = mapDeviceTabItem.get(selection);
			System.out.println("删除设备" + ip);
			selection.dispose();
			xinlutongJNA.closeEx(ip);
			mapDeviceTabItem.remove(selection);
			mapDeviceType.remove(ip);
			mapIpToDevice.remove(ip);
			com.dongluhitec.card.ui.util.FileUtils.writeObject("mapIpToDevice", mapIpToDevice);
		}
	}

	/**
	 * 弹窗添加设备
	 * @param tabFolder
	 * @param type
	 */
	public void addDevice(CTabFolder tabFolder, String type) {
		try {
			if (tabFolder.getItems().length>=4) {
				commonui.info("提示", type+"最多只能添加4个设备");
				return;
			}
			AddDeviceModel model = new AddDeviceModel();
			model.setList(sp.getCarparkService().findAllCarpark());
			model.setType("tcp");
			AddDeviceWizard v = new AddDeviceWizard(model);

			AddDeviceModel showWizard = (AddDeviceModel) commonui.showWizard(v);
			if (showWizard == null) {
				return;
			}
			String ip = showWizard.getIp();
			String name = showWizard.getName();
			showWizard.setInType(type);
			addDevice(showWizard.getDevice());
			addDevice(tabFolder, type, ip, name);
			showUsualContentToDevice(showWizard.getDevice(), showWizard.getDevice().getAdvertise());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void addDevice(SingleCarparkDevice device) throws Exception {
		String ip = device.getIp();
		SingleCarparkDevice singleCarparkDevice = mapIpToDevice.get(ip);
		if (!StrUtil.isEmpty(singleCarparkDevice)) {
			throw new Exception("ip" + ip + "的设备已存在");
		}
		mapIpToDevice.put(ip, device);
		com.dongluhitec.card.ui.util.FileUtils.writeObject("mapIpToDevice", mapIpToDevice);
	}

	/**
	 * 普通添加设备
	 * 
	 * @param tabFolder
	 * @param type
	 * @param ip
	 * @param name
	 */
	public void addDevice(CTabFolder tabFolder, String type, String ip, String name) {
		if (mapDeviceType.get(ip) != null) {
			commonui.error("添加失败", "设备" + ip + "已存在");
			return;
		}
		CTabItem tabItem = new CTabItem(tabFolder, SWT.NONE);
		tabItem.setFont(SWTResourceManager.getFont("微软雅黑", 15, SWT.NORMAL));
		tabItem.setText(name);
		Composite composite = new Composite(tabFolder, SWT.BORDER | SWT.EMBEDDED);
		tabItem.setControl(composite);
		composite.setLayout(new FillLayout());
		if (type.equals("进口")) {
			createLeftCamera(ip, composite);
		} else if (type.equals("出口")) {
			createRightCamera(ip, composite);
		}
		tabFolder.setSelection(tabItem);
		mapDeviceTabItem.put(tabItem, ip);
		mapDeviceType.put(ip, type);
	}

	/**
	 * 创建出口监控
	 * 
	 * @param ip
	 * @param northCamera
	 * 
	 */
	public void createRightCamera(String ip, Composite northCamera) {
		Frame new_Frame1 = SWT_AWT.new_Frame(northCamera);
		Canvas canvas1 = new Canvas();
		new_Frame1.add(canvas1);
		new_Frame1.pack();
		new_Frame1.setVisible(true);
		final String url = "rtsp://" + ip + ":554/h264ESVideoTest";
		final EmbeddedMediaPlayer createPlayRight = webCameraDevice.createPlay(new_Frame1, url);
		createPlayRight.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			@Override
			public void finished(final MediaPlayer mediaPlayer) {
				new Runnable() {
					public void run() {
						while (!mediaPlayer.isPlaying()) {
							// LOGGER.info("设备连接{}已断开", url);
							mediaPlayer.playMedia(url);
							Uninterruptibles.sleepUninterruptibly(10, TimeUnit.SECONDS);
						}
					}
				}.run();
			}
		});

		getView().shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				createPlayRight.release();
			}
		});
		northCamera.addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				createPlayRight.release();
			}
		});
		xinlutongJNA.openEx(ip, getView());
	}

	/**
	 * 创建进口监控
	 * 
	 * @param ip
	 * @param southCamera
	 * 
	 */
	public void createLeftCamera(String ip, Composite southCamera) {
		Frame new_Frame1 = SWT_AWT.new_Frame(southCamera);
		Canvas canvas1 = new Canvas();
		new_Frame1.add(canvas1);
		new_Frame1.pack();
		new_Frame1.setVisible(true);
		final String url = "rtsp://" + ip + ":554/h264ESVideoTest";
		final EmbeddedMediaPlayer createPlayLeft = webCameraDevice.createPlay(new_Frame1, url);
		createPlayLeft.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			@Override
			public void finished(final MediaPlayer mediaPlayer) {
				new Runnable() {
					public void run() {
						while (!mediaPlayer.isPlaying()) {
							LOGGER.info("设备连接{}已断开", url);
							mediaPlayer.playMedia(url);
							Uninterruptibles.sleepUninterruptibly(10, TimeUnit.SECONDS);
						}
					}
				}.run();
			}

			@Override
			public void error(MediaPlayer mediaPlayer) {

			}
		});
		getView().shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				createPlayLeft.release();
			}
		});
		southCamera.addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				createPlayLeft.release();
			}
		});
		xinlutongJNA.openEx(ip, getView());
	}

	/**
	 * @param type
	 * @param tabFolder
	 * 
	 */
	public void editDevice(CTabFolder tabFolder, String type) {
		try {
			CTabItem selection = tabFolder.getSelection();
			if (StrUtil.isEmpty(selection)) {
				return;
			}
			String oldIp = mapDeviceTabItem.get(selection);
			SingleCarparkDevice device = mapIpToDevice.get(oldIp);
			AddDeviceModel model = new AddDeviceModel();
			model.setDevice(device);
			AddDeviceWizard v = new AddDeviceWizard(model);
			model.setList(sp.getCarparkService().findAllCarpark());
			AddDeviceModel showWizard = (AddDeviceModel) commonui.showWizard(v);
			if (showWizard == null) {
				return;
			}
			String ip = showWizard.getIp();

			if (ip.equals(oldIp)) {
				selection.setText(showWizard.getName());
				mapIpToDevice.put(ip, showWizard.getDevice());
				com.dongluhitec.card.ui.util.FileUtils.writeObject("mapIpToDevice", mapIpToDevice);
				commonui.info("修改成功", "修改设备" + ip + "成功");
				showUsualContentToDevice(showWizard.getDevice(), showWizard.getDevice().getAdvertise());
				return;
			} else {
				if (mapDeviceType.get(ip) != null) {
					commonui.error("修改失败", "设备" + ip + "已存在");
					return;
				}
				deleteDeviceTabItem(selection);
				addDevice(showWizard.getDevice());
				addDevice(tabFolder, type, ip, showWizard.getName());
			}
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}finally{
			
		}
	}

	public CarparkMainApp getView() {
		return view;
	}

	public void setView(CarparkMainApp view) {
		this.view = view;
	}

	public Map<String, String> getMapDeviceType() {
		return mapDeviceType;
	}

	public void setMapDeviceType(Map<String, String> mapDeviceType) {
		this.mapDeviceType = mapDeviceType;
	}

	public Map<CTabItem, String> getMapDeviceTabItem() {
		return mapDeviceTabItem;
	}

	public void setMapDeviceTabItem(Map<CTabItem, String> mapDeviceTabItem) {
		this.mapDeviceTabItem = mapDeviceTabItem;
	}

	public Map<String, SingleCarparkDevice> getMapIpToDevice() {
		return mapIpToDevice;
	}

	public void setMapIpToDevice(Map<String, SingleCarparkDevice> mapIpToDevice) {
		this.mapIpToDevice = mapIpToDevice;
	}

	public CarparkMainModel getModel() {
		return model;
	}

	public void setModel(CarparkMainModel model) {
		this.model = model;
	}

	/**
	 * 归账
	 */
	public void returnAccount() {
		try {
			ReturnAccountModel model = new ReturnAccountModel();
			String userName = this.model.getUserName();
			model.setReturnUser(userName);
			CarparkInOutServiceI carparkInOutService = sp.getCarparkInOutService();
			float shouldMoney = carparkInOutService.findShouldMoneyByName(userName);
			float factMoney = carparkInOutService.findFactMoneyByName(userName);
			float freeMoney = carparkInOutService.findFreeMoneyByName(userName);
			model.setShouldReturn(shouldMoney);
			model.setFactReturn(factMoney);
			ReturnAccountWizard wizard = new ReturnAccountWizard(model, sp);
			ReturnAccountModel m = (ReturnAccountModel) commonui.showWizard(wizard);
			if (StrUtil.isEmpty(m)) {
				return;
			}
			SingleCarparkReturnAccount a = new SingleCarparkReturnAccount();
			BeanUtil.copyProperties(m, a, "returnUser", "factReturn", "shouldReturn", "operaName");
			a.setReturnTime(new Date());
			Long saveReturnAccount = sp.getCarparkService().saveReturnAccount(a);

			List<SingleCarparkInOutHistory> list = carparkInOutService.findNotReturnAccount(a.getReturnUser());
			for (SingleCarparkInOutHistory singleCarparkInOutHistory : list) {
				singleCarparkInOutHistory.setReturnAccount(saveReturnAccount);
			}
			carparkInOutService.saveInOutHistoryOfList(list);
			this.model.setTotalCharge(carparkInOutService.findFactMoneyByName(userName));
			this.model.setTotalFree(carparkInOutService.findFreeMoneyByName(userName));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 显示屏显示车牌
	 * 
	 * @param device
	 *            设备
	 * @param plateNO
	 *            显示车牌
	 */
	public boolean showPlateNOToDevice(SingleCarparkDevice device, String plateNO) {
		try {
			Device d = getDevice(device);
			Boolean carparkPlate = hardwareService.carparkPlate(d, plateNO);
			return carparkPlate;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 发送语音
	 * @param device 设备
	 * @param content语音
	 * @param opDoor是否需要开门
	 * @return
	 */
	public boolean showContentToDevice(SingleCarparkDevice device, String content,boolean opDoor) {
		try {
			if (opDoor) {
				Device d = getDevice(device);
    			Boolean carparkContentVoiceAndOpenDoor = hardwareService.carparkContentVoiceAndOpenDoor(d, content, device.getVolume()==null?1:device.getVolume());
    			openDoorToPhotograph(device.getIp());
				return carparkContentVoiceAndOpenDoor;
			}else{
    			Device d = getDevice(device);
    			return hardwareService.carparkContentVoice(d, content, device.getVolume()==null?1:device.getVolume());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 显示车为数
	 * 
	 * @param device
	 * @param content
	 * @param voice
	 * @return
	 */
	public boolean showPositionToDevice(SingleCarparkDevice device, int position) {
		try {
			Device d = getDevice(device);

			return hardwareService.carparkPosition(d, position, LPRInOutType.valueOf(device.getInType()));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private Device getDevice(SingleCarparkDevice device) {
		Device d = new Device();
		Link link = new Link();
		link.setId((long) device.getLinkAddress().hashCode());
		link.setLinkStyleEnum(LinkStyleEnum.直连设备);
		link.setType(LinkTypeEnum.TCP);
		link.setAddress(device.getLinkAddress());
		link.setProtocol(LinkProtocolEnum.Carpark);
		SerialDeviceAddress address = new SerialDeviceAddress();
		address.setAddress(device.getAddress());
		d.setAddress(address);
		d.setLink(link);
		return d;
	}

	/**
	 * 开门，设备开闸
	 * 
	 * @param device
	 */
	public boolean openDoor(SingleCarparkDevice device) {
		try {
			Boolean carparkOpenDoor = hardwareService.carparkOpenDoor(getDevice(device));
			Thread.sleep(1000);
			Set<String> keySet = mapIpToDevice.keySet();
			for (String string : keySet) {
				showPositionToDevice(mapIpToDevice.get(string), model.getTotalSlot());
			}
			openDoorToPhotograph(device.getIp());
			return carparkOpenDoor;
		} catch (Exception e) {
			return false;
		}
	}
	
	public void openDoorToPhotograph(String ip){
		xinlutongJNA.openDoor(ip);
	}
	public boolean showUsualContentToDevice(SingleCarparkDevice device, String usualContent) {
		try {
			Boolean carparkUsualContent = hardwareService.carparkUsualContent(getDevice(device), usualContent);
			return carparkUsualContent;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 免费放行
	 */
	public boolean freeCarPass() {
		String plateNo = model.getPlateNo();
		SingleCarparkInOutHistory history = model.getHistory();

		float real = 0;
		float shouldMony = model.getShouldMony();
		boolean confirm = commonui.confirm("收费确认", "车牌：" + plateNo + "应收：" + shouldMony + "免费放行");
		if (!confirm) {
			return false;
		}
		model.setReal(real);
		return true;
		// history.setFactMoney(real);
		// history.setFreeMoney(shouldMony-real);
		// sp.getCarparkInOutService().saveInOutHistory(history);

	}

	/**
	 * 收费放行
	 */
	public boolean chargeCarPass() {

		String plateNo = model.getPlateNo();
		SingleCarparkInOutHistory history = model.getHistory();

		float real = model.getReal();
		float shouldMony = model.getShouldMony();
		if (real > shouldMony) {
			commonui.error("收费提示", "实时不能超过应收");
			return false;
		}
		boolean confirm = commonui.confirm("收费确认", "车牌：" + plateNo + "应收：" + shouldMony + "实收：" + real);
		if (!confirm) {
			return false;
		}
		return true;
	}

	/**
	 * 计算收费
	 * @param endTime 
	 * @param startTime 
	 * 
	 * @return
	 */
	public float countShouldMoney(CarTypeEnum carType, Date startTime, Date endTime) {
		float calculateTempCharge = sp.getCarparkService().calculateTempCharge(carType.index(), startTime, endTime);
		return calculateTempCharge;
	}

	/**
	 * 换班
	 */
	public void changeUser() {

		ChangeUserWizard wizard = new ChangeUserWizard(new ChangeUserModel(), sp);
		ChangeUserModel showWizard = (ChangeUserModel) commonui.showWizard(wizard);
		if (StrUtil.isEmpty(showWizard)) {
			return;
		}
		SingleCarparkSystemUser systemUser = showWizard.getSystemUser();
		String userName = systemUser.getUserName();
		System.setProperty("userName", userName);
		System.setProperty("userType", systemUser.getType());

		model.setBtnClick(false);
		model.setTotalSlot(sp.getCarparkInOutService().findTotalSlotIsNow());
		model.setUserName(userName);
		model.setWorkTime(new Date());
		model.setTotalCharge(sp.getCarparkInOutService().findShouldMoneyByName(userName));
		model.setTotalFree(sp.getCarparkInOutService().findFreeMoneyByName(userName));
		model.setMonthSlot(sp.getCarparkInOutService().findFixSlotIsNow());
		model.setHoursSlot(sp.getCarparkInOutService().findTempSlotIsNow());
	}

	/**
	 * 打开记录查询页面
	 */
	public void showSearchInOutHistory() {
		
		if (app != null) {
			if (app.isOpen()) {
				app.focus();
			} else {
				app.open();
			}
			return;
		}
		ShowApp showApp = new ShowApp();
		showApp.setPresenter(inOutHistoryPresenter);
		app = showApp;
		app.open();
	}

	/**
	 * 手动抓拍
	 */
	public void handPhotograph(String ip) {
		xinlutongJNA.tigger(ip);

	}

	/**
	 * 保存车牌识别的图片
	 * 
	 * @param f
	 *            文件夹
	 * @param fileName
	 *            文件名
	 * @param bigImage
	 *            图片字节
	 */
	public void saveImage(String f, String fileName, byte[] bigImage) {
		bigImage = bigImage == null ? new byte[0] : bigImage;
		String fl = "/img/" + f;
		if (!StrUtil.isEmpty(FileUtils.readObject(CarparkManageApp.CLIENT_IMAGE_SAVE_FILE_PATH))) {
			String string = (String) FileUtils.readObject(CarparkManageApp.CLIENT_IMAGE_SAVE_FILE_PATH);
			fl = string + fl;
		}
		try {
			File file = new File(fl);
			if (!file.exists() && !file.isDirectory()) {
				Files.createParentDirs(file);
				file.mkdir();
			}
			String finalFileName = fl + "/" + fileName;
			File file2 = new File(finalFileName);
			file2.createNewFile();
			Files.write(bigImage, file2);
			String ip = CarparkClientConfig.getInstance().getDbServerIp();
			if (true) {
				long nanoTime = System.nanoTime();
				LOGGER.info("准备将图片{}上传到服务器{}",finalFileName,ip);
				try {
					String upload = FileuploadSend.upload("http://"+ip+":8899/carparkImage/", finalFileName);
					
					LOGGER.info("图片上传到服务器{}成功,{}",ip,upload);
				} catch (Exception e) {
					e.printStackTrace();
					LOGGER.error("图片上传到服务器{}失败",ip);
				}finally{
					LOGGER.info("上传图片花费时间：{}",System.nanoTime()-nanoTime);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setMapSystemSetting(Map<SystemSettingTypeEnum, String> mapSystemSetting) {
		this.mapSystemSetting = mapSystemSetting;
	}

	public void init() {
		
	}
	/**
	 *人工查找
	 * @param plateNO
	 * @param smallImg 
	 * @param bigImg 
	 */
	public void showManualSearch(String plateNO, String bigImg, String smallImg) {
		try {
			
			searchErrorCarPresenter.getModel().setPlateNo(model.getOutShowPlateNO());
			searchErrorCarPresenter.getModel().setHavePlateNoSelect(null);
			searchErrorCarPresenter.getModel().setNoPlateNoSelect(null);
			searchErrorCarPresenter.getModel().setSaveBigImg(bigImg);
			searchErrorCarPresenter.getModel().setSaveSmallImg(smallImg);
			
			searchErrorCarPresenter.setSystemSetting(mapSystemSetting);
			SearchHistoryByHandWizard wizard=new SearchHistoryByHandWizard(searchErrorCarPresenter);
			Object showWizard = commonui.showWizard(wizard);
			if (StrUtil.isEmpty(showWizard)) {
				return;
			}
			model.setOutPlateNOEditable(false);
			SingleCarparkInOutHistory select = searchErrorCarPresenter.getModel().getHavePlateNoSelect()==null?searchErrorCarPresenter.getModel().getNoPlateNoSelect():searchErrorCarPresenter.getModel().getHavePlateNoSelect();
			if (StrUtil.isEmpty(select)) {
				return;
			}
			SearchErrorCarModel m = searchErrorCarPresenter.getModel();
			select.setOutPlateNO(plateNO);
			if (!m.isInOrOut()) {
				select.setPlateNo(m.getPlateNo());
				List<SingleCarparkUser> findUserByPlateNo = sp.getCarparkUserService().findUserByPlateNo(m.getPlateNo());
				if (StrUtil.isEmpty(findUserByPlateNo)) {
					select.setCarType("临时车");
				}else{
					select.setCarType("固定车");
				}
			}
			sp.getCarparkInOutService().saveInOutHistory(select);
			view.invok(model.getIp(), 0, select.getPlateNo(), m.getBigImg(), m.getSmallImg());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 检查车牌识别间隔,现在时间在间隔时间内返回false
	 * 
	 * @param plateNO
	 */
	public boolean checkPlateNODiscernGap(Map<String, Date> mapPlateNoDate, String plateNO, Date nowDate) {
		Date date = mapPlateNoDate.get(plateNO);
		if (date != null) {
			String s = mapSystemSetting.get(SystemSettingTypeEnum.同一车牌识别间隔) == null ? SystemSettingTypeEnum.同一车牌识别间隔.getDefaultValue() : mapSystemSetting.get(SystemSettingTypeEnum.同一车牌识别间隔);
			LOGGER.info("同一车牌识别间隔为：{}", s);
			Integer timeGap = Integer.valueOf(s);
			DateTime plusSeconds = new DateTime(date).plusSeconds(timeGap);
			boolean after = plusSeconds.toDate().after(nowDate);
			if (after) {
				LOGGER.info("车牌{}在{}做过处理，暂不做处理", plateNO, StrUtil.formatDate(date, "yyyy-MM-dd HH:mm:ss"));
				return false;
			}
		}
		return true;
	}
}
