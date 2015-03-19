package eu.unifiedviews.plugins.transformer.relational;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.relational.RelationalDataUnit;
import eu.unifiedviews.dataunit.relational.WritableRelationalDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.resource.Resource;
import eu.unifiedviews.helpers.dataunit.resource.ResourceHelpers;
import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.config.migration.ConfigurationUpdate;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.extension.ExtensionInitializer;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultTolerance;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultToleranceUtils;

/**
 * {@link Relational} transforms N input internal database tables into 1 output table using user typed SELECT SQL queries.
 * Output table is also stored in internal database to be accessible by other relational DPUs
 * <p/>
 * <b>WARNING:</b>This DPU is a part of optional UV relational functionality and relational DPUs currently do not fully follow UV philosophy as the user has
 * control of physical database table names
 * <p/>
 * The general philosophy of UV so far is, that DPU developer cannot influence the physical location of the internal files, graphs -- UV manages its internal
 * stores. As a result, such approach should be similar in relational data unit and DPU developer should NOT be able to set the target table name (currently,
 * there is a configuration option for extractor and transformer, which allows to set up the target table name). Target table name is ok for loader, but not for
 * extractor/transformer, where the target table is given by the data flow in the pipeline.
 * <p/>
 * Current implementation is a compromise to be able to provide a general, SQL query based transformer for users. If user did not know the real table name, he
 * would not be able to write SQL queries. Not without complex parsing of SQL queries. Further discussion is needed to solve this issue and this will be aim of
 * future releases.
 */
@DPU.AsTransformer
public class Relational extends AbstractDpu<RelationalConfig_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(Relational.class);

    @DataUnit.AsInput(name = "inputTables")
    public RelationalDataUnit inputTables;

    @DataUnit.AsOutput(name = "outputTable")
    public WritableRelationalDataUnit outputTable;

    @ExtensionInitializer.Init
    public FaultTolerance faultTolerance;

    @ExtensionInitializer.Init(param = "eu.unifiedviews.plugins.transformer.relational.RelationalConfig__V1")
    public ConfigurationUpdate _ConfigurationUpdate;

    public Relational() {
        super(RelationalVaadinDialog.class, ConfigHistory.noHistory(RelationalConfig_V1.class));
    }

    @Override
    protected void innerExecute() throws DPUException {
        String targetTableName = this.config.getTargetTableName().toUpperCase();
        String userSqlQuery = this.config.getSqlQuery();
        final String symbolicName = targetTableName;

        final List<RelationalDataUnit.Entry> tables = FaultToleranceUtils.getEntries(faultTolerance, inputTables, RelationalDataUnit.Entry.class);

        int tablesCount = tables.size();
        if (tablesCount < 1) {
            throw ContextUtils.dpuException(ctx, "errors.tables.input");
        }

        Connection conn = null;
        Statement stmnt = null;
        try {
            conn = getDbConnectionInternal();

            if (DatabaseHelper.checkTableExists(conn, targetTableName)) {
                throw ContextUtils.dpuException(ctx, "errors.db.tableunique.long");
            }

            LOG.debug("Going to convert user select SQL query {} to Select into query", userSqlQuery);
            String selectIntoQuery = DatabaseHelper.convertSelectQueryToSelectIntoQuery(userSqlQuery, targetTableName);
            LOG.debug("Query converted to: {}", selectIntoQuery);

            // TODO: maybe here should be validated if the given tables are in the input data unit, but it requires SQL query parsing
            LOG.debug("Executing SQL transformation of input tables into output table");
            stmnt = conn.createStatement();
            stmnt.execute(selectIntoQuery);
            LOG.debug("Transformation of input tables into output table successful");

            LOG.debug("Saving output table into output relational data unit");
            this.outputTable.addExistingDatabaseTable(symbolicName, targetTableName);
            LOG.debug("Output table successfully saved into output relational data unit");

            if (this.config.getPrimaryKeyColumns() == null || this.config.getPrimaryKeyColumns().isEmpty()) {
                LOG.debug("No primary keys defined, nothing to do");
            } else {
                LOG.debug("Going to create primary keys for table {}", targetTableName);
                // TODO: CREATE TABLE AS SELECT does not preserve not null
                // quick and dirty solution: add NOT NULL for all primary keys
                for (String key : this.config.getPrimaryKeyColumns()) {
                    String alterQuery = DatabaseHelper.createAlterColumnSetNotNullQuery(targetTableName, key);
                    stmnt.execute(alterQuery);
                }
                String alterTablesQuery = DatabaseHelper.createPrimaryKeysQuery(targetTableName, this.config.getPrimaryKeyColumns());
                stmnt.execute(alterTablesQuery);
            }
            faultTolerance.execute(new FaultTolerance.Action() {

                @Override
                public void action() throws Exception {
                    Resource resource = ResourceHelpers.getResource(outputTable, symbolicName);
                    Date now = new Date();
                    resource.setCreated(now);
                    resource.setLast_modified(now);
                    ResourceHelpers.setResource(outputTable, symbolicName, resource);
                }
            });
            LOG.debug("Resource parameters for table updated");

        } catch (SQLException se) {
            LOG.error("Database error occured during transforming database tables", se);
            DatabaseHelper.tryRollbackConnection(conn);
            throw ContextUtils.dpuException(ctx, se, "errors.db.transformfailed");
        } catch (Exception e) {
            LOG.error("Error occured during transforming database tables", e);
            throw ContextUtils.dpuException(ctx, e, "errors.transformfailed");
        } finally {
            DatabaseHelper.tryCloseStatement(stmnt);
            DatabaseHelper.tryCloseConnection(conn);
        }

    }

    private Connection getDbConnectionInternal() throws DataUnitException {
        return this.inputTables.getDatabaseConnection();
    }

}
