package cn.edu.thu.log.web.service.impl;

import cn.edu.thu.log.read.LogBuffer;
import cn.edu.thu.log.web.service.WebConfigReadService;
import cn.edu.thu.log.web.service.XESConvertService;
import cn.edu.thu.log.xes.XESWriterFile;
import cn.edu.thu.log.xes.XEZWriter;


/**
 * implementation of service XES Convert.
 * @author meng
 *
 */
public class XESConvertServiceImp implements XESConvertService{
	@Override
	public void convert(String readFilePath,String resultFile) {
		WebConfigReadService configRead = new WebConfigReadServiceImpl();
		configRead.readWebConfig("miningconfig1.xml");
		//String readFilePath = new String("D:/imageclick_file/imageclick");
		//String resultFile = "logXes.xml";
		XESWriterFile writer = new XESWriterFile(configRead, readFilePath);
		writer.write(resultFile);
	}
	public LogBuffer getLogBuffer (String readFilePath) {
		WebConfigReadService configRead = new WebConfigReadServiceImpl();
		configRead.readWebConfig("miningconfig1.xml");
		System.out.println("readweb成功");
		//String readFilePath = new String("D:/imageclick_file/imageclick");
		//String resultFile = "logXes.xml";
		XESWriterFile writer = new XESWriterFile(configRead, readFilePath);
		writer.write("testXES2.xml");
		LogBuffer logBuffer=writer.getTestLogBuffer();
		return logBuffer;
	}
}
