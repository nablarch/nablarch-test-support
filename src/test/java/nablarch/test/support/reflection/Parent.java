package nablarch.test.support.reflection;

public class Parent {
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
