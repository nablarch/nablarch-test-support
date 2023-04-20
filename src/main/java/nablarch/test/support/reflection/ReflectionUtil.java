package nablarch.test.support.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * リフレクションを使った処理を提供するユーティリティクラス。
 * <p>
 * このクラスは、5uXXの頃にモックライブラリとして使っていたJMockitに組み込まれていた{@code Deencapsulation}を
 * 置き換えるために作成された。<br>
 * JMockitはjacocoと競合してJava 17でテストができなくなるため、6uXXでMockitoに置き換えられた。<br>
 * その際、Mockitoには{@code Deencapsulation}に対応するものが無かったため、必要最小限の機能のみを
 * 独自に実装したものがこのクラスになる。
 * </p>
 * <p>
 * リフレクションを使うとクラス間の静的な依存関係が分からなくなり変更に脆くなる。
 * このため、このクラスのメソッドは極力使用せず、また新規メソッドの追加も極力行わないようにすること。
 * リフレクションを使わなくてもテストできるように、クラス設計の方を調整することを検討すること。
 * </p>
 * 
 * @author Tanaka Tomoyuki
 */
public class ReflectionUtil {

    /**
     * 指定したクラスの指定した{@code static}フィールドの値を取得する。
     * <p>
     * このメソッドは、フィールドの可視性に関係なく値を取得できる。<br>
     * カプセル化を破壊し、クラス間の静的な依存関係が分からなくなるため、
     * このメソッドの乱用は避けること<br>
     * 極力このメソッドを使用しなくてもテストができるようにクラスを設計し、
     * どうしても可視性を無視して値を取得しなけばテストできない場合にのみ使用すること。
     * </p>
     * <p>
     * 指定されたクラスに指定されたフィールドが存在しない場合は、
     * 親クラスを遡ってフィールドを探索する。
     * </p>
     *
     * @param clazz フィールドの値を取得するクラスオブジェクト
     * @param fieldName 取得するフィールド名
     * @return フィールドの値
     * @param <T> フィールドの型
     * @throws IllegalArgumentException 指定されたフィールドが見つからない場合
     */
    public static <T> T getFieldValue(Class<?> clazz, String fieldName) {
        final Field field = obtainDeclaredField(clazz, fieldName)
                .orElseThrow(() -> createExceptionForClassFieldNotFound(clazz, fieldName));
        return getFieldValueForce(field, null);
    }

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

    /**
     * 指定されたフィールドの値を取得する。
     * @param clazz 対象のクラスオブジェクト
     * @param object 対象のオブジェクト({@code static}フィールドの場合は{@code null})
     * @param fieldName 取得するフィールド名
     * @return 取得したフィールドの値
     * @param <T> フィールドの型
     */
    private static <T> T getFieldValue(Class<?> clazz, Object object, String fieldName) {
        final Field field = obtainDeclaredField(clazz, fieldName)
                .orElseThrow(() -> createExceptionForInstanceFieldNotFound(object, fieldName));
        return getFieldValueForce(field, object);
    }

    /**
     * 指定したフィールドの値を強制的に取得する。
     * @param field 対象のフィールド
     * @param object フィールドを持つオブジェクト(staticフィールドの場合はnull)
     * @return フィールドの値
     * @param <T> フィールドの型
     */
    @SuppressWarnings("unchecked")
    private static <T> T getFieldValueForce(Field field, Object object) {
        try {
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
     * 指定したクラスの指定した{@code static}フィールドに、値を設定する。
     * <p>
     * このメソッドは、フィールドの可視性に関係なく値を設定できる。<br>
     * カプセル化を破壊し、クラス間の静的な依存関係が分からなくなるため、
     * このメソッドの乱用は避けること<br>
     * 極力このメソッドを使用しなくてもテストができるようにクラスを設計し、
     * どうしても可視性を無視して値を設定しなけばテストできない場合にのみ使用すること。
     * </p>
     * <p>
     * 指定されたクラスに指定された{@code static}フィールドが存在しない場合は、
     * 親クラスを遡ってフィールドを探索する。
     * </p>
     *
     * @param clazz 値を設定するクラス
     * @param fieldName フィールドの名前
     * @param value フィールドに設定する値
     * @throws IllegalArgumentException 指定されたフィールドが存在しない場合
     */
    public static void setFieldValue(Class<?> clazz, String fieldName, Object value) {
        final Field field = obtainDeclaredField(clazz, fieldName)
                .orElseThrow(() -> createExceptionForClassFieldNotFound(clazz, fieldName));
        setFieldValueForce(field, null, value);
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
        final Field field = obtainDeclaredField(object.getClass(), fieldName)
                .orElseThrow(() -> createExceptionForInstanceFieldNotFound(object, fieldName));
        setFieldValueForce(field, object, value);
    }

    /**
     * 指定したフィールドに強制的に値を設定する。
     * @param field 対象のフィールド
     * @param object フィールドを持つオブジェクト（staticフィールドの場合はnull）
     * @param value 設定する値
     */
    private static void setFieldValueForce(Field field, Object object, Object value) {
        try {
            if (!field.canAccess(object)) {
                field.setAccessible(true);
            }
            field.set(object, value);
        } catch (IllegalAccessException e) {
            // setAccessible(true) でアクセス可にするためこの例外がスローされることはない
            throw new RuntimeException(e);
        }
    }

    /**
     * インスタンスフィールドが見つからない場合の例外を生成する。
     * @param object 探索対象となったオブジェクト
     * @param fieldName フィールド名
     * @return
     */
    private static IllegalArgumentException createExceptionForInstanceFieldNotFound(Object object, String fieldName) {
        return new IllegalArgumentException("The field '" + fieldName + "' is not found in object (" + object + ").");
    }

    /**
     * クラスフィールドが見つからない場合の例外を生成する。
     * @param clazz 探索対象となった最初のクラス
     * @param fieldName フィールド名
     * @return 例外
     */
    private static IllegalArgumentException createExceptionForClassFieldNotFound(Class<?> clazz, String fieldName) {
        return new IllegalArgumentException("The field '" + fieldName + "' is not found in class (" + clazz.getName() + ").");
    }

    /**
     * 指定されたフィールドを親クラスに遡って再帰的に探索して取得する。
     * @param clazz 探索対象のクラス
     * @param fieldName 探索対象のフィールド名
     * @return 取得した {@link Field} オブジェクト
     */
    private static Optional<Field> obtainDeclaredField(Class<?> clazz, String fieldName) {
        try {
            return Optional.of(clazz.getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            final Class<?> superClass = clazz.getSuperclass();
            if (superClass == null) {
                return Optional.empty();
            } else {
                return obtainDeclaredField(superClass, fieldName);
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

    /**
     * 指定したクラスのデフォルトコンストラクタを使ってインスタンスを生成して返す。
     * <p>
     * このメソッドは、コンストラクタの可視性に関係なくインスタンスを生成できる。<br>
     * カプセル化を破壊し、クラス間の静的な依存関係が分からなくなるため、
     * このメソッドの乱用は避けること<br>
     * 極力このメソッドを使用しなくてもテストができるようにクラスを設計し、
     * どうしても可視性を無視してコンストラクタを実行しなけばテストできない場合にのみ使用すること。
     * </p>
     * <p>
     * デフォルトコンストラクタが非チェック例外をスローした場合は、その例外をそのままスローしなおす。
     * また、デフォルトコンストラクタがチェック例外をスローした場合は、その例外を{@link RuntimeException}でラップしてスローしなおす。<br>
     * この振る舞いは、このクラスの元となったJMockitの{@code Deencapsulation}に動きを合わせたためにそうなっている。
     * </p>
     * 
     * @param clazz インスタンスを生成するクラス
     * @return 生成されたインスタンス
     * @param <T> 生成されるインスタンスの型
     * @throws IllegalArgumentException デフォルトコンストラクタが存在しない場合
     */
    public static <T> T newInstance(Class<T> clazz) {
        try {
            final Constructor<T> constructor = clazz.getDeclaredConstructor();
            if (!constructor.canAccess(null)) {
                constructor.setAccessible(true);
            }
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("The default constructor is not found at " + clazz.getName() + ".", e);
        } catch (InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            } else {
                throw new RuntimeException(cause);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    

    // インスタンス化させないためコンストラクタをprivateで宣言
    private ReflectionUtil() {}
}
