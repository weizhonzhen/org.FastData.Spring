package org.FastData.Spring.Aop;

public interface IFastAop {
    void before(BeforeContext context);

    void after(AfterContext context);

    void map(MapContext context);

    void exception(Exception ex,String name);
}
