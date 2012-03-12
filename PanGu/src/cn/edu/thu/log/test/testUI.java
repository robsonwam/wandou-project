package cn.edu.thu.log.test;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.deckfour.xes.model.XLog;

import cn.edu.thu.log.mining.AlphaMiner;
import cn.edu.thu.log.preprocessrule.NoiseFormat;
import cn.edu.thu.log.read.LogBuffer;
import cn.edu.thu.log.web.service.LogReadService;
import cn.edu.thu.log.web.service.MiningConfigUIService;
import cn.edu.thu.log.web.service.WebConfigReadService;
import cn.edu.thu.log.web.service.XESConvertService;
import cn.edu.thu.log.web.service.impl.LogReadServiceImpl;
import cn.edu.thu.log.web.service.impl.MiningConfigUIServiceImpl;
import cn.edu.thu.log.web.service.impl.WebConfigReadServiceImpl;
import cn.edu.thu.log.web.service.impl.XESConvertServiceImp;

/**
 * applicationUI for test
 * 
 * @author Meng and Wan
 * 
 */

public class testUI extends JFrame {
	/** Menu */
	JMenuBar menuBar;
	/** homeMenu */
	JMenu homeMenu;
	JMenuItem openFileItem;
	JMenuItem addFileItem;
	JMenuItem saveAsItem;
	JMenuItem exitItem;

	/** helpMenu */
	JMenu aboutMenu;
	JMenuItem aboutItem;
	/** table */
	JTable logTable;
	DefaultTableModel tableModel;

	/** control Panel */
	JPanel controlPanel;
	JPanel homePanel;
	/** control buttons */
	JButton addButton;
	JButton deleteButton;
	JButton editButton;
	JButton viewButton;
	JButton searchButton;
	JTextField searchField;
	JButton sortDateButton;

	ArrayList<LogBuffer> logList;
	int selectedRow;
	String mode;
	/** chosen file */
	String filePath;
	String fileName;
	File chosenfile;
	File[] chosenfiles;
	File chosenMiningFile;
	// Service
	LogReadServiceImpl logreadservice;

	/** Config miningfile */
	JMenu miningConfig;
	JMenuItem logCleanItem;
	JMenuItem noiseIdentifyItem;
	JMenuItem activityIdentifyItem;
	JMenuItem caseIdentifyItem;
	
	/** 过程挖掘面板 */
	JMenu processMiningMainMenu;
	JMenuItem openMiningFileItem;
	JMenu processMiningMenu;
	ArrayList<String> processMiningItems;
	JMenuItem miningItem;
	JMenuItem registerMiningItem;
	



	JPanel miningConfigPanel;
	JPanel logCleanPanel;
	JPanel noiseIdentifyPanel;
	JPanel activityIdentifyPanel;
	JPanel caseIdentifyPanel;
	Container container;
	CardLayout cardManager;

	// 事件处理部分所需变量
	LogReadService readlogservice;
	MiningConfigUIService miningconfigservice;
	// MiningConfigWriteService miningconfigwriteservice;
	ArrayList<String> allProducts = new ArrayList<String>();
	DefaultTableModel noiseResultModel;
	JTable noiseResultTable;
	JList allnoiseList;
	// JList noiseResultList;
	JComboBox productCombo;
	JList productList;
	DefaultListModel productResultModel;
	JList tagList;
	DefaultTableModel activityResultModel;
	JTable activityResultTable;
	Map<String, String> productsname;
	DefaultListModel caseResultModel;
	JList caseList;
	JList caseResultList;
	DefaultTableModel tagModel;
	int flag = -1;
	int buttoncount = 0;
	ArrayList<String> tempList;

	String miningconfigfilename = null;
	
	XESConvertService XESConvert;
	/**
	 * construction function
	 * 
	 * @throws Exception
	 */

	public testUI() {

		// initiate the UI
		miningconfigservice = new MiningConfigUIServiceImpl();
		XESConvert = new XESConvertServiceImp();
		// miningconfigwriteservice=new MiningConfigWriteServiceImpl();
		// readlogservice=new LogReadServiceImpl();
		productsname = new HashMap<String, String>();
		productsname.put("新闻", "news");
		productsname.put("网页", "page");
		productsname.put("时评", "real");
		productsname.put("图片", "image");
		productsname.put("音乐", "music");
		productsname.put("网址", "nav");
		allProducts.add("news");
		allProducts.add("page");
		allProducts.add("real");
		allProducts.add("image");
		allProducts.add("music");
		allProducts.add("nav");
		start();
		initComponents();

	}

	/**
	 * initiate the fields for funtion
	 * 
	 * @throws Exception
	 */
	private void start() {
		logreadservice = new LogReadServiceImpl();
		// TODO Auto-generated method stub
	}

	/**
	 * function to draw the UI
	 */
	private void initComponents() {
		// container
		container = this.getContentPane();
		cardManager = new CardLayout();
		container.setLayout(cardManager);

		// 创建几个面板
		homePanel = new JPanel();
		logCleanPanel = new JPanel();
		noiseIdentifyPanel = new JPanel();
		activityIdentifyPanel = new JPanel();
		caseIdentifyPanel = new JPanel();

		// 菜单控件的初始化，添加事件监听，并添加到界面中
		initMenuBar();

		// draw the table to contain log infomation
		initHomePanel();

		// 初始化挖掘配置文件面板
		initMiningConfigPanel();

		// 在cardLayout布局的frame中添加不同面板
		container.add(homePanel, "home面板");
		container.add(logCleanPanel, "日志清洗面板");
		container.add(noiseIdentifyPanel, "噪音识别面板");
		container.add(activityIdentifyPanel, "活动识别面板");
		container.add(caseIdentifyPanel, "案例识别面板");

	}

	// 初始化菜单栏
	private void initMenuBar() {
		{// 主页菜单
			homeMenu = new JMenu("Home");
			openFileItem = new JMenuItem("new file");
			openFileItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						cardManager.show(container, "home面板");
						openFile();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			});
			addFileItem = new JMenuItem("add file");
			addFileItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						// addFile();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			});
			saveAsItem = new JMenuItem("exportXES");
			saveAsItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					exportXES();
				}

			});
			exitItem = new JMenuItem("exit");
			// 添加退出菜单项的事件监听
			exitItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					System.exit(0);
				}

			});
			homeMenu.add(openFileItem);
			// homeMenu.add(addFileItem);
			homeMenu.add(saveAsItem);
			homeMenu.add(exitItem);

			// 有关信息菜单
			aboutMenu = new JMenu("About");
			aboutItem = new JMenuItem("about me");
			aboutMenu.add(aboutItem);
			// 添加退出菜单项的事件监听
			aboutItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					JOptionPane
							.showMessageDialog(
									null,
									"If you want furthur question,please contact me:\n mail:572804866@qq.com",
									"about", JOptionPane.INFORMATION_MESSAGE);
				}

			});

			// 挖掘配置文件菜单
			miningConfig = new JMenu("规则配置");
			logCleanItem = new JMenuItem("日志清洗规则");
			noiseIdentifyItem = new JMenuItem("噪声识别规则");
			activityIdentifyItem = new JMenuItem("活动识别规则");
			caseIdentifyItem = new JMenuItem("案例识别规则");
			miningConfig.add(logCleanItem);
			miningConfig.add(noiseIdentifyItem);
			miningConfig.add(activityIdentifyItem);
			miningConfig.add(caseIdentifyItem);

			logCleanItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub

					cardManager.show(container, "日志清洗面板");

				}

			});

			noiseIdentifyItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					cardManager.show(container, "噪音识别面板");
				}
			});

			activityIdentifyItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					cardManager.show(container, "活动识别面板");
				}

			});

			caseIdentifyItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					cardManager.show(container, "案例识别面板");
				}

			});


			//配置过程挖掘菜单
			processMiningMainMenu=new JMenu("过程挖掘");
			openMiningFileItem=new JMenuItem("打开新的日志文件");
			processMiningMenu=new JMenu("挖掘算法选择");
			processMiningMenu.setEnabled(false);
			ArrayList<String> miningNames=new ArrayList<String>();
			miningNames.add("AlphaMiner");
			
			for(String name: miningNames){
				JMenuItem alphaItem=new JMenuItem(name);
				processMiningMenu.add(alphaItem);
				alphaItem.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						AlphaMiner alphaminer=new AlphaMiner();
						Collection<XLog> logs=XESConvert.readStandardLog(chosenMiningFile.getAbsolutePath());
						Iterator<XLog> it= logs.iterator();
						try {
							alphaminer.doMining(it.next());
						} catch (CancellationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (ExecutionException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					
				});
			}
			registerMiningItem=new JMenuItem("注册新挖掘算法");
			processMiningMainMenu.add(processMiningMenu);
			processMiningMainMenu.add(registerMiningItem);
			processMiningMainMenu.add(openMiningFileItem);	

			
			openMiningFileItem.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					openMiningFile();
				}

				
			});
			
			registerMiningItem.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					cardManager.show(container, "注册新挖掘算法");
				}
				
			});	
			

			// 对菜单栏初始化
			menuBar = new JMenuBar();
			menuBar.add(homeMenu);
			menuBar.add(miningConfig);

			menuBar.add(processMiningMainMenu);

			menuBar.add(aboutMenu);
			// 添加菜单到界面
			this.setJMenuBar(menuBar);
		}
	}

	// 初始化home面板
	private void initHomePanel() {
		homePanel.setLayout(new BorderLayout());
		{

			String[] columns = null;
			String[][] data = null;

			tableModel = new DefaultTableModel(data, columns);

			logTable = new JTable(tableModel);

			homePanel.add(new JScrollPane(logTable), BorderLayout.NORTH);

		}
		// draw the control panel
		{
			controlPanel = new JPanel();
			mode = "add";
			addButton = new JButton("add");
			addButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					mode = "add";
					// addBook();
				}

			});
			// controlPanel.add(addButton);
			deleteButton = new JButton("delete");
			deleteButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					int index = logTable.getSelectedRow();
					// deleteBook(index);
					deleteLog(index);
				}

			});
			controlPanel.add(deleteButton);
			editButton = new JButton("edit");
			editButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					mode = "edit";
					selectedRow = logTable.getSelectedRow();
					// editBook(selectedRow);
				}

			});
			// controlPanel.add(editButton);

			viewButton = new JButton("view more detailed");
			viewButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					mode = "view";
					selectedRow = logTable.getSelectedRow();
					viewLog(selectedRow);
				}

			});

			controlPanel.add(viewButton);
			searchField = new JTextField("");
			searchField.setPreferredSize(new Dimension(100, 20));

			controlPanel.add(searchField);
			searchButton = new JButton("search");
			searchButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					// search();
					searchLog();
				}

			});
			controlPanel.add(searchButton);

			sortDateButton = new JButton("sort by publishDate");
			sortDateButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
				//	sortByDate();
				}
			});
		//	controlPanel.add(sortDateButton);
			homePanel.add(controlPanel, BorderLayout.SOUTH);
		}
	}

	// 初始化挖掘配置文件面板
	private void initMiningConfigPanel() {
		initLogCleanPanel();
		initNoiseIdentifyPanel();
		initActivityIdentifyPanel();
		initCaseIdentifyPanel();
	}

	// 初始化日志清洗面板
	private void initLogCleanPanel() {
		// logCleanPanel.setBackground(Color.BLACK);
		// logCleanPanel.setLayout(new BorderLayout());
		logCleanPanel.setBorder(BorderFactory.createTitledBorder("日志清洗规则"));

		JPanel chooseCleanPanel = new JPanel();
		JPanel resultPanel1 = new JPanel();
		String[] cols = { "字段", "字段格式" };
		String[][] attr = null;
		tagModel = new DefaultTableModel(attr, cols);

		/** 绘制配置字段面板 */
		Vector<String> alltags = new Vector<String>(
				logreadservice.getLogTagsByProducts(allProducts));

		// 下拉表中放入日志中所有存在的字段，可通过读取日志配置文件
		final JComboBox chooseAttri = new JComboBox(alltags);
		// 文本框中让用户写入字段期望的正则表达式
		final JTextField expressionText = new JTextField(15);
		// 按钮将以上字段和正则表达式规则一同放入下边的table中
		JButton addButton = new JButton("添加字段规范");
		addButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String tagname = chooseAttri.getSelectedItem().toString();
				String expression = expressionText.getText();
				if (!miningconfigservice.getallLogCleanRules().containsKey(
						tagname)) {
					miningconfigservice.addLogCleanRule(tagname, expression);
					tagModel.addRow(miningconfigservice.getTagFormat()
							.toArray());
				}

			}

		});
		chooseCleanPanel.add(chooseAttri);
		chooseCleanPanel.add(expressionText);
		chooseCleanPanel.add(addButton);

		/** 绘制配置字段结果展示面板 */
		tagModel.setColumnIdentifiers(cols);
		JTable tagTable = new JTable(tagModel);
		resultPanel1.add(new JScrollPane(tagTable));

		logCleanPanel.add(chooseCleanPanel, BorderLayout.NORTH);
		logCleanPanel.add(resultPanel1, BorderLayout.CENTER);
	}

	// 初始化噪音识别面板
	private void initNoiseIdentifyPanel() {
		// noiseIdentifyPanel.setBackground(Color.RED);
		noiseIdentifyPanel
				.setBorder(BorderFactory.createTitledBorder("噪声识别规则"));
		noiseIdentifyPanel.setLayout(new GridLayout(1, 3));
		noiseResultModel = new DefaultTableModel();
		noiseResultTable = new JTable(noiseResultModel);
		// noiseResultList.setModel(noiseResultModel);
		JPanel alltagPanel = new JPanel();
		allnoiseList = new JList();
		allnoiseList.setBorder(BorderFactory.createTitledBorder("选择要设定噪音穿的列"));
		allnoiseList.setListData(logreadservice.getLogTagsByProducts(
				allProducts).toArray());
		JScrollPane noisetagPane = new JScrollPane(allnoiseList);
		allnoiseList.setPreferredSize(getPreferredSize());
		allnoiseList.setVisibleRowCount(27);
		alltagPanel.add(noisetagPane);

		String[] cols = { "噪声字段", "噪音串" };
		String[][] attr = null;
		noiseResultModel = new DefaultTableModel(attr, cols);

		JPanel chooseNoisePanel = new JPanel();
		JPanel resultPanel2 = new JPanel(new BorderLayout());

		/** 三种噪声识别规则的配置 */
		chooseNoisePanel.setLayout(new GridLayout(4, 1));
		chooseNoisePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		// 第一行用于配置多个不能出现的正则表达式，如果出现则视为噪声
		JPanel p1 = new JPanel();
		p1.setLayout(new FlowLayout(FlowLayout.LEFT));
		final JTextField noiseAttr = new JTextField(15);
		JButton addNoiseButton = new JButton("添加");
		addNoiseButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String noiseStr = noiseAttr.getText();
				String noisename = allnoiseList.getSelectedValue().toString();
				miningconfigservice.addNoiseIdentifyRule(noisename, noiseStr);
				noiseResultModel.addRow(miningconfigservice.getNoiseFormat()
						.toArray());
				noiseResultTable.setModel(noiseResultModel);

				Iterator<Entry<String, NoiseFormat>> it = miningconfigservice
						.getAllNoiseIdentifyRules().entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, NoiseFormat> entry = it.next();

				}

			}

		});
		p1.add(noiseAttr);
		p1.add(addNoiseButton);

		// 第二行用于配置连续访问动作间最小时间间隔
		JPanel p2 = new JPanel();
		p2.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel minInternalLabel = new JLabel("连续访问动作间最小时间间隔(毫秒)");
		final JTextField minInternalText = new JTextField(6);
		p2.add(minInternalLabel);
		p2.add(minInternalText);
		// 第三行用于配置连续访问最长时间
		JPanel p3 = new JPanel();
		p3.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel maxTimeLabel = new JLabel("连续访问最长时间(小时)");
		final JTextField maxTimeText = new JTextField(6);
		p3.add(maxTimeLabel);
		p3.add(maxTimeText);
		// 第四行是一个保存按钮，保存所有的噪声识别规则
		JPanel p4 = new JPanel();
		JButton saveAll = new JButton("保存噪音识别规则");
		saveAll.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String mininter = minInternalText.getText();
				String maxtime = maxTimeText.getText();
				miningconfigservice.setMinIternal(mininter);
				miningconfigservice.setMaxTime(maxtime);

			}
		});
		p4.setLayout(new FlowLayout(FlowLayout.LEFT));
		p4.add(saveAll);

		chooseNoisePanel.add(p1);
		chooseNoisePanel.add(p2);
		chooseNoisePanel.add(p3);
		chooseNoisePanel.add(p4);

		/** 噪声识别规则1中定义的正则表达式结果列表 */

		noiseResultModel.setColumnIdentifiers(cols);
		noiseResultTable = new JTable(noiseResultModel);
		JScrollPane scrollNoisePane = new JScrollPane(noiseResultTable);
		scrollNoisePane.setPreferredSize(getPreferredSize());

		resultPanel2.add(scrollNoisePane);
		resultPanel2.setBorder(BorderFactory.createTitledBorder("噪音表达式结果"));

		noiseIdentifyPanel.add(alltagPanel);
		noiseIdentifyPanel.add(chooseNoisePanel);
		noiseIdentifyPanel.add(resultPanel2);

	}

	// 初始化活动识别面板
	private void initActivityIdentifyPanel() {
		// activityIdentifyPanel.setBackground(Color.YELLOW);

		activityIdentifyPanel.setBorder(BorderFactory
				.createTitledBorder("活动识别规则"));
		activityIdentifyPanel.setLayout(new GridLayout(1, 4));
		productResultModel = new DefaultListModel();
		productList = new JList(productResultModel);
		tagList = new JList();
		String[] cols = { "活动", "取子串" };
		String[][] attr = null;
		activityResultModel = new DefaultTableModel(attr, cols);
		activityResultModel.setColumnIdentifiers(cols);
		activityResultTable = new JTable(activityResultModel);

		/** 产品面板包括一个产品选择下拉框和一个所选产品的JList列表，产品数目固定 */
		JPanel productPanel = new JPanel();
		JPanel up = new JPanel();
		up.setLayout(new GridLayout(2, 1));
		String[] productName = { "新闻", "网页", "时评", "图片", "音乐", "网址" };
		productCombo = new JComboBox(productName);
		productCombo.setBorder(BorderFactory.createTitledBorder("所有产品列表"));
		// logreadservice=new LogReadServiceImpl();
		JButton addProductButton = new JButton("添加产品");
		addProductButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				// 点击按钮后选择的产品显示在JList中

				String productStr = productCombo.getSelectedItem().toString();
				String productEngname = productsname.get(productStr);
				miningconfigservice.addAnalyzedProduct(productEngname);
				productResultModel.addElement(productStr);
				productList.setModel(productResultModel);

				// 点击按钮后在字段部分显示的字段产生变化
				tempList = new ArrayList<String>(miningconfigservice
						.getAllAnalyzedProducts());

				tagList.setListData(logreadservice.getLogTagsByProducts(
						tempList).toArray());

				// 初始化案例识别面板
				caseList.setListData(logreadservice.getLogTagsByProducts(
						tempList).toArray());
			}
		});
		up.add(productCombo);
		up.add(addProductButton);

		productList.setVisibleRowCount(22);
		productList.setPreferredSize(getPreferredSize());
		JScrollPane productPane = new JScrollPane(productList);
		// productPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		// productPane.createHorizontalScrollBar();
		// productPane.createVerticalScrollBar();
		productPane.setBorder(BorderFactory.createTitledBorder("选择的产品"));
		productPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		productPanel.setLayout(new BorderLayout());
		productPanel.add(up, BorderLayout.NORTH);
		productPanel.add(productPane);

		/** 字段面板包括对应产品的所有字段，有一个JList */
		JPanel tagPanel = new JPanel(new BorderLayout());
		tagPanel.setBorder(BorderFactory.createTitledBorder("产品所有字段"));
		//tagList.setVisibleRowCount(20);
		tagList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		tagList.setPreferredSize(getPreferredSize());
		//tagList.setAutoscrolls(true);
		
		JScrollPane tagPane = new JScrollPane(tagList);
		//tagPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		tagPane.getVerticalScrollBar().setValue(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		tagPanel.add(tagPane);
		/** 操作面板包括选择字段作为活动的所有操作，包括两个添加按钮，一个timestamp结果框，一个正则表达式输入框 */
		JPanel operationPanel = new JPanel();
		operationPanel.setBorder(BorderFactory.createTitledBorder("选择活动字段"));
		Box operationBox = Box.createVerticalBox();
		JButton addTimestampButton = new JButton("添加时间戳");
		final JTextField timestampText = new JTextField(6);
		timestampText.setEditable(false);
		final JTextField actExpressText = new JTextField(6);
		JLabel actExpressLabel = new JLabel("活动子串规则");
		addTimestampButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String selectedtime = tagList.getSelectedValue().toString();
				timestampText.setText(selectedtime);
				miningconfigservice.setTimeStamp(selectedtime);
			}

		});
		JButton addActivityButton = new JButton("添加活动");
		addActivityButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String selectedactivity = tagList.getSelectedValue().toString();
				String actExpression = actExpressText.getText();
				if (!miningconfigservice.getAllActivityIdentifyRules()
						.containsKey(selectedactivity)) {
					miningconfigservice.addActivityIdentifyRule(
							selectedactivity, actExpression);
					activityResultModel.addRow(miningconfigservice
							.getActivityFormat().toArray());
				}
				activityResultTable.setModel(activityResultModel);

			}
		});

		operationBox.add(Box.createVerticalStrut(20));
		operationBox.add(addTimestampButton);
		operationBox.add(Box.createVerticalStrut(20));// 创建一个不可见的30单位的组件
		operationBox.add(timestampText);
		operationBox.add(Box.createVerticalStrut(100));
		operationBox.add(actExpressLabel);
		operationBox.add(Box.createVerticalStrut(20));
		operationBox.add(actExpressText);
		operationBox.add(Box.createVerticalStrut(100));
		operationBox.add(addActivityButton);
		operationPanel.add(operationBox);

		/** 活动结果面板包括对选择的一列或多列字段及子串的结果展示，是一个JList */
		JPanel activityResultPanel = new JPanel(new BorderLayout());
		activityResultPanel.setBorder(BorderFactory
				.createTitledBorder("活动选择结果"));
		JScrollPane actResultPane = new JScrollPane(activityResultTable);
		actResultPane.setPreferredSize(getPreferredSize());
		actResultPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		activityResultPanel.add(actResultPane);

		/** 将所有面板加入其中 */
		activityIdentifyPanel.add(productPanel);
		activityIdentifyPanel.add(tagPanel);
		activityIdentifyPanel.add(operationPanel);
		activityIdentifyPanel.add(activityResultPanel);
	}

	// 初始化案例识别面板
	private void initCaseIdentifyPanel() {
		// caseIdentifyPanel.setBackground(Color.GREEN);
		ButtonGroup casechooseGroup = new ButtonGroup();
		caseIdentifyPanel.setBorder(BorderFactory.createTitledBorder("案例识别规则"));
		caseIdentifyPanel.setLayout(new GridLayout(1, 3));
		caseList = new JList();
		caseList.setBorder(BorderFactory.createTitledBorder("可选活动字段"));

		JPanel caseChooseModePanel = new JPanel();
		final JRadioButton mainIdButton = new JRadioButton("主案例ID（唯一）", true);
		final JRadioButton secondIdButton = new JRadioButton("可选案例ID（可组合）",
				false);
		casechooseGroup.add(mainIdButton);
		casechooseGroup.add(secondIdButton);
		caseChooseModePanel.add(mainIdButton, BorderLayout.NORTH);
		caseChooseModePanel.add(secondIdButton, BorderLayout.SOUTH);
		JButton caseIDButton = new JButton("添加案例ID");
		caseChooseModePanel.add(caseIDButton);
		JButton saveAllButton = new JButton("保存所有配置信息");
		saveAllButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				miningconfigservice.writeMiningConfig("miningconfig1.xml");
			}

		});
		caseChooseModePanel.add(saveAllButton);
		caseIDButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				buttoncount++;
				// TODO Auto-generated method stub
				if (mainIdButton.isSelected()) {
					if (buttoncount == 1) {
						caseResultModel.addElement(caseList.getSelectedValue());
						caseResultList.setModel(caseResultModel);
						miningconfigservice.addCaseIdentifyRule(caseList
								.getSelectedValue().toString(), "mainid");
					} else
						JOptionPane.showMessageDialog(container,
								"主案例ID只能选择一个字段！");

				}
				if (secondIdButton.isSelected()) {
					caseResultModel.addElement(caseList.getSelectedValue());
					caseResultList.setModel(caseResultModel);
					miningconfigservice.addCaseIdentifyRule(caseList
							.getSelectedValue().toString(), "secondid");
				}

			}

		});
		mainIdButton.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if (e.getStateChange() == e.SELECTED) {
					buttoncount = 0;
					miningconfigservice.clearCaseIdentifyRules();
					caseResultModel.clear();
					caseResultList.setModel(caseResultModel);
				}
			}

		});
		secondIdButton.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if (e.getStateChange() == e.SELECTED) {
					buttoncount = 0;
					miningconfigservice.clearCaseIdentifyRules();
					caseResultModel.clear();
					caseResultList.setModel(caseResultModel);
				}
			}

		});
		caseResultModel = new DefaultListModel();
		caseResultList = new JList(caseResultModel);
		caseResultList.setBorder(BorderFactory.createTitledBorder("已选活动字段"));
		caseIdentifyPanel.add(caseList);
		caseIdentifyPanel.add(caseChooseModePanel);
		caseIdentifyPanel.add(caseResultList);
	}

	/**
	 * add each log record for test
	 * 
	 * @param logRecord
	 *            each log record
	 */
	public void addLog(ArrayList<Object> logRecord) {

		for (int i = 0; i < logRecord.toArray().length; i++) {
			System.out.print("," + logRecord.toArray()[i]);
		}

		tableModel.addRow(logRecord.toArray());
	}

	/**
	 * set up Table's head based on product
	 * 
	 * @param logTags
	 *            name of columns
	 */
	public void setTableHead(ArrayList<String> logTags) {
		tableModel.setColumnIdentifiers(logTags.toArray());

	}

	private void emptyTable() {
		for (int i = tableModel.getRowCount() - 1; i >= 0; i--) {
			tableModel.removeRow(i);
		}
	}

	private void exportXES() {

		// chooser file
		JFileChooser chooser = new JFileChooser();
		File savedFile = null;
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setMultiSelectionEnabled(true);
		chooser.setCurrentDirectory(new File("D://"));
		int returnVal = chooser.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			if (chooser.getSelectedFile() != null) {
				savedFile = new File(chooser.getSelectedFile().getPath());

				System.out.print("\n file(has selevted)" + savedFile
						+ " has been saved");
			} else {
				savedFile = new File(chooser.getCurrentDirectory(),
						chooser.getDialogTitle());
				System.out.print("\n file(no seected)" + savedFile
						+ " has been saved");
			}

		

		//XESConvertServiceImp XESConvert = new XESConvertServiceImp();
		XESConvert.convert(chosenfile.getAbsolutePath(),
				savedFile.getAbsolutePath());
		JOptionPane.showMessageDialog(this,
				"xes has been save to " + savedFile.getAbsolutePath());
		}
	}

	/**
	 * choose to open new file
	 * 
	 */
	private void openFile() throws Exception {
		// chooser file
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setMultiSelectionEnabled(true);
		chooser.setCurrentDirectory(new File("D://"));
		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			chosenfile = chooser.getSelectedFile();
			emptyTable();
			try {

				logreadservice.readLog(chosenfile, this);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	// /**
	// * choose to apppend more files to existing table
	// *
	// */
	// private void addFile() throws Exception {
	// JFileChooser chooser = new JFileChooser();
	// chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	// chooser.setMultiSelectionEnabled(true);
	//
	// int returnVal = chooser.showOpenDialog(this);
	//
	// if (returnVal == JFileChooser.APPROVE_OPTION) {
	// chosenfile = chooser.getSelectedFile();
	//
	// }
	// try {
	//
	// logreadservice.readLog(chosenfile, this);
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// }

	/**
	 * view more detailed inforamtion about logRecord
	 * 
	 * @param index
	 */
	private void viewLog(int index) {
		LogBuffer selectedLog = logList.get(index);
		JOptionPane.showMessageDialog(this,
				"\nlogName:" + selectedLog.getLogName() + "\nlogPath"
						+ selectedLog.getLogPath() + "\nlogContent"
						+ selectedLog.getLogContent());

	}

	/**
	 * delete log record
	 */
	private void deleteLog(int index) {
		tableModel.removeRow(index);

		logList.remove(index);

	}

	/**
	 * search by searchKey
	 */
	private void searchLog() {
		String searchKey = searchField.getText();
		logreadservice.searchLog(searchKey, logList, this);

	}



	public void print(String[] strings) {
		System.out.print("\nthe strings' size is:" + strings.length);
		for (int i = 0; i < strings.length; i++) {
			System.out.print("\t" + strings[i]);
		}
	}

	public void setContent(ArrayList<LogBuffer> newlogList) {
		emptyTable();
		// for(){}
		// TODO Auto-generated method stub
		// System.out.print("\nthe size of logList in UI:"+logList.size());
		// logList=new ArrayList<Log>();
		logList = newlogList;
		// ArrayList<Log> templogList = logList;
		for (int i = 0; i < logList.size(); i++) {
			LogBuffer log = logList.get(i);
			// System.out.print("\nog content at UI"
			// + log.getLogContent().toArray());
			tableModel.addRow(log.getLogContent().toArray());
		}

	}

	public void addContent(ArrayList<LogBuffer> newlogList) {

		// TODO Auto-generated method stub
		// System.out.print("\nthe size of logList in UI:"+logList.size());
		// logList=new ArrayList<Log>();
		logList.addAll(newlogList);
		// ArrayList<Log> templogList = logList;
		for (int i = 0; i < logList.size(); i++) {
			LogBuffer log = logList.get(i);
			System.out.print("\nog content at UI"
					+ log.getLogContent().toArray());
			tableModel.addRow(log.getLogContent().toArray());
		}

	}

	public ArrayList<LogBuffer> getContent() {
		// TODO Auto-generated method stub
		// System.out.print("\nthe size of logList in UI:"+logList.size());

		return logList;

	}

	private void exportMiningConfigFile() {

		// chooser file
		JFileChooser chooser = new JFileChooser();
		File savedFile = null;
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setMultiSelectionEnabled(true);
		chooser.setCurrentDirectory(new File("D://"));
		// chooser.setSelectedFile(new File(chosenfile.getName()));
		int returnVal = chooser.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			if (chooser.getSelectedFile() != null) {
				savedFile = new File(chooser.getSelectedFile().getPath());

				System.out.print("\n 配置信息已被保存于文件" + savedFile);
			} else {
				savedFile = new File(chooser.getCurrentDirectory(),
						chooser.getDialogTitle());
				System.out.print("\n 配置信息信息保存于文件" + savedFile);
				// File savedFile = new
				// File(chooser.getCurrentDirectory()+chooser.get);
			}

		}
		miningconfigfilename = savedFile.getAbsolutePath();
		miningconfigservice.writeMiningConfig(miningconfigfilename);
		WebConfigReadService webconfigreadservice = new WebConfigReadServiceImpl();
		JOptionPane.showMessageDialog(this,
				"配置信息已经保存于" + savedFile.getAbsolutePath());
	}

	
	//打开要挖掘的日志
	private void openMiningFile() {
		// TODO Auto-generated method stub
		// chooser file
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setMultiSelectionEnabled(true);
		chooser.setCurrentDirectory(new File("E://"));
		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			chosenMiningFile = chooser.getSelectedFile();
			System.out.println("载入的文件是"+chosenMiningFile.getAbsolutePath());
			cardManager.show(container, "日志分析面板");
			processMiningMenu.setEnabled(true);
			//XESConvert.readStandardLog(chosenMiningFile.getAbsolutePath());
			
			/**
			 * 调用日志分析算法，出入要分析的日志
			 */			
		}
	}


}
