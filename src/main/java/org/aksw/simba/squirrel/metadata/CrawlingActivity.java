package org.aksw.simba.squirrel.metadata;


import org.aksw.simba.squirrel.analyzer.Analyzer;
import org.aksw.simba.squirrel.analyzer.impl.RDFAnalyzer;
import org.aksw.simba.squirrel.components.FrontierComponent;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.filter.KnownUriFilter;
import org.aksw.simba.squirrel.data.uri.filter.RDBKnownUriFilter;
import org.aksw.simba.squirrel.fetcher.http.HTTPFetcher;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.TripleBasedSink;
import org.aksw.simba.squirrel.sink.impl.sparql.SparqlBasedSink;
import org.aksw.simba.squirrel.worker.Worker;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.jena.graph.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Representation of Crawling activity. A crawling activity is started by a single worker. So, it contains a bunch of Uris
 * and some meta data, like timestamps for the start and end of the crawling activity.
 */
public class CrawlingActivity {
    KnownUriFilter knownUriFilter;

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlingActivity.class);
    /**
     * A unique id.
     */
    private String id;
    /**
     * When the activity has started.
     */
    private Date dateStarted;
    /**
     * When the activity has ended.
     */
    private Date dateEnded;

    /**
     * The uri for the crawling activity.
     */
    private CrawleableUri uri;

    /**
     * The graph where the uri is stored.
     */
    private String graphId;

    /**
     * The crawling state of the uri.
     */
    private CrawlingURIState state;

    /**
     * The worker that has been assigned the activity.
     */
    private Worker worker;

    /**
     * Number of triples crawled by this activity.
     */
    private int numTriples;

    /**
     * The sink used for the activity.
     */
    private TripleBasedSink sink;

    /**
     * Constructor
     *
     * @param uri
     * @param worker
     * @param sink
     */
    public CrawlingActivity(CrawleableUri uri, Worker worker, TripleBasedSink sink) {
        this.worker = worker;
        this.dateStarted = new Date();
        this.uri = uri;
        this.state = CrawlingURIState.UNKNOWN;
        if (sink instanceof SparqlBasedSink) {
            graphId = ((SparqlBasedSink) sink).getGraphId(uri);
        }
        id = "activity:" + graphId;
        this.sink = sink;
    }

    public void setState(CrawlingURIState state) {
        this.state = state;
    }
    public CrawleableUri getUri ()
    {
        return uri;
    }
    public void addStep (Object k){
         uri.addData(k.getClass().getSimpleName().toString(),k);
    }


    public String getHadPlan()
    {
        for(Object object : Object.class.getClasses()) {
            if (object instanceof CrawleableUri) {
                addStep(object);
            }
        }
        addStep(RDFAnalyzer.class);
        addStep(HTTPFetcher.class);
        ArrayList<String> list = new ArrayList<>();
        for(String k: uri.getData().keySet())
        {
            list.add(k);

        }
        return list.toString();
    }


    public int getNumTriples() {
        return numTriples;
    }

    public void setState(CrawleableUri uri, CrawlingURIState state) {

    }

    public void finishActivity() {
        countTriples();
        dateEnded = new Date();

    }
    public Long getCounterforCrawler (CrawleableUri uri)
    {
       return  knownUriFilter.getCrawlingCounterUri(uri);
    }

    /**
     * count the triples of the activity.
     */
    private void countTriples() {
        int sum = 0;
        if (sink instanceof SparqlBasedSink) {

            //sum += ((SparqlBasedSink) sink).getNumberOfTriplesForGraph(uri);

            numTriples = sum;
        } else {
            numTriples = -1;
        }
    }

    public String getId() {
        return id;
    }

    public String getDateStarted() {
        SimpleDateFormat ft =
            new SimpleDateFormat ("yyyy-MM-dd'T'hh:mm:ss");

        String dStarted = ft.format(dateStarted).toString();
        return dStarted;
    }

    public String getDateEnded() {

        SimpleDateFormat ft =
            new SimpleDateFormat ("yyyy-MM-dd'T'hh:mm:ss");
        String dEnded = ft.format(dateEnded).toString();
        return dEnded;
    }

    public Worker getWorker() {
        return worker;
    }

    public enum CrawlingURIState {SUCCESSFUL, UNKNOWN, FAILED;}

    public CrawlingURIState getState() {
        return state;
    }

    public CrawleableUri getCrawleableUri() {
        return uri;
    }

    public String getGraphId() {
        return graphId;
    }
}
