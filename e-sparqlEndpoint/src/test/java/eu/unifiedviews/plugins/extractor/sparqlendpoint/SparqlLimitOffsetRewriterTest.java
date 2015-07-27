package eu.unifiedviews.plugins.extractor.sparqlendpoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.UnsupportedQueryLanguageException;
import org.openrdf.query.algebra.ProjectionElem;
import org.openrdf.query.algebra.Reduced;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.parser.ParsedGraphQuery;
import org.openrdf.query.parser.QueryParserUtil;

public class SparqlLimitOffsetRewriterTest {
    static String baseURI = "http://default";

    static String query1;

    static String query2;

    static String query3;

    static String query4;

    static String query5;

    static String queryA;

    static String queryB;
    static String result3;

    @BeforeClass
    public static void init() throws IOException {
        query1 = IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream("query1.txt"));
        query2 = IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream("query2.txt"));
        query3 = IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream("query3.txt"));
        query4 = IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream("query4.txt"));
        query5 = IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream("query5.txt"));
        queryA = IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream("queryA.txt"));
        queryB = IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream("queryB.txt"));
        result3 = IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream("result3.txt"));
    }

    @Test
    public void varCollectionTestQuery1() throws MalformedQueryException, UnsupportedQueryLanguageException {
        String query = query1;
        ParsedGraphQuery parsedQuery = QueryParserUtil.parseGraphQuery(QueryLanguage.SPARQL, query, baseURI);
        System.out.println("Query1:" + parsedQuery.getTupleExpr());
        
        QueryPagingRewriter.VarCollector varCollector = new QueryPagingRewriter.VarCollector();
        varCollector.optimize(parsedQuery.getTupleExpr(), parsedQuery.getDataset(), null);

        for (Map.Entry<String, ProjectionElem> var : varCollector.getProjectionElems().entrySet()) {
            Assert.assertEquals(var.getKey(), var.getValue().getSourceName());
        }
        List<String> result = new ArrayList<>(varCollector.getProjectionElems().keySet());
        Collections.sort(result);
        Assert.assertEquals("[village]", StringUtils.join(result));
        
        for (Map.Entry<String, Var> var : varCollector.getVars().entrySet()) {
            Assert.assertEquals(var.getKey(), var.getValue().getName());
        }
        List<String> result1 = new ArrayList<>(varCollector.getVars().keySet());
        Collections.sort(result1);
        Assert.assertEquals("[village]", StringUtils.join(result1));
    }

    @Test
    public void varCollectionTestQuery2() throws MalformedQueryException, UnsupportedQueryLanguageException {
        String query = query2;
        ParsedGraphQuery parsedQuery = QueryParserUtil.parseGraphQuery(QueryLanguage.SPARQL, query, baseURI);
        System.out.println("Query2:" + parsedQuery.getTupleExpr());
        
        QueryPagingRewriter.VarCollector varCollector = new QueryPagingRewriter.VarCollector();
        varCollector.optimize(parsedQuery.getTupleExpr(), parsedQuery.getDataset(), null);

        for (Map.Entry<String, ProjectionElem> var : varCollector.getProjectionElems().entrySet()) {
            Assert.assertEquals(var.getKey(), var.getValue().getSourceName());
        }
        List<String> result = new ArrayList<>(varCollector.getProjectionElems().keySet());
        Collections.sort(result);
        Assert.assertEquals("[village]", StringUtils.join(result));
        
        for (Map.Entry<String, Var> var : varCollector.getVars().entrySet()) {
            Assert.assertEquals(var.getKey(), var.getValue().getName());
        }
        List<String> result1 = new ArrayList<>(varCollector.getVars().keySet());
        Collections.sort(result1);
        Assert.assertEquals("[village]", StringUtils.join(result1));
    }

    @Test
    public void varCollectionTestQuery3() throws MalformedQueryException, UnsupportedQueryLanguageException {
        String query = query3;
        String resultQuery = result3;
        ParsedGraphQuery parsedQuery = QueryParserUtil.parseGraphQuery(QueryLanguage.SPARQL, query, baseURI);
        System.out.println("Query3:" + parsedQuery.getTupleExpr());
        
        QueryPagingRewriter.VarCollector varCollector = new QueryPagingRewriter.VarCollector();
        varCollector.optimize(parsedQuery.getTupleExpr(), parsedQuery.getDataset(), null);

        for (Map.Entry<String, ProjectionElem> var : varCollector.getProjectionElems().entrySet()) {
            Assert.assertEquals(var.getKey(), var.getValue().getSourceName());
        }
        List<String> result = new ArrayList<>(varCollector.getProjectionElems().keySet());
        Collections.sort(result);
        Assert.assertEquals("[]", StringUtils.join(result));
        
        for (Map.Entry<String, Var> var : varCollector.getVars().entrySet()) {
            Assert.assertEquals(var.getKey(), var.getValue().getName());
        }
        List<String> result1 = new ArrayList<>(varCollector.getVars().keySet());
        Collections.sort(result1);
        Assert.assertEquals("[village]", StringUtils.join(result1));
        
        ParsedGraphQuery parsedResultQuery = QueryParserUtil.parseGraphQuery(QueryLanguage.SPARQL, resultQuery, baseURI);
        System.out.println("Result3:" + parsedResultQuery.getTupleExpr());
    }

    @Test
    public void varCollectionTestQuery4() throws MalformedQueryException, UnsupportedQueryLanguageException {
        String query = query4;
        ParsedGraphQuery parsedQuery = QueryParserUtil.parseGraphQuery(QueryLanguage.SPARQL, query, baseURI);
        System.out.println("Query4:" + parsedQuery.getTupleExpr());

        QueryPagingRewriter.VarCollector varCollector = new QueryPagingRewriter.VarCollector();
        varCollector.optimize(parsedQuery.getTupleExpr(), parsedQuery.getDataset(), null);

        for (Map.Entry<String, ProjectionElem> var : varCollector.getProjectionElems().entrySet()) {
            Assert.assertEquals(var.getKey(), var.getValue().getSourceName());
        }
        List<String> result = new ArrayList<>(varCollector.getProjectionElems().keySet());

        Collections.sort(result);
        Assert.assertEquals("[village]", StringUtils.join(result));
        
        for (Map.Entry<String, Var> var : varCollector.getVars().entrySet()) {
            Assert.assertEquals(var.getKey(), var.getValue().getName());
        }
        List<String> result1 = new ArrayList<>(varCollector.getVars().keySet());
        Collections.sort(result1);
        Assert.assertEquals("[]", StringUtils.join(result1));
    }

    @Test
    public void varCollectionTestQuery5() throws MalformedQueryException, UnsupportedQueryLanguageException {
        String query = query5;
        ParsedGraphQuery parsedQuery = QueryParserUtil.parseGraphQuery(QueryLanguage.SPARQL, query, baseURI);
        System.out.println("Query5:" + parsedQuery.getTupleExpr());

        QueryPagingRewriter.VarCollector varCollector = new QueryPagingRewriter.VarCollector();
        varCollector.optimize(parsedQuery.getTupleExpr(), parsedQuery.getDataset(), null);

        for (Map.Entry<String, ProjectionElem> var : varCollector.getProjectionElems().entrySet()) {
            Assert.assertEquals(var.getKey(), var.getValue().getSourceName());
        }
        List<String> result = new ArrayList<>(varCollector.getProjectionElems().keySet());

        Collections.sort(result);
        Assert.assertEquals("[label, village]", StringUtils.join(result));
        
        for (Map.Entry<String, Var> var : varCollector.getVars().entrySet()) {
            Assert.assertEquals(var.getKey(), var.getValue().getName());
        }
        List<String> result1 = new ArrayList<>(varCollector.getVars().keySet());
        Collections.sort(result1);
        Assert.assertEquals("[]", StringUtils.join(result1));
    }
    
    @Test
    public void varCollectionTestQueryA() throws MalformedQueryException, UnsupportedQueryLanguageException {
        String query = queryA;
        ParsedGraphQuery parsedQuery = QueryParserUtil.parseGraphQuery(QueryLanguage.SPARQL, query, baseURI);
        System.out.println("QueryA:" + parsedQuery.getTupleExpr());

        QueryPagingRewriter.VarCollector varCollector = new QueryPagingRewriter.VarCollector();
        varCollector.optimize(parsedQuery.getTupleExpr(), parsedQuery.getDataset(), null);

        for (Map.Entry<String, ProjectionElem> var : varCollector.getProjectionElems().entrySet()) {
            Assert.assertEquals(var.getKey(), var.getValue().getSourceName());
        }
        List<String> result = new ArrayList<>(varCollector.getProjectionElems().keySet());

        Collections.sort(result);
        Assert.assertEquals("[village]", StringUtils.join(result));
        
        for (Map.Entry<String, Var> var : varCollector.getVars().entrySet()) {
            Assert.assertEquals(var.getKey(), var.getValue().getName());
        }
        List<String> result1 = new ArrayList<>(varCollector.getVars().keySet());
        Collections.sort(result1);
        Assert.assertEquals("[]", StringUtils.join(result1));
    }


    @Test
    public void varCollectionTestQueryB() throws MalformedQueryException, UnsupportedQueryLanguageException {
        String query = queryB;
        ParsedGraphQuery parsedQuery = QueryParserUtil.parseGraphQuery(QueryLanguage.SPARQL, query, baseURI);
        System.out.println("QueryB:" + parsedQuery.getTupleExpr());

        QueryPagingRewriter.VarCollector varCollector = new QueryPagingRewriter.VarCollector();
        varCollector.optimize(parsedQuery.getTupleExpr(), parsedQuery.getDataset(), null);

        for (Map.Entry<String, ProjectionElem> var : varCollector.getProjectionElems().entrySet()) {
            Assert.assertEquals(var.getKey(), var.getValue().getSourceName());
        }
        List<String> result = new ArrayList<>(varCollector.getProjectionElems().keySet());

        Collections.sort(result);
        Assert.assertEquals("[village]", StringUtils.join(result));
        
        for (Map.Entry<String, Var> var : varCollector.getVars().entrySet()) {
            Assert.assertEquals(var.getKey(), var.getValue().getName());
        }
        List<String> result1 = new ArrayList<>(varCollector.getVars().keySet());
        Collections.sort(result1);
        Assert.assertEquals("[label, village]", StringUtils.join(result1));
    }
    
//    @Test
    public void testVillage() throws MalformedQueryException, UnsupportedQueryLanguageException {
        String originalQuery = "PREFIX dbo:  <http://dbpedia.org/ontology/> CONSTRUCT { ?village a dbo:Village . } WHERE { ?village a dbo:Village . }";
        String optimizedQuery = "PREFIX dbo:  <http://dbpedia.org/ontology/> CONSTRUCT { ?village a dbo:Village . } WHERE { { SELECT ?village WHERE { ?village a dbo:Village . } ORDER BY ?village } } LIMIT 10000 OFFSET 40000";
        String shortQuery = "PREFIX dbo:  <http://dbpedia.org/ontology/>  CONSTRUCT WHERE { ?village a dbo:Village . }";
        String queryStr = shortQuery;
        String baseURI = "http://default";
        ParsedGraphQuery originalQueryParsed = QueryParserUtil.parseGraphQuery(QueryLanguage.SPARQL, originalQuery, "http://default");
        ParsedGraphQuery optimizedQueryParsed = QueryParserUtil.parseGraphQuery(QueryLanguage.SPARQL, optimizedQuery, "http://default");
        ParsedGraphQuery queryStrParsed = QueryParserUtil.parseGraphQuery(QueryLanguage.SPARQL, queryStr, baseURI);
        System.out.println("Parsed Query:\n" + queryStrParsed.getTupleExpr().toString());
        System.out.println("Optimized Query:\n" + optimizedQueryParsed.getTupleExpr().toString());
        TupleExpr tupleExpr = queryStrParsed.getTupleExpr();
        Assert.assertEquals(tupleExpr.toString(), queryStrParsed.getTupleExpr().toString());
        Assert.assertEquals(originalQueryParsed.getTupleExpr().toString(), new Reduced(queryStrParsed.getTupleExpr()).toString());
    }
}
