package nablarch.test.support.web.servlet;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * {@link MockServletRequest}の単体テスト。
 * @author Tanaka Tomoyuki
 */
public class MockServletRequestTest {
    private final MockServletRequest sut = new MockServletRequest();

    @Test
    public void testServletContext() {
        assertThat(sut.getServletContext(), is(nullValue()));

        MockServletContext context = new MockServletContext();
        sut.setServletContext(context);

        assertThat(sut.getServletContext(), is(sameInstance(context)));
    }
}