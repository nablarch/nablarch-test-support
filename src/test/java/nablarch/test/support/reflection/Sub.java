package nablarch.test.support.reflection;

public class Sub extends Parent {
    public static String PUBLIC_FIELD = "Sub#PUBLIC_FIELD";
    protected static String PROTECTED_FIELD = "Sub#PROTECTED_FIELD";
    static String PACKAGE_PRIVATE_FIELD = "Sub#PACKAGE_PRIVATE_FIELD";
    private static String PRIVATE_FIELD = "Sub#PRIVATE_FIELD";
    
    public static void init() {
        Parent.init();
        PUBLIC_FIELD = "Sub#PUBLIC_FIELD";
        PROTECTED_FIELD = "Sub#PROTECTED_FIELD";
        PACKAGE_PRIVATE_FIELD = "Sub#PACKAGE_PRIVATE_FIELD";
        PRIVATE_FIELD = "Sub#PRIVATE_FIELD";
    }
    
    public String publicField = "Sub#publicField";
    protected String protectedField = "Sub#protectedField";
    String packagePrivateField = "Sub#packagePrivateField";
    private String privateField = "Sub#privateField";

    public String publicMethod () { return "Sub#publicMethod"; }
    protected String protectedMethod () { return "Sub#protectedMethod"; }
    String packagePrivateMethod () { return "Sub#packagePrivateMethod"; }
    private String privateMethod () { return "Sub#privateMethod"; }
}
