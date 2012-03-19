package cn.edu.thu.log.xes;

import java.io.File;
import java.util.Collection;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XParserRegistry;
import org.deckfour.xes.model.XLog;

/**
 * Read the split standard logs file(.XES) and merge them to one XLog
 * 
 * @author meng
 * 
 */
public class XESFileReader {
	String sourchPath;
	String name;
	XLog log;
	int size;
	int currentIndex;

	public XESFileReader() {
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		log = factory.createLog();
		size = 0;
		currentIndex = 0;
	}

	public void setUpXESPara(String sourcePath, String name) {
		this.sourchPath = sourcePath;
		this.name = name;
		File readFile = new File(sourcePath);
		getFilesize(readFile);
		System.out.print("\nthere are xes files:" + size);
	}

	/**
	 * reader for file/Directory
	 * 
	 * @param readfile
	 *            xes file
	 */
	private void getFilesize(File readfile) {
		if (!readfile.isDirectory()) {
			if (readfile.getName().startsWith(name + "_")
					&& readfile.getName().endsWith(".xes")) {
				size++;
			}
		} else if (readfile.isDirectory()) {
//			System.out.print("\nfound directory");
			File[] fileList = readfile.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				File editfile = fileList[i];

				if (!editfile.isDirectory()) {
					if (editfile.getName().startsWith(name + "_")
							&& editfile.getName().endsWith(".xes")) {
						size++;

					}

				} else if (editfile.isDirectory()) {
					getFilesize(editfile);
				}
			}
		}
	}

	public boolean hadNext() {
		if (currentIndex < size) {
			return true;
		}
		return false;
	}

	public XLog Next() {
		System.out.print("\n read xes file:" +currentIndex);
		File readFile = new File(sourchPath);
		findXESFile(readFile, currentIndex);
		return log;
	}

	/**
	 * reader for file/Directory
	 * 
	 * @param readfile
	 *            xes file
	 */
	private void findXESFile(File readfile, int currentIndexL) {
		if (!readfile.isDirectory()) {
			if (readfile.getName().startsWith(name + "_" + currentIndexL)
					&& readfile.getName().endsWith(".xes")) {
				readOneXESToLog(readfile);
			}
		} else if (readfile.isDirectory()) {
			// System.out.print("\nfound directory");
			File[] fileList = readfile.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				File editfile = fileList[i];

				if (!editfile.isDirectory()) {
					if (editfile.getName().startsWith(
							name + "_" + currentIndexL)
							&& editfile.getName().endsWith(".xes")) {
					//	System.out.print("\nfound file"+editfile.getName());
						readOneXESToLog(editfile);
					}

				} else if (editfile.isDirectory()) {
					findXESFile(editfile, currentIndexL);
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

	private void readOneXESToLog(File sourceFile) {
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		log = factory.createLog();
		// load source file
		Collection<XLog> logs = null;
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
		currentIndex++;

	}
}
