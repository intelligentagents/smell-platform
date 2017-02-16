package br.ufal.sapiens.refactoring.classifier.sniffer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class EvaluationList {
	
	private List<Float> list;
	private List<List<Integer>> matrixList; // TP, TN, FP, FN
	
	public EvaluationList() {
		this.list = new ArrayList<Float>();
		this.matrixList = new ArrayList<List<Integer>>();
	}
	
	public void add(Float value) {
		this.list.add(value);
	}
	
	public void addMatrix(List<Integer> value) {
		this.matrixList.add(value);
	}
	
	public int[] sumMatrix() {
		int tp=0, tn=0, fp=0, fn = 0;
		for (List<Integer> aList : this.matrixList) {
			tp += aList.get(0);
			tn += aList.get(1);
			fp += aList.get(2);
			fn += aList.get(3);
		}
		return new int[]{tp, tn, fp, fn};
	}
	
	public Float getMedian() {
		List<Float> values = new ArrayList<Float>();
		for (Float value : this.list) {
			if ((value != null) && (!value.isNaN())) {
				values.add(value);
			}
		}
		if (values.size() == 0) return 0f;
		Collections.sort(values);
		double pos1 = Math.floor((values.size() - 1.0) / 2.0);
		double pos2 = Math.ceil((values.size() - 1.0) / 2.0);
		
		if (pos1 == pos2) {
			return values.get((int) pos1);
		} else {
			return new Float(values.get((int) pos1) + values.get((int) pos2) / 2.0);
		}
	}
	
	public Float getAverage() {
		Float result = 0f;
		int count = 0;
		for (Float value : this.list) {
			if ((value != null) && (!value.isNaN())) {
				count += 1;
				result += value;
			}
		}
		return result / count;
	}

}
