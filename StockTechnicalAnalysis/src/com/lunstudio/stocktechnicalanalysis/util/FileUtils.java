package com.lunstudio.stocktechnicalanalysis.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
	private final static String NEW_LINE = "\n";

	public static String readFromFile(String filepath) throws Exception {
		StringBuffer buf = new StringBuffer();
		try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	buf.append(line).append("\n");
		    }
		}
		return buf.toString();
	}

	public static void writeToFile(String filepath, String content) throws Exception {
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(filepath))) {
			bw.write(content);
		}
		return;
	}

	public static void writeToFile(List<String> dataList, String filePath) throws Exception {
		BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
		for(String str : dataList) {
			writer.write(str);
			writer.write(NEW_LINE);
		}
		writer.close();
		return;
	}
	
	public static List<String> readToLine(File inFilePath, String encoding) throws Exception {
		InputStream input = new FileInputStream(inFilePath);

		BufferedReader in = new BufferedReader(new InputStreamReader(input, encoding));
	    
		String inputLine;
		List<String> lineList = new ArrayList<String>();
		while ((inputLine = in.readLine()) != null) {
			lineList.add(inputLine);
		}
		in.close();
		return lineList;
	}
	
	public static String readFile(File inFilePath, String encoding) throws Exception {
		InputStream input = new FileInputStream(inFilePath);
		BufferedReader in = new BufferedReader(new InputStreamReader(input, encoding));
		StringBuffer buf = new StringBuffer();
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			buf.append(inputLine);
		}
		in.close();
		return buf.toString();
	}

}
