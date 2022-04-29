package org.FastData.Spring.Function;

import java.io.Serializable;

@FunctionalInterface
public interface FastExpression<S, T> extends Serializable {
    T convert(S source);
}