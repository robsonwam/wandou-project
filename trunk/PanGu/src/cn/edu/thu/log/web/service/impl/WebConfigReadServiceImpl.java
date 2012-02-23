package cn.edu.thu.log.web.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import cn.edu.thu.log.web.service.WebConfigReadService;

/**
 * realize reading user config from the web page
 * @author wan
 *
 */
public class WebConfigReadServiceImpl implements WebConfigReadService{
	private ArrayList<String> activityIDList;
	private ArrayList<String> caseIDList;
	private String timestamp=null;
	
	public WebConfigReadServiceImpl(){
		activityIDList=new ArrayList<String>();
		caseIDList=new ArrayList<String>();		
	}

	public void readWebConfig(String filename){
		DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
		DocumentBuilder builder=null;
		try {
			builder=factory.newDocumentBuilder();				
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();		
		}
		//解析文档
		Document doc=null;
		try {
			doc=builder.parse(new File(filename));
			
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//获得根节点miningconfig
		Element root=doc.getDocumentElement();
		System.out.println("root: "+root.getTagName());
		//NodeList configrule=root.getChildNodes();
		//System.out.println("rootlist:"+ root.getElementsByTagName("logtag"));
		//System.out.println("configrule length: "+ root.getElementsByTagName("logtag").getLength());
		//Element logclean=(Element) root.getElementsByTagName("logtag").item(0);
		//Element noiseidentify= (Element) root.getElementsByTagName("noiseidentify").item(0);
		Element activityidentify= (Element) root.getElementsByTagName("activityidentify").item(0);
		Element caseidentfy=(Element) root.getElementsByTagName("caseidentify").item(0);
		
		//NodeList cleanruleList=logclean.getChildNodes();
		//NodeList noiseruleList=noiseidentify.getChildNodes();
		NodeList activityruleList=activityidentify.getElementsByTagName("activity");
		NodeList timestampList=activityidentify.getElementsByTagName("timestamp");
		NodeList caseruleList=caseidentfy.getElementsByTagName("case");
		
	/*	for(int i=0;i<cleanruleList.getLength();i++){
			Element logtag=(Element) cleanruleList.item(i);
		}
		
		for(int i=0;i<noiseruleList.getLength();i++){
			Element noise=(Element) noiseruleList.item(i);
		}
		*/
		for(int i=0;i<activityruleList.getLength();i++){			
			Element temp=(Element) activityruleList.item(i);			
			activityIDList.add(temp.getAttribute("activityformat"));			
		}
		Element time=(Element) timestampList.item(0);
		timestamp=new String(time.getAttribute("timestamp"));			
		
		for(int i=0;i<caseruleList.getLength();i++){
			Element cases=(Element) caseruleList.item(i);
			caseIDList.add(cases.getAttribute("caseid"));
		}
	}
	
	@Override
	public ArrayList<String> getCaseIDList() {
		// TODO Auto-generated method stub
		return caseIDList;
	}

	@Override
	public ArrayList<String> getActivityIDList() {
		// TODO Auto-generated method stub
		return activityIDList;
	}

	@Override
	public String getTimeStamp() {
		// TODO Auto-generated method stub
		return timestamp;
	}

	@Override
	public Map<String, String> getFormatActivityIDList() {
		// TODO Auto-generated method stub
		return null;
	}	
}
