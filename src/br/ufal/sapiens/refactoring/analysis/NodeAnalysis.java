package br.ufal.sapiens.refactoring.analysis;

import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.NeighbourNode;
import br.ufal.sapiens.refactoring.pr.Node;

public class NodeAnalysis {

	private Node node;
	private Smell smell;
	private boolean verify;

	public NodeAnalysis(Node node, Smell smell, boolean verify) {
		this.node = node;
		this.smell = smell;
		this.verify = verify;
	}
	
	public NodeAnalysis(NeighbourNode nNode, boolean verify) {
		this(nNode.getNode(), nNode.getRule().getSmell(), verify);
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public Smell getSmell() {
		return smell;
	}

	public void setSmell(Smell smell) {
		this.smell = smell;
	}

	public boolean isVerify() {
		return verify;
	}

	public void setVerify(boolean verify) {
		this.verify = verify;
	}
	
	@Override
	public String toString() {
		return this.smell.getShortName() + ":" + (this.verify ? 1 : 0) + ":" + this.node;
	}
	
}
