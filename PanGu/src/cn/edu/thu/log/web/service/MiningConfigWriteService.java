package cn.edu.thu.log.web.service;

import org.w3c.dom.Document;

/**
 * write web config into mining config file
 * @author wan
 *
 */
public interface MiningConfigWriteService {
	
	public Document writeMiningConfig();
}
