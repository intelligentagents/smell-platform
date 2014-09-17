package br.ufal.sapiens.refactoring.developer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufal.sapiens.refactoring.analysis.StatementAnalysis;
import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.rule.Rule;

public class Developer {

	private int id;
	private String name;
	private Map<Smell, List<Rule>> ruleMap;
	private Map<Smell, List<StatementAnalysis>> analysis;

	public Developer(int id, String name) {
		this.id = id;
		this.name = name;
		this.ruleMap = new HashMap<Smell, List<Rule>>();
		this.analysis = new HashMap<Smell, List<StatementAnalysis>>();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<Smell, List<Rule>> getRuleMap() {
		return ruleMap;
	}

	public void setRuleMap(Map<Smell, List<Rule>> ruleMap) {
		this.ruleMap = ruleMap;
	}

	public Map<Smell, List<StatementAnalysis>> getAnalysis() {
		return analysis;
	}

	public void setAnalysis(Map<Smell, List<StatementAnalysis>> analysis) {
		this.analysis = analysis;
	}
	
	public void addRule(Rule rule) {
		if (!this.ruleMap.containsKey(rule.getSmell())) {
			this.ruleMap.put(rule.getSmell(), new ArrayList<Rule>());
		}
		this.ruleMap.get(rule.getSmell()).add(rule);
	}
	
	public Rule getLastRule(Smell smell) {
		List<Rule> rules = this.ruleMap.get(smell);
		return rules.get(rules.size() - 1);
	}
	
	public float getEvaluation(Rule rule) {
		int tp = 0;
		List<StatementAnalysis> allAnalysis = this.getAnalysis().get(rule.getSmell());
		for (StatementAnalysis analysis : allAnalysis) {
			if (rule.verify(analysis.getStatement()) == analysis.isVerify()) {
				tp += 1;
			}
		}
		int total = allAnalysis.size();
		float precision = 1.0f * tp / total;
		return precision;
	}
	
	public float getEvaluationFromInitialRule(Smell smell) {
		return this.getEvaluation(this.ruleMap.get(smell).get(0));
	}
	
	public Rule getBestRule(Smell smell) {
		float precision = this.getEvaluationFromInitialRule(smell);
		Rule bestRule = null;
		for (Rule rule : this.ruleMap.get(smell)) {
			float rulePrecision = this.getEvaluation(rule);
			if (rulePrecision > precision)
				precision = rulePrecision;
				bestRule = rule;
		}
		return bestRule;
	}

	public void addAnalysis(StatementAnalysis statementAnalysis) {
		if (!this.analysis.containsKey(statementAnalysis.getSmell())) {
			this.analysis.put(statementAnalysis.getSmell(), new ArrayList<StatementAnalysis>());
		}
		this.analysis.get(statementAnalysis.getSmell()).add(statementAnalysis);
	}

}
