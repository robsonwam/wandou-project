package cn.edu.thu.log.test;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.Date;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import cn.edu.thu.log.read.Log;
import cn.edu.thu.log.web.service.impl.LogReadServiceImpl;

/**
 * applicationUI for test
 * 
 * @author Meng
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
	/** editMenu */
	JMenu editMenu;
	JMenuItem addContactItem;
	JMenuItem editContactItem;
	JMenuItem viewContactItem;
	JMenuItem deleteContactItem;

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
	JCheckBox caseIDBox;
	JCheckBoxMenuItem[] caseIDList;
	JButton sortPriceButton;
	JButton sortDateButton;

	ArrayList<LogHead> bookList;
	ArrayList<Log> logList;
	int selectedRow;
	String mode;
	/** chosen file */
	String filePath;
	String fileName;
	File chosenfile;
	File[] chosenfiles;
	// Service
	LogReadServiceImpl reader;

	/** Config miningfile */
	JMenu miningConfig;
	JMenuItem logCleanItem;
	JMenuItem noiseIdentifyItem;
	JMenuItem activityIdentifyItem;
	JMenuItem caseIdentifyItem;

	JPanel emptyPanel;
	JPanel miningConfigPanel;
	JPanel logCleanPanel;
	JPanel noiseIdentifyPanel;
	JPanel activityIdentifyPanel;
	JPanel caseIdentifyPanel;
	Container container;
	CardLayout cardManager;

	/**
	 * construction function
	 * 
	 * @throws Exception
	 */

	public testUI() {

		// initiate the UI

		initComponents();
		start();
	}

	/**
	 * initiate the fields for funtion
	 * 
	 * @throws Exception
	 */
	private void start() {
		reader = new LogReadServiceImpl();
		// TODO Auto-generated method stub

		// Book tempBook = new Book("1", "gone with wind", "literature",
		// "amerian writer", 50, "1988/3/30", "america", "2012.1.11",
		// "carol");
		//
		// tableModel.addRow(tempBook.toArrays());
		// bookList.add(tempBook);
		//
		// tempBook = new Book("2", "history of china", "history", "chinese",
		// 100,
		// "1949/10/1", "china", "2012/2/11", "reinier");
		//
		// tableModel.addRow(tempBook.toArrays());
		// bookList.add(tempBook);
		//
		// tempBook = new Book("3", "gone with wind", "literature",
		// "amerian writer", 20, "2010/5/20", "america", "2012.1.11",
		// "carol");
		//
		// tableModel.addRow(tempBook.toArrays());
		// bookList.add(tempBook);

		// parseXML=new ConfigReader();
		// parseXML.parse("../myenv.xml");

		// prop =parseXML.getProps();
		// tableModel.addRow( prop.values().toArray());
		// System.out.print(prop.keys());
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

		//在cardLayout布局的frame中添加不同面板
		container.add(homePanel, "home面板");
		container.add(logCleanPanel, "日志清洗面板");
		container.add(noiseIdentifyPanel, "噪音识别面板");
		container.add(activityIdentifyPanel, "活动识别面板");
		container.add(caseIdentifyPanel, "案例识别面板");

	}

	//初始化菜单栏
	private void initMenuBar() {
		{// 主页菜单
			homeMenu = new JMenu("Home");
			openFileItem = new JMenuItem("new file");
			openFileItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
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
					// TODO Auto-generated method stub
					// try {
					// saveToFile();
					// } catch (IOException e1) {
					// // TODO Auto-generated catch block
					// e1.printStackTrace();
					// }
					try {
						addFile();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			});
			saveAsItem = new JMenuItem("saveAs");
			saveAsItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					saveAs();
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
			homeMenu.add(addFileItem);
			homeMenu.add(saveAsItem);
			homeMenu.add(exitItem);

			// 编辑操作菜单

			editMenu = new JMenu("Edit");
			addContactItem = new JMenuItem("addContact");
			editContactItem = new JMenuItem("editContact");
			viewContactItem = new JMenuItem("viewContact");
			deleteContactItem = new JMenuItem("deleteContact");
			editMenu.add(addContactItem);
			editMenu.add(editContactItem);
			editMenu.add(viewContactItem);
			editMenu.add(deleteContactItem);

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

			// 对菜单栏初始化
			menuBar = new JMenuBar();
			menuBar.add(homeMenu);
			menuBar.add(editMenu);
			menuBar.add(aboutMenu);
			menuBar.add(miningConfig);

			// 添加菜单到界面
			this.setJMenuBar(menuBar);
		}
	}

	//初始化home面板
	private void initHomePanel() {
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
					addBook();
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
					editBook(selectedRow);
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
			sortPriceButton = new JButton("sort by price");
			sortPriceButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					sortByPrice();

				}

			});
			controlPanel.add(sortPriceButton);

			caseIDBox = new JCheckBox();
			caseIDList = new JCheckBoxMenuItem[0];

			controlPanel.add(caseIDBox);

			// caseIDBox.add
			sortDateButton = new JButton("sort by publishDate");
			sortDateButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					sortByDate();

				}

			});
			controlPanel.add(sortDateButton);
			homePanel.add(controlPanel, BorderLayout.SOUTH);
		}
	}

	//初始化挖掘配置文件面板
	private void initMiningConfigPanel() {
		initLogCleanPanel();
		initNoiseIdentifyPanel();
		initActivityIdentifyPanel();
		initCaseIdentifyPanel();
	}

	//初始化日志清洗面板
	private void initLogCleanPanel() {
		//logCleanPanel.setBackground(Color.BLACK);
		//logCleanPanel.setLayout(new BorderLayout());
		logCleanPanel.setBorder(BorderFactory.createTitledBorder("日志清洗规则"));
		JPanel chooseCleanPanel=new JPanel();
		JPanel resultPanel1=new JPanel();
		
		/**  绘制配置字段面板   */
		//下拉表中放入日志中所有存在的字段，可通过读取日志配置文件
		JComboBox chooseAttri=new JComboBox();
		//文本框中让用户写入字段期望的正则表达式
		JTextField expression=new JTextField(15);
		//按钮将以上字段和正则表达式规则一同放入下边的table中
		JButton addButton=new JButton("添加字段规范");
		
	    chooseCleanPanel.add(chooseAttri);
	    chooseCleanPanel.add(expression);
	    chooseCleanPanel.add(addButton);
	    
	    /**绘制配置字段结果战士面板 */
	    String[] cols = null;
		String[][] attr = null;
		DefaultTableModel resultModel = new DefaultTableModel(attr, cols);
		JTable resultTable = new JTable(resultModel);
		
		resultPanel1.add(new JScrollPane(resultTable));		
		
		logCleanPanel.add(chooseCleanPanel,BorderLayout.NORTH);
		logCleanPanel.add(resultPanel1,BorderLayout.CENTER);
	}

	//初始化噪音识别面板
	private void initNoiseIdentifyPanel() {		
		//noiseIdentifyPanel.setBackground(Color.RED);
		noiseIdentifyPanel.setBorder(BorderFactory.createTitledBorder("噪声识别规则"));		
		noiseIdentifyPanel.setLayout(new GridLayout(1,2));
		JPanel chooseNoisePanel=new JPanel();
		JPanel resultPanel2=new JPanel();		
		
		/**  三种噪声识别规则的配置   */
		chooseNoisePanel.setLayout(new GridLayout(3,1));
		chooseNoisePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		//第一行用于配置多个不能出现的正则表达式，如果出现则视为噪声
		JPanel p1=new JPanel();	
		p1.setLayout(new FlowLayout(FlowLayout.LEFT));
		JTextField noiseAttr=new JTextField(15);
		JButton addNoiseButton=new JButton("添加");
		p1.add(noiseAttr);
		p1.add(addNoiseButton);
		
		//第二行用于配置连续访问动作间最小时间间隔
		JPanel p2=new JPanel();	
		p2.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel minInternalLabel=new JLabel("连续访问动作间最小时间间隔(毫秒)");
		JTextField minInternalText=new JTextField(6);
		p2.add(minInternalLabel);
		p2.add(minInternalText);
		//第三行用于配置连续访问最长时间
		JPanel p3=new JPanel();
		p3.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel maxTimeLabel=new JLabel("连续访问最长时间(小时)");
		JTextField maxTimeText=new JTextField(6);
		p3.add(maxTimeLabel);
		p3.add(maxTimeText);
		
		chooseNoisePanel.add(p1);
		chooseNoisePanel.add(p2);
		chooseNoisePanel.add(p3);			
		
		/**噪声识别规则1中定义的正则表达式结果列表 */		
		/*DefaultListModel noiseResultModel=new DefaultListModel();		
		JList noiseResultList=new JList(noiseResultModel);*/
		JTextArea noiseResultArea=new JTextArea(31,22);
		JScrollPane scrollNoisePane=new JScrollPane(noiseResultArea);
		
		//这里将写入的正则表达式添加进来用noiseResultModel.addElement
		
		resultPanel2.add(scrollNoisePane);
		resultPanel2.setBorder(BorderFactory.createTitledBorder("噪音表达式结果"));
		
		noiseIdentifyPanel.add(chooseNoisePanel);
		noiseIdentifyPanel.add(resultPanel2);
		
	}

	//初始化活动识别面板
	private void initActivityIdentifyPanel() {
		activityIdentifyPanel.setBackground(Color.YELLOW);
		activityIdentifyPanel.setBorder(BorderFactory.createTitledBorder("活动识别规则"));
		
	}

	//初始化案例识别面板
	private void initCaseIdentifyPanel() {
		caseIdentifyPanel.setBackground(Color.GREEN);
		caseIdentifyPanel.setBorder(BorderFactory.createTitledBorder("案例识别规则"));
	}

	/**
	 * ����鼮
	 */
	private void addBook() {
		BookInfo addBookUI = new BookInfo();
		addBookUI.setVisible(true);

	}

	/**
	 * add each log record for test
	 * 
	 * @param logRecord
	 *            each log record
	 */
	public void addLog(ArrayList<Object> logRecord) {
		// if(tableModel.getRowCount()==0)
		// tableModel.addRow(logRecord.toArray());

		// tableModel.addRow(logRecord.toArray());

		System.out
				.print("\nthe size of the record in table" + logRecord.size());
		System.out.print("\n the content add to table:");
		for (int i = 0; i < logRecord.toArray().length; i++) {
			System.out.print("," + logRecord.toArray()[i]);
		}

		tableModel.addRow(logRecord.toArray());
		// ArrayList<String> tempList=new ArrayList<String>();
		// for(int i=0;i<5;i++){
		// tempList.add(logRecord.get(i).toString());}
		// String[] test={"test","test","test","test"};
		// //tableModel.addRow(test);

	}

	/**
	 * set up Table's head based on product
	 * 
	 * @param tableHead
	 *            name of columns
	 */
	public void setTableHead(ArrayList<Object> tableHead) {
		tableModel.setColumnIdentifiers(tableHead.toArray());

	}

	private void emptyTable() {
		for (int i = tableModel.getRowCount() - 1; i >= 0; i--) {
			tableModel.removeRow(i);
			// System.out.print("\nremove row " + i);
		}
	}

	/**
	 * choose to open new file
	 * 
	 */
	private void openFile() throws Exception {
		// chooser file
		JFileChooser chooser = new JFileChooser();
		// FileNameExtensionFilter filter = new FileNameExtensionFilter(
		// "arff Files", "arff");
		// chooser.setFileFilter(filter);
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setMultiSelectionEnabled(true);
		chooser.setCurrentDirectory(new File("D://"));
		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			// fileName = chooser.getSelectedFile().getName();
			// filePath = chooser.getSelectedFile().getPath();
			// chosenfile = chooser.getSelectedFile();
			// chosenfiles = chosenfiles[0];
			chosenfile = chooser.getSelectedFile();
			emptyTable();
			try {

				reader.readLog(chosenfile, this);

				// ArrayList<Object> test=new ArrayList<Object>();
				// test.add("test");
				// addLog(test);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		updateCaseIDList();

	}

	private void updateCaseIDList() {
		caseIDList = new JCheckBoxMenuItem[logList.get(0).getLogTagList()
				.size()];
		for (int i = 0; i < caseIDList.length; i++) {
			caseIDList[i] = new JCheckBoxMenuItem(logList.get(0)
					.getLogTagList().get(i));
			caseIDBox.add(caseIDList[i]);
		}
	}

	/**
	 * choose to apppend more files to existing table
	 * 
	 */
	private void addFile() throws Exception {
		// chooser file
		JFileChooser chooser = new JFileChooser();
		// FileNameExtensionFilter filter = new FileNameExtensionFilter(
		// "arff Files", "arff");
		// chooser.setFileFilter(filter);
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setMultiSelectionEnabled(true);

		int returnVal = chooser.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			// fileName = chooser.getSelectedFile().getName();
			// filePath = chooser.getSelectedFile().getPath();
			chosenfile = chooser.getSelectedFile();
			// chosenfiles = chooser.getSelectedFiles();
			// chosenfile=chosenfiles[0];
		}
		try {
			// reader.addLog(logList,chosenfile, this);
			reader.readLog(chosenfile, this);

			// ArrayList<Object> test=new ArrayList<Object>();
			// test.add("test");
			// addLog(test);

		} catch (Exception e) {
			e.printStackTrace();
		}
		updateCaseIDList();

	}

	/**
	 * save the file
	 */
	public void saveAs() {
		// chooser file
		JFileChooser chooser = new JFileChooser();
		// FileNameExtensionFilter filter = new FileNameExtensionFilter(
		// "arff Files", "arff");
		// chooser.setFileFilter(filter);
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setMultiSelectionEnabled(true);
		chooser.setCurrentDirectory(chosenfile.getAbsoluteFile());
		// chooser.setSelectedFile(new File(chosenfile.getName()));
		int returnVal = chooser.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			if (chooser.getSelectedFile() != null) {
				File savedFile = new File(chooser.getSelectedFile().getPath());

				System.out.print("\n file(has selevted)" + savedFile
						+ " has been saved");
			} else {
				File savedFile = new File(chooser.getCurrentDirectory(),
						chooser.getDialogTitle());
				System.out.print("\n file(no seected)" + savedFile
						+ " has been saved");
				// File savedFile = new
				// File(chooser.getCurrentDirectory()+chooser.get);
			}

		}
	}

	/**
	 * view more detailed inforamtion about logRecord
	 * 
	 * @param index
	 */
	private void viewLog(int index) {
		Log selectedLog = logList.get(index);
		JOptionPane.showMessageDialog(this,
				"\nlogName:" + selectedLog.getLogName() + "\nlogPath"
						+ selectedLog.getLogPath() + "\nlogContent"
						+ selectedLog.getLogContent());

		// System.out.print("\nlogName"+selectedLog.getLogName());
		// System.out.print("\nlogPath"+selectedLog.getLogPath());
		// System.out.print("\nlogContent"+selectedLog.getLogContent());

		// BookInfo addBookUI = new BookInfo();
		//
		// addBookUI.setbookInfo(selectedLog);
		// addBookUI.setVisible(true);

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
		reader.searchLog(searchKey, logList, this);
		// int[] location = new int[2];

	}

	/**
	 * �༭�鼮
	 */
	private void editBook(int index) {
		LogHead selectedBook = bookList.get(index);
		BookInfo addBookUI = new BookInfo();

		addBookUI.setbookInfo(selectedBook);
		addBookUI.setVisible(true);

	}

	/**
	 * �����鼮
	 */
	private void search() {
		String searchText = searchField.getText();
		int[] location = new int[2];
		for (int i = 0; i < bookList.size(); i++) {
			LogHead bookTemp = bookList.get(i);
			String[] infos = bookTemp.toArrays();
			for (int j = 0; j < infos.length; j++) {

				if (searchText.matches(infos[j])) {
				}
				location[0] = i;
				location[1] = j;
			}
		}
		this.setTitle("the book is at row " + location[0]);

	}

	/**
	 * ��ݼ�Ǯ����
	 */
	private void sortByPrice() {

		for (int i = 0; i < bookList.size(); i++) {
			for (int j = i + 1; j < bookList.size(); j++) {
				if (bookList.get(j).getPrice() < bookList.get(i).getPrice()) {
					LogHead bookTemp = bookList.get(i);
					bookList.set(i, bookList.get(j));
					bookList.set(j, bookTemp);
				}
			}
		}
		for (int i = bookList.size() - 1; i >= 0; i--) {
			tableModel.removeRow(i);
		}
		for (int i = 0; i < bookList.size(); i++) {
			tableModel.addRow(bookList.get(i).toArrays());
		}

	}

	/**
	 * ��ݳ��ʱ������
	 */
	private void sortByDate() {
		String timej;
		String[] timesJ;
		int[] timesJINT;
		String timei;
		String[] timesI;
		// Calendar dateJ = Calendar.getInstance();
		// Calendar dateI = Calendar.getInstance();
		Date dateJ;
		Date dateI;

		int[] timesIINT;
		for (int i = 0; i < bookList.size(); i++) {
			for (int j = i; j < bookList.size(); j++) {
				timej = bookList.get(j).getPublishDate();
				System.out.print("\ntimeJ:" + timej);
				timesJ = timej.split("/");
				print(timesJ);
				timesJINT = new int[3];

				timei = bookList.get(i).getPublishDate();
				System.out.print("\ntimeI:" + timei);
				timesI = timei.split("/");
				print(timesI);
				timesIINT = new int[3];
				for (int s = 0; s < 3; s++) {
					timesIINT[s] = Integer.parseInt(timesI[s]);
					timesJINT[s] = Integer.parseInt(timesJ[s]);
				}

				// dateJ.set( timesJINT[0], timesJINT[1], timesJINT[2]);
				// dateI.set( timesIINT[0], timesIINT[1], timesIINT[2]);
				dateJ = new Date(timesJINT[0], timesJINT[1] - 1, timesJINT[2]);
				dateI = new Date(timesIINT[0], timesIINT[1] - 1, timesIINT[2]);
				// System.out.print("\n year of dateJ is"+dateJ.get(Calendar.YEAR));
				// System.out.print("\n year of dateI is"+dateI.get(Calendar.YEAR));
				if (dateJ.compareTo(dateI) > 0) {
					System.out.print("\n" + dateJ + " is after " + dateI);
					LogHead bookTemp = bookList.get(i);
					bookList.set(i, bookList.get(j));
					bookList.set(j, bookTemp);
				}
			}
		}
		for (int i = bookList.size() - 1; i >= 0; i--) {
			tableModel.removeRow(i);
		}
		for (int i = 0; i < bookList.size(); i++) {
			tableModel.addRow(bookList.get(i).toArrays());
		}
	}

	public void print(String[] strings) {
		System.out.print("\nthe strings' size is:" + strings.length);
		for (int i = 0; i < strings.length; i++) {
			System.out.print("\t" + strings[i]);
		}
	}

	// �鼮��Ӻͱ༭�Ľ���
	private class BookInfo extends JFrame {
		JLabel bookIDLabel;
		JLabel nameLabel;
		JLabel typeLabel;
		JLabel authorLabel;
		JLabel priceLabel;
		JLabel publishDateLabel;
		JLabel publisherLabel;
		JLabel buyDateLabel;
		JLabel peopleLabel;
		JTextField bookIDField;
		JTextField nameField;
		JTextField typeField;
		JTextField authorField;
		JTextField priceField;
		JTextField publishDateField;
		JTextField publisherField;
		JTextField buyDateField;
		JTextField peopleField;

		JButton saveButotn;

		BookInfo() {
			Container container1 = getContentPane();
			this.setSize(120, 600);

			this.setLocation(700, 0);
			container1.setLayout(new GridLayout(11, 1));
			bookIDLabel = new JLabel("ID:");
			nameLabel = new JLabel("name:");
			typeLabel = new JLabel("type:");
			authorLabel = new JLabel("author:");
			priceLabel = new JLabel("price:");
			publishDateLabel = new JLabel("publishDate:");
			publisherLabel = new JLabel("publisher:");
			buyDateLabel = new JLabel("buyFate:");
			peopleLabel = new JLabel("people:");
			bookIDField = new JTextField("");
			nameField = new JTextField("");
			typeField = new JTextField("");
			authorField = new JTextField("");
			priceField = new JTextField("");
			publishDateField = new JTextField("");
			publisherField = new JTextField("");
			buyDateField = new JTextField("");
			peopleField = new JTextField("");

			bookIDField.setPreferredSize(new Dimension(100, 20));
			nameField.setPreferredSize(new Dimension(100, 20));
			typeField.setPreferredSize(new Dimension(100, 20));
			authorField.setPreferredSize(new Dimension(100, 20));
			priceField.setPreferredSize(new Dimension(100, 20));
			publishDateField.setPreferredSize(new Dimension(100, 20));
			publisherField.setPreferredSize(new Dimension(100, 20));
			buyDateField.setPreferredSize(new Dimension(100, 20));
			peopleField.setPreferredSize(new Dimension(100, 20));

			container1.add(bookIDLabel);
			container1.add(bookIDField);
			container1.add(nameLabel);

			container1.add(nameField);
			container1.add(typeLabel);
			container1.add(typeField);
			container1.add(authorLabel);
			container1.add(authorField);
			container1.add(priceLabel);
			container1.add(priceField);
			container1.add(publishDateLabel);
			container1.add(publishDateField);
			container1.add(publisherLabel);
			container1.add(publisherField);
			container1.add(buyDateLabel);
			container1.add(buyDateField);
			container1.add(peopleLabel);
			container1.add(peopleField);

			saveButotn = new JButton("save");
			saveButotn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					// TODO Auto-generated method stub
					double price = 0;
					if (priceField.getText() != null) {
						try {
							price = Double.parseDouble(priceField.getText());
						} catch (Exception e1) {
						}
					}
					LogHead newBook = new LogHead(bookIDField.getText(),
							nameField.getText(), typeField.getText(),
							authorField.getText(), price, publishDateField
									.getText(), publisherField.getText(),
							buyDateField.getText(), peopleField.getText());
					if (mode.matches("add")) {
						tableModel.addRow(newBook.toArrays());
						bookList.add(newBook);
					} else if (mode.matches("edit")) {
						String[] bookinfos = newBook.toArrays();
						for (int i = 0; i < bookinfos.length; i++) {
							tableModel.setValueAt(newBook.bookID, selectedRow,
									i);
						}
						bookList.set(selectedRow, newBook);

					}
				}
			});
			container1.add(saveButotn, BorderLayout.SOUTH);

		}

		public void setbookInfo(LogHead book) {
			bookIDField.setText(book.bookID);
			nameField.setText(book.name);
			typeField.setText(book.type);
			authorField.setText(book.author);
			priceField.setText(String.valueOf(book.price));
			publishDateField.setText(book.publisher);
			publisherField.setText(book.publisher);
			buyDateField.setText(book.buyDate);
			peopleField.setText(book.people);
		}

	}

	public void setContent(ArrayList<Log> newlogList) {
		emptyTable();
		// for(){}
		// TODO Auto-generated method stub
		// System.out.print("\nthe size of logList in UI:"+logList.size());
		// logList=new ArrayList<Log>();
		logList = newlogList;
		// ArrayList<Log> templogList = logList;
		for (int i = 0; i < logList.size(); i++) {
			Log log = logList.get(i);
			// System.out.print("\nog content at UI"
			// + log.getLogContent().toArray());
			tableModel.addRow(log.getLogContent().toArray());
		}

	}

	public void addContent(ArrayList<Log> newlogList) {

		// TODO Auto-generated method stub
		// System.out.print("\nthe size of logList in UI:"+logList.size());
		// logList=new ArrayList<Log>();
		logList.addAll(newlogList);
		// ArrayList<Log> templogList = logList;
		for (int i = 0; i < logList.size(); i++) {
			Log log = logList.get(i);
			System.out.print("\nog content at UI"
					+ log.getLogContent().toArray());
			tableModel.addRow(log.getLogContent().toArray());
		}

	}

	public ArrayList<Log> getContent() {
		// TODO Auto-generated method stub
		// System.out.print("\nthe size of logList in UI:"+logList.size());

		return logList;

	}

	// public int ifMerge() {
	// // TODO Auto-generated method stub
	// String question="different log format,merge files?";
	// int result=JOptionPane.showConfirmDialog(this, question);
	//
	// if(result==JOptionPane.YES_OPTION){
	// return 0;
	// }
	// else if(result==JOptionPane.NO_OPTION){
	// return 1;
	// }
	// else if(result==JOptionPane.CANCEL_OPTION){
	// return 2;
	// }
	// return 0;
	// }

}
