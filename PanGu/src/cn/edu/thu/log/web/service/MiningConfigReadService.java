package cn.edu.thu.log.web.service;

import java.util.ArrayList;

import cn.edu.thu.log.miningconfig.MiningConfigFile;
import cn.edu.thu.log.test.testUI;
/**
 * read web page and save it
 * @author wan
 *
 */
public interface MiningConfigReadService {
	public ArrayList<MiningConfigFile> readWebConfig();
}
