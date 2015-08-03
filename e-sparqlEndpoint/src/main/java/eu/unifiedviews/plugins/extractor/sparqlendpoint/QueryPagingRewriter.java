package eu.unifiedviews.plugins.extractor.sparqlendpoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.query.BindingSet;
import org.openrdf.query.Dataset;
import org.openrdf.query.IncompatibleOperationException;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.algebra.Extension;
import org.openrdf.query.algebra.Order;
import org.openrdf.query.algebra.OrderElem;
import org.openrdf.query.algebra.Projection;
import org.openrdf.query.algebra.ProjectionElem;
import org.openrdf.query.algebra.ProjectionElemList;
import org.openrdf.query.algebra.Slice;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.algebra.evaluation.QueryOptimizer;
import org.openrdf.query.algebra.helpers.QueryModelVisitorBase;
import org.openrdf.query.parser.ParsedBooleanQuery;
import org.openrdf.query.parser.ParsedGraphQuery;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.ParsedTupleQuery;
import org.openrdf.query.parser.sparql.ASTVisitorBase;
import org.openrdf.query.parser.sparql.BaseDeclProcessor;
import org.openrdf.query.parser.sparql.BlankNodeVarProcessor;
import org.openrdf.query.parser.sparql.DatasetDeclProcessor;
import org.openrdf.query.parser.sparql.PrefixDeclProcessor;
import org.openrdf.query.parser.sparql.StringEscapesProcessor;
import org.openrdf.query.parser.sparql.WildcardProjectionProcessor;
import org.openrdf.query.parser.sparql.ast.*;

public class QueryPagingRewriter implements QueryOptimizer {

    static class VarCollector implements QueryOptimizer {
        Map<String, ProjectionElem> collectedProjectionElems = new HashMap<>();

        Map<String, Var> collectedInsideVars = new HashMap<>();

        Map<String, Var> collectedOutsideVars = new HashMap<>();

        class VarCollectorVisitor extends QueryModelVisitorBase<RuntimeException> {
            int extensionsMet = 0;

            @Override
            public void meet(Extension node) throws RuntimeException {
                extensionsMet++;
                super.meet(node);
                extensionsMet--;
            }

            @Override
            public void meet(ProjectionElem node) throws RuntimeException {
                if (extensionsMet <= 1) {
                    collectedProjectionElems.put(node.getSourceName(), node);
                }
                super.meet(node);
            }

            @Override
            public void meet(Var node) throws RuntimeException {
                if ((extensionsMet <= 1) && (!node.isAnonymous())) {
                    collectedOutsideVars.put(node.getName(), node);
                }
                if (extensionsMet >= 1) {
                    collectedInsideVars.put(node.getName(), node);
                }
                super.meet(node);
            }
        }

        @Override
        public void optimize(TupleExpr tupleExpr, Dataset dataset, BindingSet bindings) {
            VarCollectorVisitor visitor = new VarCollectorVisitor();
            tupleExpr.visit(visitor);
        }

        public Map<String, ProjectionElem> getProjectionElems() {
            Map<String, ProjectionElem> result = new HashMap<>();
            for (Map.Entry<String, ProjectionElem> entry : collectedProjectionElems.entrySet()) {
                if (collectedInsideVars.containsKey(entry.getKey()) && !collectedInsideVars.get(entry.getKey()).isAnonymous()) {
                    result.put(entry.getKey(), entry.getValue());
                }
            }
            return result;
        }

        public Map<String, Var> getVars() {
            Map<String, Var> result = new HashMap<>();
            for (Map.Entry<String, Var> entry : collectedOutsideVars.entrySet()) {
                result.put(entry.getKey(), entry.getValue());
            }
            return result;
        }
    }

    static class WhereExtractor implements QueryOptimizer {
        TupleExpr whereClause;

        class WhereExtractorVisitor extends QueryModelVisitorBase<RuntimeException> {
            int extensionsMet = 0;

            @Override
            public void meet(Extension node) throws RuntimeException {
                if (extensionsMet == 0) {
                    whereClause = node.getArg();
                }
                extensionsMet++;
                super.meet(node);
            }
        }

        @Override
        public void optimize(TupleExpr tupleExpr, Dataset dataset, BindingSet bindings) {
            WhereExtractorVisitor visitor = new WhereExtractorVisitor();
            tupleExpr.visit(visitor);
        }

        public TupleExpr getWhereClause() {
            return whereClause;
        }
    }

    static class WhereReplacer implements QueryOptimizer {
        TupleExpr replacementNode;

        public WhereReplacer(TupleExpr replacementNode) {
            this.replacementNode = replacementNode;
        }

        class WhereReplacementVisitor extends QueryModelVisitorBase<RuntimeException> {
            int extensionsMet = 0;

            @Override
            public void meet(Extension node) throws RuntimeException {
                if (extensionsMet == 0) {
                    node.setArg(replacementNode);
                }
                extensionsMet++;
                super.meet(node);
            }
        }

        @Override
        public void optimize(TupleExpr tupleExpr, Dataset dataset, BindingSet bindings) {
            WhereReplacementVisitor visitor = new WhereReplacementVisitor();
            tupleExpr.visit(visitor);
        }
    }

    TupleExpr optimizedTupleExpr;

    public static Map<String, Var> asVars(Map<String, ProjectionElem> projectionElems, Map<String, Var> vars) {
        Map<String, Var> result = new LinkedHashMap<>();
        Set<String> toDeduplicateSet = new HashSet<>();
        toDeduplicateSet.addAll(vars.keySet());
        toDeduplicateSet.addAll(projectionElems.keySet());
        List<String> toSortList = new ArrayList<>(toDeduplicateSet);
        Collections.sort(toSortList);
        for (String key : toSortList) {
            if (vars.containsKey(key)) {
                result.put(key, vars.get(key));
            } else if (projectionElems.containsKey(key)) {
                result.put(key, new Var(key));
            }
        }
        return result;
    }

    public static Map<String, ProjectionElem> asProjectionElems(Map<String, ProjectionElem> projectionElems, Map<String, Var> vars) {
        Map<String, ProjectionElem> result = new LinkedHashMap<>();
        Set<String> toDeduplicateSet = new HashSet<>();
        toDeduplicateSet.addAll(vars.keySet());
        toDeduplicateSet.addAll(projectionElems.keySet());
        List<String> toSortList = new ArrayList<>(toDeduplicateSet);
        Collections.sort(toSortList);
        for (String key : toSortList) {
            if (projectionElems.containsKey(key)) {
                result.put(key, new ProjectionElem(key));
            } else if (vars.containsKey(key)) {
                result.put(key, new ProjectionElem(key));
            }
        }
        return result;
    }

    @Override
    public void optimize(TupleExpr tupleExpr, Dataset dataset, BindingSet bindings) {
        VarCollector varCollector = new VarCollector();
        varCollector.optimize(tupleExpr, dataset, bindings);
        Map<String, ProjectionElem> projectionElems = asProjectionElems(varCollector.getProjectionElems(), varCollector.getVars());
        Map<String, Var> projectionVars = asVars(varCollector.getProjectionElems(), varCollector.getVars());
        Var firstVar = projectionVars.values().iterator().next();

        WhereExtractor whereExtractor = new WhereExtractor();
        whereExtractor.optimize(tupleExpr, dataset, bindings);

        TupleExpr whereClause = whereExtractor.getWhereClause();
        ProjectionElemList projectionElemList = new ProjectionElemList(projectionElems.values());
        OrderElem orderElem = new OrderElem(firstVar);
        Order order = new Order(whereClause, orderElem);
        Projection projection = new Projection(order, projectionElemList);
        WhereReplacer whereReplacer = new WhereReplacer(projection);
        whereReplacer.optimize(tupleExpr, dataset, bindings);
        optimizedTupleExpr = new Slice(tupleExpr, 0, 100);
    }

    public TupleExpr getOptimizedTupleExpr() {
        return optimizedTupleExpr;
    }

    static class VarCollectorAstVisitor extends ASTVisitorBase {
        int whereMet = 0;

        boolean parentSelect = false;

        Map<String, ASTVar> collectedOutsideVars = new HashMap<>();

        @Override
        public Object visit(ASTWhereClause node, Object data) throws VisitorException {
            whereMet++;
            Object result = super.visit(node, data);
            whereMet--;
            return result;
        }

        @Override
        public Object visit(ASTSelect node, Object data) throws VisitorException {
            parentSelect = true;
            Object result = super.visit(node, data);
            parentSelect = false;
            return result;
        }

        @Override
        public Object visit(ASTVar node, Object data) throws VisitorException {
            if ((whereMet == 1)){// || ((whereMet == 1) && node.jjtGetParent() instanceof ASTProjectionElem)) {
                if (!node.isAnonymous()) {
                    collectedOutsideVars.put(node.getName(), node);
                }
            }
            return super.visit(node, data);
        }

        public Map<String, ASTVar> getCollectedOutsideVars() {
            return collectedOutsideVars;
        }
    }

    public ASTQueryContainer parseQuery(String queryStr, String baseURI) throws MalformedQueryException {
        try {
            ASTQueryContainer qc = SyntaxTreeBuilder.parseQuery(queryStr);
            StringEscapesProcessor.process(qc);
            BaseDeclProcessor.process(qc, baseURI);
            Map<String, String> prefixes = PrefixDeclProcessor.process(qc);
            WildcardProjectionProcessor.process(qc);
            BlankNodeVarProcessor.process(qc);

            if (qc.containsQuery()) {

                // handle query operation

                ASTQuery queryNode = qc.getQuery();
                if (queryNode instanceof ASTSelectQuery) {
                }
                else if (queryNode instanceof ASTConstructQuery) {
                }
                else if (queryNode instanceof ASTAskQuery) {
                }
                else if (queryNode instanceof ASTDescribeQuery) {
                }
                else {
                    throw new RuntimeException("Unexpected query type: " + queryNode.getClass());
                }

                // Handle dataset declaration
                Dataset dataset = DatasetDeclProcessor.process(qc);
                if (dataset != null) {
                }

                return qc;
            }
            else {
                throw new IncompatibleOperationException("supplied string is not a query operation");
            }
        } catch (ParseException e) {
            throw new MalformedQueryException(e.getMessage(), e);
        } catch (TokenMgrError e) {
            throw new MalformedQueryException(e.getMessage(), e);
        }
    }
}
