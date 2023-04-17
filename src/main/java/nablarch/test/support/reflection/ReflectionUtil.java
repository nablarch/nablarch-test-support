package nablarch.test.support.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

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
            final Field field = obtainDeclaredField(object.getClass(), object, fieldName);
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
            final Field field = obtainDeclaredField(object.getClass(), object, fieldName);
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
    private static Field obtainDeclaredField(Class<?> clazz, Object object, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            final Class<?> superClass = clazz.getSuperclass();
            if (superClass == null) {
                throw new IllegalArgumentException("The field '" + fieldName + "' is not found in object (" + object + ").");
            } else {
                return obtainDeclaredField(superClass, object, fieldName);
            }
        }
    }

    /**
     * 指定されたオブジェクトの指定された引数なしのメソッドを実行する。
     * <p>
     * このメソッドは、メソッドの可視性に関係なくメソッドを実行できる。<br>
     * カプセル化を破壊し、クラス間の静的な依存関係が分からなくなるため、
     * このメソッドの乱用は避けること<br>
     * 極力このメソッドを使用しなくてもテストができるようにクラスを設計し、
     * どうしても可視性を無視してメソッドを実行しなけばテストできない場合にのみ使用すること。
     * </p>
     * <p>
     * 指定されたオブジェクトに指定されたメソッドが存在しない場合は、
     * 親クラスを遡ってメソッドを探索する。
     * </p>
     *
     * @param object メソッドを実行するオブジェクト
     * @param methodName 実行するメソッドの名前
     * @return メソッドの戻り値 ({@code void} の場合は {@code null} を返す)
     * @param <T> 戻り値の型
     */
    public static <T> T invokeMethod(Object object, String methodName) {
        return invokeMethod(object, methodName, List.of(), List.of());
    }

    /**
     * 指定されたオブジェクトの指定されたメソッドを実行する。
     * <p>
     * このメソッドは、メソッドの可視性に関係なくメソッドを実行できる。<br>
     * カプセル化を破壊し、クラス間の静的な依存関係が分からなくなるため、
     * このメソッドの乱用は避けること<br>
     * 極力このメソッドを使用しなくてもテストができるようにクラスを設計し、
     * どうしても可視性を無視してメソッドを実行しなけばテストできない場合にのみ使用すること。
     * </p>
     * <p>
     * 指定されたオブジェクトに指定されたメソッドが存在しない場合は、
     * 親クラスを遡ってメソッドを探索する。
     * </p>
     * 
     * @param object メソッドを実行するオブジェクト
     * @param methodName 実行するメソッドの名前
     * @param argTypes 引数の型のリスト
     * @param args メソッドに渡す引数のリスト
     * @return メソッドの戻り値 ({@code void} の場合は {@code null} を返す)
     * @param <T> 戻り値の型
     */
    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(Object object, String methodName, List<Class<?>> argTypes, List<Object> args) {
        final Method method = obtainDeclaredMethod(object.getClass(), object, methodName, argTypes);
        if (!method.canAccess(object)) {
            method.setAccessible(true);
        }
        try {
            return (T)method.invoke(object, args.toArray(Object[]::new));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static Method obtainDeclaredMethod(Class<?> clazz, Object object, String methodName, List<Class<?>> argTypes) {
        try {
            return clazz.getDeclaredMethod(methodName, argTypes.toArray(Class<?>[]::new));
        } catch (NoSuchMethodException e) {
            final Class<?> superclass = clazz.getSuperclass();
            if (superclass != null) {
                return obtainDeclaredMethod(superclass, object, methodName, argTypes);
            }

            final String argTypesText = argTypes.stream().map(Class::getName).collect(Collectors.joining(", "));
            throw new IllegalArgumentException(
                    "The method '" + methodName + "(" + argTypesText + ")' is not found in object (" + object + ").");
        }
    }

    // インスタンス化させないためコンストラクタをprivateで宣言
    private ReflectionUtil() {}
}
