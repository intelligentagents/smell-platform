package br.ufal.sapiens.refactoring.metrics.pmd;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.util.designer.Designer;

public class PMDDesigner {
	
	public static void main(String[] args) throws FileNotFoundException {
		Designer.main(args);
	}

}
