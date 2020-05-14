package lab2;

import java.util.*;

import com.sun.javafx.collections.MappingChange;

class Triplet<FIRST,SECOND, THIRD> {
	FIRST t0;
	SECOND t1;
	THIRD t2;
	public Triplet(FIRST node, SECOND method, THIRD t2){
		this.t0=node;
		this.t1=method;
		this.t2 = t2;
		
	}
	
	public String toString() {
		return  "[ q" + String.valueOf(t0) + " v" + (String) t1 +" q"+ String.valueOf(t2) + " ]";
	}
	
	public int hashCode() {
		return (String.valueOf(t0)+this.t1+String.valueOf(t2)).hashCode();
	}
	@SuppressWarnings("unchecked")
	public boolean equals(Object a) {
		if(((Triplet<FIRST,SECOND, THIRD>)a).t0 == this.t0 && ((Triplet<FIRST,SECOND, THIRD>) a).t1.equals(this.t1) && ((Triplet<FIRST,SECOND, THIRD>)a).t2 == this.t2) {
			return true;
		}
		return false;
	}
}

public class CFG {

	public Integer initial;
//	public Set<Symbols> v = new HashSet<Symbols>();
//	public Set<Symbols> t = new HashSet<Symbols>();
//	public Map<Symbols, Symbols[]> rules = new HashMap<Symbols, Symbols[]>();
	
	
	public Map<Triplet<Integer, String, Integer>, Integer> mapping = new HashMap<>();
	

	public Map<Integer, Set<List<Integer>> >final_map= new HashMap<>();
	

	public CFG() {
		
	}

	public void productConstruction(FlowGraph f, Automaton d) {
		initial = 0;
		Iterator<Integer> src= d.getStates().iterator();
		
		
		// made the hashmap for all the triplets

		while (src.hasNext()) {
			Iterator<Integer> dst= d.getStates().iterator();

			Integer srcEnter= Integer.valueOf(src.next());

			while (dst.hasNext()) {
				Iterator <Integer> node = f.nodes.iterator();

				Integer dstEnter = Integer.valueOf(dst.next());

				while (node.hasNext()) {

					Integer nodeEnter= node.next();

					mapping.put(new Triplet<Integer, String, Integer>(Integer.valueOf(srcEnter),String.valueOf(nodeEnter), dstEnter), mapping.size() + 1);


					// adding to the final transition map
					final_map.put(mapping.size(), new HashSet<List<Integer>>());
				}
				Iterator <String> methods=f.methods.iterator();

				while(methods.hasNext()){

					String methodsEnter = methods.next();

					mapping.put(new Triplet<Integer, String, Integer>(srcEnter,methodsEnter,dstEnter),mapping.size()+1);
					// adding to the final transition map
					final_map.put(mapping.size(), new HashSet<List<Integer>>());
				}
			}
		}
		
		Iterator <String> symbols = d.getSymbols().iterator();
		while(symbols.hasNext()) {
			String sym = symbols.next();
			Triplet<Integer, String, Integer> tuple = new Triplet<Integer, String, Integer>(-1, String.valueOf(sym), -1);
			mapping.put(tuple, mapping.size()+1);
		}
		mapping.put(new Triplet<Integer,String,Integer>(-1,"eps",-1), mapping.size()+1);
		
		
		//Production rule 5 as well
		Iterator <Integer> startState = d.trans.keySet().iterator();
		while (startState.hasNext()) {
			Integer a = Integer.valueOf(startState.next());
			Map<Integer, Set<String>> end = (HashMap<Integer, Set<String>>) d.trans.get(a);
			Iterator <Integer> endState = end.keySet().iterator();
			while (endState.hasNext()) {
				Integer b = Integer.valueOf(endState.next());
				Iterator<String> symIter = end.get(b).iterator();
				while (symIter.hasNext()) {
					String s = symIter.next();
					Triplet<Integer, String, Integer> toInsert = new Triplet<Integer, String, Integer>(a, String.valueOf(s), b);
//					
					List<Integer> l = new ArrayList<Integer>();
					Triplet<Integer, String, Integer > tuple = new Triplet<Integer, String, Integer>(-1, s, -1); 

					l.add(mapping.get(tuple));
					System.out.println(new Triplet<Integer, String, Integer>(a, String.valueOf(s), b)+"-> "+tuple);
					if (final_map.get(mapping.get(toInsert)) == null) {
						Set<List<Integer>> set = new HashSet<List<Integer>>();
						set.add(l);
						final_map.put(mapping.get(toInsert), set);
						
					}
					else {
						final_map.get(mapping.get(toInsert)).add(l);
						
					}
					
					
				}
			}
			
		}




		// adding the S

		// assumed that v0 is 0 and q0 is also zero

		final_map.put(0,new HashSet<List<Integer>>());
		Iterator <Integer> fStates = d.getAcceptingStates().iterator();

		while(fStates.hasNext()){
			List<Integer> l = new ArrayList<Integer>();
			Triplet<Integer, String, Integer > tuple = new Triplet<Integer, String, Integer>(0, String.valueOf(0),fStates.next()); 
			l.add(mapping.get(tuple));
			final_map.get(0).add(l);
//			System.out.println(0 + "->" + tuple);
		}

		//step 1 done



		//begin step 2 for all the epsilon edges 

		Iterator<Integer> keysFlowGraph = f.edges.keySet().iterator();
		while(keysFlowGraph.hasNext()){

			Integer key= keysFlowGraph.next();
			
			Set<Pair<Integer, String>> setofedges = (Set<Pair<Integer, String>>) f.edges.get(key);
			
			Iterator<Pair<Integer, String>> pairCalls = setofedges.iterator();

			while(pairCalls.hasNext()){
				Pair<Integer,String> pairCall = pairCalls.next();

				if (pairCall.method.equals("eps")){
					src= d.getStates().iterator();
					while(src.hasNext()){
						Iterator<Integer> dst= d.getStates().iterator();

						Integer srcEnter= src.next();

						while (dst.hasNext()) {
							Integer dstEnter= dst.next();

							List<Integer> l = new ArrayList<Integer>();
							Triplet<Integer, String, Integer> sourceTuple = new Triplet<Integer, String, Integer>(srcEnter,String.valueOf(key) , dstEnter);
							Triplet<Integer, String, Integer> destTuple = new Triplet<Integer, String, Integer>(srcEnter, String.valueOf(pairCall.node), dstEnter);
							l.add(mapping.get(destTuple));
							
//							System.out.println(sourceTuple +" -> "+ destTuple);
							
							if (final_map.get(mapping.get(sourceTuple)) == null) {
								Set<List<Integer>> set = new HashSet<List<Integer>>();
								set.add(l);
								final_map.put(mapping.get(sourceTuple), set);
								if (mapping.get(sourceTuple)==84) {
									System.out.println(53);
								}
							}
							else {
								final_map.get(mapping.get(sourceTuple)).add(l);
								if (mapping.get(sourceTuple)==84) {
									System.out.println(54);
								}
							}


						}
					}
				}
			}
			

		}
		
		// step 2 done
		
		//step 3
		int count=0;
		keysFlowGraph = f.edges.keySet().iterator();
		while(keysFlowGraph.hasNext()){

			Integer key= keysFlowGraph.next();
			
			Set<Pair<Integer, String>> setofedges = (Set<Pair<Integer, String>>) f.edges.get(key);
			
			Iterator<Pair<Integer, String>> pairCalls = setofedges.iterator();

			while(pairCalls.hasNext()){
				Pair<Integer,String> pairCall = pairCalls.next();
				if(!pairCall.method.equals("eps")) {
					
					Iterator<Integer> qa= d.getStates().iterator();
					
					while(qa.hasNext()) {
						Integer qaEnter=qa.next();
						Iterator <Integer> qb = d.getStates().iterator();
						
						while(qb.hasNext()) {
							Integer qbEnter =qb.next();
							Iterator <Integer> qc = d.getStates().iterator();
							while(qc.hasNext()) {
								Integer qcEnter =qc.next();
								Iterator <Integer> qd = d.getStates().iterator();
								while(qd.hasNext()) {
									Integer qdEnter = qd.next();
									List<Integer> l = new ArrayList<Integer>();
									Triplet<Integer, String, Integer> sourceTuple = new Triplet<Integer, String, Integer>(Integer.valueOf(qaEnter),String.valueOf(key) , Integer.valueOf(qdEnter));
									Triplet<Integer, String, Integer> dest1 = new Triplet<Integer, String, Integer>(qaEnter, pairCall.method, qbEnter);
									l.add(mapping.get(dest1));
									
									Integer m = (Integer)f.entryNodes.get(pairCall.method);
									
									Triplet<Integer, String, Integer> dest2 = new Triplet<Integer, String, Integer>(qbEnter, String.valueOf(m), qcEnter);
									l.add(mapping.get(dest2));
									Triplet<Integer, String, Integer> dest3 = new Triplet<Integer, String, Integer>(qcEnter, String.valueOf(pairCall.node), qdEnter);
									l.add(mapping.get(dest3));
									final_map.get(mapping.get(sourceTuple)).add(l);
									if (mapping.get(sourceTuple)==84) {
										System.out.println(55);
									}
//									System.out.println(sourceTuple+" -> "+dest1+dest2+dest3);
//									count++;
									
									
								}
							}
						}
					}
						
				}
			}

		}
//		System.out.println(count);
		
		//Production rule 4
		
		Iterator<Integer> returnNodes = f.returnNodes.iterator();
		while(returnNodes.hasNext()) {
			Integer ret=returnNodes.next();
			Iterator<Integer> states= d.getStates().iterator();
			while(states.hasNext()) {
				Integer source=states.next();
				List<Integer> l = new ArrayList<Integer>();
				Triplet<Integer, String, Integer> sourceTuple = new Triplet<Integer, String, Integer>(source,String.valueOf(ret) , source);
				Triplet<Integer, String, Integer> destTuple = new Triplet<Integer, String, Integer>(-1,"eps" , -1);
//				System.out.println(mapping.get(destTuple));
				l.add(mapping.get(destTuple));
				final_map.get(mapping.get(sourceTuple)).add(l);
				if (mapping.get(sourceTuple)==84) {
					System.out.println(56);
				}
//				System.out.println(sourceTuple +" -> "+ destTuple);
//				count++;
			}
		}
//		System.out.println(count);
		
		//Production rule 5	
		

	}
	
	public void checkingEmptiness() {
		Map<Integer, Set<List<Integer>>> generating = new HashMap<Integer, Set<List<Integer>>>();
		Map<Integer, Triplet<Integer, String, Integer>> revMap = new HashMap<Integer, Triplet<Integer, String, Integer>>();
		
		for(Map.Entry<Triplet<Integer, String, Integer>, Integer> entry : mapping.entrySet()){
		    revMap.put(entry.getValue(), entry.getKey());
		}
		
		boolean addNewElement = true;
		boolean allGenerating = false; //that all right hand symbols in a production rule is generating
		List<Integer> trackElement= new ArrayList<Integer>();
		
		Iterator<Triplet<Integer, String, Integer>> terminal = this.mapping.keySet().iterator();
		
		while (terminal.hasNext()) {
			Triplet<Integer, String, Integer> enter = terminal.next();
			
			//!enter.t1.equals("eps") &&
//			System.out.println(enter);
			if (enter.t0 == -1 && enter.t2 == -1) {
//				System.out.println(enter.t1);
				Set<List<Integer>> s = new HashSet<List<Integer>>();
				s.add(new ArrayList<Integer>(mapping.get(enter)));
				generating.put(mapping.get(enter), s);
			}
		}
		
		while (addNewElement == true && !generating.containsKey(0)) {
			addNewElement = false;
			
			Iterator<Integer> finalMapIterator = this.final_map.keySet().iterator();
			while (finalMapIterator.hasNext()) {
				Integer currentKey = finalMapIterator.next();
				if (generating.containsKey(currentKey)) {
					continue;
				}
				else 
				{
					Iterator<List<Integer>> elementIterator = final_map.get(currentKey).iterator();
					allGenerating=false;
					while (elementIterator.hasNext()) {
						List<Integer> currentRule = elementIterator.next();
						for (int i = 0; i < currentRule.size(); i++) {
							if (!generating.containsKey(currentRule.get(i))) {
								break;
							}
							
							if (i==currentRule.size()-1) {
								System.out.println(43);
								trackElement=currentRule;
								allGenerating=true;
							}
						}
						if(allGenerating==true) {
							System.out.println(42);
							break;
							
						}
						
					}
					if(allGenerating == true) {
//						System.out.println(41);
//						generating.put(currentKey, trackElement);
//						addNewElement=true;
						if(!generating.containsKey(currentKey)) {
//							Set<List<Integer>> s = new HashSet<List<Integer>>();
//							s.add(trackElement);
							generating.put(currentKey, Collections.singleton(trackElement));
						}
						else {
							generating.get(currentKey).add(trackElement);
						}
						addNewElement=true;
					}
				}
			}
			
			System.out.println(generating);
			
			if (generating.get(0) == null) {
				System.out.println("Empty");
			}
			else {
				System.out.println("Non Empty");
				System.out.println(revMap.get(82));
				System.out.println(revMap.get(83));
				System.out.println(revMap.get(84));
				
				boolean reachedTerminal = false;
				Integer leftmost;
				
				List<Integer> stack = new ArrayList<>();
				stack.add(0);
				List<Integer> counterExample = new ArrayList<>();
				counterExample.add(0);
				
				while (!stack.isEmpty()) {
					leftmost = stack.get(0);
					stack.remove(0);
					counterExample.add(-1);
//					stack.add(0, generating.get(leftmost).)
					for (int i = generating.get(leftmost).iterator().next().size() - 1; i>=0; i--) {
						stack.add(0, generating.get(leftmost).iterator().next().get(i));
					}
					counterExample.addAll(stack);
				}
				System.out.println(counterExample);
				
				System.out.println(revMap.get(16));
				
				String t = "";
				String ans = "";
				Integer current;
				for (int j = 0; j < counterExample.size()-1; j++) {
					current = counterExample.get(j);
					if (current == 0) {
						ans = ans + "S";
						continue;
					}
					if (counterExample.get(j) == -1) {
						ans = ans + "=>" + t;
					}
					else {
						if (revMap.get(counterExample.get(j)).t0 == -1) {
							if (revMap.get(counterExample.get(j)).t1.equals("eps")) {
								ans = ans;
							}
							else {
								ans = ans + revMap.get(counterExample.get(j)).t1;
								t = t + revMap.get(counterExample.get(j)).t1;
							}
						}
						else {
							ans = ans + revMap.get(counterExample.get(j));
						}
					}
				}
				System.out.println(ans);
				
			}
			
			
			
			
			
			
		}
	}
}