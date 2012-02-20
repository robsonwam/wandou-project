package cn.edu.thu.log.read;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
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
	private ArrayList<Object> logHead;
	/** list of log body tags */
	private ArrayList<Object> logBody;
	/** list of log tags */
	private ArrayList<Object> logTags;
	/** configuration file:"config.xml" */
	private final String CONFIGFILE = "config.xml";
	/**category of file,lie imageclick*/
	private String fileCate;
	/** symbols in log */
	private String logHeadTokenizer;
	private String logBodyTokenizer;
	private String logHeadBodyTokenizer;
	private String escapeSymbol;

	/**
	 * Constructor
	 */
	public LogConfig() {
		logHead = new ArrayList<Object>();
		logBody = new ArrayList<Object>();
		logTags = new ArrayList<Object>();
		fileCate = new String("");
	}

	/**
	 * read the configuration file ,set up the tags for logHead and logBody
	 * 
	 * @param fileCate
	 *            : category of log,like imageclick,pagesearch
	 */
	private void readConfig(String newfileCate) {

		if (fileCate.matches(newfileCate)) {
			// when the file belong to same category,like imageSeach,then the
			System.out.print("\n belong to same category");
			// log tags does not change

		} else {// read the configuration file
			try {
				File f = new File(CONFIGFILE);
				DocumentBuilderFactory factory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = (Document) builder.parse(f);
				// read log head tags
				readLogHead(doc);
				// read log Body tags
				readLogBody(doc, newfileCate);
				// set up log tags
				logTags = new ArrayList<Object>();
				logTags.addAll(logHead);
				logTags.addAll(logBody);
				// System.out.print("\n log tags are:" + logTags);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		// update fileCate to newFileCate
		fileCate = newfileCate;

	}

	/**
	 * read the configuration file ,set up the tags for logHead and merge
	 * logBody from logFiles that come from different categories
	 * 
	 * @param fileCate
	 *            : category of log,like imageclick,pagesearch
	 */
	private void mergeConfig(ArrayList<String> newFileCateList) {

		try {
			File f = new File(CONFIGFILE);
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = (Document) builder.parse(f);
			// read log Head
			readLogHead(doc);
			// merge log Body
			// logBody = new ArrayList<Object>();

			mergeLogBody(doc, newFileCateList);

			// set up log tags
			logTags = new ArrayList<Object>();
			logTags.addAll(logHead);
			logTags.addAll(logBody);
			// System.out.print("\n log tags are:" + logTags);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * read log head
	 */
	private void readLogHead(Document doc) {
		NodeList logheadNodes = ((org.w3c.dom.Document) doc)
				.getElementsByTagName("logHead");
		logHead = new ArrayList<Object>();
		logHead.addAll(readAttributes(logheadNodes));
	}

	/**
	 * read log body
	 */
	private void readLogBody(Document doc, String newfileCate) {
		NodeList logBodyNodes = ((org.w3c.dom.Document) doc)
				.getElementsByTagName(newfileCate);
		logBody = new ArrayList<Object>();
		logBody.addAll(readAttributes(logBodyNodes));
		System.out.print("\n new log body tags are:" + logBody);
	}

	private void mergeLogBody(Document doc, ArrayList<String> newFileCateList) {
		logBody = new ArrayList<Object>();

		for (int s = 0; s < newFileCateList.size(); s++) {
			String newfileCate = newFileCateList.get(s);

			NodeList logBodyNodes = ((org.w3c.dom.Document) doc)
					.getElementsByTagName(newfileCate);
			ArrayList<Object> newLogBody = readAttributes(logBodyNodes);
			logBody.addAll(newLogBody);
		}
		System.out.print("\n merged log body tags are:" + logBody);
	}

	/**
	 * 
	 * @param attributes
	 * @return
	 */
	private ArrayList<Object> readAttributes(NodeList attributes) {

		ArrayList<Object> attributeList = new ArrayList<Object>();
		for (int i = 0; i < attributes.getLength(); i++) {
			Node logheadItem = attributes.item(i);
			NodeList logParams = logheadItem.getChildNodes();
			for (int j = 0; j < logParams.getLength(); j++) {
				Node temp = logParams.item(j);
				NodeList tempList = temp.getChildNodes();

				for (int s = 0; s < tempList.getLength(); s++) {
					// System.out.print("\n" + tempList.item(s).getNodeName());
					Object value = tempList.item(s).getNodeValue();
					// System.out.print(":" + value);
					attributeList.add(value);

				}
			}
		}
		return attributeList;
	}

	/**
	 * check if the new file and existed file belong to the same category
	 * 
	 * @param newfileCate
	 *            new category that the file belong to
	 * @return is belong to same category
	 */
	public boolean checkFileCate(String newfileCate) {
		boolean sameCate = true;
		if (fileCate == null)
			sameCate = true;
		else {
			if (!newfileCate.equals(fileCate)) {
				sameCate = false;
			}
		}
		return sameCate;

	}

	/**
	 * get tags logHead
	 * 
	 * @return the list og tags of logHead
	 */
	public ArrayList<Object> getLogHead() {
		try {
			File f = new File(CONFIGFILE);
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();

			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = (Document) builder.parse(f);
			readLogHead(doc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return logHead;
	}

	// **get and set*/
	public void setLogHead(ArrayList<Object> logHead) {
		this.logHead = logHead;
	}

	public ArrayList<Object> getLogBody() {
		return logBody;
	}

	public void setLogBody(ArrayList<Object> logBody) {
		this.logBody = logBody;
	}

	public ArrayList<Object> getLogTags() {
		return logTags;
	}

	public void setLogTags(ArrayList<Object> logTags) {
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

	/**
	 * main function :read the confireration file
	 * 
	 * @param readfile
	 *            absoluth path of chosen file
	 */
	public void config(File readfile) {
		// TODO Auto-generated method stub
		ArrayList<String> cateList = new ArrayList<String>();
		LogFilesReader logFilesReader = new LogFilesReader();
		cateList = logFilesReader.getCateList(readfile);

		if (cateList.size() > 1) {

			System.out.print("\ncate list" + cateList);
			mergeConfig(cateList);

		} else {
			String newfileCate = cateList.get(0);
			readConfig(newfileCate);
		}

		// read config to set up the log symbols
		readSymbols();

	}

	/**
	 * function to read the Symbols from config file
	 */
	private void readSymbols() {
		// try{
		logHeadTokenizer = getKeyValue("logHeadTokenizer");

		logBodyTokenizer = getKeyValue("logBodyTokenizer");
		logHeadBodyTokenizer = getKeyValue("logHeadBodyTokenizer");
		escapeSymbol = getKeyValue("escapeSymbol");
		System.out.print("\nlogHeadTokenizer:" + logHeadTokenizer);
		System.out.print("\nlogBodyTokenizer:" + logBodyTokenizer);
		System.out.print("\nlogHeadBodyTokenizer:" + logHeadBodyTokenizer);
		System.out.print("\nescapeSymbol:" + escapeSymbol);

		// NodeList logHeadTokenNodes = ((org.w3c.dom.Document) doc)
		// .getElementsByTagName("logHeadTokenizer");
		// logHeadTokenizer=logHeadTokenNodes.item(0).getNodeValue();
		// NodeList logBodyTokenNodes = ((org.w3c.dom.Document) doc)
		// .getElementsByTagName("logBodyTokenizer");
		// logBodyTokenizer=logBodyTokenNodes.item(0).getNodeValue();
		// NodeList logHeadBodyTokenNodes = ((org.w3c.dom.Document) doc)
		// .getElementsByTagName("logHeadBodyTokenizer");
		// logHeadBodyTokenizer=logHeadBodyTokenNodes.item(0).getNodeValue();
		// NodeList escapeSymbolNodes = ((org.w3c.dom.Document) doc)
		// .getElementsByTagName("escapeSymbol");
		// escapeSymbol=escapeSymbolNodes.item(0).getNodeValue();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

	}

	/**
	 * find the value of key in configuration file
	 * 
	 * @param key
	 *            key of configItem
	 * @return value of configItem
	 */
	private String getKeyValue(String key) {
		String value = null;
		File f = new File(CONFIGFILE);
		DocumentBuilderFactory factory;
		DocumentBuilder builder;
		Document doc;
		try {
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			doc = (Document) builder.parse(f);
			NodeList nodeList = ((org.w3c.dom.Document) doc)
					.getElementsByTagName(key);

			ArrayList<Object> attributeList = new ArrayList<Object>();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node logheadItem = nodeList.item(i);
				NodeList logParams = logheadItem.getChildNodes();
				// for (int j = 0; j < logParams.getLength(); j++) {
				value = logParams.item(0).getNodeValue();

				// Node temp = logParams.item(j);
				// NodeList tempList = temp.getChildNodes();
				//
				// for (int s = 0; s < tempList.getLength(); s++) {
				// // System.out.print("\n" +
				// tempList.item(s).getNodeName());
				// Object value = tempList.item(s).getNodeValue();
				// // System.out.print(":" + value);
				// attributeList.add(value);
				//
				// }
				// }
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * get the tags of logbody in one specific category
	 * 
	 * @param cate
	 *            category,like imageclick
	 * @return tags of logbody in this category
	 * 
	 */
	public ArrayList<Object> getLogBodyByCate(String cate) {
		// TODO Auto-generated method stub
		ArrayList<Object> logBodyByCate = new ArrayList<Object>();
		try {
			File f = new File(CONFIGFILE);
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = (Document) builder.parse(f);
			NodeList logBodyNodes = ((org.w3c.dom.Document) doc)
					.getElementsByTagName(cate);

			logBodyByCate.addAll(readAttributes(logBodyNodes));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return logBodyByCate;

	}
	/**
	 * get the tags of logbody in one specific product,
	 * 
	 * @param cate
	 *            category,like imageclick
	 * @return tags of logbody in this category
	 * 
	 */
	 public ArrayList<Object> getLogBodyByProducts(ArrayList<String> products)
	 {
	 // TODO Auto-generated method stub
	 ArrayList<Object> logBodByProduct = new ArrayList<Object>();
	
	 try {
	 File f = new File(CONFIGFILE);
	 DocumentBuilderFactory factory = DocumentBuilderFactory
	 .newInstance();
	 DocumentBuilder builder = factory.newDocumentBuilder();
	 Document doc = (Document) builder.parse(f);
	
	 for (int s = 0; s < products.size(); s++) {
	 String product = products.get(s);
	
	 NodeList cateList = ((org.w3c.dom.Document) doc)
	 .getElementsByTagName(product);
	
	 for (int i = 0; i < cateList.getLength(); i++) {
	 Node cate=cateList.item(i);
	 NodeList nodeList1= cate.getChildNodes();
	 ArrayList<Object> newLogBody = readAttributes(nodeList1);
	 logBodByProduct.addAll(newLogBody);
	 }
	 }
	
	 // for(int i=0;i<products.size();i++){
	 // logBodyNodes= ((org.w3c.dom.Document) doc)
	 // .getElementsByTagName(products.get(i));}
	 //
	 // logBodyByCate.addAll(readAttributes(logBodyNodes));
	
	 } catch (Exception e) {
	 e.printStackTrace();
	 }
	 return logBodByProduct;
	
	 }

}
