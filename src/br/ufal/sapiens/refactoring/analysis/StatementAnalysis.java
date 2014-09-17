package br.ufal.sapiens.refactoring.analysis;

import br.ufal.sapiens.refactoring.classifier.smell.Smell;
import br.ufal.sapiens.refactoring.classifier.sniffer.NeighbourStatement;
import br.ufal.sapiens.refactoring.pr.Statement;

public class StatementAnalysis {

	private Statement statement;
	private Smell smell;
	private boolean verify;

	public StatementAnalysis(Statement statement, Smell smell, boolean verify) {
		this.statement = statement;
		this.smell = smell;
		this.verify = verify;
	}
	
	public StatementAnalysis(NeighbourStatement nStatement, boolean verify) {
		this(nStatement.getStatement(), nStatement.getRule().getSmell(), verify);
	}

	public Statement getStatement() {
		return statement;
	}

	public void setStatement(Statement statement) {
		this.statement = statement;
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
		return this.smell.getShortName() + ":" + (this.verify ? 1 : 0) + ":" + this.statement;
	}
	
}
