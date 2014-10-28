package br.ufal.sapiens.refactoring.pr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {

	private String name;
	private NodeType type;
	private Project project;
	private List<String> metricNames;
	private Map<String, Float> metricValues;

	public Node(String name, NodeType type) {
		this.name = name;
		this.type = type;
		this.metricValues = new HashMap<String, Float>();
		this.metricNames = new ArrayList<String>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public NodeType getType() {
		return type;
	}

	public void setType(NodeType type) {
		this.type = type;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public void addMetricValue(String name, Float value) {
		this.metricNames.add(name);
		this.metricValues.put(name, value);
	}

	public void addMetricValues(String[] names, String[] values) {
		for (int i = 0; i < names.length; i++) {
			this.addMetricValue(names[i], Float.parseFloat(values[i]));
		}
	}

	public List<String> getMetricNames() {
		return metricNames;
	}

	public void setMetricNames(List<String> metricNames) {
		this.metricNames = metricNames;
	}

	public Map<String, Float> getMetricValues() {
		return metricValues;
	}

	public void setMetricValues(Map<String, Float> metricValues) {
		this.metricValues = metricValues;
	}

	@Override
	public String toString() {
		return this.name + ": " + this.metricValues.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.name.equals(((Node)obj).getName());
	}
	
	@Override
	public int hashCode() {
		return this.name.hashCode();
	}
		
}
