package cn.edu.thu.log.web.service.impl;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.thu.log.preprocessrule.ActivityIdentifyRule;
import cn.edu.thu.log.preprocessrule.CaseIdentifyRule;
import cn.edu.thu.log.preprocessrule.LogCleanRule;
import cn.edu.thu.log.preprocessrule.NoiseFormat;
import cn.edu.thu.log.preprocessrule.NoiseIdentifyRule;
import cn.edu.thu.log.web.service.MiningConfigUIService;

/**
 * realize the mimingconfigUIService to save the user config in UI
 * 
 * @author wan
 * 
 */
public class MiningConfigUIServiceImpl implements MiningConfigUIService {
	LogCleanRule cleanrule;
	NoiseIdentifyRule noiserule;
	ActivityIdentifyRule activityrule;
	CaseIdentifyRule caserule;

	private ArrayList<Map<String, String>> logCleanRules = null;
	private ArrayList<Map<String, String>> activityIdentifyRules = null;

	public MiningConfigUIServiceImpl() {
		cleanrule = new LogCleanRule();
		noiserule = new NoiseIdentifyRule();
		activityrule = new ActivityIdentifyRule();
		caserule = new CaseIdentifyRule();

		logCleanRules = new ArrayList<Map<String, String>>();
		activityIdentifyRules = new ArrayList<Map<String, String>>();
	}

	/** 日志清洗规则服务 */
	@Override
	public void addLogCleanRule(String tagname, String expression) {
		// TODO Auto-generated method stub
		cleanrule.addLogCleanRule(tagname, expression);
	}

	@Override
	public Map<String, String> getallLogCleanRules() {
		// TODO Auto-generated method stub
		return cleanrule.getAllLogCleanRules();
	}

	public ArrayList<String> getTagFormat() {
		return cleanrule.getTagFormat();
	}

	/** 噪声识别规则服务 */
	@Override
	public void addNoiseIdentifyRule(String noisetag, String noisestr) {
		// TODO Auto-generated method stub
		noiserule.addNoiseIdentifyRule(noisetag, noisestr);
	}

	public String getNoiseIdentifyRule() {
		return noiserule.getNoise();
	}
	
	public ArrayList<String> getNoiseFormat(){
		return noiserule.getAllNoiseFormat();
	}
	
	@Override
	public Integer getMinInternal() {
		// TODO Auto-generated method stub
		return noiserule.getMininternal();
	}

	@Override
	public Integer getMaxTime() {
		// TODO Auto-generated method stub
		return noiserule.getMaxtime();
	}

	@Override
	public Map<String, NoiseFormat> getAllNoiseIdentifyRules() {
		// TODO Auto-generated method stub
		return noiserule.getAllNoiseIdentifyRules();
	}

	@Override
	public void setMinIternal(String min) {
		// TODO Auto-generated method stub
		noiserule.setMininternal(Integer.parseInt(min));
	}

	@Override
	public void setMaxTime(String max) {
		// TODO Auto-generated method stub
		noiserule.setMaxtime(Integer.parseInt(max));
	}

	/** 活动识别规则服务 */
	@Override
	public void addAnalyzedProduct(String productname) {
		// TODO Auto-generated method stub
		activityrule.addAnalyzedProduct(productname);
	}

	@Override
	public Set<String> getAllAnalyzedProducts() {
		return activityrule.getAllAnalyzedProducts();
	}

	@Override
	public void addActivityIdentifyRule(String activityname,
			String actexpression) {
		// TODO Auto-generated method stub
		activityrule.addActivityIdentifyRule(activityname, actexpression);
	}

	@Override
	public Map<String, String> getAllActivityIdentifyRules() {
		// TODO Auto-generated method stub
		return activityrule.getAllActivityIdentifyRules();
	}

	@Override
	public ArrayList<String> getActivityFormat() {
		return activityrule.getAcitivityFormat();
	}

	@Override
	public String getTimeStamp() {
		// TODO Auto-generated method stub
		return activityrule.getTimestamp();
	}

	@Override
	public void setTimeStamp(String timestamp) {
		// TODO Auto-generated method stub
		activityrule.setTimestamp(timestamp);
	}

	/** 案例识别规则服务 */
	@Override
	public void addCaseIdentifyRule(String caseidentifyrule, String type) {
		// TODO Auto-generated method stub
		caserule.addCaseIdentifyRules(caseidentifyrule, type);
	}

	@Override
	public Map<String, String> getAllCaseIdentifyRules() {
		// TODO Auto-generated method stub
		return caserule.getAllCaseIdentifyRules();
	}

	@Override
	public void clearCaseIdentifyRules() {
		// TODO Auto-generated method stub
		caserule.clearCaseIdentifyRules();
	}

	@Override
	public void writeMiningConfig(String outfile) {
		// TODO Auto-generated method stub
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			System.err.println(pce);
			System.exit(1);
		}
		Document doc = builder.newDocument(); // 下面建立挖掘配置文件
		// 先建立根元素"miningconfig"
		Element root = doc.createElement("miningconfig");
		doc.appendChild(root);

		/** 建立子元素logclean，日誌清洗規則 */
		Element logclean = doc.createElement("logclean");
		// 需要定义规则的字段logtag，属性是字段名和正则表达式

		Set tempset1 = cleanrule.getAllLogCleanRules().entrySet();
		Iterator logcleanit = tempset1.iterator();
		while (logcleanit.hasNext()) {
			Element logtag = doc.createElement("logtag");
			Map.Entry<String, String> entry1 = (Map.Entry<String, String>) logcleanit
					.next();
			logtag.setAttribute("tagname", entry1.getKey());
			logtag.setAttribute("tagformat", entry1.getValue());
			System.out.println("\n读取logclean配置： " + entry1.getKey()
					+ entry1.getValue());
			logclean.appendChild(logtag);
		}

		/** 建立子元素noiseidentify，噪声识别规则 */
		Element noiseidentify = doc.createElement("noiseidentify");
		// 需要定义规则的noise，属性是定义的噪声串；

		Iterator<Entry<String, NoiseFormat>> noiseidentifyit = noiserule
				.getAllNoiseIdentifyRules().entrySet().iterator();
		while (noiseidentifyit.hasNext()) {
			Entry<String, NoiseFormat> entry = noiseidentifyit.next();
			String tagname = entry.getKey();
			ArrayList<String> strList = new ArrayList<String>(entry.getValue()
					.getStrList());
			Iterator<String> it = strList.iterator();
			while (it.hasNext()) {
				Element noise = doc.createElement("noise");
				noise.setAttribute("noisetag", tagname);
				System.out.println("\n读取noise字段： " + tagname);
				String str = it.next();
				noise.setAttribute("noiseformat", str);
				noiseidentify.appendChild(noise);
				System.out.println("\n读取noise格式： " + str);
			}

		}
		
		// 定义最小间隔时间mininternal
		Element mininternal = doc.createElement("mininternal");
		mininternal.setAttribute("mininternal", noiserule.getMininternal()
				.toString());
		noiseidentify.appendChild(mininternal);
		System.out.println("\nnoise最小时间间隔： " + noiserule.getMininternal());
		// 最长访问时间maxtime
		Element maxtime = doc.createElement("maxtime");
		maxtime.setAttribute("maxtime", noiserule.getMaxtime().toString());
		noiseidentify.appendChild(maxtime);
		System.out.println("\nnoise最长时间： " + noiserule.getMaxtime());

		/** 建立子元素activityidentify，活动识别规则 */
		Element activityidentify = doc.createElement("activityidentify");
		// 需要定义timestamp
		Element timestamp = doc.createElement("timestamp");
		timestamp.setAttribute("timestamp", activityrule.getTimestamp());
		activityidentify.appendChild(timestamp);
		System.out.println("\nactivity时间戳： " + activityrule.getTimestamp());
		// 需要定义活动，属性为字段名

		Set tempset3 = activityrule.getAllActivityIdentifyRules().entrySet();
		Iterator activityidentifyit = tempset3.iterator();
		while (activityidentifyit.hasNext()) {
			Element activity = doc.createElement("activity");
			Map.Entry<String, String> entry3 = (Map.Entry<String, String>) activityidentifyit
					.next();
			activity.setAttribute("activityname", entry3.getKey());
			activity.setAttribute("activityformat", entry3.getValue());
			activityidentify.appendChild(activity);
			System.out.println("\n读取activity配置： " + entry3.getKey()
					+ entry3.getValue());
		}
		// 需要选择要分析的产品
		Element product = null;
		Iterator<String> productsit = activityrule.getAllAnalyzedProducts()
				.iterator();
		while (productsit.hasNext()) {
			product = doc.createElement("product");
			String productStr = productsit.next();
			product.setAttribute("productname", productStr);
			activityidentify.appendChild(product);
			System.out.println("\n读取activity选取产品配置： " + productStr);
		}

		/** 建立子元素caseidentify，案例识别规则 */
		Element caseidentify = doc.createElement("caseidentify");
		// 需要定义案例识别id，分主案例id和自定义案例id，互斥选择

		Set tempset2 = caserule.getAllCaseIdentifyRules().entrySet();
		Iterator caseidentifyit = tempset2.iterator();
		while (caseidentifyit.hasNext()) {
			Element caseid = doc.createElement("case");
			Map.Entry<String, String> entry2 = (Map.Entry<String, String>) caseidentifyit
					.next();
			caseid.setAttribute("caseid", entry2.getKey());
			caseid.setAttribute("type", entry2.getValue());
			caseidentify.appendChild(caseid);
			System.out.println("\n读取case配置： " + entry2.getKey()
					+ entry2.getValue());
		}

		// 添加到根节点
		root.appendChild(logclean);
		root.appendChild(noiseidentify);
		root.appendChild(activityidentify);
		root.appendChild(caseidentify);

		try {
			// FileOutputStream fos = new FileOutputStream(outfile);

			OutputStreamWriter outwriter = new OutputStreamWriter(
					new FileOutputStream(outfile));
			BufferedWriter bw = new BufferedWriter(outwriter);
			// ((XmlDocument)doc).write(outwriter); //出错！
			callWirteMiningConfig(doc, bw);
			bw.close();
			// outwriter.close();
			// fos.close();
			// ((XmlDocument)doc).write(new FileOutputStream( "d:\\a.txt "));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void callWirteMiningConfig(Document doc, Writer writer) {
		// TODO Auto-generated method stub
		try {
			DOMSource source = new DOMSource(doc);
			Result result = new StreamResult(writer);
			Transformer xformer = TransformerFactory.newInstance()
					.newTransformer();
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");// 设置自动换行
			xformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			xformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	public LogCleanRule getCleanrule() {
		return cleanrule;
	}

	public void setCleanrule(LogCleanRule cleanrule) {
		this.cleanrule = cleanrule;
	}

	public NoiseIdentifyRule getNoiserule() {
		return noiserule;
	}

	public void setNoiserule(NoiseIdentifyRule noiserule) {
		this.noiserule = noiserule;
	}

	public ActivityIdentifyRule getActivityrule() {
		return activityrule;
	}

	public void setActivityrule(ActivityIdentifyRule activityrule) {
		this.activityrule = activityrule;
	}

	public CaseIdentifyRule getCaserule() {
		return caserule;
	}

	public void setCaserule(CaseIdentifyRule caserule) {
		this.caserule = caserule;
	}

}
