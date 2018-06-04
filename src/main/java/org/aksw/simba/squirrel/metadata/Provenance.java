package org.aksw.simba.squirrel.metadata;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.impl.sparql.QueryGenerator;
import org.aksw.simba.squirrel.sink.impl.sparql.SparqlBasedSink;
import org.apache.jena.graph.*;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Provenance {

    private Sink sink;

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(SparqlBasedSink.class);


    public Provenance(String updateDatasetURI, String queryDatasetURI) {
        sink = new SparqlBasedSink(updateDatasetURI, queryDatasetURI);
    }

    public void addMetadata(final CrawlingActivity crawlingActivity) {
        ConcurrentLinkedQueue<Triple> lstTriples = new ConcurrentLinkedQueue<>();
        Node nodeCrawlingActivity = NodeFactory.createLiteral("crawlingActivity" + crawlingActivity.getId());
        lstTriples.add(new Triple(nodeCrawlingActivity, NodeFactory.createURI("prov:startedAtTime"), NodeFactory.createLiteral(crawlingActivity.getDateStarted())));
        lstTriples.add(new Triple(nodeCrawlingActivity, NodeFactory.createURI("prov:endedAtTime"), NodeFactory.createLiteral(crawlingActivity.getDateEnded())));
        lstTriples.add(new Triple(nodeCrawlingActivity, NodeFactory.createURI("sq:Status"), NodeFactory.createLiteral(crawlingActivity.getStatus().toString())));
        lstTriples.add(new Triple(nodeCrawlingActivity, NodeFactory.createURI("prov:wasAssociatedWith"), NodeFactory.createLiteral(String.valueOf(crawlingActivity.getWorker().getId()))));
        lstTriples.add(new Triple(nodeCrawlingActivity, NodeFactory.createURI("sq:numberOfTriples"), NodeFactory.createLiteral(String.valueOf(crawlingActivity.getNumTriples()))));
        //TODO lstTriples.add(new Triple(nodeCrawlingActivity, NodeFactory.createURI("sq:hostedOn"), NodeFactory.createLiteral(datasetPrefix)));
        for (CrawleableUri uri : crawlingActivity.getMapUri().keySet()) {
            //String k = uri.toString();
            //String kstr = k.replace("\"", "");
            lstTriples.add(new Triple(nodeCrawlingActivity, NodeFactory.createURI("prov:wasGeneratedBy"), NodeFactory.createURI(uri.getUri().toString())));
        }

        sink.openSinkForUri(null);
        for (Triple triple : lstTriples) {
            sink.addTriple(null, triple);
        }
        sink.closeSinkForUri(null);
        LOGGER.info("MetaData successfully added for Crawlingactivity: " + crawlingActivity.getId());
    }


    public int getNumberOfTriplesForGraph(CrawleableUri uri) {
        return -1;
        //TODO modify with the changes in deduplication branch (sink.getTriples.size())

        /*QueryExecution q = QueryExecutionFactory.sparqlService(queryDatasetURI,
            QueryGenerator.getInstance().getSelectAllQuery(uri));
        ResultSet results = q.execSelect();
        int sum = 0;
        while (results.hasNext()) {
            results.next();
            sum++;
        }
        return sum;
        */
    }

}
