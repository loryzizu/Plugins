package eu.unifiedviews.plugins.transformer.relational;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.relational.RelationalDataUnit;
import eu.unifiedviews.dataunit.relational.WritableRelationalDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.relationalhelper.RelationalHelper;
import eu.unifiedviews.helpers.dataunit.resourcehelper.Resource;
import eu.unifiedviews.helpers.dataunit.resourcehelper.ResourceHelpers;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;
import eu.unifiedviews.helpers.dpu.localization.Messages;

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
public class Relational extends ConfigurableBase<RelationalConfig_V1> implements ConfigDialogProvider<RelationalConfig_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(Relational.class);

    private Messages messages;

    private DPUContext context;

    @DataUnit.AsInput(name = "inputTables")
    public RelationalDataUnit inputTables;

    @DataUnit.AsOutput(name = "outputTable")
    public WritableRelationalDataUnit outputTable;

    public Relational() {
        super(RelationalConfig_V1.class);
    }

    @Override
    public void execute(DPUContext context) throws DPUException, InterruptedException {
        this.context = context;
        this.messages = new Messages(this.context.getLocale(), this.getClass().getClassLoader());

        String targetTableName = this.config.getTargetTableName().toUpperCase();
        String userSqlQuery = this.config.getSqlQuery();
        String symbolicName = targetTableName;

        Iterator<RelationalDataUnit.Entry> tablesIteration;
        try {
            tablesIteration = RelationalHelper.getTables(this.inputTables).iterator();
        } catch (DataUnitException ex) {
            this.context.sendMessage(DPUContext.MessageType.ERROR, this.messages.getString("errors.dpu.failed"), this.messages.getString("errors.tables.iterator"), ex);
            return;
        }

        int tablesCount = 0;
        while (tablesIteration.hasNext()) {
            tablesIteration.next();
            tablesCount++;
        }
        if (tablesCount < 1) {
            this.context.sendMessage(DPUContext.MessageType.ERROR, this.messages.getString("errors.dpu.failed"), this.messages.getString("errors.tables.input"));
            return;
        }

        Connection conn = null;
        Statement stmnt = null;
        try {
            conn = getDbConnectionInternal();

            if (DatabaseHelper.checkTableExists(conn, targetTableName)) {
                this.context.sendMessage(DPUContext.MessageType.ERROR, this.messages.getString("errors.db.tableunique.short", targetTableName), this.messages.getString("errors.db.tableunique.long"));
                return;
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
                conn.commit();
            }

            if (this.config.getIndexedColumns() == null || this.config.getIndexedColumns().isEmpty()) {
                LOG.debug("No indexed columns defined for target table, nothing to do");
            } else {
                LOG.debug("Going to create indexes for table {}", targetTableName);
                for (String indexedColumn : this.config.getIndexedColumns()) {
                    String indexQuery = DatabaseHelper.getCreateIndexQuery(targetTableName, indexedColumn);
                    stmnt.execute(indexQuery);
                }
                conn.commit();
            }

            Resource resource = ResourceHelpers.getResource(this.outputTable, symbolicName);
            Date now = new Date();
            resource.setCreated(now);
            resource.setLast_modified(now);
            ResourceHelpers.setResource(this.outputTable, symbolicName, resource);
            LOG.debug("Resource parameters for table updated");

        } catch (SQLException se) {
            LOG.error("Database error occured during transforming database tables", se);
            DatabaseHelper.tryRollbackConnection(conn);
            throw new DPUException(this.messages.getString("errors.db.transformfailed"), se);
        } catch (Exception e) {
            LOG.error("Error occured during transforming database tables", e);
            throw new DPUException(this.messages.getString("errors.transformfailed"), e);
        } finally {
            DatabaseHelper.tryCloseStatement(stmnt);
            DatabaseHelper.tryCloseConnection(conn);
        }

    }

    @Override
    public AbstractConfigDialog<RelationalConfig_V1> getConfigurationDialog() {
        return new RelationalVaadinDialog();
    }

    private Connection getDbConnectionInternal() throws DataUnitException {
        return this.inputTables.getDatabaseConnection();
    }

}
