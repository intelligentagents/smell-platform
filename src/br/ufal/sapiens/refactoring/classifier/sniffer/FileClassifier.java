package br.ufal.sapiens.refactoring.classifier.sniffer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.ufal.sapiens.refactoring.analysis.NodeAnalysis;
import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.pr.Node;

public class FileClassifier extends Classifier {
	
	private Set<String> detectedSmells;
	
	public FileClassifier(String name, Smell smell, String filename) throws Exception {
		super(name, smell);
		this.readFile(filename);
	}
	
	public void readFile(String filename) throws Exception {
		this.detectedSmells = new HashSet<String>();
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		while ((line = br.readLine()) != null) {
			this.detectedSmells.add(line.replace(";", ""));
		}
		br.close();
	}

	@Override
	public Boolean verify(Node node) {
		return this.detectedSmells.contains(node.getName());
	}

	@Override
	public Classifier update(List<NodeAnalysis> analysis) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getMetricNames() {
		// TODO Auto-generated method stub
		return null;
	}

}
