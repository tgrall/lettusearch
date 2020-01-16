package com.redislabs.lettusearch.search.field;

import static com.redislabs.lettusearch.protocol.CommandKeyword.GEO;

import com.redislabs.lettusearch.protocol.RediSearchCommandArgs;

import lombok.Builder;

public class GeoField extends Field {

	@Builder
	private GeoField(String name) {
		super(name);
	}

	@Override
	protected <K, V> void buildField(RediSearchCommandArgs<K, V> args) {
		args.add(GEO);
	}
}
