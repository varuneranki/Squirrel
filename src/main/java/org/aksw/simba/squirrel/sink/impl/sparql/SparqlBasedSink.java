package org.aksw.simba.squirrel.sink.impl.sparql;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.metadata.CrawlingActivity;
import org.aksw.simba.squirrel.sink.Sink;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Variable;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SparqlBasedSink implements Sink {
    /**
     * Interval that specifies how many triples are to be buffered at once until they are sent to the DB.
     */
    private static final int SENDING_INTERVAL_BUFFERED_TRIPLES = 100;
    /**
     * The URI of the DB in which updates can be performed.
     */
    private String updateDatasetURI;
    /**
     * The URI of the DB in which querys can be performed.
     */
    private String queryDatasetURI;
    /**
     * The data structure (map) in which the triples are buffered.
     */
    private ConcurrentHashMap<CrawleableUri, ConcurrentLinkedQueue<Triple>> mapBufferedTriples = new ConcurrentHashMap<>();

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(SparqlBasedSink.class);

    /**
     * Constructor of SparqlBasedSink
     *
     * @param updateDatasetURI The URI of the DB in which updates can be performed.
     * @param queryDatasetURI  The URI of the DB in which querys can be performed.
     */
    public SparqlBasedSink(String updateDatasetURI, String queryDatasetURI) {
        this.updateDatasetURI = updateDatasetURI;
        this.queryDatasetURI = queryDatasetURI;
    }

    public void addMetadata(final CrawlingActivity crawlingActivity) {
        ConcurrentLinkedQueue<Triple> lstTriples = new ConcurrentLinkedQueue<>();
        Node nodeCrawlingActivity = new Node_Variable("crawlingActivity" + crawlingActivity.getId());
        lstTriples.add(new Triple(nodeCrawlingActivity, new Node_Variable("prov:startedAtTime"), new Node_Variable(crawlingActivity.getDateStarted())));
        lstTriples.add(new Triple(nodeCrawlingActivity, new Node_Variable("prov:endedAtTime"), new Node_Variable(crawlingActivity.getDateEnded())));
        lstTriples.add(new Triple(nodeCrawlingActivity, new Node_Variable("sq:Status"), new Node_Variable(crawlingActivity.getStatus().toString())));
        lstTriples.add(new Triple(nodeCrawlingActivity, new Node_Variable("prov:wasAssociatedWith"), new Node_Variable(String.valueOf(crawlingActivity.getWorker().getId()))));
        lstTriples.add(new Triple(nodeCrawlingActivity, new Node_Variable("sq:numberOfTriples"), new Node_Variable(String.valueOf(crawlingActivity.getNumTriples()))));
        //TODO lstTriples.add(new Triple(nodeCrawlingActivity, new Node_Variable("sq:hostedOn"), new Node_Variable(datasetPrefix)));
        for (CrawleableUri uri : crawlingActivity.getMapUri().keySet()) {
            String k = uri.toString();
            String kstr = k.replace("\"", "");
            lstTriples.add(new Triple(nodeCrawlingActivity, new Node_Variable("prov:wasGeneratedBy"), new Node_Variable(kstr)));
        }

        UpdateRequest request = UpdateFactory.create(QueryGenerator.getInstance().getAddQuery(null, lstTriples));
        UpdateProcessor proc = UpdateExecutionFactory.createRemote(request, updateDatasetURI);
        proc.execute();

    }


    public int getNumberOfTriplesForGraph(CrawleableUri uri) {
        QueryExecution q = QueryExecutionFactory.sparqlService(queryDatasetURI,
            QueryGenerator.getInstance().getSelectAllQuery(uri));
        ResultSet results = q.execSelect();
        int sum = 0;
        while (results.hasNext()) {
            results.next();
            sum++;
        }
        return sum;
    }

    @Override
    public void addTriple(CrawleableUri uri, Triple triple) {
        mapBufferedTriples.get(uri).add(triple);

        if (mapBufferedTriples.get(uri).size() >= SENDING_INTERVAL_BUFFERED_TRIPLES) {
            sendAllTriplesToDB(uri, mapBufferedTriples.get(uri));
        }
    }

    @Override
    public void openSinkForUri(CrawleableUri uri) {
        mapBufferedTriples.put(uri, new ConcurrentLinkedQueue<>());
    }

    @Override
    public void closeSinkForUri(CrawleableUri uri) {
        if (!mapBufferedTriples.get(uri).isEmpty()) {
            sendAllTriplesToDB(uri, mapBufferedTriples.get(uri));
        }
        mapBufferedTriples.remove(uri);
    }

    /**
     * Method to send all buffered triples to the database
     *
     * @param uri
     * @param tripleList
     */
    private void sendAllTriplesToDB(CrawleableUri uri, ConcurrentLinkedQueue<Triple> tripleList) {
        UpdateRequest request = UpdateFactory.create(QueryGenerator.getInstance().getAddQuery(uri, tripleList));
        UpdateProcessor proc = UpdateExecutionFactory.createRemote(request, updateDatasetURI);
        proc.execute();
    }

    @Override
    public void addData(CrawleableUri uri, InputStream stream) {
        throw new UnsupportedOperationException();
    }
}
