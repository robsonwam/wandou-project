package cn.edu.thu.log.web.service;
/**
 * interface of service XES Convert
 * @author meng
 *
 */
public interface XESConvertService {
	/**
	 * convert logfile to xesfile
	 * @param readFilePath logFile
	 * @param resultFile result xes file
	 */
	public void convert(String readFilePath,String resultFile) ;
}
