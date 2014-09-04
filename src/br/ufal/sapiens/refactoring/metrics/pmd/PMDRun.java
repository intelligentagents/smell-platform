package br.ufal.sapiens.refactoring.metrics.pmd;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import net.sourceforge.pmd.PMD;

public class PMDRun {
	
	public static void main(String[] args) throws FileNotFoundException {
		String path = "/home/hozano/apps/ganttproject-2.0.10-src/ganttproject/src";
//		String path = "/home/hozano/apps/ganttproject-2.0.10-src/ganttproject/src/net/sourceforge/ganttproject/GanttProject.java";
		String[] argz = new String[]{"-R", "/home/hozano/git/badsmells-analyser/platform/src/resources/ruleset.xml",
				"-d", path};
		
		PrintStream out = new PrintStream(new FileOutputStream("gantt-lpl.txt"));
		System.setOut(out);
		PMD.main(argz);
	}

}
