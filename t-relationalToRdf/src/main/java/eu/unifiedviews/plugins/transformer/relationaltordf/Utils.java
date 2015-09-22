package eu.unifiedviews.plugins.transformer.relationaltordf;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {

    private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

    private Utils() {

    }

    public static String convertStringToURIPart(String part) {
        try {
            return URLEncoder.encode(part, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("Unsupported encoding", ex);
        }
    }

    public static void tryCloseDbResources(Connection conn, Statement stmnt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            LOG.warn("Failed to close result set", e);
        }

        try {
            if (stmnt != null) {
                stmnt.close();
            }
        } catch (SQLException e) {
            LOG.warn("Failed to close result statement", e);
        }

        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            LOG.warn("Failed to close result connection", e);
        }
    }

    public static void tryCloseDbResources(Statement stmnt, ResultSet rs) {
        tryCloseDbResources(null, stmnt, rs);
    }

}
