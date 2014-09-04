package br.ufal.sapiens.refactoring.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.ufal.sapiens.refactoring.pr.Statement;
import br.ufal.sapiens.refactoring.pr.StatementType;

public class FileUtil {
	
	public static List<String[]> getCSVData(String file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		List<String[]> lines = new ArrayList<String[]>();
		while ((line = br.readLine()) != null) {
			String[] cols = line.split(",");
			lines.add(cols);
		}
		br.close();
		return lines;
	}

}
