package nablarch.test.support.reflection;

import java.io.IOException;

public class ThrowExceptionConstructor {
    public ThrowExceptionConstructor() throws IOException {
        throw new IOException("test");
    }
}
