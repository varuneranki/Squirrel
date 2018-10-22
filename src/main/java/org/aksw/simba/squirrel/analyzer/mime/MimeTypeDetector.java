package org.aksw.simba.squirrel.analyzer.mime;

import org.aksw.simba.squirrel.analyzer.impl.RDFAnalyzer;
import org.apache.jena.riot.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class MimeTypeDetector implements TypeDetector {
    private static final Logger LOGGER = LoggerFactory.getLogger(RDFAnalyzer.class);

    protected List<Lang> mimeTypes = new ArrayList<>();

    public Lang detectMimeType(File data) {

        LinkedList<FiniteStateMachine> machinesList = new LinkedList<FiniteStateMachine>(); //3.i

        setMimeTypes(Lang.RDFXML, Lang.TURTLE);

        Lang detectedMimeType = null;

        try {
            FileInputStream inputStream = new FileInputStream(data.getAbsolutePath());

            for (Lang type : mimeTypes) {
                machinesList.add(FiniteStateMachineFactory.create(type.getName()));
            }


            char current;

            while (inputStream.available() > 0) {
                current = (char) inputStream.read();

                ListIterator<FiniteStateMachine> iter = machinesList.listIterator();

                while(iter.hasNext()) {
                    FiniteStateMachine machine = iter.next();
                    machine.switchState(String.valueOf(current));

                    if (machine.isError())
                        iter.remove();
                }

            }

            if(machinesList.size() != 0)
                detectedMimeType = machinesList.get(0).getMimeType();
            else
                detectedMimeType = Lang.RDFNULL;
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return detectedMimeType;
    }

    private void setMimeTypes(Lang ... types) {
        for(Lang type : types)
            mimeTypes.add(type);
    }
}
