package org.aksw.simba.squirrel.analyzer.mime;

import org.apache.jena.riot.Lang;

import java.io.File;


/**
 * This interface defines the functionality to detect the mime-types, especially
 * of RDF serializations
 */

public interface TypeDetector {

    public Lang detectMimeType(File data);
}
