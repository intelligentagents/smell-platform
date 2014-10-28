package br.ufal.sapiens.refactoring.developer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufal.sapiens.refactoring.analysis.NodeAnalysis;
import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.rule.Rule;
import br.ufal.sapiens.refactoring.classifier.sniffer.rule.RuleEvaluator;

public class Developer {

	private int id;
	private String name;
	private Map<Smell, List<Rule>> ruleMap;
	private Map<Smell, List<NodeAnalysis>> analysis;

	public Developer(int id, String name) {
		this.id = id;
		this.name = name;
		this.ruleMap = new HashMap<Smell, List<Rule>>();
		this.analysis = new HashMap<Smell, List<NodeAnalysis>>();
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

	public Map<Smell, List<NodeAnalysis>> getAnalysis() {
		return analysis;
	}

	public void setAnalysis(Map<Smell, List<NodeAnalysis>> analysis) {
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
	
	
	public float getEvaluationFromInitialRule(Smell smell) {
		return RuleEvaluator.getEvaluation(this.ruleMap.get(smell).get(0), this.getAnalysis().get(smell));
	}
	
	public Rule getBestRule(Smell smell) {
		float bestEvaluation = this.getEvaluationFromInitialRule(smell);
		Rule bestRule = this.ruleMap.get(smell).get(0);
		for (Rule rule : this.ruleMap.get(smell)) {
			float ruleEvaluation = RuleEvaluator.getEvaluation(rule, this.getAnalysis().get(smell));
			if (ruleEvaluation > bestEvaluation) {
				bestEvaluation = ruleEvaluation;
				bestRule = rule;
			}
		}
		return bestRule;
	}

	public void addAnalysis(NodeAnalysis nodeAnalysis) {
		if (!this.analysis.containsKey(nodeAnalysis.getSmell())) {
			this.analysis.put(nodeAnalysis.getSmell(), new ArrayList<NodeAnalysis>());
		}
		this.analysis.get(nodeAnalysis.getSmell()).add(nodeAnalysis);
	}

}
