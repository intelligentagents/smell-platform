package br.ufal.sapiens.refactoring.classifier.sniffer;

import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Rule;
import br.ufal.sapiens.refactoring.pr.Node;

public class NeighbourNode {
	private Node node;
	private Rule rule;
	private double distance;

	public NeighbourNode(Node node, Rule rule, double distance) {
		this.node = node;
		this.rule = rule;
		this.distance = distance;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public Rule getRule() {
		return rule;
	}

	public void setRule(Rule rule) {
		this.rule = rule;
	}
	
	@Override
	public String toString() {
		return this.node.toString() + " - Distance: " + this.distance;
	}

}
