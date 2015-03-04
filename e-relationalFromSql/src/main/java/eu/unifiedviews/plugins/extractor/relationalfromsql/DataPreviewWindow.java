package eu.unifiedviews.plugins.extractor.relationalfromsql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * Data preview window for SQL data
 * Based on {@link RelationalFromSqlConfig_V2}, data is obtained from database and data is shown in table
 */
public class DataPreviewWindow extends Window {

    private static final long serialVersionUID = 7315376034063375700L;

    private Table previewTable;

    /**
     * Constructor for preview data window
     * 
     * @param config
     *            DPU configuration with database connection parameters and SQL select query
     * @param limit
     *            Limit result rows to
     * @throws SQLException
     *             If something fails
     */
    public DataPreviewWindow(RelationalFromSqlConfig_V2 config, int limit) throws SQLException {
        setClosable(true);
        center();
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setWidth("100%");
        layout.setHeight("-1px");

        this.previewTable = createTable(config, limit);
        layout.addComponent(this.previewTable);
        setContent(layout);
    }

    @SuppressWarnings("unchecked")
    private Table createTable(RelationalFromSqlConfig_V2 config, int limit) throws SQLException {
        Table table = new Table();
        Connection conn = null;
        Statement stmnt = null;
        ResultSet rs = null;
        try {
            conn = RelationalFromSqlHelper.createConnection(config);
            stmnt = conn.createStatement();
            rs = stmnt.executeQuery(getLimitedQuery(config.getSqlQuery(), limit));
            ResultSetMetaData meta = rs.getMetaData();

            List<ColumnDefinition> columns = RelationalFromSqlHelper.getTableColumnsFromMetaData(meta);
            for (ColumnDefinition column : columns) {
                table.addContainerProperty(column.getColumnName(), getClassForColumn(column), null);
            }
            while (rs.next()) {
                Object itemId = table.addItem();
                Item item = table.getItem(itemId);
                for (ColumnDefinition column : columns) {
                    item.getItemProperty(column.getColumnName()).setValue(rs.getObject(column.getColumnName()));
                }
            }
        } finally {
            RelationalFromSqlHelper.tryCloseDbResources(conn, stmnt, rs);
        }

        return table;
    }

    private String getLimitedQuery(String query, int limit) {
        StringBuilder limitedQuery = null;
        if (query.endsWith(";")) {
            limitedQuery = new StringBuilder(query.substring(0, query.length() - 1));
        } else {
            limitedQuery = new StringBuilder(query);
        }
        limitedQuery.append(" LIMIT ");
        limitedQuery.append(limit);

        return limitedQuery.toString();

    }

    private Class<?> getClassForColumn(ColumnDefinition column) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(column.getTypeClassName());
        } catch (ClassNotFoundException e) {
            clazz = String.class;
        }

        return clazz;
    }

}
