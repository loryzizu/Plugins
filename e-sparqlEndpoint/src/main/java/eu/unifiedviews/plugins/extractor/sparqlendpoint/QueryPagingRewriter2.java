package eu.unifiedviews.plugins.extractor.sparqlendpoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.syntax.ElementSubQuery;

public class QueryPagingRewriter2 {
    public static List<String> getCollectedVars(String query) {
        Query qc = QueryFactory.create(query);
        System.out.println("QueryB:" + qc);

        List<Var> vars = qc.getProjectVars();

        Collections.sort(vars, new Comparator<Var>() {
            @Override
            public int compare(Var o1, Var o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        List<String> result = new ArrayList<>();
        for (Var var : vars) {
            result.add(var.getName());
        }
        return result;
    }
    
    public static boolean hasLimit(String query) {
        return QueryFactory.create(query).hasLimit();
    }
    
    public static boolean isOrdered(String query) {
        return QueryFactory.create(query).isOrdered();
    }
    
    public static String rewriteQuery(String query, long limit, long offset) {
        Query qc = QueryFactory.create(query);
        List<Var> vars = qc.getProjectVars();
        
        Query q = QueryFactory.make();
        q.setQueryPattern(qc.getQueryPattern());     
        q.setQuerySelectType();
        for (com.hp.hpl.jena.sparql.core.Var var : vars) {
            q.addResultVar(var.getName());
        }
        for (com.hp.hpl.jena.sparql.core.Var var : vars) {
            q.addOrderBy(var.getVarName(), Query.ORDER_ASCENDING);
        }
        
        Query w = QueryFactory.make();
        w.setQueryConstructType();
        w.setConstructTemplate(qc.getConstructTemplate());
        w.setQueryPattern(new ElementSubQuery(q));
        w.setLimit(limit);
        w.setOffset(offset);
        return w.toString(Syntax.syntaxSPARQL_10);
    }
}
