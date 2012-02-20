package cn.edu.thu.log.web.service;
import java.io.Writer;

import org.w3c.dom.Document;

/**
 * write web config into mining config file
 * @author wan
 *
 */
public interface MiningConfigWriteService {
	
	//读取界面的用户配置，写成doc形式
	public void writeMiningConfig(String outfile);
	
	//将doc配置文件写入xml中
	public void callWirteMiningConfig(Document doc, Writer writer);
}
