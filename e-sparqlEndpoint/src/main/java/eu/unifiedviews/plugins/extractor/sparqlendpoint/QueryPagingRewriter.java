package eu.unifiedviews.plugins.extractor.sparqlendpoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openrdf.query.BindingSet;
import org.openrdf.query.Dataset;
import org.openrdf.query.algebra.Extension;
import org.openrdf.query.algebra.ProjectionElem;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.algebra.evaluation.QueryOptimizer;
import org.openrdf.query.algebra.helpers.QueryModelVisitorBase;

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
                if ((extensionsMet <= 1)&&(!node.isAnonymous())) {
                    collectedOutsideVars.put(node.getName(), node);
                }
                if (extensionsMet >= 1){
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
            for (Map.Entry<String, Var> entry : collectedOutsideVars.entrySet() ) {
                result.put(entry.getKey(), entry.getValue());
            }
            return result;
        }
    }

    @Override
    public void optimize(TupleExpr tupleExpr, Dataset dataset, BindingSet bindings) {
        VarCollector varCollector = new VarCollector();
        varCollector.optimize(tupleExpr, dataset, bindings);
    }
}
