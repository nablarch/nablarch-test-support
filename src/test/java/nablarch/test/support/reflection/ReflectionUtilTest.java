package nablarch.test.support.reflection;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;

/**
 * {@link ReflectionUtil}の単体テスト。
 * 
 * @author Tanaka Tomoyuki
 */
public class ReflectionUtilTest {

    /**
     * 指定されたオブジェクトで定義されたフィールドは可視性に関係なく全て取得できること。
     */
    @Test
    public void testGetFieldValue() {
        final Parent parent = new Parent();
        
        assertThat(ReflectionUtil.getFieldValue(parent, "publicField"), is("Parent#publicField"));
        assertThat(ReflectionUtil.getFieldValue(parent, "protectedField"), is("Parent#protectedField"));
        assertThat(ReflectionUtil.getFieldValue(parent, "packagePrivateField"), is("Parent#packagePrivateField"));
        assertThat(ReflectionUtil.getFieldValue(parent, "privateField"), is("Parent#privateField"));
    }

    /**
     * サブクラスのフィールドに対してgetFieldValueを使ったときのテスト。
     * <ul>
     *   <li>オーバーライドされたフィールドはサブクラスの値が取得できること</li>
     *   <li>親クラスのフィールドも取得できること</li>
     * </ul>
     */
    @Test
    public void testGetFieldValueAtSubClass() {
        final Sub sub = new Sub();

        assertThat(ReflectionUtil.getFieldValue(sub, "publicField"), is("Sub#publicField"));
        assertThat(ReflectionUtil.getFieldValue(sub, "protectedField"), is("Sub#protectedField"));
        assertThat(ReflectionUtil.getFieldValue(sub, "packagePrivateField"), is("Sub#packagePrivateField"));
        assertThat(ReflectionUtil.getFieldValue(sub, "privateField"), is("Sub#privateField"));
        assertThat(ReflectionUtil.getFieldValue(sub, "parentOnlyField"), is("Parent#parentOnlyField"));
    }

    /**
     * getFieldValueで指定されたフィールドが存在しない場合は例外がスローされること。
     */
    @Test
    public void testGetFieldValueThrowsExceptionIfNotFoundField() {
        final Parent parent = new Parent();

        final IllegalArgumentException exception
                = assertThrows(IllegalArgumentException.class, () -> ReflectionUtil.getFieldValue(parent, "unknownField"));

        assertThat(exception.getMessage(), is("The field 'unknownField' is not found in object (" + parent + ")."));
    }

    /**
     * 指定されたオブジェクトのフィールドに可視性に関係なく値が設定できること。
     */
    @Test
    public void testSetFieldValue() {
        final Parent parent = new Parent();
        
        ReflectionUtil.setFieldValue(parent, "publicField", "PUBLIC_FIELD");
        ReflectionUtil.setFieldValue(parent, "protectedField", "PROTECTED_FIELD");
        ReflectionUtil.setFieldValue(parent, "packagePrivateField", "PACKAGE_PRIVATE_FIELD");
        ReflectionUtil.setFieldValue(parent, "privateField", "PRIVATE_FIELD");

        assertThat(ReflectionUtil.getFieldValue(parent, "publicField"), is("PUBLIC_FIELD"));
        assertThat(ReflectionUtil.getFieldValue(parent, "protectedField"), is("PROTECTED_FIELD"));
        assertThat(ReflectionUtil.getFieldValue(parent, "packagePrivateField"), is("PACKAGE_PRIVATE_FIELD"));
        assertThat(ReflectionUtil.getFieldValue(parent, "privateField"), is("PRIVATE_FIELD"));
    }

    /**
     * サブクラスのフィールドに対してsetFieldValueを使ったときのテスト。
     * <ul>
     *   <li>オーバーライドされたフィールドに設定できること</li>
     *   <li>親クラスのフィールドも設定できること</li>
     * </ul>
     */
    @Test
    public void testSetFieldValueAtSubClass() {
        final Sub sub = new Sub();

        ReflectionUtil.setFieldValue(sub, "publicField", "PUBLIC_FIELD");
        ReflectionUtil.setFieldValue(sub, "protectedField", "PROTECTED_FIELD");
        ReflectionUtil.setFieldValue(sub, "packagePrivateField", "PACKAGE_PRIVATE_FIELD");
        ReflectionUtil.setFieldValue(sub, "privateField", "PRIVATE_FIELD");
        ReflectionUtil.setFieldValue(sub, "parentOnlyField", "PARENT_ONLY_FIELD");

        assertThat(ReflectionUtil.getFieldValue(sub, "publicField"), is("PUBLIC_FIELD"));
        assertThat(ReflectionUtil.getFieldValue(sub, "protectedField"), is("PROTECTED_FIELD"));
        assertThat(ReflectionUtil.getFieldValue(sub, "packagePrivateField"), is("PACKAGE_PRIVATE_FIELD"));
        assertThat(ReflectionUtil.getFieldValue(sub, "privateField"), is("PRIVATE_FIELD"));
        assertThat(ReflectionUtil.getFieldValue(sub, "parentOnlyField"), is("PARENT_ONLY_FIELD"));
    }

    /**
     * setFieldValueで指定されたフィールドが存在しない場合は例外がスローされること。
     */
    @Test
    public void testSetFieldValueThrowsExceptionIfNotFoundField() {
        final Parent parent = new Parent();

        final IllegalArgumentException exception
                = assertThrows(IllegalArgumentException.class,
                () -> ReflectionUtil.setFieldValue(parent, "unknownField", "value"));

        assertThat(exception.getMessage(), is("The field 'unknownField' is not found in object (" + parent + ")."));
    }
}