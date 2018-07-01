package org.aksw.simba.squirrel.metadata;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import static org.apache.jena.vocabulary.DCTerms.provenance;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.impl.sparql.QueryGenerator;
import org.aksw.simba.squirrel.sink.impl.sparql.SparqlBasedSink;
import org.apache.jena.atlas.lib.Pair;
import org.apache.jena.graph.*;
import org.apache.jena.iri.IRI;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.system.PrefixMap;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Provenance {

    private Sink sink;

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(SparqlBasedSink.class);


    public Provenance(String updateDatasetURI, String queryDatasetURI) {
        sink = new SparqlBasedSink(updateDatasetURI, queryDatasetURI);
    }

    public void addMetadata(final CrawlingActivity crawlingActivity) {
        List<Triple> lstTriples = new LinkedList<>();

        Node nodeCrawlingActivity = NodeFactory.createLiteral("crawlingActivity" + crawlingActivity.getId());


        lstTriples.add(new Triple(nodeCrawlingActivity,Prov.startedAtTime.asNode(), NodeFactory.createLiteral(crawlingActivity.getDateStarted())));
        lstTriples.add(new Triple(nodeCrawlingActivity,Prov.endedAtTime.asNode(), NodeFactory.createLiteral(crawlingActivity.getDateEnded())));
        lstTriples.add(new Triple(nodeCrawlingActivity,Sq.status.asNode(), NodeFactory.createLiteral(crawlingActivity.getStatus().toString())));
        lstTriples.add(new Triple(nodeCrawlingActivity, Prov.wasAssociatedWith.asNode(), NodeFactory.createLiteral(String.valueOf(crawlingActivity.getWorker().getId()))));
        lstTriples.add(new Triple(nodeCrawlingActivity, Sq.numberOfTriples.asNode(), NodeFactory.createLiteral(String.valueOf(crawlingActivity.getNumTriples()))));
        lstTriples.add(new Triple(nodeCrawlingActivity,Prov.qualifiedAssociation.asNode(),NodeFactory.createLiteral(Prov.Association.toString())));
        lstTriples.add(new Triple(NodeFactory.createLiteral(Prov.Association.toString()),NodeFactory.createLiteral(Prov.agent.toString()),NodeFactory.createLiteral(String.valueOf(crawlingActivity.getWorker().getId()))));
        //TODO lstTriples.add(new Triple(nodeCrawlingActivity, NodeFactory.createURI("sq:hostedOn"), NodeFactory.createLiteral(datasetPrefix)));
        lstTriples.add(new Triple(nodeCrawlingActivity,Prov.hadPlan.asNode(),NodeFactory.createLiteral(crawlingActivity.getHadPlan())));
        lstTriples.add(new Triple(nodeCrawlingActivity, Prov.wasGeneratedBy.asNode(), NodeFactory.createURI(crawlingActivity.getUri().toString())));



        sink.openSinkForUri(crawlingActivity.uri);
        for (Triple triple : lstTriples) {
            sink.addTriple(crawlingActivity.uri, triple);
        }
        sink.closeSinkForUri(crawlingActivity.uri);
        LOGGER.info("MetaData successfully added for Crawlingactivity: " + crawlingActivity.getId());
    }




}