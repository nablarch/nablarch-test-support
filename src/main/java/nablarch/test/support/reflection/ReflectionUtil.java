package nablarch.test.support.reflection;

import java.lang.reflect.Field;

/**
 * リフレクションを使った処理を提供するユーティリティクラス。
 * 
 * @author Tanaka Tomoyuki
 */
public class ReflectionUtil {

    /**
     * 指定したオブジェクトの指定したフィールドの値を取得する。
     * <p>
     * このメソッドは、フィールドの可視性に関係なく値を取得できる。<br>
     * カプセル化を破壊し、クラス間の静的な依存関係が分からなくなるため、
     * このメソッドの乱用は避けること<br>
     * 極力このメソッドを使用しなくてもテストができるようにクラスを設計し、
     * どうしても可視性を無視して値を取得しなけばテストできない場合にのみ使用すること。
     * </p>
     * <p>
     * 指定されたオブジェクトに指定されたフィールドが存在しない場合は、
     * 親クラスを遡ってフィールドを探索する。
     * </p>
     * 
     * @param object フィールドの値を取得するオブジェクト
     * @param fieldName 取得するフィールド名
     * @return フィールドの値
     * @param <T> フィールドの型
     * @throws IllegalArgumentException 指定されたフィールドが見つからない場合
     */

    public static <T> T getFieldValue(Object object, String fieldName) {
        return getFieldValue(object.getClass(), object, fieldName);
    }

    @SuppressWarnings("unchecked")
    private static <T> T getFieldValue(Class<?> clazz, Object object, String fieldName) {
        try {
            final Field field = clazz.getDeclaredField(fieldName);
            if (!field.canAccess(object)) {
                field.setAccessible(true);
            }
            return (T) field.get(object);
        } catch (NoSuchFieldException e) {
            final Class<?> superClass = clazz.getSuperclass();
            if (superClass == null) {
                throw new IllegalArgumentException("The field 'unknownField' is not found in object (" + object + ").");
            } else {
                return getFieldValue(superClass, object, fieldName);
            }
        } catch (IllegalAccessException e) {
            // setAccessible(true) でアクセス可にするためこの例外がスローされることはない
            throw new RuntimeException(e);
        }
    }
    
    
    // インスタンス化させないためコンストラクタをprivateで宣言
    private ReflectionUtil() {}
}
