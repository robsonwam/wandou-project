package cn.edu.thu.log.web.service;

import java.util.ArrayList;

import cn.edu.thu.log.preprocessrule.LogCleanRule;
import cn.edu.thu.log.test.testUI;

/**
 * read user config from the web page
 * @author wan
 *
 */
public interface WebConfigReadService {
	
	public ArrayList<LogCleanRule> readWebConfig();
	
}
