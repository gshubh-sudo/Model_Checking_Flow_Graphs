package lab2;

import java.util.*;

public class Solution {
	public static void main(String[] args) {
		String file = "lab2/Simple/simple.spec";
		Automaton a = new Automaton(file);
//		a.printGV();
		
		FlowGraph f = new FlowGraph("lab2/Simple/simple.cfg");
//		System.out.println(f.entryNodes);
//		System.out.println(f.returnNodes);
//		System.out.println(f.nodes);
//		System.out.println(f.methods);
//		Iterator<Integer> src = f.edges.keySet().iterator();
//		while (src.hasNext()) {
//			Integer currSrc = src.next();
//			System.out.println(currSrc);
//			Iterator<Pair<Integer, String>> curr = f.edges.get(currSrc).iterator();
//			while (curr.hasNext()) {
//				curr.next().printP();
//			}
//		}
		
		CFG c = new CFG();
		a.complement();
		c.productConstruction(f, a);
		
//		
//		System.out.println(c.initial);
		c.checkingEmptiness();
//		c.mapping.forEach((key,value)->System.out.println(key+":"+value));
//		c.final_map.forEach((key,value)->System.out.println(key+":"+value));
////		System.out.println(c.mapping);
//		c.mapping.forEach((key,value)->System.out.println(key+":"+value));
////		System.out.println(c.final_map);
//		System.out.println(c.final_map.size());
	}
}
