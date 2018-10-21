package org.aksw.simba.squirrel.fetcher.manage;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.fetcher.Fetcher;
import org.aksw.simba.squirrel.fetcher.ftp.FTPFetcher;
import org.aksw.simba.squirrel.fetcher.http.HTTPFetcher;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URI;

import static org.junit.Assert.*;

public class SimpleOrderedFetcherManagerTest {

        @Test
        public void testCase1() throws Exception {
            CrawleableUri uri = new CrawleableUri(new URI("http://danbri.org/foaf.rdf"));
            FetcherDummyTest dummyTest1 = new FetcherDummyTest(true);
            FetcherDummyTest dummyTest2 = new FetcherDummyTest(true);
            SimpleOrderedFetcherManager manager = new SimpleOrderedFetcherManager(dummyTest1, dummyTest2);
            manager.setFetchers(dummyTest1, dummyTest2);
            File resultFile = manager.fetch(uri);
            assertEquals(true,dummyTest1.isPostFlag()); // 1st fetcher has been called
            assertNotNull(dummyTest1.getResult()); // checks that the result of first fetcher is not null i.e. result!=null
            assertEquals(false, dummyTest2.isPostFlag());// checks that the 2nd fetcher has not been called as the result of first fetcher is not null
        }
        @Test
        public void testCase2() throws Exception {
            CrawleableUri uri = new CrawleableUri(new URI("http://danbri.org/foaf.rdf"));
            FetcherDummyTest dummyTest1 = new FetcherDummyTest(false);
            FetcherDummyTest dummyTest2 = new FetcherDummyTest(true);
            SimpleOrderedFetcherManager manager = new SimpleOrderedFetcherManager(dummyTest1, dummyTest2);
            manager.setFetchers(dummyTest1, dummyTest2);
            File resultFile = manager.fetch(uri);
            assertEquals(true,dummyTest1.isPostFlag()); // 1st fetcher has been called
            assertNull(dummyTest1.getResult()); // optional, to make sure that the result is null
            assertEquals(true, dummyTest2.isPostFlag()); // 2nd fetcher has been called
        }
        @Test
        public void testCase3() throws Exception {
            CrawleableUri uri = new CrawleableUri(new URI("http://danbri.org/foaf.rdf"));
            FetcherDummyTest dummyTest1 = new FetcherDummyTest(false);
            FetcherDummyTest dummyTest2 = new FetcherDummyTest(false);
            SimpleOrderedFetcherManager manager = new SimpleOrderedFetcherManager(dummyTest1, dummyTest2);
            manager.setFetchers(dummyTest1, dummyTest2);
            File resultFile = manager.fetch(uri);
            assertEquals(true,dummyTest1.isPostFlag()); // 1st fetcher has been called
            assertNull(dummyTest1.getResult()); //optional
            assertEquals(true, dummyTest2.isPostFlag()); //2nd fetcher has been called
        }


}
