package cn.edu.thu.log.read;

import java.io.File;
import java.util.ArrayList;

/**
 * Reader for log files
 * 
 * @author Meng
 * 
 */
public class LogFilesReader {
	/**
	 *list of category
	 */
	ArrayList<String> cateList;

	/**
	 * get the list of categories that files belong to
	 * 
	 * @param readfile
	 *            read file/Directory
	 * @return list of category
	 */
	public ArrayList<String> getCateList(File readfile) {
		cateList = new ArrayList<String>();
		readFile(readfile);
		return cateList;
	}

	/**
	 * reader for file/Directory
	 * 
	 * @param readfile
	 */
	private void readFile(File readfile) {
		if (!readfile.isDirectory()) {

			String[] fileNameInfo = readfile.getName().split("_");

			String newfileCate = fileNameInfo[0];
			// readConfig(newfileCate);
			if (!cateList.contains(newfileCate)) {
				cateList.add(newfileCate);
				System.out.print("\n cate List add " + newfileCate);
			}

		} else if (readfile.isDirectory()) {
			File[] fileList = readfile.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				File editfile = fileList[i];
				if (!editfile.isDirectory()) {
					String[] fileNameInfo = editfile.getName().split("_");
					String newfileCate = fileNameInfo[0];
					// readConfig(newfileCate);
					if (!cateList.contains(newfileCate)) {
						cateList.add(newfileCate);
					}

				} else if (editfile.isDirectory()) {
					readFile(editfile);
				}
			}
		}
	}

}
