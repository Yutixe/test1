import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import org.chocosolver.solver.trace.Chatterbox;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;


/* Tiny Bin Packing solver, based on input with 3 objects and 2 bins; 
 * Model by Simple Linear Programming, using 1D arrays to represent objects and bins; 
 * Using 2D array to represent allocation and trivial-greedy searching method without any heuristics.
 * 
 * Author: Teng Yu  8/11/2015
 * 
 * Acknowledgement: Prof. K.Brown for online notes of Java-Choco introduction.
 */

public class TinyBinPacking {

	public static void main(String[] args) {
		
		//the array of objects/balls in which the value of each dimension is the size of the corresponding object.
		int[] objectSizes = {10,20,30};
		//the number of objects
	    int nObjects = objectSizes.length;
	    //the array of bins in which the value of each dimension is the capacity of the corresponding bin.
	    int[] binSizes = {40,30}; 
		//the number of bins
		int nBins = binSizes.length;
		
		//create the Java-Choco solver
		Solver solver = new Solver();
		
		//create the Variables		
		//an array of intVars, for the total size added to each bin
        IntVar[] binLoad = new IntVar[nBins];
        for (int bin = 0; bin < nBins; bin++) {
        binLoad[bin] = VariableFactory.enumerated("binLoad", 0, binSizes[bin], solver);
        }        
		//a 2D binary array, so that binPacking[i][j]==1 means that object j was placed in bin i
        IntVar[][] binPacking = VariableFactory.enumeratedMatrix("solution", nBins, nObjects, 0 , 1, solver);        
        //the transpose of the above matrix, so that we can work with an array for each object
		IntVar[][] binPackingT = new IntVar[nObjects][nBins];
        for (int bin = 0; bin < nBins; bin++) {
        	for (int object = 0; object < nObjects; object++) {
        		binPackingT[object][bin] = binPacking[bin][object];
        	}
        }
        
        //CONSTRAINTS        
        //for each object in a bin, add the sizes to get the bin load
        for (int bin = 0; bin<nBins; bin++) {
            solver.post(IntConstraintFactory.scalar(binPacking[bin], objectSizes, binLoad[bin]));        	
        }
        
        //for each object, make sure it is allocated in exactly 1 bin
        for (int object = 0; object < nObjects; object++) {
        	solver.post(IntConstraintFactory.sum(binPackingT[object], VariableFactory.fixed(1, solver)));
        }
        
        //SEARCH
        //trivial search method by findSolution; using Chatterbox to show the process
        Chatterbox.showSolutions(solver);
        solver.findSolution();
        Chatterbox.printStatistics(solver);
        
        //print out our own solution
        for (int bin = 0; bin < nBins; bin++) {
        	System.out.print("Bin " + bin + ": ");
        	for (int object = 0; object < nObjects; object++) {
        		if (binPacking[bin][object].getValue() == 1) {     //if object is in bin
        			System.out.print(object + "(" + objectSizes[object] + ") ");   //print the details
        		}
        	}
        	System.out.println("[" + binLoad[bin].getValue() + "]");    //print the bin load
        }        
	}

}
