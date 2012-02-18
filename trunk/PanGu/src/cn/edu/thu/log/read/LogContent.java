package cn.edu.thu.log.read;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import cn.edu.thu.log.test.testUI;

/**
 * reader for content of log file,which contains functions that can read content
 * of log file
 * 
 * @author Meng
 * 
 */
public class LogContent {
	/** content of logHead in one logRecord */
	String logHeadContent;
	/** content of logBody logRecord */
	String logBodyContent;
	/** list of categories that files belong to */
	ArrayList<String> cateList;
	/** Log's configuration class */
	LogConfig logConfig;
	ArrayList<Log> logList;

	/**
	 * constructor
	 * 
	 * @param logConfig
	 *            Log's configuration
	 */
	public LogContent(LogConfig logConfig) {
		this.logConfig = logConfig;
		logList = new ArrayList<Log>();
	}

	/**
	 * main function:show the content of read file/Directory
	 * 
	 * @param readfile
	 *            log file
	 * @param logUI
	 *            GUI for test
	 */
	public void showContent(File readfile, testUI logUI) {
		LogFilesReader logfilesReader = new LogFilesReader();
		cateList = new ArrayList<String>();
		cateList = logfilesReader.getCateList(readfile);
		readFile(readfile, logUI);
		// if(cateList.size()>1)
		// {
		//			
		// readFile(readfile, logUI);
		// }
		// else{
		// readFile(readfile, logUI);
		// }

	}

	/**
	 * reader for file/Directory
	 * 
	 * @param readfile
	 *            log file
	 * @param logUI
	 *            GUI for test
	 */
	private void readFile(File readfile, testUI logUI) {
		if (!readfile.isDirectory()) {
			readFileContent(readfile, logUI);
			System.out.println(readfile.getName() + " is not Directory\n");
		} else if (readfile.isDirectory()) {
			System.out.println(readfile.getName() + " is Directory\n");
			File[] fileList = readfile.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				File editfile = fileList[i];
				if (!editfile.isDirectory()) {
					readFileContent(editfile, logUI);

				} else if (editfile.isDirectory()) {
					readFile(editfile, logUI);
				}
			}
		}
	}

	/**
	 * read content of one file
	 * 
	 * @param file
	 *            log file
	 * @param logUI
	 *            GUI for test
	 */
	private void readFileContent(File file, testUI logUI) {

		BufferedReader reader;
		String record;
		String logHeadTokenizer = logConfig.getLogHeadTokenizer();
		String logBodyTokenizer = logConfig.getLogBodyTokenizer();
		String logHeadBodyTokenizer = logConfig.getLogHeadBodyTokenizer();
		String escapeSymble = logConfig.getEscapeSymbol();

		// read each log Record
		try {
			reader = new BufferedReader(new FileReader(file));
			while ((record = reader.readLine()) != null) {
				Log log = new Log();
				// set up the file path and file name for this log
				log.setLogName(file.getName());
				log.setLogPath(file.getPath());
				// String temprecord = record.concat(logBodyTokenizer);
				String temprecord = record;
				// deal with each log record
				ArrayList<Object> params = new ArrayList<Object>();
				ArrayList<Object> headparams = new ArrayList<Object>();
				ArrayList<Object> bodyparams = new ArrayList<Object>();
				int startIndex = 0;
				// split logRecord to logHead and lolgBody
				for (int i = 0; i < temprecord.length(); i++) {
					if (String.valueOf(temprecord.charAt(i)).matches(
							logHeadBodyTokenizer)) {
						// if (temprecord.charAt(i) == ';') {
						// logHeadContent=record.
						// params.add(temprecord.substring(startIndex, i));
						logHeadContent = record.substring(0, i);
						logHeadContent = logHeadContent
								.concat(logHeadTokenizer);
						// logHeadContent=
						// logHeadContent.concat(logHeadTokenizer);
						logBodyContent = record.substring(i, record.length());
						logBodyContent = logBodyContent
								.concat(logBodyTokenizer);
						// System.out.print("\n log body content "
						// + logBodyContent);
						// System.out.print("\n log Head content "
						// + logHeadContent);
						// startIndex = i + 1;
					}
				}
				// split log head to params
				startIndex = 0;
				for (int i = 0; i < logHeadContent.length(); i++) {
					if (String.valueOf(logHeadContent.charAt(i)).matches(
							logHeadTokenizer)) {
						headparams.add(logHeadContent.substring(startIndex, i));
						startIndex = i + 1;
					}
					// the token after"\" does not count
					String speciaString = new String("\\\\");
					// System.out.print("\n escapteSymble before is"+escapeSymble);
					if (escapeSymble.matches(speciaString)) {
						// "\" is also the escape symble in java. so if the log use "\" as escapesymble, the java will inteprated as "\\\\"
						escapeSymble = new String("\\\\");

					}
					// System.out.print("\n escapteSymble after is"+escapeSymble);
					if (String.valueOf(logHeadContent.charAt(i)).matches(
							escapeSymble)) {
						System.out.print("\n found escape Symble:"
								+ logHeadContent.charAt(i));
						i += 2;
					}
				}
				// split log body to params
				startIndex = 0;
				for (int i = 0; i < logBodyContent.length(); i++) {
					if (String.valueOf(logBodyContent.charAt(i)).matches(
							logBodyTokenizer)) {
						bodyparams.add(logBodyContent.substring(startIndex, i));
						startIndex = i + 1;
					}

					// the token after"\" does not count
					String speciaString = new String("\\\\");
					// System.out.print("\n escapteSymble before is"+escapeSymble);
					if (escapeSymble.matches(speciaString)) {
						// "\" is also the escape symble in java. so if the log use "\" as escapesymble, the java will inteprated as "\\\\"
						escapeSymble = new String("\\\\");

					}
					// System.out.print("\n escapteSymble after is"+escapeSymble);
					if (String.valueOf(logBodyContent.charAt(i)).matches(
							escapeSymble)) {
						System.out.print("\n found escape Symble:"
								+ logBodyContent.charAt(i));
						i += 2;
					}

				}
				// combine the headparams and the bodyparams to generate log
				// params
				params.addAll(headparams);
				params.addAll(bodyparams);
				System.out.print("\nheadParams" + headparams);
				System.out.print("\nbodyparams" + bodyparams);

				// for (int i = 0; i < temprecord.length(); i++) {
				// if (String.valueOf(temprecord.charAt(i)).matches(
				// logBodyTokenizer)) {
				// params.add(temprecord.substring(startIndex, i));
				// startIndex = i + 1;
				// }
				// if (String.valueOf(temprecord.charAt(i)).matches(
				// logHeadBodyTokenizer)) {
				// // if (temprecord.charAt(i) == ';') {
				// // logHeadContent=record.
				// params.add(temprecord.substring(startIndex, i));
				// logHeadContent = record.substring(0, i);
				// logBodyContent = record.substring(i, record.length());
				// // System.out.print("\n log body content "
				// // + logBodyContent);
				// // System.out.print("\n log Head content "
				// // + logHeadContent);
				// startIndex = i + 1;
				// }
				// // the token after"\" does not count
				// String speciaString = new String("\\\\");
				// //
				// System.out.print("\n escapteSymble before is"+escapeSymble);
				// if (escapeSymble.matches(speciaString)) {
				// //
				// "\" is also the escape symble in java. so if the log use "\" as escapesymble, the java will inteprated as "\\\\"
				// escapeSymble = new String("\\\\");
				//
				// }
				// //
				// System.out.print("\n escapteSymble after is"+escapeSymble);
				// if (String.valueOf(temprecord.charAt(i)).matches(
				// escapeSymble)) {
				// System.out.print("\n found escape Symble:"
				// + temprecord.charAt(i));
				// i += 2;
				// }
				// // the token"," between"()" does not count
				// // if (temprecord.charAt(i) == '(') {
				// // for (int j = i; j < temprecord.length(); j++) {
				// // if (temprecord.charAt(j) == ')') {
				// // i = j;
				// // break;
				// // }
				// //
				// // }
				// //
				// // }
				//
				// }
				// System.out.print("\nread log params:" + params);

				String category = file.getName().split("_")[0];
				int indexCate = 0;
				for (int i = 0; i < cateList.size(); i++) {
					if (cateList.get(i).matches(category)) {
						indexCate = i;
					}
				}
				int insertIndex = 0;
				int insertParamscount = 0;
				// LogConfig logConfig=new LogConfig();
				insertIndex += headparams.size();
				// insertIndex += logConfig.getLogHead().size();
				System.out.print("\nlog head size:"
						+ logConfig.getLogHead().size());

				for (int i = 0; i < indexCate; i++) {

					int tempBodyParamCount = logConfig.getLogBodyByCate(
							cateList.get(i)).size();
					// insertIndex+=tempBodyParamCount;
					insertParamscount += tempBodyParamCount;
					System.out.print("\nlog body size:" + tempBodyParamCount);

				}

				logConfig.getLogBody().size();
				for (int j = 0; j < insertParamscount; j++) {
					params.add(insertIndex, null);
				}
				System.out.print("\n params add:" + params);
				// logUI.addLog(params);
				log.setLogContent(params);

				System.out.print("\nadd log:" + log.getLogContent());
				logList.add(log);

			}
			reader.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param readfile
	 *            file need to be read
	 * @param logUI
	 *            GUI for test/ it is not used here
	 * @return
	 */
	public ArrayList<Log> getContent(File readfile, testUI logUI) {
		// TODO Auto-generated method stub
		// the List of log that represent the content that want to be read
		logList = new ArrayList<Log>();

		LogFilesReader logfilesReader = new LogFilesReader();
		cateList = new ArrayList<String>();
		cateList = logfilesReader.getCateList(readfile);

		readFile(readfile, logUI);
		// if(cateList.size()>1)
		// {
		//			
		// readFile(readfile, logUI);
		// }
		// else{
		// readFile(readfile, logUI);
		// }

		return logList;
	}
}
