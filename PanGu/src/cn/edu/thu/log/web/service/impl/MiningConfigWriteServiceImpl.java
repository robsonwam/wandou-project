package cn.edu.thu.log.web.service.impl;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
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
import cn.edu.thu.log.preprocessrule.NoiseIdentifyRule;
import cn.edu.thu.log.web.service.MiningConfigWriteService;


/**
 * realize writing web config into mining config file
 * @author wan
 *
 */
public class MiningConfigWriteServiceImpl implements MiningConfigWriteService {

	private LogCleanRule logclean;
	private NoiseIdentifyRule noiseidentify;
	private ActivityIdentifyRule activityidentify;
	private CaseIdentifyRule caseidentify;
	
	private Map<String,String> alllogcleanrules;
	private Map<String,String> allcaseidentifyrules;	
	private Set<String> allnoiseidentifyrules;
	private Integer mininternalvalue=0;
	private Integer maxtimevalue=0;	
	private String timestamptag=null;
	private Set<String> allanalyzedproducts;
	private Map<String,String> allactivityidentifyrules;
	
	public MiningConfigWriteServiceImpl(LogCleanRule logcleanrule,
			NoiseIdentifyRule noiseidentifyrule,
			ActivityIdentifyRule activityidentifyrule,
			CaseIdentifyRule caseidentifyrule){
		this.logclean=logcleanrule;
		this.noiseidentify=noiseidentifyrule;
		this.activityidentify=activityidentifyrule;
		this.caseidentify=caseidentifyrule;
		
		alllogcleanrules=new HashMap<String,String>();
		allcaseidentifyrules=new HashMap<String,String>();
		allnoiseidentifyrules=new HashSet<String>();
		allanalyzedproducts=new HashSet<String>();
		allactivityidentifyrules=new HashMap<String,String>();
		alllogcleanrules=logclean.getAllLogCleanRules();		
	}
	
	@Override
	public void writeMiningConfig(String outfile) {
		alllogcleanrules=logclean.getAllLogCleanRules();
        allcaseidentifyrules=caseidentify.getAllCaseIdentifyRules();		
		allnoiseidentifyrules=noiseidentify.getAllNoiseIdentifyRules();
		mininternalvalue=noiseidentify.getMininternal();
		maxtimevalue=noiseidentify.getMaxtime();		
		timestamptag=activityidentify.getTimestamp();
		allanalyzedproducts=activityidentify.getAllAnalyzedProducts();
		allactivityidentifyrules=activityidentify.getAllActivityIdentifyRules();
		
		 // TODO Auto-generated method stub 
		 DocumentBuilderFactory docFactory=DocumentBuilderFactory.newInstance();
		 DocumentBuilder builder=null; 
		 try { 
			 builder = docFactory.newDocumentBuilder();
			 }
		 catch (ParserConfigurationException pce) {
			 System.err.println(pce);
			 System.exit(1); 
			 } 
		 Document doc = builder.newDocument(); //下面建立挖掘配置文件
		 //先建立根元素"miningconfig" 
		 Element root = doc.createElement("miningconfig"); 
		 doc.appendChild(root);
		 
		 /** 建立子元素logclean，日誌清洗規則 */		
		 Element logclean=doc.createElement("logclean");
		 //需要定义规则的字段logtag，属性是字段名和正则表达式
		 
		 Set tempset1=alllogcleanrules.entrySet();
		 Iterator logcleanit=tempset1.iterator();
		 while(logcleanit.hasNext()){ 
			 Element logtag=doc.createElement("logtag");
			 Map.Entry<String, String> entry1=(Map.Entry<String, String>) logcleanit.next();
			 logtag.setAttribute("tagname",entry1.getKey()); 
			 logtag.setAttribute("tagformat",entry1.getValue());
			 System.out.println("\n读取logclean配置： "+entry1.getKey()+entry1.getValue());
			 logclean.appendChild(logtag); 
		 }
	
		 /** 建立子元素noiseidentify，噪声识别规则 */		
		 Element noiseidentify=doc.createElement("noiseidentify");
		 //需要定义规则的noise，属性是定义的噪声串； 
		 		 
		 Iterator<String> noiseidentifyit=allnoiseidentifyrules.iterator();
		 while(noiseidentifyit.hasNext()){ 
			 Element noise=doc.createElement("noise");			 
			 noise.setAttribute("noiseformat",noiseidentifyit.next()); 			 
			 noiseidentify.appendChild(noise); 
			 System.out.println("\n读取noise配置： "+noiseidentifyit.next());
		 }		
		 //定义最小间隔时间mininternal
		 Element mininternal=doc.createElement("mininternal");
		 mininternal.setAttribute("mininternal", mininternalvalue.toString());
		 noiseidentify.appendChild(mininternal); 
		 System.out.println("\nnoise最小时间间隔： "+mininternalvalue);
		 //最长访问时间maxtime
		 Element maxtime=doc.createElement("maxtime"); 
		 maxtime.setAttribute("maxtime", maxtimevalue.toString());
		 noiseidentify.appendChild(maxtime);
		 System.out.println("\nnoise最长时间： "+maxtimevalue);
		 
		 /** 建立子元素activityidentify，活动识别规则 */		
		 Element activityidentify=doc.createElement("activityidentify");
		 //需要定义timestamp 
		 Element timestamp=doc.createElement("timestamp");
		 timestamp.setAttribute("timestamp", timestamptag); 
		 activityidentify.appendChild(timestamp);
		 System.out.println("\nactivity时间戳： "+timestamptag);
		 //需要定义活动，属性为字段名
		 
		 Set tempset3=allactivityidentifyrules.entrySet();
		 Iterator activityidentifyit=tempset3.iterator();
		 while(activityidentifyit.hasNext()){ 
			 Element activity=doc.createElement("activity");
			 Map.Entry<String, String> entry3=(Map.Entry<String, String>) activityidentifyit.next();
			 activity.setAttribute("activityname",entry3.getKey()); 
			 activity.setAttribute("activityformat",entry3.getValue());
			 activityidentify.appendChild(activity); 
			 System.out.println("\n读取activity配置： "+entry3.getKey()+entry3.getValue());
		 }		 
		 //需要选择要分析的产品 
		 Element product=null; 
		 Iterator<String> productsit=allanalyzedproducts.iterator();
		 while(productsit.hasNext()){
			 product=doc.createElement("product");	
			 product.setAttribute("productname",productsit.next());
			 activityidentify.appendChild(product); 
			 System.out.println("\n读取activity选取产品配置： "+productsit.next());
		 }
		 
		 /** 建立子元素caseidentify，案例识别规则 */		
		 Element caseidentify=doc.createElement("caseidentify");
		 //需要定义案例识别id，分主案例id和自定义案例id，互斥选择 
		 
		 Set tempset2=allcaseidentifyrules.entrySet();
		 Iterator caseidentifyit=tempset2.iterator();
		 while(caseidentifyit.hasNext()){ 
			 Element caseid=doc.createElement("case");
			 Map.Entry<String, String> entry2=(Map.Entry<String, String>) caseidentifyit.next();
			 caseid.setAttribute("type",entry2.getKey()); 
			 caseid.setAttribute("caseid",entry2.getValue());			
			 caseidentify.appendChild(caseid);
			 System.out.println("\n读取case配置： "+entry2.getKey()+entry2.getValue());
		 }				
		 
		 //添加到根节点
		 root.appendChild(logclean); 
		 root.appendChild(noiseidentify);
		 root.appendChild(activityidentify); 
		 root.appendChild(caseidentify);
		
		 try {
			//FileOutputStream fos = new FileOutputStream(outfile);
			
			 OutputStreamWriter outwriter = new OutputStreamWriter(new FileOutputStream(outfile));
			 BufferedWriter bw=new BufferedWriter(outwriter);
			// ((XmlDocument)doc).write(outwriter); //出错！
			callWirteMiningConfig(doc, bw);
			bw.close();
			//outwriter.close();
			//fos.close();
			//((XmlDocument)doc).write(new  FileOutputStream( "d:\\a.txt ")); 

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
}
