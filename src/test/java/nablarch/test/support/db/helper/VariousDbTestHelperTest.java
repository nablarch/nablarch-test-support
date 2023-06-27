package nablarch.test.support.db.helper;

import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * {@link VariousDbTestHelper}の単体テスト。
 * 
 * @author Tanaka Tomoyuki
 */
@RunWith(DatabaseTestRunner.class)
public class VariousDbTestHelperTest {
    private static final Logger LOGGER = LoggerManager.get(VariousDbTestHelperTest.class);
    
    private Connection con;
    private PreparedStatement ps;
    private ResultSet rs;

    /**
     * フィールドに {@link Temporal}が設定されている場合、
     * {@link TemporalType}によって適切な型のカラムが生成されることをテスト。
     * @throws Exception 例外が発生した場合
     */
    @Test
    public void testCreateTableWithTemporalType() throws Exception {
        VariousDbTestHelper.createTable(TestTable.class);

        con = VariousDbTestHelper.getNativeConnection();
        ps = con.prepareStatement("SELECT ENTITY_ID, DATE_COL, TIMESTAMP_COL, TIME_COL FROM TEST_TABLE");
        rs = ps.executeQuery();

        final ResultSetMetaData metaData = rs.getMetaData();
        
        assertThat("The type of DATE_COL is DATE.", metaData.getColumnType(2), is(equalTo(Types.DATE)));
        assertThat("The type of TIMESTAMP_COL is TIMESTAMP", metaData.getColumnType(3), is(equalTo(Types.TIMESTAMP)));
        assertThat("The type of TIME_COL is TIME", metaData.getColumnType(4), is(equalTo(Types.TIME)));
    }

    @After
    public void closeConnection() throws Exception {
        closeQuietly(rs);
        closeQuietly(ps);
        
        if (con != null) {
            con.close();
        }
    }
    
    private void closeQuietly(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                LOGGER.logWarn("failed to close ResultSet", e);
            }
        }
    }

    private void closeQuietly(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                LOGGER.logWarn("failed to close Statement", e);
            }
        }
    }
    

    @Entity
    @Table(name = "TEST_TABLE")
    public static class TestTable {

        @Id
        @Column(name = "ENTITY_ID", length = 18, nullable = false)
        public Long id;
        
        @Column(name = "DATE_COL")
        @Temporal(TemporalType.DATE)
        public Date date;

        @Column(name = "TIMESTAMP_COL")
        @Temporal(TemporalType.TIMESTAMP)
        public Timestamp timestamp;

        @Column(name = "TIME_COL")
        @Temporal(TemporalType.TIME)
        public Time time;
    }
}
