
package org.aksw.simba.squirrel.analyzer.mime;

import org.apache.jena.riot.RDFLanguages;

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
        State first = new RtState();
        State second = new RtState();
        State third = new RtState();
        State fourth = new RtState();
        State fifth = new RtState();
        State sixth = new RtState(true, false);
        State errorState = new RtState(true, true);

        first.with(new RtTransition("<", second));
        first.with(new RtTransition("[^<]", errorState));
        second.with(new RtTransition("\\?", third));
        second.with(new RtTransition("[^\\?]", errorState));
        third.with(new RtTransition("x", fourth));
        third.with(new RtTransition("[^x]", errorState));
        fourth.with(new RtTransition("m", fifth));
        fourth.with(new RtTransition("[^m]", errorState));
        fifth.with(new RtTransition("l", sixth));
        fifth.with(new RtTransition("[^l]", errorState));

        return new Automata(first, RDFLanguages.RDFXML);
    }


    /**
     * Builds a finite state machine to validate a simple
     * TURTLE file
     * @return
     */
    private FiniteStateMachine buildTurtleStateMachine() {
        State first = new RtState();
        State second = new RtState();
        State third = new RtState();
        State fourth = new RtState();
        State fifth = new RtState();
        State sixth = new RtState();
        State seventh = new RtState();
        State eighth = new RtState(true, false);
        State errorState = new RtState(true, true);

        first.with(new RtTransition("[\\@]", second));
        first.with(new RtTransition("[^\\@]", errorState));
        second.with(new RtTransition("p", third));
        second.with(new RtTransition("[^p]", errorState));
        third.with(new RtTransition("r", fourth));
        third.with(new RtTransition("[^r]", errorState));
        fourth.with(new RtTransition("e", fifth));
        fourth.with(new RtTransition("[^e]", errorState));
        fifth.with(new RtTransition("f", sixth));
        fifth.with(new RtTransition("[^f]", errorState));
        sixth.with(new RtTransition("i", seventh));
        sixth.with(new RtTransition("[^i]", errorState));
        seventh.with(new RtTransition( "x", eighth));
        seventh.with(new RtTransition("[^x]", errorState));

        return new Automata(first, RDFLanguages.TURTLE);
    }
}
