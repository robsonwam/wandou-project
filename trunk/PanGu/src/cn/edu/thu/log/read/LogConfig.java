package cn.edu.thu.log.read;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import cn.edu.thu.log.test.testUI;

/**
 * reader for configeration file,which contains functions that can read log tags
 * tags(head/body).
 * 
 * @author Meng
 * 
 */
public class LogConfig {
	/** list of log head tags */
	private ArrayList<String> logHead;
	/** list of log body tags */
	private ArrayList<String> logBody;
	/** list of log tags */
	private ArrayList<String> logTags;

	/** category of file,lie imageclick */
	private String fileCate;
	/** symbols in log */
	private String logHeadTokenizer;
	private String logBodyTokenizer;
	private String logHeadBodyTokenizer;
	private String escapeSymbol;
	private Document doc;
	private File configFile;
	private final String LOGHEAD_INDENTIFIER = "loghead";
	private final String LOGBODY_INDENTIFIER = "logbody";
	private final String ACTION_INDENTIFIER = "action";
	private final String PRODUCT_INDENTIFIER = "product";
	private final String LOGHEADTOKENIZER_NAME = "logHeadTokenizer";
	private final String LOGBODYTOKENIZER_NAME = "logBodyTokenizer";
	private final String LOGHEADBODYTOKENIZER_NAME = "logHeadBodyTokenizer";
	private final String ESCAPE_NAME = "escapeSymbol";
	private final String TOKENIZER_INDENTIFIER = "Tokenizer";

	/**
	 * Constructor
	 */
	public LogConfig() {
		logHead = new ArrayList<String>();
		logBody = new ArrayList<String>();
		logTags = new ArrayList<String>();
		fileCate = new String("");
		// f=new File(CONFIGFILE);
	}

	/**
	 * main function :read the confireration file and set up config attribute
	 * 
	 * @param configFilePath
	 *            configFile
	 */
	public void config(String configFilePath) {
		// TODO Auto-generated method stub
		try {
			configFile = new File(configFilePath);
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = (Document) builder.parse(configFile);

			logHead = new ArrayList<String>();
			logBody = new ArrayList<String>();
			logTags = new ArrayList<String>();

			// ArrayList<String> cateList = new ArrayList<String>();
			// LogFilesReader logFilesReader = new LogFilesReader();
			// cateList = logFilesReader.getCateList(readfile);

			logHead = readLogHead();
		//	System.out.print("\nlogHead:" + logHead);
			// logBody=readLogBody();
			logTags.addAll(logHead);
			logTags.addAll(logBody);
			// readTags("logHead");
			// if (cateList.size() > 1) {
			//
			// System.out.print("\ncate list" + cateList);
			// mergeConfig(cateList);
			//
			// } else {
			// String newfileCate = cateList.get(0);
			// readConfig(newfileCate);
			// }

			// read config to set up the log symbols
			readSymbols();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param configFilePath
	 *            configFile
	 * @param logFile
	 *            directory of File of log
	 */
	public void config(String configFilePath, String logFile) {
		// TODO Auto-generated method stub
		try {
			configFile = new File(configFilePath);
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = (Document) builder.parse(configFile);

			logHead = new ArrayList<String>();
			logBody = new ArrayList<String>();
			logTags = new ArrayList<String>();

			// ArrayList<String> cateList = new ArrayList<String>();
			// LogFilesReader logFilesReader = new LogFilesReader();
			// cateList = logFilesReader.getCateList(readfile);

			logHead = readLogHead();
			//System.out.print("\nlogHead:" + logHead);
			logBody = readLogBody(logFile);
			//System.out.print("\nlogBody:" + logBody);
			logTags.addAll(logHead);
			logTags.addAll(logBody);
			// readTags("logHead");
			// if (cateList.size() > 1) {
			//
			// System.out.print("\ncate list" + cateList);
			// mergeConfig(cateList);
			//
			// } else {
			// String newfileCate = cateList.get(0);
			// readConfig(newfileCate);
			// }
			// read config to set up the log symbols
			readSymbols();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void readSymbols() {
		// TODO Auto-generated method stub
		NodeList tokenList = doc.getElementsByTagName(TOKENIZER_INDENTIFIER);
		for (int i = 0; i < tokenList.getLength(); i++) {
			Node tokenizer = tokenList.item(i);
			String tokenName = tokenizer.getAttributes().getNamedItem("name")
					.getNodeValue();
			if (tokenName.matches(LOGHEADTOKENIZER_NAME)) {
				logHeadTokenizer = tokenizer.getAttributes().getNamedItem(
						"value").getNodeValue();
			} else if (tokenName.matches(LOGBODYTOKENIZER_NAME)) {
				logBodyTokenizer = tokenizer.getAttributes().getNamedItem(
						"value").getNodeValue();
			} else if (tokenName.matches(LOGHEADBODYTOKENIZER_NAME)) {
				logHeadBodyTokenizer = tokenizer.getAttributes().getNamedItem(
						"value").getNodeValue();
			} else if (tokenName.matches(ESCAPE_NAME)) {
				escapeSymbol = tokenizer.getAttributes().getNamedItem("value")
						.getNodeValue();
			}

		}

	}

	/**
	 * read log Head
	 * 
	 * @return Tags of logHead
	 */
	public ArrayList<String> readLogHead() {

		ArrayList<String> logheadTagList = readTags(LOGHEAD_INDENTIFIER, null);
		return logheadTagList;
	}

	/**
	 * read log file
	 * 
	 * @param logFile
	 *            director or file of log
	 * @return a list of merged tags from all action
	 */
	public ArrayList<String> readLogBody(String logFile) {

		LogFilesReader logFilesReader = new LogFilesReader();
		ArrayList<String> actionList = logFilesReader.getCateList(logFile);
		ArrayList<String> testLogBodyTagList = new ArrayList<String>();// for
		// test
		// List of tagList of each action(like imageclick)
		ArrayList<ArrayList<String>> tagsList = new ArrayList<ArrayList<String>>();

		ArrayList<String> tempTagList = new ArrayList<String>();
		tempTagList = readTags(ACTION_INDENTIFIER, actionList);
		tagsList.add(tempTagList);
		testLogBodyTagList.addAll(tempTagList);
		logBody = testLogBodyTagList;
	//	mergeLogBody(tagsList);

		return testLogBodyTagList;
	}
//!!!!!还不太清楚如果tag被merge了，那么记录怎么办
	private void mergeLogBody(ArrayList<ArrayList<String>> tagsList) {
	//	ArrayList<String> tag=new ArrayList
		
	}

	/**
	 * read the tags of the element that contains the element. like logHead,
	 * actionNode
	 * 
	 * @param containTagsElement
	 * @return the list of tagName
	 */
	public ArrayList<String> readTags(String nodeLevel,
			ArrayList<String> matchList) {

		ArrayList<String> tagList = new ArrayList<String>();
		// the list of foundNode in same level ,like action, loghead
		NodeList nodeList = doc.getElementsByTagName(nodeLevel);
		// System.out.print("\nin the level:" + nodeLevel
		// + " the number of nodes that can be found:"
		// + nodeList.getLength());
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			if (matchList == null) {
				// get childList
				NodeList tagNodeList = node.getChildNodes();
				for (int t = 0; t < tagNodeList.getLength(); t++) {
					// get childNode
					Node tagNode = tagNodeList.item(t);
					// read value of attribute "name"
					if (tagNode.hasAttributes()) {
						String tagName = tagNode.getAttributes().getNamedItem(
								"name").getNodeValue();
						tagList.add(tagName);
					}
				}

			} else {
				String name = null;
				if (node.hasAttributes()) {
					name = node.getAttributes().getNamedItem("name")
							.getNodeValue();
				}
				// System.out
				// .print("\n the size of matchList:" + matchList.size());
				for (int j = 0; j < matchList.size(); j++) {
					if (name.equals(matchList.get(j))) {
						// get childList
						NodeList tagNodeList = node.getChildNodes();
						for (int t = 0; t < tagNodeList.getLength(); t++) {
							// get childNode
							Node tagNode = tagNodeList.item(t);
							// read value of attribute "name"
							if (tagNode.hasAttributes()) {
								String tagName = tagNode.getAttributes()
										.getNamedItem("name").getNodeValue();
								tagList.add(tagName);
							}
						}
					}
				}
			}
		}
		return tagList;
	}

	/**
	 * 
	 * @param matchProductList
	 *            the list of products like List of image,page
	 * @return
	 */
	public ArrayList<String> readTagsByProducts(
			ArrayList<String> matchProductList) {
		ArrayList<String> tagList = new ArrayList<String>();
		NodeList productNodeList = doc
				.getElementsByTagName(PRODUCT_INDENTIFIER);
		for (int i = 0; i < productNodeList.getLength(); i++) {
			Node productNode = productNodeList.item(i);

			String productName = null;
			if (productNode.hasAttributes()) {
				productName = productNode.getAttributes().getNamedItem("name")
						.getNodeValue();
			}
			for (int j = 0; j < matchProductList.size(); j++) {
				if (productName.equals(matchProductList.get(j))) {
					// get childList:actionNodeList
					NodeList actionNodeList = productNode.getChildNodes();
					for (int t = 0; t < actionNodeList.getLength(); t++) {
						// get childNode:actionNoce
						Node actionNode = actionNodeList.item(t);
						//get tagNodeList
						NodeList tagNodeList = actionNode.getChildNodes();
						for (int p = 0; p < tagNodeList.getLength(); p++) {
							//get tagNode
							Node tagNode=tagNodeList.item(p);
							if (tagNode.hasAttributes()) {
								String tagName = tagNode.getAttributes()
										.getNamedItem("name").getNodeValue();
								tagList.add(tagName);
								
							}
						}
						// read value of attribute "name"
						
					}
				}
			}
		}

		return tagList;
	}

	public ArrayList<String> getLogBodyByCate(String action) {
		// TODO Auto-generated method stub
		ArrayList<String> matchList = new ArrayList<String>();
		matchList.add(action);
		ArrayList<String> resultList = new ArrayList<String>();
		resultList.addAll(readTags("action", matchList));
		return resultList;
	}

	/**
	 * get tags logHead
	 * 
	 * @return the list og tags of logHead
	 */
	public ArrayList<String> getLogHead() {
		return logHead;
	}

	// **get and set*/
	public void setLogHead(ArrayList<String> logHead) {
		this.logHead = logHead;
	}

	public ArrayList<String> getLogBody() {
		return logBody;
	}

	public void setLogBody(ArrayList<String> logBody) {
		this.logBody = logBody;
	}

	public ArrayList<String> getLogTags() {
		return logTags;
	}

	public void setLogTags(ArrayList<String> logTags) {
		this.logTags = logTags;
	}

	public String getLogHeadTokenizer() {
		return logHeadTokenizer;
	}

	public void setLogHeadTokenizer(String logHeadTokenizer) {
		this.logHeadTokenizer = logHeadTokenizer;
	}

	public String getLogBodyTokenizer() {
		return logBodyTokenizer;
	}

	public void setLogBodyTokenizer(String logBodyTokenizer) {
		this.logBodyTokenizer = logBodyTokenizer;
	}

	public String getLogHeadBodyTokenizer() {
		return logHeadBodyTokenizer;
	}

	public void setLogHeadBodyTokenizer(String logHeadBodyTokenizer) {
		this.logHeadBodyTokenizer = logHeadBodyTokenizer;
	}

	public String getEscapeSymbol() {
		return escapeSymbol;
	}

	public void setEscapeSymbol(String escapeSymbol) {
		this.escapeSymbol = escapeSymbol;
	}

	public String getFileCate() {
		return fileCate;
	}

	public void setFileCate(String fileCate) {
		this.fileCate = fileCate;
	}

}
