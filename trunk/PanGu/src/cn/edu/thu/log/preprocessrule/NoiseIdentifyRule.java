package cn.edu.thu.log.preprocessrule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * save the noise identify rule
 * 
 * @author wan
 * 
 */
public class NoiseIdentifyRule {
	// 存储定义的noisetagname，和其在noiseformatList对应的位置，后面是它的字段
	private String noise;
	private Integer mininternal = 300;
	private Integer maxtime = Integer.MAX_VALUE;
	private Map<String, NoiseFormat> noiseformatMap;
	private NoiseFormat temp;
    private ArrayList<String> noise_format;
	private String noisetagname=null;
	private String noiseformat=null;
	
	// private int i=0;//开始定义的tagname放在noisetagList的第0位；

	public NoiseIdentifyRule() {

		// noisestrrule=new HashMap<String,String>();
		// noisetagList=new HashMap<String,Integer>();
		temp = new NoiseFormat();
		noiseformatMap = new HashMap<String, NoiseFormat>();
		
	}

	public void addNoiseIdentifyRule(String tagname, String noisestr) {
		Set<String> str = new HashSet<String>();
		if (!noiseformatMap.containsKey(tagname)) {
			temp.setTagname(tagname);
			str.add(noisestr);
			temp.setStrList(str);			
		} 
		else {
			Iterator<Entry<String, NoiseFormat>> it = noiseformatMap.entrySet()
					.iterator();
			while (it.hasNext()) {
				Entry<String, NoiseFormat> entry = it.next();
				str.addAll(entry.getValue().getStrList());
				str.add(noisestr);
				temp.setTagname(tagname);
				temp.setStrList(str);
				//if(entry.getValue().getStrList().contains(noisestr))
					
			}
		}
		noiseformatMap.put(tagname, temp);
		this.noisetagname=tagname;
		this.noiseformat=noisestr;
		/*
		 * if(!noisetagList.containsKey(tagname)){ //插入tagname
		 * i=noiseformatList.size();//获得当前的结尾位置大小 noisetagList.put(tagname,i);
		 * noiseformatList.add(tagname); //插入noisestr
		 * noiseformatList.add(noisestr); } else{//如果存在这个字段
		 * i=noisetagList.get(tagname); int minj=0;//存储下一个tagname的位置
		 * //所有noisetagList中的位置向后错一位，改变noisetagList中每个key对应的值大小，要加1
		 * TreeSet<Integer> valueset=new TreeSet<Integer>();; Set<String>
		 * tagnameSet=noisetagList.keySet(); Iterator<String>
		 * it=tagnameSet.iterator(); while(it.hasNext()){ String name=it.next();
		 * int j=noisetagList.get(name); if(j>i){ valueset.add(j);
		 * noisetagList.put(name, j+1); } } minj=valueset.first();
		 * System.out.println("\nvalueset.first() is: "+valueset.first());
		 * 
		 * //判断新加入的值是否重复 for(int y=i;y<minj;y++) if()
		 * //将新加入的键规则直接放入noiseformatList中对应noisetag的后面
		 * noiseformatList.add(i+1,noisestr); } }
		 * 
		 * public Integer getMinIndexAfterI(String tagname){ int
		 * x=noisetagList.get(tagname);
		 */

	}
	
	public ArrayList<String> getAllNoiseFormat(){
		noise_format=new ArrayList<String>();
		noise_format.add(noisetagname);
		noise_format.add(noiseformat);	
		return noise_format;			
	}	

	public Map<String, NoiseFormat> getAllNoiseIdentifyRules() {
		return noiseformatMap;
	}

	public Integer getMininternal() {
		return mininternal;
	}

	public void setMininternal(Integer mininternal) {
		this.mininternal = mininternal;
	}

	public Integer getMaxtime() {
		return maxtime;
	}

	public void setMaxtime(Integer maxtime) {
		this.maxtime = maxtime;
	}

	public String getNoise() {
		return noise;
	}

	public void setNoise(String noise) {
		this.noise = noise;
	}

}
