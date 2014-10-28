package br.ufal.sapiens.refactoring.classifier.sniffer;

import java.util.List;

import br.ufal.sapiens.refactoring.analysis.NodeAnalysis;
import br.ufal.sapiens.refactoring.classifier.sniffer.simple.Rule;

public class ClassifierEvaluator {
	
	public static float getEvaluation(Classifier classifier, List<NodeAnalysis> allAnalysis) {
		return getAccuracy(classifier, allAnalysis);
	}
	
	public static float getAccuracy(Classifier classifier, List<NodeAnalysis> allAnalysis) {
		int tp = 0;
		int fp = 0;
		int tn = 0;
		int fn = 0;
		for (NodeAnalysis analysis : allAnalysis) {
			if (classifier.verify(analysis.getNode())) {
				if (analysis.isVerify()) tp += 1;
				else fp += 1;
			} else {
				if (!analysis.isVerify()) tn += 1;
				else fn += 1;
			}
		}
		return (1.0f*(tp + tn)) / (tp + fp + tn + fn);
	}
	
	public String getPrecisionRecall(Classifier classifer, List<NodeAnalysis> allAnalysis) {
		float precision = this.getPrecision(classifer, allAnalysis);
		float recall = this.getRecall(classifer, allAnalysis);
		return "" + precision + " / " + recall;
	}
	
	public float getPrecision(Classifier classifer, List<NodeAnalysis> allAnalysis) {
		int tp = 0;
		int fp = 0;
		for (NodeAnalysis analysis : allAnalysis) {
			if (classifer.verify(analysis.getNode())) {
				if (analysis.isVerify()) tp += 1;
				else fp += 1;
			}
		}
		float precision = 1.0f * tp / (tp + fp);
		return precision;
	}
	
	public float getRecall(Classifier classifer, List<NodeAnalysis> allAnalysis) {
		int tp = 0;
		int fp = 0;
		int tn = 0;
		int fn = 0;
		for (NodeAnalysis analysis : allAnalysis) {
			if (classifer.verify(analysis.getNode())) {
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
	
	public float getKappa(Classifier classifer, List<NodeAnalysis> allAnalysis) {
		int tp = 0;
		int fp = 0;
		int tn = 0;
		int fn = 0;
		for (NodeAnalysis analysis : allAnalysis) {
			if (classifer.verify(analysis.getNode())) {
				if (analysis.isVerify()) tp += 1;
				else fp += 1;
			} else {
				if (!analysis.isVerify()) tn += 1;
				else fn += 1;
			}
		}
		return this.calculateKappa(tn, fn, fp, tp);
	}
	
	private float calculateKappa(int tn, int fn, int fp, int tp) {
		float prA = (1.0f*(tn+tp)) / (fn+tn+tp+fp);
		float accA = (1.0f * (tp + fp)) / (fn+tn+tp+fp);
		float accB = (1.0f * (tp + fn)) / (fn+tn+tp+fp);
		float negA = (1.0f * (tn + fn)) / (fn+tn+tp+fp);
		float negB = (1.0f * (tn + fp)) / (fn+tn+tp+fp);
		float prE = accA*accB + negA*negB;
		return (prA - prE) / (1.0f - prE);
	}

}
