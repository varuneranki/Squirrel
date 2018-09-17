//package org.aksw.simba.squirrel.fetcher.http;
//
//
//import org.aksw.simba.squirrel.AbstractServerMockUsingTest;
//import org.aksw.simba.squirrel.data.uri.CrawleableUri;
//import org.aksw.simba.squirrel.data.uri.CrawleableUriFactory;
//import org.aksw.simba.squirrel.data.uri.CrawleableUriFactoryImpl;
//import org.aksw.simba.squirrel.data.uri.UriType;
//import org.aksw.simba.squirrel.simulation.CrawleableResource;
//import org.aksw.simba.squirrel.simulation.StringResource;
//import org.apache.jena.rdf.model.Model;
//import org.apache.jena.rdf.model.ModelFactory;
//import org.apache.jena.riot.Lang;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.Parameterized;
//import org.simpleframework.http.core.Container;
//
//import java.io.File;
//import java.net.URI;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
//import static org.junit.Assert.assertNotNull;
//
//@RunWith(Parameterized.class)
//public class HTTPFetcherTest extends AbstractServerMockUsingTest {
//
//    public HTTPFetcherTest(Container container) {
//        super(container);
//    }
//
//
//    @Parameterized.Parameters
//    public static Collection<Object[]> data() throws Exception {
//
//        //CrawleableUri uri = new CrawleableUri(new URI());
//        String server1Url = "http://127.0.0.1:" + SERVER_PORT;
//        String server2Url = "http://127.0.0.2:" + SERVER_PORT;
//        String server3Url = "http://127.0.0.3:" + SERVER_PORT;
//
//        Model model1, model2, model3;
//        /*
//         * Simple scenario in which resource1 is the seed and points to resource2 which
//         * points to resource3.
//         */
//        model1 = ModelFactory.createDefaultModel();
//        model1.add(model1.createResource(server1Url + "/entity_1"), model1.createProperty(server2Url + "/property_1"),
//            model1.createLiteral("literal"));
//        model2 = ModelFactory.createDefaultModel();
//        model2.add(model2.createResource(server1Url + "/entity_1"), model2.createProperty(server2Url + "/property_1"),
//            model2.createResource(server3Url + "/entity_2"));
//        model3 = ModelFactory.createDefaultModel();
//        model3.add(model3.createResource(server1Url + "/entity_2"), model3.createProperty(server2Url + "/property_1"),
//            model3.createLiteral("literal2"));
//        scenarios.add(new Object[] {
//            new CrawleableUri[] { uriFactory.create(new URI(server1Url + "/entity_1"), UriType.DEREFERENCEABLE) },
//            new CrawleableResource[] { new StringResource(model1, server1Url + "/entity_1", Lang.N3),
//                new StringResource(model2, server2Url + "/property_1", Lang.N3),
//                new StringResource(model3, server3Url + "/entity_2", Lang.N3) } });
//
//        /*
//         * The same scenario with different serializations.
//         */
//        model1 = ModelFactory.createDefaultModel();
//        model1.add(model1.createResource(server1Url + "/entity_1"), model1.createProperty(server2Url + "/property_1"),
//            model1.createLiteral("literal"));
//        model2 = ModelFactory.createDefaultModel();
//        model2.add(model2.createResource(server1Url + "/entity_1"), model2.createProperty(server2Url + "/property_1"),
//            model2.createResource(server3Url + "/entity_2"));
//        model3 = ModelFactory.createDefaultModel();
//        model3.add(model3.createResource(server1Url + "/entity_2"), model3.createProperty(server2Url + "/property_1"),
//            model3.createLiteral("literal2"));
//        scenarios.add(new Object[] {
//            new CrawleableUri[] { uriFactory.create(new URI(server1Url + "/entity_1"), UriType.DEREFERENCEABLE) },
//            new CrawleableResource[] { new StringResource(model1, server1Url + "/entity_1", Lang.RDFXML),
//                new StringResource(model2, server2Url + "/property_1", Lang.TURTLE),
//                new StringResource(model3, server3Url + "/entity_2", Lang.RDFJSON) } });
//
//        /*
//         * Example in which the dump fetcher needs to be able to read the data like a
//         * normal fetcher.
//         */
//        model1 = ModelFactory.createDefaultModel();
//        model1.add(model1.createResource(server1Url + "/entity_1.n3"),
//            model1.createProperty(server2Url + "/property_1.n3"), model1.createLiteral("literal"));
//        model2 = ModelFactory.createDefaultModel();
//        model2.add(model2.createResource(server1Url + "/entity_1.n3"),
//            model2.createProperty(server2Url + "/property_1.n3"),
//            model2.createResource(server3Url + "/entity_2.n3"));
//        model3 = ModelFactory.createDefaultModel();
//        model3.add(model3.createResource(server1Url + "/entity_2.n3"),
//            model3.createProperty(server2Url + "/property_1.n3"), model3.createLiteral("literal2"));
//        scenarios.add(new Object[] {
//            new CrawleableUri[] { uriFactory.create(new URI(server1Url + "/entity_1.n3"), UriType.DUMP) },
//            new CrawleableResource[] { new StringResource(model1, server1Url + "/entity_1.n3", Lang.N3),
//                new StringResource(model2, server2Url + "/property_1.n3", Lang.N3),
//                new StringResource(model3, server3Url + "/entity_2.n3", Lang.N3) } });
//        return scenarios;
//    }
//
//
//
//
//    @Test
//    public void fetch() throws Exception {
//        CrawleableUri tempUri = new CrawleableUri(new URI("http://danbri.org/foaf.rdf"));
//        HTTPFetcher fetcher = new HTTPFetcher();
//        File data = fetcher.fetch(tempUri);
//        assertNotNull(data);
//
//    }
//
//}
