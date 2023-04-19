package com.zucchetti.provider;

import javax.ws.rs.ext.ParamConverter;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.math.NumberUtils;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public class ConverterNumber implements ParamConverter<Number>
{
	public static ConverterNumber create(@NonNull Class<? extends Number> numberClass)
	{
		return new ConverterNumber(numberClass);
	}

	private final Class<? extends Number> numberClass;

	@Override
	public Number fromString(String value)
	{
		Validate.isTrue(value != null);
		return numberClass.cast(NumberUtils.createNumber(value));
	}

	@Override
	public String toString(Number value)
	{
		Validate.isTrue(value != null);
		return value.toString();
	}
}
