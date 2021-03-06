package com.redislabs.lettusearch.index.field;

import static com.redislabs.lettusearch.protocol.CommandKeyword.NOINDEX;
import static com.redislabs.lettusearch.protocol.CommandKeyword.SORTABLE;

import com.redislabs.lettusearch.RediSearchArgument;
import com.redislabs.lettusearch.protocol.RediSearchCommandArgs;

import io.lettuce.core.internal.LettuceAssert;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public abstract @Getter @Setter class Field implements RediSearchArgument {

	static final String MUST_NOT_BE_EMPTY = "must not be empty";
	static final String MUST_NOT_BE_NULL = "must not be null";

	private String name;
	private boolean sortable;
	private boolean noIndex;

	protected Field(String name) {
		this.name = name;
	}

	@Override
	public <K, V> void build(RediSearchCommandArgs<K, V> args) {
		LettuceAssert.notNull(name, "name " + MUST_NOT_BE_NULL);
		LettuceAssert.notEmpty(name, "name " + MUST_NOT_BE_EMPTY);
		args.add(name);
		buildField(args);
		if (sortable) {
			args.add(SORTABLE);
		}
		if (noIndex) {
			args.add(NOINDEX);
		}
	}

	protected abstract <K, V> void buildField(RediSearchCommandArgs<K, V> args);

}