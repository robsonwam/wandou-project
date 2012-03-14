//package cn.edu.thu.log.boot;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.Properties;
//
//import org.processmining.framework.boot.Boot.Level;
//import org.processmining.framework.util.PathHacker;
//
//public class Boot {
//	
//	
//	static {
//
//		Properties ini = new Properties();
//		FileInputStream is;
//		try {
//			is = new FileInputStream("MiningAlgorithm.ini");
//		} catch (FileNotFoundException e) {
//			throw new RuntimeException("MiningAlgorithm.ini file not found. Exiting UI", e);
//		}
//
//		try {
//			ini.load(is);
//			is.close();
//		} catch (IOException e) {
//			throw new RuntimeException("Error while reading MiningAlgorithm.ini file. Exiting UI.", e);
//		}
//
//		if (!ini.containsKey("PROM_VERSION") || !ini.containsKey("RELEASE_PACKAGE")) {
//			throw new RuntimeException("Error while reading ProM.ini file, missing required"
//					+ " keys PROM_VERSION and or RELEASE_PACKAGE . Exiting ProM.");
//		}
//
//		PROM_VERSION = ini.getProperty("PROM_VERSION");
//
//		RELEASE_PACKAGE = ini.getProperty("RELEASE_PACKAGE");
//
//		DO_SERIALIZATION = Boolean.parseBoolean(ini.getProperty("DO_SERIALIZATION", "true"));
//
//		try {
//			VERBOSE = Level.valueOf(ini.getProperty("VERBOSE", Level.ALL.name()));
//		} catch (IllegalArgumentException e) {
//			if (e.getMessage().toLowerCase().endsWith(".true")) {
//				VERBOSE = Level.ALL;
//			} else if (e.getMessage().toLowerCase().endsWith(".false")) {
//				VERBOSE = Level.NONE;
//			} else {
//				throw e;
//			}
//		}
//
//		LIB_FOLDER = ini.getProperty("LIB_FOLDER", "lib").replace("/", File.separator);
//		PathHacker.addLibraryPathFromDirectory(new File("." + File.separator + LIB_FOLDER));
//
//		IMAGES_FOLDER = LIB_FOLDER + File.separator
//				+ ini.getProperty("IMAGES_FOLDER", "images").replace("/", File.separator);
//		PathHacker.addLibraryPathFromDirectory(new File("." + File.separator + IMAGES_FOLDER));
//
//		MACRO_FOLDER = LIB_FOLDER + File.separator
//				+ ini.getProperty("MACRO_FOLDER", "macros").replace("/", File.separator);
//		PathHacker.addLibraryPathFromDirectory(new File("." + File.separator + MACRO_FOLDER));
//
//		try {
//			DEFAULT_REPOSITORY = new URL(ini.getProperty("PACKAGE_URL",
//					"http://prom.win.tue.nl/ProM/packages/packages.xml"));
//		} catch (MalformedURLException e) {
//			try {
//				DEFAULT_REPOSITORY = new URL("http://prom.win.tue.nl/ProM/packages/packages.xml");
//			} catch (MalformedURLException e1) {
//				assert (false);
//			}
//		}
//
//		String prom_user_folder = ini.getProperty("PROM_USER_FOLDER", "").replace("/", File.separator);
//		if (prom_user_folder.equals("")) {
//			PROM_USER_FOLDER = System.getProperty("user.home", "") + File.separator + ".ProM";
//		} else {
//			PROM_USER_FOLDER = prom_user_folder;
//		}
//
//		PACKAGE_FOLDER = PROM_USER_FOLDER + File.separator
//				+ ini.getProperty("PACKAGE_FOLDER", "packages").replace("/", File.separator);
//
//		WORKSPACE_FOLDER = PROM_USER_FOLDER + File.separator
//				+ ini.getProperty("WORKSPACE_FOLDER", "workspace").replace("/", File.separator);
//
//		if (VERBOSE == Level.ALL) {
//			System.out.println("Ini file processed");
//		}
//
//	}
//}
