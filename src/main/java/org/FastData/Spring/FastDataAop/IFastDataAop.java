package org.FastData.Spring.FastDataAop;

public interface IFastDataAop {
    void before(BeforeContext context);

    void after(AfterContext context);

    void mapBefore(MapBeforeContext context);

    void mapAfter(MapAfterContext context);

    void exception(ExceptionContext context);
}
