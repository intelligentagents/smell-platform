package br.ufal.sapiens.refactoring.metrics;

/*
 * Created on Dec 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
import java.io.*;
import java.util.Vector;

import javax.swing.JFileChooser;

import weka.classifiers.functions.geneticprogramming.*;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.util.Enumeration;

/**
 * @author Yan Levasseur
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GPTest {

	static final int UNDEFINED = -1;
	
	// Problem types
	static final int CLASSIFICATION = 0;
	static final int REGRESSION = 1;
	
	// GP styles
	static final int TESTPARTS = 1;
	static final int ROULETTE = 2;
	static final int TOURNAMENT = 3;
	static final int CUSTOM = 4;
	
	public static void main(String args[]){
		try{
			
//			 ****************** Test variables	
			
			boolean wantPrint = true;
			boolean wantDetails = false;
			PrintStream PS = System.out;
			// PrintStream PS = new PrintStream("Test_output.txt");
			int problemType = UNDEFINED;
			int GPType = CUSTOM;			
			
//			 ****************** Obtain training Data	
			
			int nbOfClasses;
			int nbOfAttributes;
			
//	        JFileChooser fileChooser = new JFileChooser();
//	        fileChooser.showOpenDialog(null);	                    
//			FileReader FR = new FileReader(fileChooser.getSelectedFile().getAbsolutePath());
//			Instances instances = new Instances(FR);
			
			DataSource src = new DataSource("gantt-gc.csv");
			Instances instances = src.getDataSet();
			
			PS.println("\nInput file was successfully read.");
			
//			 ****************** Check type of problem from database
			
			nbOfAttributes = instances.numAttributes();
			instances.setClassIndex(nbOfAttributes - 1);
			if(instances.attribute(instances.numAttributes()-1).isNominal()){
				nbOfClasses = instances.numClasses();
				problemType = CLASSIFICATION;
			}else{
				nbOfClasses = 0;
				problemType = REGRESSION;
			}
						
			if(wantPrint){
				if(problemType == CLASSIFICATION)
					PS.print("\nClassification problem.\n");
				else if(problemType == REGRESSION)
					PS.print("\nSymbolic Regression problem.\n");
			}
			
			
//			 ****************** Parameters		
			
			int size = 2000;
			double valProp = 0.2;
			int depth = 3;
			int generations = 50;
			
//			 ****************** Detailed parameters
			
			int mStyle = 0;
			int tCross = 5;
			int tMut = 3;
			int tNew = 10;
			double cross = 0.85;
			double mutation = 0.15;
			double newP = 0.00;
			double pI = 0.5;
			double pFC = 0.5; 
			
//			 ****************** Program parts testing section
	
			if(GPType == TESTPARTS){
/*				FitnessEvaluator FE = new ClassifierEvaluator();
				GeneticParameters GP = new GeneticParameters();
				GP.setParamsForStandardProportionnalGP(size, valProp, depth, nbOfAttributes-1, nbOfClasses, 
						cross, mutation, newP);
				ProgramRules PR = GP.getProgramRules();
				PR.setADFR(new ADFRules(2,2,2,2));
				
				MainProgramTree firstProgram = new MainProgramTree(PR);
				firstProgram.fullInit(PR, FE, instances);
				
				MainProgramTree secondProgram = new MainProgramTree(PR);
				secondProgram.fullInit(PR, FE, instances);
				PS.print("\nPrograms were created.");
				
				PS.print("\nProgram no.1\n" + firstProgram.toString(null, null, PR) + "\n");
				PS.print("\nProgram no.2\n" + secondProgram.toString(null, null, PR) + "\n");
				
				PS.print("\nHere goes crossover.");
				firstProgram.crossover(PR, secondProgram, FE, instances, true);
				PS.print("\nAfter crossover.\n");
				
				PS.print("\nProgram no.1\n" + firstProgram.toString(null, null, PR) + "\n");
				PS.print("\nProgram no.2\n" + secondProgram.toString(null, null, PR) + "\n");
	*/		
			}else{
			
//			 ****************** Full program testing zone
				
				if(wantPrint) PS.print("\nReadying experiment.");		
				GeneticParameters GP = new GeneticParameters();
				
				if(GPType == ROULETTE){
//					 THIS IS STD GP WITH PROPORTIONNAL SELECTION (ROULETTE) 
//					 AND ELITISM FROM POPULATION FORMED OF PARENTS AND CHILDREN
					GP.setParamsForStandardProportionnalGP(size, valProp, depth, nbOfAttributes-1, nbOfClasses,
							cross, mutation, newP);
				}
				
				if(GPType == TOURNAMENT){
//					 THIS IS STD TOURNAMENT GP
					GP.setParamsForStandardTournamentGP(size, valProp, mStyle, tCross, tMut,
						tNew, cross, mutation, newP, depth, nbOfAttributes-1,
						nbOfClasses, pI, pFC);
				}
				
				if(problemType == REGRESSION){
					GP.setFitnessEvaluator(new RMSEEvaluator());
				}
				
				if(GPType == CUSTOM){
					
					DataPreProcessor dP = null; // new LinearNormalizationPreProcessor();
					
					size = 2000; depth = 6; generations = 50;
					int inputs = nbOfAttributes-1;
					int classes = nbOfClasses;
					pI = 0.5; pFC = 0.5;
					
					int nADFS=1, minNbArgsADF=1, maxNbArgsADF=2, mxDepthADF=3;
					ADFRules ADFR = new ADFRules(nADFS, minNbArgsADF, maxNbArgsADF, mxDepthADF);
					FunctionRules FRules = new FunctionRules(ADFR);
					
					ProgramRules PR = new ProgramRules(depth, inputs, classes, pI, pFC, ADFR, FRules);
					
					FitnessEvaluator FE;
					if(problemType == REGRESSION){
						FE = new RMSEEvaluator();
					}else{
						FE = new ClassifierEvaluator();
					}
					PopulationInitializer PI = new TreePopHalfHalfInitializer();
					
					Vector operators = new Vector();
					GeneticOperator GPO;
					
					int nbOfParents = 8;    	int nbOfChildrenMade = 4;    	double proportion = 0.9;
			    	GPO = new TreeCrossoverOperator(nbOfParents, nbOfChildrenMade, proportion);
			    	if(proportion > 0.0) operators.add(GPO);
			    	
			    	nbOfParents = 5;    	nbOfChildrenMade = 2;    	proportion = 0.08;
			    	GPO = new TreeMutationOperator(nbOfParents, nbOfChildrenMade, proportion);
			    	if(proportion > 0.0) operators.add(GPO);
			    	
			    	nbOfParents = 1;    	nbOfChildrenMade = 1;    	proportion = 0.00;
			    	GPO = new TreeNodeMutationOperator(nbOfParents, nbOfChildrenMade, proportion);
			    	if(proportion > 0.0) operators.add(GPO);
			    	
			    	nbOfParents = 1;    	nbOfChildrenMade = 1;    	proportion = 0.00;
			    	GPO = new ReproductionOperator(nbOfParents, nbOfChildrenMade, proportion);
			    	if(proportion > 0.0) operators.add(GPO);
			    	
			    	nbOfParents = 0;    	nbOfChildrenMade = 2;    	proportion = 0.02;
			    	GPO = new NewProgramTreeOperator(nbOfParents, nbOfChildrenMade, proportion);
			    	if(proportion > 0.0) operators.add(GPO);
					
			    	ProgramSelector chosenPS = new TournamentSelector();
			    	EliteManager chosenEM = new SimplifyAndKeepBestsEliteManager(20);
			    	EvolutionController EC = new ContinuousEvolutionController();
			    	
					GP = new GeneticParameters(dP, size, valProp, PR, /* FE ,*/ PI, operators, chosenPS, chosenEM, EC);
					GP.setFitnessEvaluator(FE);
				}

				GPExperiment theExperiment = new GPExperiment(GP, instances);				
				if(wantPrint) PS.print("\n\n** GP Experiment **\n" + theExperiment.toString());
				
				if(wantPrint) PS.print("\n*** Experiment ready to go. ***\n");
				
				if(wantPrint) PS.print("\nCreating initial population.\n");
				theExperiment.createPopulation();
				if(wantPrint) PS.print("\nInitial Best Program, training fitness = " + theExperiment.getBestProgramTrainingFitness());
				if(wantPrint) PS.print("\n" + theExperiment.getBestProgramString() + "\n");
				if(wantDetails) PS.print("\nInitial population : ");
				if(wantDetails) PS.print(theExperiment.getPopulationString());
				
				int i = 1;
				while(i < generations){
					theExperiment.oneGeneration();
					i++;
					if(wantPrint) PS.print("\nBest program from generation " + i + "\n" + theExperiment.getBestProgramString() + "\n");
					if(wantDetails) PS.print("\n\nPopulation from generation " + i + " :");
					if(wantDetails) PS.print(theExperiment.getPopulationString());
				}
				
				if(wantPrint) PS.print("\n\nGenetic experiment finished run!");
				if(wantPrint & (PS != System.out)) PS.close();
				
				System.out.print("\nHere is the elite population at the end of run :");
				System.out.print("\n" + theExperiment.getEliteString() + "\n");
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}