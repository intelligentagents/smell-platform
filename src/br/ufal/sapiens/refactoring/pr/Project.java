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
	private Map<String,Statement> classStatements;
	private Map<String,Statement> methodStatements;

	public Project(String name, String path) {
		this.name = name;
		this.path = path;
		this.classStatements = new HashMap<String,Statement>();
		this.methodStatements = new HashMap<String,Statement>();
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
	
	public Statement getStatementFromName(String name) {
		if (this.classStatements.containsKey(name)) {
			return classStatements.get(name);
		} else if (this.methodStatements.containsKey(name)) {
			return methodStatements.get(name);
		}
		return null;
	}
	
	private Statement getOrCreateStatement(String name, StatementType statementType) {
		Map<String,Statement> statements = getStatements(statementType);
		if (statements.containsKey(name)) {
			return statements.get(name);
		}
		return new Statement(name, statementType);
	}
	
	public void addStatementsFromCSV(String file, StatementType statementType) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		String[] headers = null;
		List<Statement> statements = new ArrayList<Statement>();
		while ((line = br.readLine()) != null) {
			String[] cols = line.split(",");
			if (cols[0].startsWith("name")) {
				headers = cols;
				continue;
			}
			Statement statement = this.getOrCreateStatement(cols[0], statementType);
			statement.addMetricValues(Arrays.copyOfRange(headers, 1, headers.length), //removing first column 
									  Arrays.copyOfRange(cols, 1, cols.length)); //removing first column
			statements.add(statement);
		}
		br.close();
		this.addStatements(statements, statementType);
	}

	private void addStatements(List<Statement> statements, StatementType statementType) {
		for (Statement statement : statements) {
			this.addStatement(statement);
		}
	}

	private void addStatement(Statement statement) {
		statement.setProject(this);
		Map<String,Statement> statements = getStatements(statement.getType());
		statements.put(statement.getName(), statement);
	}

	public Map<String,Statement> getStatements(StatementType statementType) {
		if (statementType == StatementType.ClassDefinition) {
			return this.classStatements;
		} else if (statementType == StatementType.MethodDefinition) {
			return this.methodStatements;
		}
		return null;
	}

}
