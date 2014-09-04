package br.ufal.sapiens.refactoring.metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.neighboursearch.LinearNNSearch;
import br.ufal.sapiens.refactoring.classifier.sniffer.NeighbourStatement;
import br.ufal.sapiens.refactoring.classifier.sniffer.rule.Rule;
import br.ufal.sapiens.refactoring.pr.Statement;

public class WekaUtil {
	
	public static List<NeighbourStatement> getNeighbourStatements(List<Statement> statements, Rule rule, int kNeighbors) throws Exception {
		Instances instances = createWekaInstances(new ArrayList<Statement>(statements), rule);
		Instance instance = createWekaInstance(null, rule, instances);
		LinearNNSearch nns = new LinearNNSearch(instances);
		Instances neighboursInstaces = nns.kNearestNeighbours(instance, kNeighbors);

		List<Statement> selectedStatements = WekaUtil.neighboursFromInstances(statements, neighboursInstaces);
		
		List<NeighbourStatement> nStatements = new ArrayList<NeighbourStatement>();
		double distances[] = nns.getDistances();
		for (int i = 0; i < selectedStatements.size(); i++) {
			nStatements.add(new NeighbourStatement(selectedStatements.get(i), rule, distances[i]));
		}
		
		return nStatements;
		
	}
	
	private static Instances createWekaInstances(List<Statement> statements, Rule rule) {
		FastVector fv = new FastVector();
		fv.addElement(new Attribute("name", (FastVector) null));
		for (String metricName : rule.getMetricNames()) {
			fv.addElement(new Attribute(metricName));
		}
		Instances instances = new Instances("NNS", fv, statements.size());
		
		for (Statement statement : statements) {
			Instance instance = createWekaInstance(statement, rule, instances);
			if (instance != null)
				instances.add(createWekaInstance(statement, rule, instances));
		}
		return instances;
	}
	
	private static Instance createWekaInstance(Statement statement, Rule rule, Instances instances) {
		List<String> metricNames = rule.getMetricNames();
		int columns = metricNames.size();
		Instance instance = new Instance(columns + 1);
		instance.setDataset(instances);
		
		if (null == statement) {
			instance.setValue(0, "TARGET");
			for (int i = 0; i < columns; i++) {
				instance.setValue(i+1, rule.getMetricThresholds().get(metricNames.get(i)));
			}
		} else {
			instance.setValue(0, statement.getName());
			for (int i = 0; i < columns; i++) {
				Float value = statement.getMetricValues().get(metricNames.get(i));
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
	
	public static List<Statement> neighboursFromInstances(List<Statement> statements, Instances instances) {
		List<Statement> neighbours = new ArrayList<Statement>();
		int index = instances.numInstances();
		for (int i = 0; i < index; i++) {
			Instance instance = instances.instance(i);
			String line = instance.toString();
			Statement statement = getStatementFromName(line.split(",")[0], statements);
			neighbours.add(statement);
		}
		
		return neighbours;
	}
	
	public static Statement getStatementFromName(String name, List<Statement> statements) {
		for (Statement statement : statements) {
			if (statement.getName().equals(name)) {
				return statement;
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
