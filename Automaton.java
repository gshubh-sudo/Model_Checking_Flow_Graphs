package lab2;

import java.io.FileInputStream;

import java.lang.Integer;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

public class Automaton {
    
    public TransMap<Integer, String> trans = new TransMap<Integer, String>();
    protected Integer initial;
    public Set<Integer> accepting = new HashSet<Integer>();
    public StateMap<Integer, String> actualTrans = new StateMap<Integer, String>();
    
    public Automaton() {
    	
    }
    
    public Automaton(String file) {
    	try {
			InputStream input = new FileInputStream(file);
			Scanner s = new Scanner(input);
			while (s.hasNextLine()) {
				String currentLine = s.nextLine();
				String[] curr = currentLine.split("-");
				
				//Parsing the line starting with =>
				if (currentLine.substring(0,2).equals("=>")) {
					if (curr[1].equals("eps")) {
						System.out.println("1");
						initial = Integer.parseInt(curr[0].substring(4,curr[0].length()-1));
					}
					System.out.println("2");
					if (curr[0].charAt(2) == '(') {
						System.out.println("3");
						accepting.add(Integer.parseInt(curr[0].substring(4,curr[0].length()-1)));
					}
					System.out.println("4");
				}
				else {
					if (!curr[1].equals("eps")) {
						addTransition(Integer.parseInt(curr[0].substring(2, curr[0].length()-1)), Integer.parseInt(curr[2].substring(3, curr[2].length()-1)), curr[1]);
					}
					if (curr[0].charAt(0) == '(') {
						accepting.add(Integer.parseInt(curr[0].substring(2,curr[0].length()-1)));
					}
					System.out.println("8");
				}
			}
		}
		catch (FileNotFoundException e) {
			System.out.println(e);
		}
    }
    
    
    public Automaton(Automaton a) {
        initial = a.initial;
        accepting.addAll(a.accepting);
        for (Integer src : a.trans.keySet())
            for (Integer dst : a.trans.get(src).keySet())
                addTransitions(src, dst, a.trans.get(src).get(dst));
    }
    
    
    public void addTransition(Integer src, Integer dst, String c) {
    	System.out.println("22");
        addTransitions(src, dst, Collections.singleton(c));
        System.out.println("21");
    }
    
    public void complement() {
    	Set<Integer> newAccept = new HashSet<Integer>();
    	for (Integer state: this.getStates()) {
    		if (!this.accepting.contains(state)) {
    			newAccept.add(state);
    		}
    	}
    	this.accepting = newAccept;
    }
    
    
    public void addTransitions(Integer src, Integer dst, Collection<String> cs) {
        if (!trans.containsKey(src)){
            trans.put(src, new HashMap<Integer, Set<String>>());
            actualTrans.put(src, new HashMap<String, Set<Integer>>());}
        
        Map<Integer, Set<String>> stateTransitions = trans.get(src);
        if (!stateTransitions.containsKey(dst))
            stateTransitions.put(dst, new HashSet<String>());
        
        stateTransitions.get(dst).addAll(cs);

        Map<String, Set<Integer>> actualTransitions = actualTrans.get(src);
        for (String s : cs) {
            if (!actualTransitions.containsKey(s)) {
                actualTransitions.put(s, new HashSet<Integer>());
            }
            //System.out.println(actualTransitions.get(s));
            actualTransitions.get(s).addAll(Collections.singleton(dst));    
        }
        
    }
    
    
    public TransMap<Integer, String> getTransitions() {
        return trans;
    }
    
    
    public void addAcceptingState(Integer acc) {
        accepting.add(acc);
    }
    
    
    public void setInitialState(Integer initial) {
        this.initial = initial;
    }
    

    public Integer getInitialState() {
        return initial;
    }
    
    public Set<Integer> getAcceptingStates() {
        return accepting;
    }

    public Integer getFirstAcceptingState() {
        return accepting.iterator().next();
    }
    

    public Set<Integer> getSuccessors(Integer src, String sym) {
        Set<Integer> successors = new HashSet<Integer>();
        if (trans.containsKey(src))
            for (Integer dst : trans.get(src).keySet())
                if (trans.get(src).get(dst).contains(sym))
                    successors.add(dst);
        return successors;
    }
    
    
    public void mergeStates(Integer s1, Integer s2) {
        
        if (s1.equals(s2))
            return;
        
        // copy all s2-ingoing to s1-ingoing
        for (Integer src : getStates())
            if (trans.containsKey(src) && trans.get(src).containsKey(s2))
                addTransitions(src, s1, trans.get(src).get(s2));
        
        // copy all a2-outgoing to a1-outgoing
        if (trans.containsKey(s2))
            for (Integer dst : getStates())
                if (trans.get(s2).containsKey(dst))
                    addTransitions(s1, dst, trans.get(s2).get(dst));
        
        // remove a2.
        trans.remove(s2);
        for (Integer s : trans.keySet())
            trans.get(s).remove(s2);
        
        if (initial.equals(s2))
            initial = s1;
        
        if (accepting.remove(s2))
            accepting.add(s1);
    }
    
    
    
    // Returns the largest state explicitly mentioned in this automaton.
    public Set<Integer> getStates() {
        Set<Integer> states = new HashSet<Integer>();
        states.add(initial);
        states.addAll(accepting);
        for (Integer src : trans.keySet())
            for (Integer dst : trans.get(src).keySet())
                if (!trans.get(src).get(dst).isEmpty()) {
                    states.add(src);
                    states.add(dst);
                }
        return states;
    }

    public Set<String> getSymbols() {
        Set<String> symbols = new HashSet<String>();
        // states.add(initial);
        // states.addAll(accepting);
        for (Integer src : trans.keySet())
            for (Integer dst : trans.get(src).keySet())
                if (!trans.get(src).get(dst).isEmpty()) {
                    symbols.addAll(trans.get(src).get(dst));
                    // states.add(dst);
                }
        return symbols;
    }
    
    public void printGV() {
        
        String acc = "";
        for (Integer q : accepting)
            acc += stateStringRep(q) + " ";
        
        System.out.println("digraph finite_state_machine {");
        System.out.println("    rankdir=LR;");
        System.out.println("    size=\"10,10\"");
        System.out.println("    node [shape = doublecircle]; " + acc + ";");
        System.out.println("    node [shape = circle];");
        
        for (Integer src : trans.keySet()) {
            for (Integer dst : trans.get(src).keySet()) {
                Set<String> syms = trans.get(src).get(dst);
                System.out.println(
                        stateStringRep(src) + " -> " + 
                        stateStringRep(dst) + 
                        " [ label = \"" + symsStringRep(syms) + "\" ];");
            }
        }
        System.out.println("}");
    }
    
    public void emptyAcceptingStates() {
    	accepting.clear();
    }
    public void add(Automaton b) {
    	this.trans.putAll(b.trans);
    }
    
    protected String stateStringRep(Integer s) {
        return "q" + s;
    }
    
    protected String symsStringRep(Set<String> syms) {
        return syms.toString().replaceAll("\\[|,|\\]", "").replace("\"", "");
    }
}


class TransMap<State, Sym> extends HashMap<State, Map<State, Set<Sym>>> {
}

class StateMap<State, Sym> extends HashMap<State, Map<Sym, Set<State>>> {
}