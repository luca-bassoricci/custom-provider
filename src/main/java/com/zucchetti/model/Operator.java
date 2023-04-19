package com.zucchetti.model;

import lombok.NonNull;
import lombok.Value;

@Value(staticConstructor = "of")
public class Operator
{
	@NonNull
	String op;
}
