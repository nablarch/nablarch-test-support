package nablarch.test.support.reflection;

public class Parent {
    public static String PUBLIC_FIELD = "Parent#PUBLIC_FIELD";
    protected static String PROTECTED_FIELD = "Parent#PROTECTED_FIELD";
    static String PACKAGE_PRIVATE_FIELD = "Parent#PACKAGE_PRIVATE_FIELD";
    private static String PRIVATE_FIELD = "Parent#PRIVATE_FIELD";
    private static String PARENT_ONLY_FIELD = "Parent#PARENT_ONLY_FIELD";
    
    public static void init() {
        PUBLIC_FIELD = "Parent#PUBLIC_FIELD";
        PROTECTED_FIELD = "Parent#PROTECTED_FIELD";
        PACKAGE_PRIVATE_FIELD = "Parent#PACKAGE_PRIVATE_FIELD";
        PRIVATE_FIELD = "Parent#PRIVATE_FIELD";
        PARENT_ONLY_FIELD = "Parent#PARENT_ONLY_FIELD";
    }
    
    public String publicField = "Parent#publicField";
    protected String protectedField = "Parent#protectedField";
    String packagePrivateField = "Parent#packagePrivateField";
    private String privateField = "Parent#privateField";
    private String parentOnlyField = "Parent#parentOnlyField";
    
    public String publicMethod () { return "Parent#publicMethod"; }
    protected String protectedMethod () { return "Parent#protectedMethod"; }
    String packagePrivateMethod () { return "Parent#packagePrivateMethod"; }
    private String privateMethod () { return "Parent#privateMethod"; }
    private String parentOnlyMethod () { return "Parent#parentOnlyMethod"; }

    private void voidMethod () {}
    
    private String argsMethod(int i, String s) { return "(" + i + ", " + s + ")"; }
    
    private String overload() { return "no args"; }
    private String overload(int i) { return "i=" + i; }
    private String overload(String s) { return "s=" + s; }
    
    private void throwException() { throw new UnsupportedOperationException("test"); }
}
