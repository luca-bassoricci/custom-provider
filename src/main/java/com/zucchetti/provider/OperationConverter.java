package com.zucchetti.provider;

import javax.ws.rs.ext.ParamConverter;

import com.zucchetti.model.Operation;
import com.zucchetti.model.Operator;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OperationConverter<T> implements ParamConverter<Operation<T>>
{
	private final ParamConverter<T> paramConverter;
	private final String operand;

	@Override
	public Operation<T> fromString(String value)
	{
		if (value == null)
		{
			throw new IllegalArgumentException();
		}
		int indexOf = value.indexOf('_');
		return Operation.of(operand, Operator.of(value.substring(0, indexOf)),
				paramConverter.fromString(value.substring(indexOf + 1)));
	}

	@Override
	public String toString(Operation<T> value)
	{
		if (value == null)
		{
			throw new IllegalArgumentException();
		}
		return value.getOperator().getOp() + '_' + paramConverter.toString(value.getValue());
	}
}
