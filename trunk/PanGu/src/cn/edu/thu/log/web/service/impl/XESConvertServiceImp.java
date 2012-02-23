package cn.edu.thu.log.web.service.impl;

import cn.edu.thu.log.web.service.WebConfigReadService;
import cn.edu.thu.log.xes.XESWriter;

public class XESConvertServiceImp {
	public void convert(String readFilePath,String resultFile) {
		WebConfigReadService configRead = new WebConfigReadServiceImpl();
		configRead.readWebConfig("miningconfig2.xml");
		//String readFilePath = new String("D:/imageclick_file/imageclick");
		//String resultFile = "logXes.xml";
		XESWriter writer = new XESWriter(configRead, readFilePath);
		writer.write(resultFile);
	}
}
