package org.FastData.Spring.Aop;

public final class FastDataConfig {
    private static IFastAop aop;

    public static IFastAop getAop() {
        return aop;
    }

    public static void setAop(IFastAop aop) {
        FastDataConfig.aop = aop;
    }
}
