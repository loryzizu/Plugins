package eu.unifiedviews.plugins.transformer.relational;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
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
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;
import eu.unifiedviews.helpers.dpu.localization.Messages;

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
                this.context.sendMessage(DPUContext.MessageType.ERROR, this.messages.getString("errors.db.tableunique.short"), this.messages.getString("errors.db.tableunique.long"));
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
            this.outputTable.addExistingDatabaseTable(targetTableName, targetTableName);
            LOG.debug("Output table successfully saved into output relational data unit");

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
