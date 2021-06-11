package nablarch.test.support;

import org.junit.rules.ExternalResource;

import java.util.Properties;

/**
 * システムプロパティをテスト前の状態に戻すJUnitルール。
 * <p>
 * このクラスは、テスト前のシステムプロパティの状態を記録します。
 * そして、テスト後に記録しておいた状態でシステムプロパティをロールバックします。
 * </p>
 * @author Tanaka Tomoyuki
 */
public class SystemPropertyCleaner extends ExternalResource {

    private Properties originalProperties;

    @Override
    protected void before() {
        originalProperties = (Properties) System.getProperties().clone();
    }

    @Override
    protected void after() {
        System.setProperties(originalProperties);
    }
}
