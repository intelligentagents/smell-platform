package br.ufal.sapiens.refactoring.classifier.sniffer.simple;

import java.util.List;

import br.ufal.sapiens.refactoring.analysis.NodeAnalysis;
import br.ufal.sapiens.refactoring.classifier.sniffer.ClassifierEvaluator;

public class RuleEvaluator extends ClassifierEvaluator {
	
	public float getEvaluation(Rule rule, List<NodeAnalysis> allAnalysis) {
		return getAccuracy(rule, allAnalysis);
	}
	
	public static float getAccuracy(Rule rule, List<NodeAnalysis> allAnalysis) {
		int tp = 0;
		int fp = 0;
		int tn = 0;
		int fn = 0;
		for (NodeAnalysis analysis : allAnalysis) {
			if (rule.verify(analysis.getNode())) {
				if (analysis.isVerify()) tp += 1;
				else fp += 1;
			} else {
				if (!analysis.isVerify()) tn += 1;
				else fn += 1;
			}
		}
		return (1.0f*(tp + tn)) / (tp + fp + tn + fn);
	}
	
	public static String getPrecisionRecall(Rule rule, List<NodeAnalysis> allAnalysis) {
		float precision = getPrecision(rule, allAnalysis);
		float recall = getRecall(rule, allAnalysis);
		return "" + precision + " / " + recall;
	}
	
	public static float getPrecision(Rule rule, List<NodeAnalysis> allAnalysis) {
		int tp = 0;
		int fp = 0;
		for (NodeAnalysis analysis : allAnalysis) {
			if (rule.verify(analysis.getNode())) {
				if (analysis.isVerify()) tp += 1;
				else fp += 1;
			}
		}
		float precision = 1.0f * tp / (tp + fp);
		return precision;
	}
	
	public static float getRecall(Rule rule, List<NodeAnalysis> allAnalysis) {
		int tp = 0;
		int fp = 0;
		int tn = 0;
		int fn = 0;
		for (NodeAnalysis analysis : allAnalysis) {
			if (rule.verify(analysis.getNode())) {
				if (analysis.isVerify()) tp += 1;
				else fp += 1;
			} else {
				if (!analysis.isVerify()) tn += 1;
				else fn += 1;
			}
		}
		float recall = 1.0f * tp / (tp + fn);
		return recall;
	}
	
	public static float getKappa(Rule rule, List<NodeAnalysis> allAnalysis) {
		int tp = 0;
		int fp = 0;
		int tn = 0;
		int fn = 0;
		for (NodeAnalysis analysis : allAnalysis) {
			if (rule.verify(analysis.getNode())) {
				if (analysis.isVerify()) tp += 1;
				else fp += 1;
			} else {
				if (!analysis.isVerify()) tn += 1;
				else fn += 1;
			}
		}
		return calculateKappa(tn, fn, fp, tp);
	}
	
	private static float calculateKappa(int tn, int fn, int fp, int tp) {
		float prA = (1.0f*(tn+tp)) / (fn+tn+tp+fp);
		float accA = (1.0f * (tp + fp)) / (fn+tn+tp+fp);
		float accB = (1.0f * (tp + fn)) / (fn+tn+tp+fp);
		float negA = (1.0f * (tn + fn)) / (fn+tn+tp+fp);
		float negB = (1.0f * (tn + fp)) / (fn+tn+tp+fp);
		float prE = accA*accB + negA*negB;
		return (prA - prE) / (1.0f - prE);
	}

}
