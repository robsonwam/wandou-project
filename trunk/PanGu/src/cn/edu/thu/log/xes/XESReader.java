package cn.edu.thu.log.xes;

import java.io.File;
import java.util.Collection;

import javax.swing.JFileChooser;

import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XParserRegistry;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.util.XTimer;

/**
 * Read the one standard log file,like .XES .MXML
 * 
 * @author meng
 * 
 */
public class XESReader {
	/**
	 * read logs in standard format file
	 * 
	 * @return collections of XLog
	 */
	public Collection<XLog> readStandardLog() {

		JFileChooser openChooser = new JFileChooser();
		openChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		openChooser.showOpenDialog(null);
		File source = openChooser.getSelectedFile();
		// load source file
		Collection<XLog> logs = null;
		XTimer timer = new XTimer();
		try {
			for (XParser parser : XParserRegistry.instance().getAvailable()) {
				if (parser.canParse(source)) {
					System.out.println("Using input parser: " + parser.name());
					logs = parser.parse(source);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Reading / building performance: "
				+ timer.getDurationString());
		System.gc();
		System.gc();
		System.gc();
		timer.start();
		int events = 0;
		XLogInfo info = null;
		for (XLog log : logs) {
			info = XLogInfoFactory.createLogInfo(log);
			events += info.getNumberOfEvents();
		}
		System.out.println("Iteration performance: "
				+ timer.getDurationString());
		System.out.println("# Events: " + events + ".");
		System.out.println("# Logs: " + logs.size() + ".");

		return logs;
	}

	/**
	 * read logs in standard format file
	 * @param standardLogFile the absolute path of xes file
	 * @return collections of XLog
	 */

	public Collection<XLog> readStandardLog(String sourcePath) {
		System.out.println("miningfile is："+ sourcePath);
		File sourceFile = new File(sourcePath);
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
			System.out.print("\nno file selected\n");
		}
		System.out.println("Reading / building performance: "
				+ timer.getDurationString());
		System.gc();
		System.gc();
		System.gc();
		timer.start();
		int events = 0;
		XLogInfo info = null;
		for (XLog log : logs) {
			info = XLogInfoFactory.createLogInfo(log);
			events += info.getNumberOfEvents();
		}
		System.out.println("Iteration performance: "
				+ timer.getDurationString());
		System.out.println("# Events: " + events + ".");
		System.out.println("# Logs: " + logs.size() + ".");

		return logs;
	}
}
