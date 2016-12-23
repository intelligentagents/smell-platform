package br.ufal.sapiens.refactoring.pr;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufal.sapiens.refactoring.classifier.smell.Smell;

public class Project {

	private String name;
	private String path;
	private Map<String,Node> classNodes;
	private Map<String,Node> methodNodes;

	public Project(String name, String path) {
		this.name = name;
		this.path = path;
		this.classNodes = new HashMap<String,Node>();
		this.methodNodes = new HashMap<String,Node>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public Node getNodeFromName(String name) {
		if (this.classNodes.containsKey(name)) {
			return classNodes.get(name);
		} else if (this.methodNodes.containsKey(name)) {
			return methodNodes.get(name);
		}
		return null;
	}
	
	public Node getOrCreateNode(String name, NodeType nodeType) {
		Map<String,Node> nodes = getNodes(nodeType);
		if (nodes.containsKey(name)) {
			return nodes.get(name);
		}
		return new Node(name, nodeType);
	}
	
	public void addNodesFromCSV(String file, NodeType nodeType) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		String[] headers = null;
		List<Node> nodes = new ArrayList<Node>();
		while ((line = br.readLine()) != null) {
			String[] cols = line.split(",");
			if (cols.length == 1) cols = line.split(";");
			if (cols[0].startsWith("name")) {
				headers = cols;
				continue;
			}
			Node node = this.getOrCreateNode(cols[0], nodeType);
			node.addMetricValues(Arrays.copyOfRange(headers, 1, headers.length), //removing first column 
									  Arrays.copyOfRange(cols, 1, cols.length)); //removing first column
			nodes.add(node);
		}
		br.close();
		this.addNodes(nodes, nodeType);
	}

	private void addNodes(List<Node> nodes, NodeType nodeType) {
		for (Node node : nodes) {
			this.addNode(node);
		}
	}
	
	public void addNodes(List<Node> nodes) {
		for (Node node : nodes) {
			this.addNode(node);
		}
	}

	private void addNode(Node node) {
		node.setProject(this);
		Map<String,Node> nodes = getNodes(node.getType());
		nodes.put(node.getName(), node);
	}

	public Map<String,Node> getNodes(NodeType nodeType) {
		if (nodeType == NodeType.ClassDefinition) {
			return this.classNodes;
		} else if (nodeType == NodeType.MethodDefinition) {
			return this.methodNodes;
		}
		return null;
	}
	
	public String getAnalysisSource(String smellShortName) {
		return "data/icpc2015/" + this.name + "/an-" + smellShortName + ".csv";
	}

}
