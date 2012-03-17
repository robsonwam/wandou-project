package cn.edu.thu.log.test;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import cn.edu.thu.log.web.service.XESConvertService;
import cn.edu.thu.log.web.service.impl.XESConvertServiceImp;

/**
 * get original Log location from reading XES files 
 * xes file now need a location and a name
 * original Log location now is the location which contains name
 * will be changed to ip+","+name based on application
 * 
 * @author Meng
 * 
 */
public class testXESReverse {
	public static void main(String[] args) throws FileNotFoundException {
		XESConvertService XESConvert = new XESConvertServiceImp();
		ArrayList<String> originalLogLocationList = XESConvert
				.reserveXEStoLogLoc("E:/result", "bigData");
		System.out
				.print("\noriginalLogLocationList:" + originalLogLocationList);

	}
}
