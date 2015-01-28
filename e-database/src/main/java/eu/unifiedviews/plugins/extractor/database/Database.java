package eu.unifiedviews.plugins.extractor.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.relational.WritableRelationalDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;
import eu.unifiedviews.helpers.dpu.localization.Messages;

@DPU.AsExtractor
public class Database extends ConfigurableBase<DatabaseConfig_V1> implements ConfigDialogProvider<DatabaseConfig_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(Database.class);

    private Messages messages;

    private DPUContext context;

    @DataUnit.AsOutput(name = "internalDb")
    public WritableRelationalDataUnit outInternalDb;

    public Database() {
        super(DatabaseConfig_V1.class);
    }

    @Override
    public void execute(DPUContext context) throws DPUException, InterruptedException {
        this.context = context;
        this.messages = new Messages(context.getLocale(), this.getClass().getClassLoader());
        String shortMessage = this.getClass().getSimpleName() + " starting.";
        String longMessage = String.format("Configuration: DatabaseUrl: %s, username: %s, password: %s, "
                + "useSSL: %s, SQL query: %s",
                this.config.getDatabaseURL(), this.config.getUserName(), "***",
                this.config.isUseSSL(), this.config.getSqlQuery());
        LOG.info(shortMessage + " " + longMessage);

        try {
            Class.forName(this.config.getJdbcDriverName());
        } catch (ClassNotFoundException e) {
            throw new DPUException(this.messages.getString("errors.driver.loadfailed"), e);
        }

        Connection conn = null;
        Statement stmnt = null;
        ResultSet rs = null;
        ResultSetMetaData meta = null;

        try {
            try {
                LOG.debug("Connecting to the source database");
                conn = DatabaseHelper.createConnection(this.config);
                LOG.debug("Connected to the source database");
            } catch (SQLException e) {
                throw new DPUException(this.messages.getString("errors.db.connectionfailed"), e);
            }

            try {
                stmnt = conn.createStatement();
                LOG.info("Executing query " + this.config.getSqlQuery());
                rs = stmnt.executeQuery(this.config.getSqlQuery());
                meta = rs.getMetaData();
            } catch (SQLException e) {
                throw new DPUException(this.messages.getString("errors.db.queryfailed"), e);
            }

            try {
                String sourceTableName = DatabaseHelper.getSourceTableName(this.config.getSqlQuery());

                String tableName = this.outInternalDb.addNewDatabaseTable(sourceTableName);
                LOG.debug("New database table {} added to relational data unit", tableName);

                String createTableQuery = QueryBuilder.getCreateTableQueryFromMetaData(meta, tableName);

                LOG.debug("Creating internal db representation as " + createTableQuery);
                createInternalTable(createTableQuery);
                LOG.debug("Created database table in internal database");

                LOG.debug("Inserting data from source table into internal table");
                insertDataFromSelect(meta, rs, tableName);
                LOG.debug("Inserting data from source table into internal table successful");
            } catch (Exception e) {
                throw new DPUException(this.messages.getString("errors.db.transformfailed"), e);
            }
        } finally {
            DatabaseHelper.tryCloseDbResources(conn, stmnt, rs);
        }
    }

    private void createInternalTable(String createQuery) throws DataUnitException {
        Statement stmnt = null;
        Connection conn = null;
        try {
            conn = getConnectionInternal();
            stmnt = conn.createStatement();
            stmnt.executeUpdate(createQuery);
            conn.commit();
        } catch (SQLException e) {
            LOG.error("Failed to create internal db table", e);
            DatabaseHelper.tryRollbackConnection(conn);
            throw new DataUnitException("Error creating internal database table");
        } finally {
            DatabaseHelper.tryCloseStatement(stmnt);
            DatabaseHelper.tryCloseConnection(conn);
        }
    }

    private void insertDataFromSelect(ResultSetMetaData meta, ResultSet rs, String tableName) throws DataUnitException {
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            conn = getConnectionInternal();
            String insertQuery = QueryBuilder.getInsertQueryForPreparedStatement(meta, tableName);
            LOG.debug("Insert query for inserting into internal DB table: {}", insertQuery);
            ps = conn.prepareStatement(insertQuery);
            LOG.debug("Prepared statement for inserting data created");
            while (rs.next()) {
                fillInsertData(ps, meta, rs);
                ps.execute();
            }
            conn.commit();
        } catch (SQLException e) {
            LOG.error("Failed to load data into internal table");
            DatabaseHelper.tryRollbackConnection(conn);
            throw new DataUnitException("Error loading SQL data into internal database");
        } finally {
            DatabaseHelper.tryCloseStatement(ps);
            DatabaseHelper.tryCloseConnection(conn);
        }
    }

    private void fillInsertData(PreparedStatement ps, ResultSetMetaData meta, ResultSet rs) throws SQLException {
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            ps.setObject(i, rs.getObject(i));
        }
    }

    private Connection getConnectionInternal() throws DataUnitException {
        return this.outInternalDb.getDatabaseConnection();
    }

    @Override
    public AbstractConfigDialog<DatabaseConfig_V1> getConfigurationDialog() {
        return new DatabaseVaadinDialog();
    }

}
