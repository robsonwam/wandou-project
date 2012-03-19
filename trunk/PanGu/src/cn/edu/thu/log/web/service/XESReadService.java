package cn.edu.thu.log.web.service;

import org.deckfour.xes.model.XLog;

import cn.edu.thu.log.xes.XESFileReader;

public interface XESReadService {
	public XESFileReader xesFileReader=null;
	public boolean hasNext() ;

	public XLog next() ;
	
}
