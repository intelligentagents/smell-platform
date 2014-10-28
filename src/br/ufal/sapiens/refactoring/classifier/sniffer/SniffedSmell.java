package br.ufal.sapiens.refactoring.classifier.sniffer;

import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.pr.Node;

public class SniffedSmell {
	private Smell smell;
	private Sniffer sniffer;
	private Node node;
	private int certaintyFactor = 100;

	public SniffedSmell(Smell smell, Sniffer sniffer, Node node) {
		super();
		this.smell = smell;
		this.sniffer = sniffer;
		this.node = node;
	}

	public Smell getSmell() {
		return smell;
	}

	public void setSmell(Smell smell) {
		this.smell = smell;
	}

	public Sniffer getSniffer() {
		return sniffer;
	}

	public void setSniffer(Sniffer sniffer) {
		this.sniffer = sniffer;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public int getCertaintyFactor() {
		return certaintyFactor;
	}

	public void setCertaintyFactor(int certaintyFactor) {
		this.certaintyFactor = certaintyFactor;
	}

}
