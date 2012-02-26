package cn.edu.thu.log.preprocessrule;

import java.util.HashSet;
import java.util.Set;

public class NoiseFormat{
	private String tagname=null;
	private Set<String> strList;
	
	public NoiseFormat(){
		strList=new HashSet<String>();
	}
	
	public String getTagname() {
		return tagname;
	}
	public void setTagname(String tagname) {
		this.tagname = tagname;
	}
	public Set<String> getStrList() {
		return strList;
	}
	public void setStrList(Set<String> strList) {
		this.strList = strList;
	}
	
}
