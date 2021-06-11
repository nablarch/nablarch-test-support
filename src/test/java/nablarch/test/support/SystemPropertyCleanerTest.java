package nablarch.test.support;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/**
 * {@link SystemPropertyCleaner} の単体テスト。
 *
 * @author Tanaka Tomoyuki
 */
public class SystemPropertyCleanerTest {
    private final SystemPropertyCleaner sut = new SystemPropertyCleaner();

    @Before
    public void setUp() {
        System.clearProperty("message");
    }

    @Test
    public void 内容が変更されたプロパティの値が元に戻ること() throws Throwable {
        System.setProperty("message", "original");
        assertThat(System.getProperty("message"), is("original"));

        sut.before();

        System.setProperty("message", "modified");
        assertThat(System.getProperty("message"), is("modified"));

        sut.after();

        assertThat(System.getProperty("message"), is("original"));
    }

    @Test
    public void 削除されたプロパティが元に戻ること() throws Throwable {
        System.setProperty("message", "original");
        assertThat(System.getProperty("message"), is("original"));

        sut.before();

        System.clearProperty("message");
        assertThat(System.getProperty("message"), is(nullValue()));

        sut.after();

        assertThat(System.getProperty("message"), is("original"));
    }

    @Test
    public void 追加されたプロパティが削除されていること() throws Throwable {
        assertThat(System.getProperty("message"), is(nullValue()));

        sut.before();

        System.setProperty("message", "added");
        assertThat(System.getProperty("message"), is("added"));

        sut.after();

        assertThat(System.getProperty("message"), is(nullValue()));
    }
}