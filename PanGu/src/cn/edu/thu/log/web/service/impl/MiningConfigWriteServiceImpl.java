package cn.edu.thu.log.web.service.impl;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.thu.log.web.service.MiningConfigWriteService;

/**
 * realize writing web config into mining config file
 * @author wan
 *
 */
public class MiningConfigWriteServiceImpl implements MiningConfigWriteService {

	@Override
	public void writeMiningConfig(String outfile) {
		
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
		 Element logtag=null; 
		 //for(int i=0;i<logcleanlength;i++){ 
			 logtag=doc.createElement("logtag");
			 logtag.setAttribute("字段名",""); 
			 logtag.setAttribute("格式规则", "");
			 logclean.appendChild(logtag); 
		 //}
		
		 /** 建立子元素noiseidentify，噪声识别规则 */		
		 Element noiseidentify=doc.createElement("noiseidentify");
		 //需要定义规则的noise，属性是定义的噪声串； 
		 Element noise=null; 
		 //for(int i=0;i<noiseidentifylengh;i++){
			 noise=doc.createElement("noise");
			 noise.setAttribute("噪声串", ""); 
			 noiseidentify.appendChild(noise); 
		 //}
		 //定义最小间隔时间mininternal
		 Element mininternal=doc.createElement("mininternal");
		 mininternal.setAttribute("最短间隔", "");
		 noiseidentify.appendChild(mininternal); 
		 //最长访问时间maxtime
		 Element maxtime=doc.createElement("maxtime"); 
		 maxtime.setAttribute("最长时间", "");
		 noiseidentify.appendChild(maxtime);
		 
		 /** 建立子元素activityidentify，活动识别规则 */		
		 Element activityidentify=doc.createElement("activityidentify");
		 //需要定义timestamp 
		 Element timestamp=doc.createElement("timestamp");
		 timestamp.setAttribute("时间戳", ""); 
		 //需要定义活动，属性为字段名
		 Element activity=null; 
		 //for(int i=0;i<activitylength;i++){
			 activity.setAttribute("活动字段名", "");
			 activity.setAttribute("活动子串规则", ""); 
			 activityidentify.appendChild(activity);
		//} 
		 //需要选择要分析的产品 
		 Element product=null; 
		 //for(int i=0;i<productlength;i++){
			 product.setAttribute("产品名称", "");
			 activityidentify.appendChild(product); 
		 //}
		 
		 /** 建立子元素caseidentify，案例识别规则 */		
		 Element caseidentify=doc.createElement("caseidentify");
		 //需要定义案例识别id，分主案例id和自定义案例id，互斥选择 
		 Element
		 caseid=doc.createElement("caseid"); caseid.setAttribute("id类型", "");
		 caseid.setAttribute("案例id", "  +   +的形式");//或者再 建立子元素？
		 caseidentify.appendChild(caseid);
		 
		 //添加到根节点 root.appendChild(logclean); root.appendChild(noiseidentify);
		 root.appendChild(activityidentify); root.appendChild(caseidentify);
		
		 try {
			FileOutputStream fos = new FileOutputStream(outfile);
			OutputStreamWriter outwriter = new OutputStreamWriter(fos);
			// ((XmlDocument)doc).write(outwriter); //出错！
			callWirteMiningConfig(doc, outwriter);
			outwriter.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void callWirteMiningConfig(Document doc, Writer writer) {
		// TODO Auto-generated method stub
		try {
			Source source = new DOMSource(doc);
			Result result = new StreamResult(writer);
			Transformer xformer = TransformerFactory.newInstance()
					.newTransformer();
			// xformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			xformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
}
