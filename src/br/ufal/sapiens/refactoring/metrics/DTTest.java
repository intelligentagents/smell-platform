package br.ufal.sapiens.refactoring.metrics;

import java.io.File;
import java.io.IOException;

import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

public class DTTest {
	
	public DTTest() throws Exception {
		CSVLoader loader = new CSVLoader();
	    loader.setSource(new File("/home/hozano/Desktop/fe-devs-gantt.csv"));
	    Instances data = loader.getDataSet();
	    data.deleteAttributeAt(0);
	    data.setClassIndex(5);
		
		J48 classifier = new J48();
		classifier.buildClassifier(data);
		System.out.println(classifier.toString());
		System.out.println(classifier.prefix());
		
	}
	
	public static void main(String[] args) throws Exception {
		new DTTest();
		
	}

}
