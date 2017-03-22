package nablarch.test.support.db.datasource;

import java.sql.Connection;

public interface DbInitializer {

    void initialize(Connection connection) throws Exception;
}
