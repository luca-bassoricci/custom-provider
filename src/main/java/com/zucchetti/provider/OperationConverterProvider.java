package com.zucchetti.provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

import com.zucchetti.OperationParam;
import com.zucchetti.model.Operation;

@Provider
public class OperationConverterProvider implements ParamConverterProvider
{
	@SuppressWarnings("unchecked")
	@Override
	public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations)
	{
		if (rawType.isAssignableFrom(Operation.class))
		{
			final var annot = operationParamAnnot(annotations);
			// Extract name from my custom annotation or throw IAE
			final String name = annot.map(OperationParam::value)
					.orElseThrow(() -> new IllegalArgumentException("Unable to detect operation name"));
			// Extract bounded type from custom annotation or from parameter passed by RR
			// framework
			Class rt = annot.map(OperationParam::boundedType).filter(Predicate.not(Void.class::equals))
					.orElseGet(() -> (Class) ((ParameterizedType) genericType).getActualTypeArguments()[0]);
			ParamConverter<T> pc;
			if (Number.class.isAssignableFrom(rt))
			{
				pc = (ParamConverter<T>) ConverterNumber.create(rt);
			}
			else if (rt.isEnum())
			{
				pc = ConverterEnum.create(rt);
			}
			else
			{
				throw new IllegalStateException("Unable to found converter for " + rt);
			}
			return (ParamConverter<T>) new OperationConverter<>(pc, name);
		}
		return null;
	}

	private Optional<OperationParam> operationParamAnnot(Annotation[] annotations)
	{
		return Arrays.stream(annotations).filter(OperationParam.class::isInstance).findFirst()
				.map(OperationParam.class::cast);
	}
}
