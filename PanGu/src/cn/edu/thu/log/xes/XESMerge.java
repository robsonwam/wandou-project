package cn.edu.thu.log.xes;

import java.io.File;
import java.util.Collection;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XParserRegistry;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.util.XTimer;

/**
 * Read the split standard logs file(.XES) and merge them to one XLog
 * 
 * @author meng
 * 
 */
public class XESMerge {

	XLog log;

	public XESMerge() {
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		log = factory.createLog();
	}

	/**
	 * find xes files by name and address(this address is a filePath)
	 * 
	 * @param sourcePath
	 *            address or ip
	 * @param name
	 *            name of xes
	 * @return merged log
	 */
	public XLog mergeXES(String sourcePath, String name) {
		System.out.print("\n start merge");
		File readFile = new File(sourcePath);
		readFile(readFile, name);
		return log;

	}

	/**
	 * reader for file/Directory
	 * 
	 * @param readfile
	 *            xes file
	 */
	private void readFile(File readfile, String name) {
		if (!readfile.isDirectory()) {
			if (readfile.getName().startsWith(name + "_")) {
				mergeOneXESToLog(readfile);
			}
		} else if (readfile.isDirectory()) {
			System.out.print("\nfound directory");
			File[] fileList = readfile.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				File editfile = fileList[i];

				if (!editfile.isDirectory()) {
					if (editfile.getName().startsWith(name + "_")) {
						mergeOneXESToLog(editfile);
					}

				} else if (editfile.isDirectory()) {
					readFile(editfile, name);
				}
			}
		}
	}

	/**
	 * read logs in standard format file
	 * 
	 * @param standardLogFile
	 *            the absolute path of xes file
	 * @return collections of XLog
	 */

	public Collection<XLog> mergeOneXESToLog(File sourceFile) {
		// load source file
		Collection<XLog> logs = null;
		XTimer timer = new XTimer();
		try {
			for (XParser parser : XParserRegistry.instance().getAvailable()) {
				if (parser.canParse(sourceFile)) {
					System.out.println("Using input parser: " + parser.name());
					logs = parser.parse(sourceFile);

					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (XLog eachLog : logs) {
			log.addAll(eachLog);

		}

		return logs;
	}
}
