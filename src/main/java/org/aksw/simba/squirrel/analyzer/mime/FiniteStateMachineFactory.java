
package org.aksw.simba.squirrel.analyzer.mime;

import org.apache.jena.riot.RDFLanguages;
import java.util.ArrayList;

public class FiniteStateMachineFactory {

    static public FiniteStateMachine create(String mimeType) {
        return new FiniteStateMachineFactory().buildStateMachine(mimeType);
    }

    private FiniteStateMachine buildStateMachine(String mimeType) {
        switch (mimeType) {
            case "RDF/XML":
                return buildRDFStateMachine();
            case "Turtle":
                return buildTurtleStateMachine();
            default:
                return null;
        }
    }

    /**
     * Builds a finite state machine to validate a simple
     * RDF file
     * @return
     */
    private FiniteStateMachine buildRDFStateMachine() {

        ArrayList<State> listOfStates = new ArrayList<>();

        String[] validRules = {"<", "\\?", "x", "m", "l"};
        String[] invalidRules = {"[^<]", "[^\\?]", "[^x]", "[^m]", "[^l]"};

        populateStates(listOfStates, 5);
        populateTransitions(listOfStates, validRules, invalidRules);

        return new Automata(listOfStates.get(0), RDFLanguages.RDFXML);
    }

    /**
     * Builds a finite state machine to validate a simple
     * TURTLE file
     * @return
     */
    private FiniteStateMachine buildTurtleStateMachine() {


        ArrayList<State> listOfStates = new ArrayList<>();

        String[] validRules = {"\\@", "p", "r", "e", "f","i","x"};
        String[] invalidRules = {"[^\\@]", "[^p]", "[^r]", "[^e]", "[^f]","[^i]","[^x]"};

        populateStates(listOfStates, 5);
        populateTransitions(listOfStates, validRules, invalidRules);

        return new Automata(listOfStates.get(0), RDFLanguages.TURTLE);
    }

    private void populateStates(ArrayList<State>  current, int numberOfStates) {
        for(int i=0; i<numberOfStates; i++)
            current.add(new RtState());
        current.add(new RtState(true, false));
        current.add(new RtState(true, true));
    }

    private void populateTransitions(ArrayList<State> current, String[] validRules, String[] invalidRules) {

        int i = 0;

        for(int j=0;j<(current.size()-2);j++) {
            current.get(j).with(new RtTransition(validRules[i], current.get(j+1)));
            current.get(j).with(new RtTransition(invalidRules[i++], current.get(current.size()-1)));
        }
    }






}
