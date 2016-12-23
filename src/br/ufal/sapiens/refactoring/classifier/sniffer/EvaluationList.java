package br.ufal.sapiens.refactoring.classifier.sniffer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class EvaluationList {
	
	private List<Float> list;
	
	public EvaluationList() {
		this.list = new ArrayList<Float>();
	}
	
	public void add(Float value) {
		this.list.add(value);
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
