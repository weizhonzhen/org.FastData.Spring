package org.FastData.Spring.Aop;

public interface IFastAop {
    void before(BeforeContext context);

    void after(AfterContext context);

    void mapBefore(MapBeforeContext context);

    void mapAfter(MapAfterContext context);

    void exception(ExceptionContext context);
}
