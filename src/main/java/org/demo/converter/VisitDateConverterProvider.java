package org.demo.converter;

import org.demo.model.VisitDate;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class VisitDateConverterProvider implements ParamConverterProvider {
    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (rawType.getName().equals(VisitDate.class.getName())) {
            return new ParamConverter<T>() {
                @Override
                public T fromString(String value) {
                    return null;
                }

                @Override
                public String toString(T value) {
                    return null;
                }
            };
        }
        return null;
    }
}
