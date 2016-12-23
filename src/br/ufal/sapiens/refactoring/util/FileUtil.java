package br.ufal.sapiens.refactoring.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufal.sapiens.refactoring.analysis.NodeAnalysis;
import br.ufal.sapiens.refactoring.classifier.sniffer.Sniffer;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Rule;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.knn.heuristic.SimpleHeuristicSniffer;
import br.ufal.sapiens.refactoring.developer.Developer;
import br.ufal.sapiens.refactoring.pr.Node;
import br.ufal.sapiens.refactoring.pr.NodeType;
import br.ufal.sapiens.refactoring.pr.Project;

public class FileUtil {
	
	public static List<String[]> getCSVData(String file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		List<String[]> lines = new ArrayList<String[]>();
		while ((line = br.readLine()) != null) {
			String[] cols = line.split(",");
			if (cols.length <= 1) cols = line.split(";");
			lines.add(cols);
		}
		br.close();
		return lines;
	}
	
	public static String getNodeTypeAsString(NodeType nType) {
		if (nType == NodeType.ClassDefinition) return "Class";
		if (nType == NodeType.MethodDefinition) return "Method";
		if (nType == NodeType.AttributeDefinition) return "Attribute";
		return null;
	}
	
	public static Project loadProjectNodesFromFile(Project project, String source, NodeType nodeType) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(source));
		String line;
		String[] headers = null;
		List<Node> nodes = new ArrayList<Node>();
		String nTypeString = getNodeTypeAsString(nodeType);
		while ((line = br.readLine()) != null) {
			String[] cols = line.split(",");
			if (cols.length == 1) cols = line.split(";");
			if (cols[0].startsWith("Type")) {
				headers = cols;
				continue;
			}
			if (!cols[0].contains(nTypeString)) continue;
			Node node = project.getOrCreateNode(cols[1], nodeType);
			node.addMetricValues(Arrays.copyOfRange(headers, 1, headers.length), //removing first column 
									  Arrays.copyOfRange(cols, 1, cols.length)); //removing first column
			nodes.add(node);
		}
		br.close();
		project.addNodes(nodes);
		return project;
	}
	
	public static Map<Node,NodeAnalysis> loadAnalysisFromMatrixFile(Sniffer sniffer,
			String analysisSource, Project project, Developer developer) throws IOException {
		List<String[]> data = FileUtil.getCSVData(analysisSource);
		Map<Node,NodeAnalysis> analysisMap = new HashMap<Node,NodeAnalysis>();
		for (int i = 0; i < data.size(); i++) {
			Node node = project.getNodeFromName(data.get(i)[0]);
			if (node == null) {
				System.out.println("Node not found: " + data.get(i)[1]);
			} else {
				boolean verify = ("1".equals(data.get(i)[developer.getId()])) ? true : false;
				NodeAnalysis analysis = new NodeAnalysis(node,
						sniffer.getSmell(), verify);
				analysisMap.put(analysis.getNode(), analysis);
			}
		}
		return analysisMap;
	}

}
