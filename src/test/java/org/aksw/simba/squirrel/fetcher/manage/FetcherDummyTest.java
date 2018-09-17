package org.aksw.simba.squirrel.fetcher.manage;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.fetcher.Fetcher;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class FetcherDummyTest implements Fetcher {

    private boolean preFlag;
    private boolean postFlag = false;
    private File fetcherFile;


    public boolean isPreFlag() {
        return preFlag;
    }

    public void setPreFlag(boolean preFlag) {
        this.preFlag = preFlag;
    }

    public boolean isPostFlag() {
        return postFlag;
    }

    public void setPostFlag(boolean postFlag) {
        this.postFlag = postFlag;
    }

    public File getFetcherFile() {
        return fetcherFile;
    }

    public void setFetcherFile(File fetcherFile) {
        this.fetcherFile = fetcherFile;
    }

    public File FetcherDummyTest(boolean preFlag) throws Exception {
        setPreFlag(preFlag);
        if (preFlag) {
           File fetcherFile =  fetch(null);
           setPostFlag(true);
           return fetcherFile;
        }
        else{
            return null;
        }

    }

    @Override
    public File fetch(CrawleableUri uri)
    {
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}

