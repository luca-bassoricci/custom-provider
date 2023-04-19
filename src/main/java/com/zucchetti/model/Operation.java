package com.zucchetti.model;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.FieldDefaults;

@Value(staticConstructor = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Operation<V>
{
	@NonNull
	String operand;
	@NonNull
	Operator operator;
	V value;
}
