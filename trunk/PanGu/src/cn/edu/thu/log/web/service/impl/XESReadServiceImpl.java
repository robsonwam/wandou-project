package cn.edu.thu.log.web.service.impl;

import org.deckfour.xes.model.XLog;

import cn.edu.thu.log.web.service.XESReadService;
import cn.edu.thu.log.xes.XESFileReader;

public class XESReadServiceImpl implements XESReadService {

	public XESFileReader xesFileReader;
	
	public XESReadServiceImpl(String sourcePath, String name) {
		xesFileReader = new XESFileReader();
		xesFileReader.setUpXESPara(sourcePath, name);
	}
	@Override
	public boolean hasNext() {
		return xesFileReader.hadNext();
	}
	@Override
	public XLog next() {
		return xesFileReader.Next();
	}
}
