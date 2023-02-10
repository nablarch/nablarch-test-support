package nablarch.test.support.web.servlet;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * {@link MockServletContext}の単体テスト。
 * @author Tanaka Tomoyuki
 */
public class MockServletContextTest {

    private final MockServletContext sut = new MockServletContext();

    @Test
    public void testContextPath() {
        assertThat(sut.getContextPath(), is(nullValue()));

        sut.setContextPath("/test");

        assertThat(sut.getContextPath(), is("/test"));
    }
}