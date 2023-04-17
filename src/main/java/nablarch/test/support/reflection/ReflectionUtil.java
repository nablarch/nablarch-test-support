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
    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object object, String fieldName) {
        try {
            final Field field = getDeclaredField(object.getClass(), object, fieldName);
            if (!field.canAccess(object)) {
                field.setAccessible(true);
            }
            return (T) field.get(object);
        } catch (IllegalAccessException e) {
            // setAccessible(true) でアクセス可にするためこの例外がスローされることはない
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 指定したオブジェクトの指定したフィールドに、値を設定する。
     * <p>
     * このメソッドは、フィールドの可視性に関係なく値を設定できる。<br>
     * カプセル化を破壊し、クラス間の静的な依存関係が分からなくなるため、
     * このメソッドの乱用は避けること<br>
     * 極力このメソッドを使用しなくてもテストができるようにクラスを設計し、
     * どうしても可視性を無視して値を設定しなけばテストできない場合にのみ使用すること。
     * </p>
     * <p>
     * 指定されたオブジェクトに指定されたフィールドが存在しない場合は、
     * 親クラスを遡ってフィールドを探索する。
     * </p>
     * 
     * @param object 値を設定するオブジェクト
     * @param fieldName フィールドの名前
     * @param value フィールドに設定する値
     * @throws IllegalArgumentException 指定されたフィールドが存在しない場合
     */
    public static void setFieldValue(Object object, String fieldName, Object value) {
        try {
            final Field field = getDeclaredField(object.getClass(), object, fieldName);
            if (!field.canAccess(object)) {
                field.setAccessible(true);
            }
            field.set(object, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 指定されたフィールドを親クラスに遡って再帰的に探索して取得する。
     * @param clazz 探索対象のクラス
     * @param object 探索対象のオブジェクト
     * @param fieldName 探索対象のフィールド名
     * @return 取得した {@link Field} オブジェクト
     * @throws IllegalArgumentException フィールドが見つからない場合
     */
    private static Field getDeclaredField(Class<?> clazz, Object object, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            final Class<?> superClass = clazz.getSuperclass();
            if (superClass == null) {
                throw new IllegalArgumentException("The field 'unknownField' is not found in object (" + object + ").");
            } else {
                return getDeclaredField(superClass, object, fieldName);
            }
        }
    }

    // インスタンス化させないためコンストラクタをprivateで宣言
    private ReflectionUtil() {}
}
