package org.aksw.simba.squirrel.analyzer.mime;

import org.aksw.simba.squirrel.analyzer.impl.RDFAnalyzer;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;

public class MimeTypeDetector implements TypeDetector {
	private static final Logger LOGGER = LoggerFactory.getLogger(RDFAnalyzer.class);

    public Lang detectMimeType(File data) {

        LinkedList<FiniteStateMachine> machinesList = new LinkedList<FiniteStateMachine>(); //3.i
        Lang mimeType = null;

        try {
            FileInputStream inputStream = new FileInputStream(data.getAbsolutePath());

            machinesList.add(FiniteStateMachineFactory.create(RDFLanguages.RDFXML));
            machinesList.add(FiniteStateMachineFactory.create(RDFLanguages.TURTLE));

            char current;

            while (inputStream.available() > 0 && machinesList.size() > 1) { //3.iv (until only one machine is in list)
                current = (char) inputStream.read();

                FiniteStateMachine machine = machinesList.remove(); // (machines gets removed from list for processing)
                machine.switchState(String.valueOf(current)); //3.ii

                if (!machine.isError()) // (the machine is added only if its not in error state)
                    machinesList.addLast(machine); //3.iii
            }

            mimeType = machinesList.get(0).getMimeType();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mimeType;
    }
}
