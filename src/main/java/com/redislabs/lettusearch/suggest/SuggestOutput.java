package com.redislabs.lettusearch.suggest;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import io.lettuce.core.LettuceStrings;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.output.CommandOutput;

public class SuggestOutput<K, V> extends CommandOutput<K, V, List<SuggestResult<V>>> {

	private SuggestResult<V> current;
	private SuggestGetOptions options;

	public SuggestOutput(RedisCodec<K, V> codec, SuggestGetOptions options) {
		super(codec, new ArrayList<>());
		this.options = options;
	}

	@Override
	public void set(ByteBuffer bytes) {
		if (current == null) {
			current = new SuggestResult<>();
			if (bytes != null) {
				current.setString(codec.decodeValue(bytes));
			}
			output.add(current);
			if (!options.isWithScores() && !options.isWithPayloads()) {
				current = null;
			}
		} else {
			if (current.getScore() == null && options.isWithScores()) {
				if (bytes != null) {
					current.setScore(LettuceStrings.toDouble(decodeAscii(bytes)));
				}
				if (!options.isWithPayloads()) {
					current = null;
				}
			} else {
				if (current.getPayload() == null && options.isWithPayloads()) {
					if (bytes != null) {
						current.setPayload(codec.decodeValue(bytes));
					}
					current = null;
				}
			}
		}
	}

}