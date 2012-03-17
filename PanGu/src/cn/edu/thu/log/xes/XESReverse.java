package cn.edu.thu.log.xes;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XParserRegistry;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import cn.edu.thu.log.util.PanGuConstants;

public class XESReverse {
	ArrayList<String> originalLogLocationList;

	public XESReverse() {
		originalLogLocationList = new ArrayList<String>();
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
	public void reverse(String sourcePath, String name) {
		System.out.print("\n start reverse");
		File readFile = new File(sourcePath);
		readFile(readFile, name);

	}
public ArrayList<String> getOriginalLogLocationList(){
	return originalLogLocationList;
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
				reverseOneXES(readfile);
			}
		} else if (readfile.isDirectory()) {
			System.out.print("\nfound directory");
			File[] fileList = readfile.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				File editfile = fileList[i];

				if (!editfile.isDirectory()) {
					if (editfile.getName().startsWith(name + "_")) {
						reverseOneXES(editfile);
					}

				} else if (editfile.isDirectory()) {
					readFile(editfile, name);
				}
			}
		}
	}

	public void reverseOneXES(File xesFile) {
		Collection<XLog> logs = null;
		try {
			for (XParser parser : XParserRegistry.instance().getAvailable()) {
				if (parser.canParse(xesFile)) {
					System.out.println("Using input parser: " + parser.name());
					logs = parser.parse(xesFile);

					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (XLog eachLog : logs) {
			if (eachLog.iterator().hasNext()) {
				XTrace eachTrace = eachLog.iterator().next();
				if (eachTrace.iterator().hasNext()) {
					XEvent eachEvent = eachTrace.iterator().next();
					{
						String originalLogLocation = eachEvent.getAttributes()
								.get(PanGuConstants.LOGPATH_ATTRIBUTE)
								.toString();
						if (!originalLogLocationList
								.contains(originalLogLocation)) {
							originalLogLocationList.add(originalLogLocation);
						}
					}
				}
			}
			// get event->get logPath

		}
	}
}
