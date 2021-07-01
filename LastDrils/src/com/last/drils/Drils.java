package com.last.drils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;


public class Drils {
	
	private  int pointsNumber = 51;
	private int  set_number=12;
	private double tMax = 200;
	private double best_score_solution=0.0;
	private double total_path_distance;
	private double total_path_profit;
	private int[] set_profit;
	private int[][] set_cluster;
	private int[] points_profit= new int [pointsNumber]; 
	public int[] points_cluster= new int [pointsNumber];
	private int set_data[][];
	private double[][] points = new double[pointsNumber][2];
	public double m_Distance[][] = new double[pointsNumber][pointsNumber];
	
	//randomSet is the first path of clusters indexes
	private	List<Integer> randomSet= new ArrayList<Integer>();
	private List<Integer> secondRandomSet = new ArrayList<Integer>();

	// randomPath is the first path contains one point from each cluster 
	private List<Integer> randomPath= new ArrayList<Integer>();

	// firstPath is the first path contains the first feasible solution
	private List<Integer> firstPath= new ArrayList<Integer>();
		
	// this path is the solution that is being optimized
	private List<Integer> solution = new ArrayList<Integer>();

	// we save in this list the best solution		
	private List<Integer> best_solution = new ArrayList<Integer>();


				
	// List<Integer> pX = new ArrayList<Integer>();
	private List<Integer> clustersOfPx = new ArrayList<Integer>();


	public static void main(String[] args) {
      Drils main = new Drils();
		

		main.read_data();
		main.prepare_data();
		main.algorithm();
		
	}
	
	public void read_data() {
		read_txt_file();
		readCSVfile("one.csv");
		fillDistanceMatrix();
	}
	
	private void read_txt_file() {
		Read_data_set fileReader = new Read_data_set();
		set_data = fileReader.read("file.txt");
		set_number = set_data.length;
	}
	
	private void readCSVfile(String fileName) {
		int i = 0;
		File file = new File(fileName);
		try {
			Scanner inputStream = new Scanner(file);
			inputStream.next();
			while (inputStream.hasNext()) {
				String data = inputStream.next();
				String values[] = data.split(";");
				for (int j = 0; j < 2; j++) {
					points[i][j] = Double.parseDouble(values[j + 1]);
				}
				i++;
			}
			inputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void fillDistanceMatrix() {
		for (int i = 0; i < pointsNumber; i++) {
			for (int j = 0; j < pointsNumber; j++) {
				m_Distance[i][j] = calculateDistanceBetweenPoints(i, j);
//				System.out.print(" "+m_Distance[i][j]);
			}
//System.out.println();
		}
	}
	
	private double calculateDistanceBetweenPoints(int a, int b) {
		double x1 = points[a][0];
		double x2 = points[b][0];
		double y1 = points[a][1];
		double y2 = points[b][1];
		return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
	}
	
	
	public void prepare_data() {
		fill_set_profit();
		fill_set_cluster();
		fill_points_profit();
		fill_points_cluster();
	}
	
	// fill_set_profit is : each cluster has its profit ****set_profit
	private void fill_set_profit() {
		
		set_profit = new int[set_number];
		for (int i = 0; i < set_number; i++) {			
			set_profit[i] = set_data[i][1];
//				System.out.println(" "+set_profit[i]);
		}
	}

	// fill_set_cluster is : each cluster has its vertices****set_cluster

	private void fill_set_cluster() {
		set_cluster = new int[set_number][];
		for (int i = 0; i < set_number; i++) {
			set_cluster[i] = new int[set_data[i].length - 2];
			for (int j = 0; j < (set_data[i].length - 2); j++) {
				set_cluster[i][j] = set_data[i][j + 2] - 1;
//System.out.print(" "+set_cluster[i][j]);
			}
//			System.out.println();
		}
	}
	
	/*
	 * the index of the array is the id point and the value of each case is the
	 * profit****points_profit
	 */

	private void fill_points_profit() {
		int index;
		for (int i = 0; i < set_data.length; i++) {
			for (int j = 2; j <set_data[i].length; j++) {
				index = set_data[i][j] - 1;
				points_profit[index] = set_data[i][1];
				
			}

		}
		
		for (int i=0;i <pointsNumber;i++) {
//			System.out.println(i +" * "+points_profit[i]);
		}

	}
	
	/*
	 * the index of the array is the id point and the value of each case is the
	 * cluster id****points_cluster
	 */

	private void fill_points_cluster() {
		int index;
		for (int i = 0; i < set_data.length; i++) {
			for (int j = 2; j < set_data[i].length; j++) {
				index = set_data[i][j] - 1;
				points_cluster[index] = i;

//					System.out.println("index " + index + "  " + points_cluster[index]);
			}

		}

	}

	public void algorithm () {
		set_into_randomSet(generateRandomSet(set_number));
		float sec;
		long start = System.currentTimeMillis();

		do {
			System.out.println("******************************");
//		for (int i=0;i<100;i++) {
			swapAlgorithm(randomSet);
			System.out.println("............................");
			System.out.println("The randomSet after the solution is");
	        System.out.println(randomSet);
	        System.out.println("............................");
	        System.out.println(" We escape to create the scondRandomSet ");
	        set_into_secondRandomSet(escape_to_create_secondRandomSolution(randomSet))  ;
	        System.out.println(".."+ secondRandomSet);
	        swapAlgorithm(secondRandomSet);
	        set_into_randomSet(crossover(randomSet,secondRandomSet));
	        System.out.println("The randomSet is : "+ randomSet);
//		}
		
		long end = System.currentTimeMillis();
		sec = (end - start) / 1000F;
	} while (sec <40 );
	
		System.out.println("...........................");
		System.out.println("...........................");
		System.out.println("...........................");
		System.out.println("best_solution*****"+ best_solution);
		System.out.println("best_score_solution***"+ best_score_solution);
		get_randomSet_from_solution_test(best_solution);
	}
	
	//Generate randomly the first path of clusters indexes starts and return to the origin.		
	public List<Integer> generateRandomSet(int pNumberOfVertices) {
		List<Integer> localrandomPath= new ArrayList<Integer>();
		for (int i = 0; i < pNumberOfVertices; i++) {
			localrandomPath.add(i);
		}
		localrandomPath.add(0);
		Collections.shuffle(localrandomPath.subList(1, pNumberOfVertices));
		System.out.println(localrandomPath);
		
		return localrandomPath;
	}
	
	
	public void set_into_randomSet(List<Integer> pPath) {
		randomSet.addAll(pPath);
		
	}
	
	public void set_into_randomPath(List<Integer> pPath) {
		randomPath.addAll(pPath);
		
	}
	
	public void set_into_firstPath(List<Integer> pPath) {
		firstPath.addAll(pPath);
	}
	
	
	public void swapAlgorithm(List<Integer>pRandomSet){
		set_into_randomPath(generateInitialSolution(pRandomSet));
		set_into_firstPath(generate_first_path(randomPath));
		local_search(solution,firstPath);
		randomPath.clear();
		firstPath.clear();
		compare_results(solution);
		get_randomSet_from_solution(solution,pRandomSet);
		solution.clear();	    
	}
	
//  This function is created only to generate the first random path	

	public List<Integer> generateInitialSolution(List<Integer> Prandomset) {
		List<Integer> localrandomPath= new ArrayList<Integer>();
		localrandomPath.add(0);
		int index = 0;
		for (int i = 1; i < set_number; i++) {
			index = findNearstPoint(index, Prandomset.get(i));
			localrandomPath.add(index);
		}
		localrandomPath.add(0);
		System.out.println(localrandomPath);
    return localrandomPath;
	}
	
	private int findNearstPoint(int pointA, int clusterB) {
		double distance;

		int index = set_cluster[clusterB][0];
		distance = m_Distance[pointA][index];
		for (int i = 1; i < set_cluster[clusterB].length; i++) {
			if (distance > m_Distance[pointA][set_cluster[clusterB][i]]) {
				distance = m_Distance[pointA][set_cluster[clusterB][i]];
				index = set_cluster[clusterB][i];
			}
		}
		return index;
	}
	
	public List<Integer> generate_first_path(List<Integer> pRandomPath) {
		double timeBudget;
		double lastDistance;
		double nextDistance;
		List<Integer> path= new ArrayList<Integer>();
		path.add(0);
		path.add(pRandomPath.get(1));
		timeBudget = tMax - (m_Distance[pRandomPath.get(0)][pRandomPath.get(1)]);

		for (int i = 1; i < pRandomPath.size() - 1; i++) {
			nextDistance = m_Distance[pRandomPath.get(i)][pRandomPath.get(i + 1)];
			lastDistance = m_Distance[pRandomPath.get(i + 1)][0];
			timeBudget = timeBudget - nextDistance - lastDistance;

			if (timeBudget > 0) {

				path.add(pRandomPath.get(i + 1));
			}

		}
		path.add(0);
		System.out.println(path);
		total_path_distance= calculate_path_distance(path);
		total_path_profit= calculate_path_profit(path);

		return path;
	}
	
	private double calculate_path_distance(List<Integer> pPath) {
		double distance = 0;
		for (int i = 0; i < pPath.size() - 1; i++) {
			distance = distance + m_Distance[pPath.get(i)][pPath.get(i + 1)];
		}
		System.out.println("path distance is  " + distance);
		return distance;
	}
	
	private int calculate_path_profit(List<Integer> path) {
		int profit_path = 0;
		for (int i = 0; i < path.size() ; i++) {
			profit_path = profit_path + points_profit[path.get(i)] ;
		}
		System.out.println("The new profit is " + profit_path);
		return profit_path;
	}
	
	public void local_search (List<Integer> pSolution, List<Integer> path) {
		double delta = 0;
//		verify_clear_list(pSolution);
		pSolution.addAll(path);	
		
		do {
//			System.out.println("three");
			delta = move(pSolution);
		} while (delta < 0);
		System.out.println("**********************************************");
		System.out.println("Here is our solution after the swap neighborhood");
		System.out.println(pSolution);
		total_path_distance= calculate_path_distance(pSolution);
		total_path_profit= calculate_path_profit(pSolution);
		System.out.println("**********************************************");
//		compare_results(pSolution );
		}
	
	public double move(List<Integer> solution) {
		
		double local_delta;
		
		for (int i = 2; i < solution.size() - 2; i++) {
			for (int j = i + 1; j < solution.size() - 1; j++) {
				
				local_delta = delta(solution, i, j);

				if (local_delta < 0) {
					
					swapElements(solution, j, i);

					valid_insert(solution.size() - 2);
//					System.out.println(solution);
//					System.out.println("local_delta"+local_delta);
					return local_delta;
				}

			}
		}
			
		return 0;
	}
	
	protected void swapElements(List<Integer> path, int j, int i) {
		int temp = path.get(j);
		path.set(j, path.get(i));
		path.set(i, temp);
	}
	
	private void valid_insert(int last_index) {
		double timeBudget;
		double nextDistance;
		double lastDistance;
//		System.out.println("one");
		timeBudget = tMax - calculate_path_distance(solution);
		if (last_index < set_number - 1) {
			nextDistance = m_Distance[randomPath.get(last_index)][randomPath.get(last_index + 1)];
			lastDistance = m_Distance[randomPath.get(last_index + 1)][0];
			timeBudget = timeBudget - nextDistance - lastDistance;
			
			if (timeBudget > 0) {
//				System.out.println("we insert");
	            solution.add(solution.size()-1,randomPath.get(last_index + 1));
//	            System.out.println();
//	            System.out.println("solution after the add ");
//	            System.out.println(solution);
//	            System.out.println();
			}
			
			
		}

	}
	
	public double delta(List<Integer> path, int pI, int pJ) {
		double delta = 0;
		double scoreOne = 0;
		double scoreTwo = 0;
		int i = pI;
		int j = pJ;
		
		if (j<path.size()-1) {
		
		if ((j - i) == 1 ||(j - i) == 2) {
			scoreOne = m_Distance[path.get(i - 1)][path.get(i)] + m_Distance[path.get(j)][path.get(j+1)];
			scoreTwo = m_Distance[path.get(i - 1)][path.get(j)] + m_Distance[path.get(i)][path.get(j+1)];		
} else {
	
			scoreOne = m_Distance[path.get(i - 1)][path.get(i)] + m_Distance[path.get(i)][path.get(i + 1)]
					+ m_Distance[path.get(j - 1)][path.get(j)] + m_Distance[path.get(j)][path.get(j+1)];

			scoreTwo = m_Distance[path.get(i - 1)][path.get(j)] + m_Distance[path.get(j)][path.get(i + 1)]
					+ m_Distance[path.get(j - 1)][path.get(i)] + m_Distance[path.get(i)][path.get(j+1)];
			
			
		}
		}
		
		
		delta = scoreTwo - scoreOne;
		return delta;
	}
	
	public void compare_results(List<Integer> pSolution) {
		double score = calculate_path_profit(pSolution);
		
		if (score > best_score_solution) {
			best_score_solution = score;
			best_solution.clear();
			best_solution.addAll(pSolution);
			System.out.println("The best solution is "+best_solution);
			System.out.println("The best score is "+ best_score_solution);
			System.out.println("-----------------------------------------");
			}

	}
	

	
	public void get_randomSet_from_solution(List<Integer> pSolution,List<Integer> pRandomSet) {
		int clusters_used;
		List<Integer> path = new ArrayList<Integer>();
		int a;
		for (int i = 0; i < pSolution.size()-1; i++) {
			a = points_cluster[pSolution.get(i)];
			path.add(a);
		}
		
		clusters_used=path.size();
 
		for (int i=clusters_used;i<pRandomSet.size();i++) {
			a = pRandomSet.get(i);
			path.add(a);
		}
		
		
		pRandomSet.clear();
		pRandomSet.addAll(path);
	}
	
	public List<Integer> escape_to_create_secondRandomSolution(List<Integer> pRandomSet) {
		
		int indexOne;
		int indexTwo;
		int temp;
		List<Integer> path = new ArrayList<Integer>();
		List<Integer> removed_list = new ArrayList<Integer>();
		path.addAll(pRandomSet);
		Random rand = new Random();
		indexOne = rand.nextInt(path.size()-2)+1;
temp= path.size()-indexOne;
//	System.out.println("indexOne is "+ indexOne);
	indexTwo= indexOne+ rand.nextInt(temp-1)+1;
//	System.out.println("temp "+ temp);
//	System.out.println("indexTwo "+indexTwo);
	removed_list.addAll(path.subList(indexOne,indexTwo ));
//	System.out.println("removed_list  "+removed_list);
	Collections.shuffle(removed_list);
//	System.out.println("removed_list after shuffle  "+removed_list);
	path.subList(indexOne,indexTwo ).clear();
//	System.out.println("after removing removed list "+pRandomSet );
	path.addAll(1,removed_list);
//	System.out.println(path);
	
	return path;
	
	}
	
	public void set_into_secondRandomSet(List<Integer> path) {
		secondRandomSet.addAll(path);
	}
	
	public List<Integer> crossover (List<Integer> randomSetOne, List<Integer> randomSetTwo) {
		int indexOne;
		int indexTwo;
		int temp;
		int randomNumber;
		List<Integer> path = new ArrayList<Integer>();
		List<Integer> pathTemp = new ArrayList<Integer>();

//		System.out.println("randomSetOne  : "+randomSetOne);
//		System.out.println("randomSetTwo  : "+randomSetTwo);
		/*
		 * we will take a subset of one route randomly
		 * if randomNumber=0 we will take the subset from randomSetOne
		 * otherwise we will take from randomSetTwo
		 */
		Random rand = new Random();
		randomNumber = rand.nextInt(2);
//		System.out.println(randomNumber);
		indexOne = rand.nextInt(randomSetOne.size()-2)+1;
		temp= randomSetOne.size()-indexOne;
//			System.out.println("indexOne is "+ indexOne);
			indexTwo= indexOne+ rand.nextInt(temp-1)+1;
//			System.out.println("indexTwo is "+ indexTwo);			
		
		
		if (randomNumber==0) {
			pathTemp.addAll(randomSetOne.subList(indexOne,indexTwo));
//			System.out.println("the taken subset is  "+ pathTemp);
			randomSetTwo.removeAll(pathTemp);
			path.addAll(randomSetTwo);
			path.addAll(1, pathTemp);
			
		}
		else {
			pathTemp.addAll(randomSetTwo.subList(indexOne,indexTwo));
//			System.out.println("the taken subset is  "+ pathTemp);
			randomSetOne.removeAll(pathTemp);
			path.addAll(randomSetOne);
			path.addAll(1, pathTemp);
			

		}
		
		System.out.println("sooo"+path);
		randomSetOne.clear();
		randomSetTwo.clear();
		return path;
		
	}
	
	public void get_randomSet_from_solution_test(List<Integer> pBestSolution) {
		
		List<Integer> path = new ArrayList<Integer>();
		int a;
		for (int i = 0; i < pBestSolution.size(); i++) {
			a = points_cluster[pBestSolution.get(i)];
			path.add(a);
		}
		calculate_path_distance(pBestSolution);
		System.out.println("Clusters used are ..."+path);
	}
	
}
