package cn.edu.thu.log.preprocessrule;

import java.util.ArrayList;
import java.util.Vector;

import cn.edu.thu.log.web.service.impl.LogReadServiceImpl;

/**
 * read the user's configuration once and save the tags 
 * which are used later for anylyzing 
 * @author wan
 *
 */

public class AnalyzedTags {
	private Vector<String> analyzedtags;
	private LogReadServiceImpl logread;
	public AnalyzedTags() {
		logread=new LogReadServiceImpl();		
	}

	public Vector<String> getAnalyzedtags() {
		return analyzedtags;
	}

	public void setAnalyzedtags(ArrayList<String> productList) {
		//ArrayList<Object> tagList=logread.getLogTagsByProducts(productList);
		//ListIterator iter=
		
	}
}
