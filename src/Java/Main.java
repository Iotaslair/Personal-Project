import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;



public class Main {
	
	
	//ArrayList of the rows passed in, if you loop through the first ArrayList you get each row
	static ArrayList<ArrayList<String>> database = new ArrayList<ArrayList<String>>();
	//ArrayList of features that can be used thus far
	static ArrayList<String> features = new ArrayList<String>();
	
	static ArrayList<String> semicolon = new ArrayList<String>();
	static HashMap<String,ArrayList<String>> semicolonDict = new HashMap<String,ArrayList<String>>();
	
	public static void main(String[] args) {
		
		/*
		//code to test tree printing
		Tree<String> tree = makeTree("1");
		
		Node<String> root = tree.getRoot();
		
		Tree<String> two = makeTree("2");
		two.addToChildren(makeTree("3"), "three");
		two.addToChildren(makeTree("4"), "four");
		Tree<String> five = makeTree("5");
		five.addToChildren(makeTree("6"), "six");
		five.addToChildren(makeTree("7"), "seven");
		
		root.addToChildren(two, "two");
		root.addToChildren(five, "five");
		System.out.println(Tree.printTree(root));
		//tree.print();
		//seven.print();
		System.out.println("Program over");
		*/
		/*
		Tree<String> fakeOne = makeTree("1");
		Tree<String> fakeTwo = makeTree("2");
		fakeOne.addToChildren(fakeTwo, "Two");
		fakeTwo.addToChildren(makeTree("3"), "Three");
		System.out.println(Tree.printTree(fakeOne.getRoot()));
		*/
		
		
		
		semicolon.add("DevType");
		
		semicolon.add("CommunicationTools");
		semicolon.add("EducationTypes");
		semicolon.add("SelfTaughtTypes");
		semicolon.add("HackathonReasons");
		semicolon.add("LanguageWorkedWith");
		semicolon.add("LanguageDesireNextYear");
		semicolon.add("DatabaseWorkedWith");
		semicolon.add("DatabaseDesireNextYear");
		semicolon.add("PlatformWorkedWith");
		semicolon.add("PlatformDesireNextYear");
		semicolon.add("FrameworkWorkedWith");
		semicolon.add("FrameworkDesireNextYear");
		semicolon.add("IDE");
		semicolon.add("Methodology");
		semicolon.add("VersionControl");
		semicolon.add("AdBlockerReasons");
		semicolon.add("ErgonomicDevices");
		semicolon.add("Gender");
		semicolon.add("SexualOrientation");
		semicolon.add("RaceEthnicity");
		
		
		//Scanner reader = new Scanner(System.in);
		//String csvFile = "";
		int pruningLimit = 0;
		int base = 6;
		int currLevel = 0;
		int maxLevel = 70;
		
		/*
		System.out.println("Please put in the directory of the CSV including filename.csv");
		System.out.println("For me it is in the directory /Users/Me/A3/dm_a3/data/ID3Data.csv");
		csvFile = reader.nextLine();
		
		System.out.println("Please put in the pruning limit (This is the value after this many nodes is left in ");
		System.out.println("a example the tree returns a node with how likely it thinks a patient will go to the appointment)");
		System.out.println("Don't put any commas in");
		pruningLimit = reader.nextInt();
		
		//reader.close();
		*/
		
		//String csvFile = "/Users/billy/Data mining/A3/dm_a3/data/PreProcessedData no unique.csv";
		//String csvFile = "/Users/billy/Data mining/A3/dm_a3/data/PreProcessedData no unique age.csv";
		//String csvFile = "/Users/billy/Data mining/A3/dm_a3/data/ID3 Data.csv"; 
		//String csvFile = "/Users/billy/Data mining/A3/dm_a3/data/PreProcessedData no unique no neigh.csv"; 
		//String csvFile = "/Users/billy/Data mining/A3/dm_a3/data/PreProcessedData 200 no unique.csv";
		//String csvFile = "/Users/billy/Data mining/A3/dm_a3/data/PreProcessedData size 10 no unique special.csv";
		
		//String csvFile = "/Users/billy/Data mining/Personal-Project/data/StackOverflow Columns deleted commas deleted jobSatisfaction changed Salary.csv";
		String csvFile = "/Users/billy/eclipse-workspace/Personal Project/Salary Changed.csv";
		//String csvFile = "/Users/billy/Data mining/Personal-Project/data/StackOverflow Columns deleted commas deleted 500.csv";
		//String csvFile = "/Users/billy/Data mining/Personal-Project/data/StackOverflow learn 20.csv";
		
		String line = "";
		String cvsSplitBy = ",";
		
		
		long startTime = System.nanoTime();

		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

		    while ((line = br.readLine()) != null) {

		        // use comma as separator
		        String[] row = line.split(cvsSplitBy);
		        
		        //used for printing out the rows
		        /*
		        for(String x : row)
		        {
		        	System.out.print(x + " ");
		        }
		        System.out.println();
		        */
		        
		        if(row[0].equals("Hobby"))
		        	addFeatures(row);
		        else
		        	addDatabase(row);
		        
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		semicolonDict = createDict(database);
		
		/*
		for(String x : semicolon)
		{
			//System.out.println(x);
			System.out.println(x + " " + semicolonDict.get(x));
		}
		System.out.println();
		*/
		
		System.out.println("Starting to make your tree");
		System.out.println();
	    Tree<String> finished = DTL(database,features,"NA","JobSatisfaction", pruningLimit, base, currLevel, maxLevel);
	    System.out.println();
		finished.print();
		 
		System.out.println("Finished");
		//used to calculate how long it take to run the algorithm
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		double seconds = (double)duration / 1000000000.0;
		System.out.println("It took " + seconds + " seconds to complete the algorithm");
		
	}

	
	
	//The decision tree learner. This is the code Doucette gave us
	static Tree<String> DTL(ArrayList<ArrayList<String>> examples, ArrayList<String> attributes,String defaultValue, String Class, int prune, int base, int currLevel, int maxLevel)
	{
		currLevel++;
		//stops a tree from expanding too far
		if(currLevel > maxLevel)
		{
			return makeTree(atEnd(examples,Class));
		}
		
		//if examples is empty return a tree with default value
		if(examples.isEmpty())
		{
			return makeTree(defaultValue);
		}
		
		if(funct1(examples,"0"))
			return makeTree("0");
		if(funct1(examples,"1"))
			return makeTree("1");
		if(funct1(examples,"2"))
			return makeTree("2");
		if(funct1(examples,"3"))
			return makeTree("3");
		if(funct1(examples,"4"))
			return makeTree("4");
		if(funct1(examples,"5"))
			return makeTree("5");
		if(funct1(examples,"6"))
			return makeTree("6");
		if(funct1(examples,"7"))
			return makeTree("7");
		if(funct1(examples,"NA"))
			return makeTree("NA");
		
		//if we run out of features to split on then output how likely it is that the patient showed up to the appointment
		if(checkIfEmpty(attributes,Class))
		{
			return makeTree(atEnd(examples,Class));
		}
		
		//if examples is less than the pruning limit then return how likely it is that the patient showed up to the appointment
		if(examples.size() < prune)
		{
			return makeTree(atEnd(examples,Class));
		}
		
		//chooses the best attribute to split on based on information gain
		String best = chooseAttribute(examples,attributes,Class, base);
		System.out.println("Printing out best attribute " + best);
		//make a empty tree 
		Tree<String> tree= makeTree(best);
		//finds all the values in the row best and sets it equals to temp
		ArrayList<String> temp = funct2(examples,best);
		
		for(String x: temp)
		{
			//gets all the rows that have String x in the best column
			ArrayList<ArrayList<String>> careAbout = funct3(examples,best,x);
			//used for the default value when recursing 
			String mean = meanForJobs(careAbout,Class);
			//takes out best from the features the tree can split on 
			ArrayList<String> smaller = funct4(attributes, best);
			System.out.println("Going a level deeper");
			Tree<String> subtree = DTL(careAbout,smaller,mean,Class, prune,base, currLevel, maxLevel);
			System.out.println("Returned from the deeper level");
			//attaches the subtree we just made to our tree
			tree = attachTree(tree,subtree,x);
		}
		return tree;
	}
	
	static boolean checkIfEmpty(ArrayList<String> attributes, String Class)
	{
		ArrayList<String> temp = funct4(attributes,Class);
		return temp.isEmpty();
	}
	
	
	static String atEnd(ArrayList<ArrayList<String>> careAbout,String Class)
	{
		String rtn = "";
		int value = 0;
		int bottom = 0;
		int place = getIntForAttribute(Class); //place of the class (counting from 0)
		for(ArrayList<String> x : careAbout)
		{
			if(!x.get(place).equals("NA"))
			{
				value = value + Integer.parseInt(x.get(place));
				bottom++;
			}
		}
		if(bottom == 0)
			return "The algorithm expects that you have a rating of " + 0 + " for " + Class;
		rtn = "The algorithm expects that you have a rating of " + (int)value/bottom + " for " + Class;
		
		return rtn;
	}
	
	static String meanForJobs(ArrayList<ArrayList<String>> careAbout,String Class)
	{
		String rtn = "";
		int value = 0;
		int bottom = 0;
		int place = getIntForAttribute(Class); //place of the class (counting from 0)
		//System.out.println("careAbout in meanForJobs " + careAbout);
		for(ArrayList<String> x : careAbout)
		{
			if(!x.get(place).equals("NA"))
			{
				value = value + Integer.parseInt(x.get(place));
				bottom++;
			}
		}
		if(bottom == 0)
			return "" + 0;
		rtn = "" + (int) (value/bottom);
		return rtn;
	}
	
	
	//returns true if all elements in examples match the string thing (will be either 'Yes' or 'No')
	//called to check if all of the examples are made up of thing values
	static boolean funct1(ArrayList<ArrayList<String>> examples,String thing)
	{
		boolean rtn = true;
		for(ArrayList<String> x : examples)
		{
			if(!x.get(x.size()-1).equals(thing))
			{
				rtn = false;
			}
		}
		return rtn;
	}
	
	//returns all the values that the column best contains
	static ArrayList<String> funct2(ArrayList<ArrayList<String>> examples, String best) 
	{
		//System.out.println("Inside of funct2 " + best);
		int x = getIntForAttribute(best);
		//System.out.println("Location of best " + x);
		ArrayList<String> rtn = new ArrayList<String>();
		//System.out.println("Examples" + examples);
		for (ArrayList<String> loop : examples)
		{
			//System.out.println("Inside of funct2 loop");
			//System.out.println("Loop" + loop);
			if(rtn.contains(loop.get(x)))
			{
				//is contained so do nothing
			}
			else
			{
				rtn.add(loop.get(x));
			}
		}
		return rtn;
	}
	
	//returns a ArrayList<ArrayList<String>>, returns all rows in examples where in the best column its value.equals(x)
	static ArrayList<ArrayList<String>> funct3(ArrayList<ArrayList<String>> examples, String best, String x)
	{
		ArrayList<ArrayList<String>> rtn = new ArrayList<ArrayList<String>>();
		int place = getIntForAttribute(best);
		
		for(ArrayList<String> outerGuy : examples)
		{
			if(outerGuy.get(place).equals(x))
			{
				rtn.add(outerGuy);
			}
		}
		return rtn;
	}
	
	//returns the more common true or false within careAbout
	static boolean mean(ArrayList<ArrayList<String>> careAbout,String Class) 
	{
		int yes = 0;
		int no = 0;
		int place = getIntForAttribute(Class); //place of the class (counting from 0)
		for(ArrayList<String> x : careAbout)
		{
			if(x.get(place).equals("Yes"))
				yes++;
			else if(x.get(place).equals("No"))
				no++;
			else
			{
				//weird thing happened in mean
				assert(false);
			}
		}
		
		if(yes > no)
			return true;
		else 
			return false;
	}
	
	//returns the % of yes in careAbout, used when you run out of features and when you're past the pruning limit
	static double specialMean(ArrayList<ArrayList<String>> careAbout,String Class) 
	{
		int yes = 0;
		int no = 0;
		int place = getIntForAttribute(Class); //place of the class (counting from 0)
		for(ArrayList<String> x : careAbout)
		{
			if(x.get(place).equals("Yes"))
				yes++;
			else if(x.get(place).equals("No"))
				no++;
			else
			{
				//weird thing happened in mean
				assert(false);
			}
		}
		
		return 100 * ((yes * 1.0)/(yes + no));
	}
	
	//returns a ArrayList<Strings> of everything in attributes except for best
	static ArrayList<String> funct4(ArrayList<String> attributes, String best) 
	{
		ArrayList<String> smaller = new ArrayList<String>();
		for(String x : attributes)
		{
			smaller.add(x);
		}
		
		if(smaller.contains(best))
		{
			smaller.remove(best);
			return smaller;
		}
		else
		{
			//weird thing happened where best didn't show up in attributes
			assert(false);
		}
		return null;
	}
	
	static HashMap<String,ArrayList<String>> createDict(ArrayList<ArrayList<String>> database)
	{
		HashMap<String,ArrayList<String>> temp = new HashMap<String,ArrayList<String>>();
		for(String x : semicolon)
		{
			ArrayList<String> isNull = createForDict(database,x);
			if(!isNull.isEmpty())
				temp.put(x, isNull);
		}
		return temp;
	}
	
	static ArrayList<String> createForDict(ArrayList<ArrayList<String>> examples, String best)
	{
		int x = getIntForAttribute(best);
		//System.out.println("Location of best " + x);
		ArrayList<String> rtn = new ArrayList<String>();
		//System.out.println("Examples" + examples);
		for (ArrayList<String> loop : examples)
		{
			String[] special = loop.get(x).split(";");
			for(String inLoop : special)
			{
				//System.out.println(inLoop);
				if(rtn.contains(inLoop))
				{
					//is contained so do nothing
				}
				else
				{
					rtn.add(inLoop);
				}
			}
		}
		return rtn;
	}
		
	//Uses Information gain to calculate what the best attribute is
	static String chooseAttribute(ArrayList<ArrayList<String>> examples, ArrayList<String> attributes,String Class, int base) 
	{
		String rtn = "";
		double best = -1.0;
		//a dictionary of each attribute and their information gain value
		HashMap<String,Double> gains = new HashMap<String,Double>();
		//loops through the available attributes
		for(String attribute: attributes)
		{
			//if the attribute we're working on is the class then skip over it
			if(attribute.equals(Class))
				continue;
			
			//The set of all databases with String x (defined below) in the attribute column 
			ArrayList<ArrayList<ArrayList<String>>> subset = new ArrayList<ArrayList<ArrayList<String>>>();
			
			if(semicolon.contains(attribute))
			{
				//System.out.println("Running " + attribute + " through semicolon things");
				HashMap<String,ArrayList<String>> temp = createDict(examples);
				for(String x : temp.get(attribute))
				{
					ArrayList<ArrayList<String>> careAbout = funct3Semi(examples, attribute,x);
					//System.out.println("Creating dictionary for " + x + " " + careAbout);
					subset.add(careAbout);
				}
				
				//System.out.println(attribute + " " + informationGainSemicolon(examples,subset,attribute,Class,temp));
				//System.out.println();
				gains.put(attribute,informationGainSemicolon(examples,subset,attribute,Class,temp,base));
			}
			else
			{
				//the set of all Strings in attribute 
				ArrayList<String> temp = funct2(examples,attribute);
				//loops through all values in the row attribute
				for(String x: temp)
				{
					//grabs all the rows with string x in the attribute column
					ArrayList<ArrayList<String>> careAbout = funct3(examples,attribute,x);
					//adds careAbout to the subset 
					subset.add(careAbout);
				}
				//adds the string and its information gain value to the dictionary gains 
				gains.put(attribute,informationGain(examples, subset, attribute,Class,base));
			}
		}
		
		ArrayList<String> fakeAttributes = funct4(attributes,Class);
		
		//chooses highest information gain
		for(int i = 0; i < gains.size(); i++)
		{
			//System.out.println("Information gain for " + fakeAttributes.get(i) + " " + gains.get(fakeAttributes.get(i)));
			if( gains.get(fakeAttributes.get(i)) > best)
			{
				best = gains.get(fakeAttributes.get(i));
				rtn = fakeAttributes.get(i);
			}
		}
		//System.out.println();
		//returns the string with the highest information gain
		return rtn;
	}
	
	static ArrayList<ArrayList<String>> funct3Semi(ArrayList<ArrayList<String>> examples, String best, String x)
	{
		ArrayList<ArrayList<String>> rtn = new ArrayList<ArrayList<String>>();
		int place = getIntForAttribute(best);
		
		for(ArrayList<String> outerGuy : examples)
		{
			if(outerGuy.get(place).contains(x))
			{
				rtn.add(outerGuy);
			}
		}
		return rtn;
	}
	
	// returns a double for the information gain
	static double informationGainSemicolon(ArrayList<ArrayList<String>> inFirst, ArrayList<ArrayList<ArrayList<String>>> Databases, String column, String Class, HashMap<String,ArrayList<String>> dict,int base)
	//								database essentially
	{
		//calculates the entropy of the full set
		double rtn = 0.0;
		double top = 0.0; //should have been named first but ran out of names
		double bottom = 0.0; //same as above but for second 
		
		//calculates the entropy of the first set
		ArrayList<Double> temp = new ArrayList<Double>();
		
		for(String x : funct2(inFirst,Class))
		{
			temp.add(funct5(inFirst,Class, x));
		}
		top = informationContent(temp,base);
		//System.out.println("Top " + column + " " + top);
		//
		int inSecondSize = 0;
		for(ArrayList<ArrayList<String>> temp1 : Databases)
		{
			for(ArrayList<String> temp2 : temp1)
				inSecondSize++;
		}
		
		//calculates the second part of information gain (each subset)
		//looping through each of the subsets
		for(ArrayList<ArrayList<String>> subset : Databases)
		{
			//thing to pass to information content
			ArrayList<Double> second = new ArrayList<Double>();
			//gets all the Strings in the subset
			ArrayList<String> resultFromFunct2 = funct2(subset,Class);
			//System.out.println("Subset " + subset);
			//System.out.println("resultFromFunct2 " + resultFromFunct2);
			//loops through the thing from above
			for(String x : resultFromFunct2)
			{
				double resultFromFunct5 = funct5(subset,Class, x);
				second.add(resultFromFunct5);
			}
			//System.out.println("Probabilities for " + column + " " + second);
			//calculates the second component of the information gain
			bottom = bottom + ( (subset.size()/(inSecondSize * 1.0)) * informationContent(second,base) );
		}
		//System.out.println("First term in InformationGain " + column + " " + top);
		//System.out.println("Second term in InformationGain " + column + " " + bottom);
		//System.out.println();
		rtn = top - bottom;
		return rtn;
	}
	
	// returns a double for the information gain
	static double informationGain(ArrayList<ArrayList<String>> inFirst, ArrayList<ArrayList<ArrayList<String>>> Databases, String column, String Class,int base)
	//								database essentially
	{
		//calculates the entropy of the full set
		double rtn = 0.0;
		double top = 0.0; //should have been named first but ran out of names
		double bottom = 0.0; //same as above but for second 
		
		//calculates the entropy of the first set
		ArrayList<Double> temp = new ArrayList<Double>();
		
		//System.out.println(funct2(inFirst,Class));
		
		for(String x : funct2(inFirst,Class))
		{
			temp.add(funct5(inFirst,Class, x));
		}
		//System.out.println("Probabilities for top " + column + " " + temp);
		top = informationContent(temp,base);
		
		//
		int inSecondSize = 0;
		for(ArrayList<ArrayList<String>> temp1 : Databases)
		{
			for(ArrayList<String> temp2 : temp1)
				inSecondSize++;
		}
		
		//calculates the second part of information gain (each subset)
		//looping through each of the subsets
		for(ArrayList<ArrayList<String>> subset : Databases)
		{
			//thing to pass to information content
			ArrayList<Double> second = new ArrayList<Double>();
			//gets all the Strings in the subset
			ArrayList<String> resultFromFunct2 = funct2(subset,Class);
			//loops through the thing from above
			for(String x : resultFromFunct2)
			{
				double resultFromFunct5 = funct5(subset,Class, x);
				second.add(resultFromFunct5);
			}
			//System.out.println("Probabilities for " + column + " " + second);
			//calculates the second component of the information gain
			bottom = bottom + ( (subset.size()/(inSecondSize * 1.0)) * informationContent(second,base) );
		}
		//System.out.println("First term in InformationGain " + column + " " + top);
		//System.out.println("Second term in InformationGain " + column + " " + bottom);
		rtn = top - bottom;
		return rtn;
	}

	//returns probability of for a string x in the parameter in in a specific column column 
	static double funct5(ArrayList<ArrayList<String>> in, String column, String careAbout)
	{
		double rtn = 0.0;
		double bottom = in.size();
		int place = getIntForAttribute(column);
		for(ArrayList<String> x : in)
		{
			if(x.get(place).equals(careAbout))
				rtn++;
		}
		//System.out.println("rtn " + rtn);
		//System.out.println("bottom " + bottom);
		rtn = rtn / bottom;
		return rtn;
	}
	
	//returns probability of for a string x in the parameter in in a specific column column 
	static double funct5Semi(ArrayList<ArrayList<String>> in, String column, String careAbout)
	{
		double rtn = 0.0;
		double bottom = in.size();
		int place = getIntForAttribute(column);
		
		System.out.println("Value we're searching for in column " + column + " funct5Semi " + careAbout);
		
		for(ArrayList<String> x : in)
		{
			if(x.get(place).contains(careAbout))
				rtn++;
		}
		System.out.println("Number of times " + careAbout + " showed up in the dataset " + rtn);
		System.out.println("bottom " + bottom);
		rtn = rtn / bottom;
		return rtn;
	}
	
	//returns a double that measures entropy 1 = high entropy 0 = low entropy
	//takes in a ArrayList of probabilities for each item
	static double informationContent(ArrayList<Double> in, int base)
	{
		double rtn = 0.0;
		for(double x : in)
		{
			if(x == 0.0)
				rtn = rtn + 0;
			else
				rtn = rtn + ( -x*((Math.log10(x))/Math.log10(base)));
		}
		return rtn;
	}
	
	//returns the index of the string best in features
	static int getIntForAttribute(String best)
	{
		return features.indexOf(best);
	}
	
	//attaches a tree child to a parent tree and sets the path to do so
	static Tree<String> attachTree(Tree<String> parent, Tree<String> child, String path)
	{
		parent.addToChildren(child,path);
		return parent;
	}
	
	//makes a tree out the in variable
	static Tree<String> makeTree(String in)
	{
		//Node<String> examplesNode = new Node<String>(in);
		
		return new Tree<String>(in);
	}

	//sets up the features
	static void addFeatures(String[] in)
	{
		for(String x : in)
		{
			features.add(x);
		}
	}
	
	//sets up the database
	static void addDatabase(String[] in)
	{
		ArrayList<String> temp = new ArrayList<String>();
		for( String x : in)
		{
			temp.add(x);
		}
		
		database.add(temp);
	}
	
	
}

