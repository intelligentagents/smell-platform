package br.ufal.sapiens.refactoring.metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.neighboursearch.LinearNNSearch;
import br.ufal.sapiens.refactoring.classifier.sniffer.NeighbourNode;
import br.ufal.sapiens.refactoring.classifier.sniffer.rule.Rule;
import br.ufal.sapiens.refactoring.pr.Node;

public class WekaUtil {
	
	public static List<NeighbourNode> getNeighbourNodes(List<Node> nodes, Rule rule, int kNeighbors) throws Exception {
		Instances instances = createWekaInstances(new ArrayList<Node>(nodes), rule);
		Instance instance = createWekaInstance(null, rule, instances);
		LinearNNSearch nns = new LinearNNSearch(instances);
		Instances neighboursInstaces = nns.kNearestNeighbours(instance, kNeighbors);

		List<Node> selectedNodes = WekaUtil.neighboursFromInstances(nodes, neighboursInstaces);
		
		List<NeighbourNode> nNodes = new ArrayList<NeighbourNode>();
		double distances[] = nns.getDistances();
		for (int i = 0; i < selectedNodes.size(); i++) {
			nNodes.add(new NeighbourNode(selectedNodes.get(i), rule, distances[i]));
		}
		
		return nNodes;
		
	}
	
	private static Instances createWekaInstances(List<Node> nodes, Rule rule) {
		FastVector fv = new FastVector();
		fv.addElement(new Attribute("name", (FastVector) null));
		for (String metricName : rule.getMetricNames()) {
			fv.addElement(new Attribute(metricName));
		}
		Instances instances = new Instances("NNS", fv, nodes.size());
		
		for (Node node : nodes) {
			Instance instance = createWekaInstance(node, rule, instances);
			if (instance != null)
				instances.add(createWekaInstance(node, rule, instances));
		}
		return instances;
	}
	
	private static Instance createWekaInstance(Node node, Rule rule, Instances instances) {
		List<String> metricNames = rule.getMetricNames();
		int columns = metricNames.size();
		Instance instance = new Instance(columns + 1);
		instance.setDataset(instances);
		
		if (null == node) {
			instance.setValue(0, "TARGET");
			for (int i = 0; i < columns; i++) {
				instance.setValue(i+1, rule.getMetricThresholds().get(metricNames.get(i)));
			}
		} else {
			instance.setValue(0, node.getName());
			for (int i = 0; i < columns; i++) {
				Float value = node.getMetricValues().get(metricNames.get(i));
				if (value != null)
					instance.setValue(i+1, value);
			}	
		}
		return instance;
	}
	
//	public static List<NeighbourStatement> getNeighbourStatements(Project project, Rule rule, int kNeighbors) throws Exception {
//		weka.core.converters.ConverterUtils.DataSource src = new weka.core.converters.ConverterUtils.DataSource(project.getPath());
//		Instances data = src.getDataSet();
//		System.out.println(data);
//		
//		if (data.classIndex() == -1)
//			data.setClassIndex(data.numAttributes() - 1);
//		
//		LinearNNSearch nns = new LinearNNSearch(data);
//		int numVariables = rule.getExpressions().size();
//		Instance instance = new Instance(numVariables + 1); // Including the name column
//		
//		for (int i = 1; i <= numVariables; i++) {
//			instance.setValue(i, rule.getExpressions().get(i-1).getValue());
//		}
//		Instances result = nns.kNearestNeighbours(instance, kNeighbors);
//		System.out.println(result);
//		return null;
////		List<Statement> statements = new ArrayList<Statement>(project.getStatements().values());
////		statements = WekaUtil.neighboursFromInstances(statements, result);
//		
////		List<NeighbourStatement> nStatements = new ArrayList<NeighbourStatement>();
////		double distances[] = nns.getDistances();
////		for (int i = 0; i < statements.size(); i++) {
////			nStatements.add(new NeighbourStatement(statements.get(i), rule, distances[i]));
////		}
////		
////		return nStatements;
//		
//	}
	
	public static List<Node> neighboursFromInstances(List<Node> nodes, Instances instances) {
		List<Node> neighbours = new ArrayList<Node>();
		int index = instances.numInstances();
		for (int i = 0; i < index; i++) {
			Instance instance = instances.instance(i);
			String line = instance.toString();
			Node node = getNodeFromName(line.split(",")[0], nodes);
			neighbours.add(node);
		}
		
		return neighbours;
	}
	
	public static Node getNodeFromName(String name, List<Node> nodes) {
		for (Node node : nodes) {
			if (node.getName().equals(name)) {
				return node;
			}
		}
		return null;
	}
	
	public static void main(String[] args) throws Exception {
//		Project project = Project.fromCSV("Gantt", "/home/hozano/git/smells/papers/ictai2014/experiment/observer/dados/pmd-gantt.csv");
//		Sniffer sniffer = new GodClassSniffer();
//		List<NeighbourStatement> statements = WekaUtil.getNeighbourStatements(project, sniffer.getRule(), 5, );
//		for (NeighbourStatement neighbourStatement : statements) {
//			System.out.println(neighbourStatement);
//		}
//		
		
	}


}
