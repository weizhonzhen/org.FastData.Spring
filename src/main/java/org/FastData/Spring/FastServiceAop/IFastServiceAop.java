package org.FastData.Spring.FastServiceAop;

public interface IFastServiceAop {

    public void before(BeforeContext before);

    public void after(AfterContext after);

    public void exception(ExceptionContext exception);
}
