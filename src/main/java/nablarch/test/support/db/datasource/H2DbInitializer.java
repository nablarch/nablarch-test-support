package nablarch.test.support.db.datasource;

import java.sql.Connection;
import java.sql.Statement;

/**
 *
 */
public class H2DbInitializer implements DbInitializer {

    @Override
    public void initialize(final Connection connection) throws Exception {
        final Statement statement = connection.createStatement();
        statement.execute("create schema ssd_master");
        statement.execute("create schema test_schema");
    }
}
