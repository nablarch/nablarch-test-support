package nablarch.test.support.reflection;

public class Sub extends Parent {
    public String publicField = "Sub#publicField";
    protected String protectedField = "Sub#protectedField";
    String packagePrivateField = "Sub#packagePrivateField";
    private String privateField = "Sub#privateField";

    public String publicMethod () { return "Sub#publicMethod"; }
    protected String protectedMethod () { return "Sub#protectedMethod"; }
    String packagePrivateMethod () { return "Sub#packagePrivateMethod"; }
    private String privateMethod () { return "Sub#privateMethod"; }
}
