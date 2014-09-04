package br.ufal.sapiens.refactoring.classifier.sniffer;

import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.pr.Statement;

public class SniffedSmell {
	private Smell smell;
	private Sniffer sniffer;
	private Statement statement;
	private int certaintyFactor = 100;

	public SniffedSmell(Smell smell, Sniffer sniffer, Statement statement) {
		super();
		this.smell = smell;
		this.sniffer = sniffer;
		this.statement = statement;
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

	public Statement getStatement() {
		return statement;
	}

	public void setStatement(Statement statement) {
		this.statement = statement;
	}

	public int getCertaintyFactor() {
		return certaintyFactor;
	}

	public void setCertaintyFactor(int certaintyFactor) {
		this.certaintyFactor = certaintyFactor;
	}

}
