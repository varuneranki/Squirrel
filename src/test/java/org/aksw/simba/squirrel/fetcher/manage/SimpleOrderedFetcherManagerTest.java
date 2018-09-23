package org.aksw.simba.squirrel.fetcher.manage;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.fetcher.Fetcher;
import org.junit.Test;

import java.io.File;
import java.net.URI;

import static org.junit.Assert.assertNull;

public class SimpleOrderedFetcherManagerTest {


    @Test
    public void testCase1() throws Exception {
        CrawleableUri uri = new CrawleableUri(new URI("http://danbri.org/foaf.rdf"));
        FetcherDummyTest dummyTest1 = new FetcherDummyTest();
        dummyTest1.FetcherDummyTest(true);
        FetcherDummyTest dummyTest2 = new FetcherDummyTest();
        dummyTest2.FetcherDummyTest(true);
        SimpleOrderedFetcherManager manager = new SimpleOrderedFetcherManager(dummyTest1, dummyTest2);
        manager.setFetchers(dummyTest1, dummyTest2);
        File resultFile = manager.fetch(uri);
    }

    @Test
    public void testCase2() throws Exception {
        CrawleableUri uri = new CrawleableUri(new URI("http://danbri.org/foaf.rdf"));
        FetcherDummyTest dummyTest1 = new FetcherDummyTest();
        dummyTest1.FetcherDummyTest(false);
        FetcherDummyTest dummyTest2 = new FetcherDummyTest();
        dummyTest2.FetcherDummyTest(true);
        SimpleOrderedFetcherManager manager = new SimpleOrderedFetcherManager(dummyTest1, dummyTest2);
        manager.setFetchers(dummyTest1, dummyTest2);
        File resultFile = manager.fetch(uri);
    }

    @Test
    public void testCase3() throws Exception {
        CrawleableUri uri = new CrawleableUri(new URI("http://danbri.org/foaf.rdf"));
        FetcherDummyTest dummyTest1 = new FetcherDummyTest();
        dummyTest1.FetcherDummyTest(true);
        FetcherDummyTest dummyTest2 = new FetcherDummyTest();
        dummyTest2.FetcherDummyTest(true);
        SimpleOrderedFetcherManager manager = new SimpleOrderedFetcherManager(dummyTest1, dummyTest2);
        manager.setFetchers(dummyTest1, dummyTest2);
        File resultFile = manager.fetch(uri);
    }

}
