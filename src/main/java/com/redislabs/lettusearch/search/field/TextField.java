package com.redislabs.lettusearch.search.field;

import static com.redislabs.lettusearch.protocol.CommandKeyword.NOSTEM;
import static com.redislabs.lettusearch.protocol.CommandKeyword.PHONETIC;
import static com.redislabs.lettusearch.protocol.CommandKeyword.TEXT;
import static com.redislabs.lettusearch.protocol.CommandKeyword.WEIGHT;

import com.redislabs.lettusearch.protocol.RediSearchCommandArgs;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Accessors(fluent = true)
public @Getter @Setter class TextField extends Field {

	private Double weight;
	private boolean noStem;
	private PhoneticMatcher matcher;

	@Builder
	private TextField(String name, double weight, boolean noStem, PhoneticMatcher matcher) {
		super(name);
		this.weight = weight;
		this.noStem = noStem;
		this.matcher = matcher;
	}

	@Override
	protected <K, V> void buildField(RediSearchCommandArgs<K, V> args) {
		args.add(TEXT);
		if (noStem) {
			args.add(NOSTEM);
		}
		if (weight != null) {
			args.add(WEIGHT);
			args.add(weight);
		}
		if (matcher != null) {
			args.add(PHONETIC);
			args.add(matcher.getCode());
		}
	}
}
