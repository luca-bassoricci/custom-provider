package com.zucchetti.provider;

import java.util.Arrays;
import java.util.Optional;

import javax.ws.rs.ext.ParamConverter;

import org.apache.commons.lang3.Validate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public class ConverterEnum<E extends Enum<E>> implements ParamConverter<E>
{
	public static <T extends Enum<T>> ConverterEnum<T> create(@NonNull Class<T> enumClass)
	{
		return new ConverterEnum<>(enumClass);
	}

	@NonNull
	Class<E> enumClass;
	@Getter(value = AccessLevel.PROTECTED, lazy = true)
	private final E[] enumConstants = _getEnumConstants();

	private E[] _getEnumConstants()
	{
		return this.enumClass.getEnumConstants();
	}

	@Override
	public E fromString(String value)
	{
		Validate.isTrue(value != null);
		return this.findByName(value).or(() -> this.findByToString(value))
				.orElseThrow(() -> new IllegalArgumentException(value));
	}

	@Override
	public String toString(E value)
	{
		Validate.isTrue(value != null);
		return value.name();
	}

	protected final Optional<E> findByName(String value)
	{
		return Arrays.stream(this.getEnumConstants()).filter(e -> e.name().equalsIgnoreCase(value)).findFirst();
	}

	protected final Optional<E> findByToString(String value)
	{
		return Arrays.stream(this.getEnumConstants()).filter(e -> e.toString().equalsIgnoreCase(value)).findFirst();
	}
}
