package com.redislabs.lettusearch.search.field;

import static com.redislabs.lettusearch.CommandKeyword.NOSTEM;
import static com.redislabs.lettusearch.CommandKeyword.PHONETIC;
import static com.redislabs.lettusearch.CommandKeyword.TEXT;
import static com.redislabs.lettusearch.CommandKeyword.WEIGHT;

import io.lettuce.core.protocol.CommandArgs;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TextField extends Field {

	private final Double weight;
	private final boolean noStem;
	private final Matcher matcher;

	@Builder
	public TextField(String name, boolean sortable, boolean noIndex, Double weight, boolean noStem, Matcher matcher) {
		super(name, sortable, noIndex);
		this.weight = weight;
		this.noStem = noStem;
		this.matcher = matcher;
	}

	public static enum Matcher {

		English("dm:en"), French("dm:fr"), Portuguese("dm:pt"), Spanish("dm:es");

		private String code;

		Matcher(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}
	}

	@Override
	protected <K, V> void buildField(CommandArgs<K, V> args) {
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
