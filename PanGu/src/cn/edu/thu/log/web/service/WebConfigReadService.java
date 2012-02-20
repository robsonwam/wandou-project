package cn.edu.thu.log.web.service;

import java.util.ArrayList;

import cn.edu.thu.log.miningconfig.MiningConfigFile;
import cn.edu.thu.log.test.testUI;

/**
 * read user config form the web page
 * @author wan
 *
 */
public interface WebConfigReadService {
	
	public ArrayList<MiningConfigFile> readWebConfig();
	
}
