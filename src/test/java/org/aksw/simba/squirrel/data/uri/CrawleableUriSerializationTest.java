package org.aksw.simba.squirrel.data.uri;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;

import org.aksw.simba.squirrel.Constants;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.msgpack.template.Template;
import org.msgpack.type.ValueType;
import org.msgpack.unpacker.Unpacker;

import com.google.gson.Gson;

@RunWith(Parameterized.class)
public class CrawleableUriSerializationTest {

    @Parameters
    public static Collection<Object[]> data() throws Exception {
        CrawleableUri temp = new CrawleableUri(new URI("http://localhost/test"));
        temp.addData(Constants.URI_TYPE_KEY, Constants.URI_TYPE_VALUE_DEREF);
        temp.addData(Constants.URI_HTTP_MIME_TYPE_KEY, "application/json-ld");
        temp.addData(Constants.URI_HTTP_CHARSET_KEY, "utf-8");
        temp.addData(Constants.URI_PREFERRED_RECRAWL_ON, System.currentTimeMillis() + 100000L);

        return Arrays.asList(new Object[][] { { new CrawleableUri(new URI("http://localhost/test")) },
                { new CrawleableUri(new URI("http://google.de")) },
                { new CrawleableUri(new URI("http://google.de"), InetAddress.getByName("192.168.100.1"),
                        UriType.DEREFERENCEABLE) },
                { new CrawleableUri(new URI("http://google.de"), InetAddress.getByName("192.168.100.1"),
                        UriType.DUMP) },
                { new CrawleableUri(new URI("http://google.de"), InetAddress.getByName("192.168.100.1"),
                        UriType.SPARQL) },
                { new CrawleableUri(new URI("http://google.de"), InetAddress.getByName("192.168.100.1"),
                        UriType.UNKNOWN) },
                { new CrawleableUri(new URI("http://dbpedia.org"), null, UriType.SPARQL) },
                { new CrawleableUri(new URI("http://google.de"), InetAddress.getByName("255.255.255.255")) },
                { temp } });
    }

    private CrawleableUri uri;

    public CrawleableUriSerializationTest(CrawleableUri uri) {
        this.uri = uri;
    }

    @Test
    public void test() throws URISyntaxException, IOException {
        CrawleableUri parsedUri;
        // Gson gson = new Gson();
        MessagePack msgpack = new MessagePack();
        msgpack.register(InetAddress.class, new Template<InetAddress>() {

            @Override
            public void write(Packer pk, InetAddress v) throws IOException {
                byte count = 0;
                byte[] ipData = v.getAddress();
                if ((ipData != null) && (ipData.length > 0)) {
                    ++count;
                }
                String hostName = v.getCanonicalHostName();
                if (hostName != null) {
                    count += 2;
                }
                pk.write(count);
                if ((ipData != null) && (ipData.length > 0)) {
                    pk.write(v.getAddress());
                }
                if (hostName != null) {
                    pk.write(hostName);
                }
            }

            @Override
            public void write(Packer pk, InetAddress v, boolean required) throws IOException {
                write(pk, v, required);
            }

            @Override
            public InetAddress read(Unpacker u, InetAddress to) throws IOException {
                byte mask = u.readByte();
                byte[] ipData = null;
                if ((mask & 0x1) > 0) {
                    ipData = u.readByteArray();
                }
                String hostName = null;
                if ((mask & 0x2) > 0) {
                    hostName = u.readString();
                }
                if (ipData == null) {
                    if (hostName == null) {
                        return null;
                    } else {
                        return InetAddress.getByName(hostName);
                    }
                } else {
                    if (hostName == null) {
                        return InetAddress.getByAddress(ipData);
                    } else {
                        return InetAddress.getByAddress(hostName, ipData);
                    }
                }
            }

            @Override
            public InetAddress read(Unpacker u, InetAddress to, boolean required) throws IOException {
                return read(u, to);
            }
        });
        msgpack.register(UriType.class, new Template<UriType>() {

            @Override
            public void write(Packer pk, UriType v) throws IOException {
                pk.write(v.ordinal());
            }

            @Override
            public void write(Packer pk, UriType v, boolean required) throws IOException {
                write(pk, v);
            }

            @Override
            public UriType read(Unpacker u, UriType to) throws IOException {
                return UriType.values()[u.readInt()];
            }

            @Override
            public UriType read(Unpacker u, UriType to, boolean required) throws IOException {
                return read(u, to);
            }
        });

        byte[] raw = msgpack.write(uri);

        // String json = gson.toJson(uri);

        // System.out.println(json);
        // parsedUri = gson.fromJson(json, CrawleableUri.class);
        parsedUri = msgpack.read(raw, CrawleableUri.class);
        Assert.assertEquals(uri.getIpAddress(), parsedUri.getIpAddress());
        Assert.assertEquals(uri.getType(), parsedUri.getType());
        Assert.assertEquals(uri.getUri(), parsedUri.getUri());
        for (String key : uri.getData().keySet()) {

            Assert.assertEquals(uri.getData(key), parsedUri.getData(key));

        }
        Assert.assertEquals(uri.getData().size(), parsedUri.getData().size());
    }
}
