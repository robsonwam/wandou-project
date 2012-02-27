package cn.edu.thu.log.web.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cn.edu.thu.log.preprocessrule.NoiseFormat;
import cn.edu.thu.log.web.service.WebConfigReadService;

/**
 * realize reading user config from the web page
 * @author wan
 *
 */
public class WebConfigReadServiceImpl implements WebConfigReadService{
	private Map<String,String> logCleanList;
	private Map<String,NoiseFormat> noisestringList;
	private String min=null;
	private String max=null;
	private ArrayList<String> activityIDList;
	private String timestamp=null;
	private ArrayList<String> productList;	
	private ArrayList<String> caseIDList;
	private NoiseFormat temp;
	private String miningconfigfilename=null;
	
	public WebConfigReadServiceImpl(){
		activityIDList=new ArrayList<String>();
		caseIDList=new ArrayList<String>();		
		productList=new ArrayList<String>();
		logCleanList=new HashMap<String,String>();
		noisestringList=new HashMap<String,NoiseFormat>();
		temp=new NoiseFormat();
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
		Element logclean=(Element) root.getElementsByTagName("logclean").item(0);
		Element noiseidentify= (Element) root.getElementsByTagName("noiseidentify").item(0);
		Element activityidentify= (Element) root.getElementsByTagName("activityidentify").item(0);
		Element caseidentfy=(Element) root.getElementsByTagName("caseidentify").item(0);
		
		NodeList cleanruleList=logclean.getElementsByTagName("logtag");
		NodeList noisestrList=noiseidentify.getElementsByTagName("noise");
		NodeList minList=noiseidentify.getElementsByTagName("mininternal");
		NodeList maxList=noiseidentify.getElementsByTagName("maxtime");
		NodeList activityruleList=activityidentify.getElementsByTagName("activity");
		NodeList timestampList=activityidentify.getElementsByTagName("timestamp");
		NodeList proList=activityidentify.getElementsByTagName("product");
		NodeList caseruleList=caseidentfy.getElementsByTagName("case");
		
	
		for(int i=0;i<cleanruleList.getLength();i++){
			Element tag =(Element) cleanruleList.item(i);
			logCleanList.put(tag.getAttribute("tagname"),tag.getAttribute("tagformat"));
			System.out.println("tagname="+tag.getAttribute("tagname")+" tagformat="+tag.getAttribute("tagformat"));
		}			
		
		for(int i=0;i<noisestrList.getLength();i++){			
			Element no=(Element) noisestrList.item(i);	
			String tagname=no.getAttribute("noisetag");
			String noiseformat=no.getAttribute("noiseformat");
			Set<String> strList=new HashSet<String>();
			
			if(!noisestringList.containsKey(tagname)){
				strList.add(noiseformat);
				temp.setTagname(tagname);
				temp.setStrList(strList);				
			}
			else{
				Iterator<Entry<String, NoiseFormat>> it=noisestringList.entrySet().iterator();
				while(it.hasNext()){
					Entry<String, NoiseFormat> entry=it.next();
					strList.addAll(entry.getValue().getStrList());
					strList.add(noiseformat);
					temp.setTagname(tagname);
					temp.setStrList(strList);
				}
			}				
			noisestringList.put(tagname, temp);					
		}		
		
		Element mi=(Element) minList.item(0);
		min=new String(mi.getAttribute("mininternal"));	
		
		Element ma=(Element) maxList.item(0);
		max=new String(ma.getAttribute("maxtime"));	
		
		for(int i=0;i<activityruleList.getLength();i++){			
			Element temp=(Element) activityruleList.item(i);			
			activityIDList.add(temp.getAttribute("activityname"));			
		}		
		
		Element time=(Element) timestampList.item(0);
		timestamp=new String(time.getAttribute("timestamp"));			
		
		for(int i=0;i<proList.getLength();i++){
			Element products=(Element) proList.item(i);
			productList.add(products.getAttribute("productname"));
		}
		
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

	@Override
	public ArrayList<String> getProductsList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getLogCleanList() {
		// TODO Auto-generated method stub
		return logCleanList;
	}

	@Override
	public Map<String,NoiseFormat> getNoiseStringList() {
		// TODO Auto-generated method stub
		return noisestringList;
	}

	@Override
	public String getMin() {
		// TODO Auto-generated method stub
		return min;
	}

	@Override
	public String getMax() {
		// TODO Auto-generated method stub
		return max;
	}

	public String getMiningconfigfilename() {
		return miningconfigfilename;
	}	
	
}
