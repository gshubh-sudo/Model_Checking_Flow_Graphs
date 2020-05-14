package lab2;

import java.util.*;
import java.io.*;
import java.lang.*;

class Pair <Integer,String> {
	Integer node;
	String method;
	public Pair(Integer node, String method){
		this.node=node;
		this.method=method;
	}
	
	public void printP() {
		System.out.println(node);
		System.out.println(method);
	}
}

public class FlowGraph {
	public Map<String, Integer> entryNodes = new HashMap<String, Integer>();
	public Set<Integer> returnNodes = new HashSet<Integer>();
	public Set<Integer> nodes = new HashSet<Integer>();
	public Set<String> methods = new HashSet<String>();
	public Map<Integer, Set<Pair<Integer, String>>> edges = new HashMap<Integer, Set<Pair<Integer, String>>>();
	
	public FlowGraph() {
		
		
	}
	
	public FlowGraph(String file) {
		try {
			InputStream input = new FileInputStream(file);
			Scanner s = new Scanner(input);
			while (s.hasNextLine()) {
				String currentLine = (String) s.nextLine();
				String[] curr = currentLine.split(" ");
				if (curr[0].equals("node")) {
					Integer num = (Integer.valueOf(curr[1].substring(1)));
					String sym = curr[2].substring(5, curr[2].length() - 1);
					methods.add(sym);
					nodes.add(num);
					if (curr.length == 4) {
						if (curr[3].equals("entry")) {
							entryNodes.put(sym, num);
						}
						if (curr[3].equals("ret")) {
							returnNodes.add(num);
						}
					}
					
				}
				
				if (curr[0].equals("edge")) {
					Integer num1 = (Integer.valueOf(curr[1].substring(1)));
					Integer num2 = (Integer.valueOf(curr[2].substring(1)));
					String sym = curr[3];
					methods.add(sym);
					Pair<Integer, String> p = new Pair<Integer, String>(num2, sym);
					if (edges.get(num1)==null) {
						Set<Pair<Integer, String>> pairs = new HashSet<Pair<Integer, String>>();
						pairs.add(p);
						edges.put(num1, pairs);
					}
					else {
						System.out.println(num1);
						edges.get(num1).add(p);
					}
				}
			}
		}
		catch (FileNotFoundException e) {
			System.out.println("31");
			System.out.println(e);
		}
		
		
	}

	
} 