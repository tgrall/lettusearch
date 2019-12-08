package com.redislabs.lettusearch;

import static com.redislabs.lettusearch.Beers.FIELD_ABV;
import static com.redislabs.lettusearch.Beers.FIELD_NAME;
import static com.redislabs.lettusearch.Beers.FIELD_STYLE;
import static com.redislabs.lettusearch.Beers.INDEX;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.redislabs.lettusearch.search.HighlightOptions;
import com.redislabs.lettusearch.search.HighlightOptions.TagOptions;
import com.redislabs.lettusearch.search.Limit;
import com.redislabs.lettusearch.search.SearchOptions;
import com.redislabs.lettusearch.search.SearchResult;
import com.redislabs.lettusearch.search.SearchResults;

public class TestSearch extends AbstractBaseTest {

	@Test
	public void phoneticFields() {
		SearchResults<String, String> results = commands.search(INDEX, "eldur");
		Assert.assertEquals(7, results.getCount());
	}

	@Test
	public void searchNoContent() {
		SearchResults<String, String> results = commands.search(INDEX, "Hefeweizen",
				new SearchOptions().withScores(true).noContent(true).limit(new Limit().num(100)));
		Assert.assertEquals(22, results.getCount());
		Assert.assertEquals(22, results.size());
		Assert.assertEquals("1836", results.get(0).getDocumentId());
		Assert.assertEquals(1.2, results.get(0).getScore(), 0.000001);
	}

	@Test
	public void get() {
		Map<String, String> map = commands.get(INDEX, "1836");
		Assert.assertEquals("Widmer Brothers Hefeweizen", map.get(FIELD_NAME));
	}

	@Test
	public void mget() {
		List<Map<String, String>> mapList = commands.ftMget(INDEX, "1836", "1837", "292929292");
		Assert.assertEquals(3, mapList.size());
		Assert.assertEquals("Widmer Brothers Hefeweizen", mapList.get(0).get(FIELD_NAME));
		Assert.assertEquals("Hefe Black", mapList.get(1).get(FIELD_NAME));
	}

	@Test
	public void del() {
		boolean deleted = commands.del(INDEX, "1836", true);
		Assert.assertTrue(deleted);
		Map<String, String> map = commands.get(INDEX, "1836");
		Assert.assertNull(map);
	}

	@Test
	public void searchReturn() {
		SearchResults<String, String> results = commands.search(INDEX, "pale",
				new SearchOptions().returnField(FIELD_NAME).returnField(FIELD_STYLE));
		Assert.assertEquals(256, results.getCount());
		SearchResult<String, String> result1 = results.get(0);
		Assert.assertNotNull(result1.get(FIELD_NAME));
		Assert.assertNotNull(result1.get(FIELD_STYLE));
		Assert.assertNull(result1.get(FIELD_ABV));
	}

	@Test
	public void searchInvalidReturn() {
		SearchResults<String, String> results = commands.search(INDEX, "pale",
				new SearchOptions().returnField(FIELD_NAME).returnField(FIELD_STYLE).returnField(""));
		Assert.assertEquals(256, results.getCount());
		SearchResult<String, String> result1 = results.get(0);
		Assert.assertNotNull(result1.get(FIELD_NAME));
		Assert.assertNotNull(result1.get(FIELD_STYLE));
		Assert.assertNull(result1.get(FIELD_ABV));
	}

	@Test
	public void searchHighlight() {
		String term = "pale";
		String query = "@style:" + term;
		TagOptions tagOptions = new TagOptions().open("<b>").close("</b>");
		SearchResults<String, String> results = commands.search(INDEX, query,
				new SearchOptions().highlight(new HighlightOptions()));
		for (SearchResult<String, String> result : results) {
			Assert.assertTrue(highlighted(result, FIELD_STYLE, tagOptions, term));
		}
		results = commands.search(INDEX, query,
				new SearchOptions().highlight(new HighlightOptions().field(FIELD_NAME)));
		for (SearchResult<String, String> result : results) {
			Assert.assertFalse(highlighted(result, FIELD_STYLE, tagOptions, term));
		}
		tagOptions = new TagOptions().open("[start]").close("[end]");
		results = commands.search(INDEX, query,
				new SearchOptions().highlight(new HighlightOptions().field(FIELD_STYLE).tags(tagOptions)));
		for (SearchResult<String, String> result : results) {
			Assert.assertTrue(highlighted(result, FIELD_STYLE, tagOptions, term));
		}
	}

	private boolean highlighted(SearchResult<String, String> result, String fieldName, TagOptions tags, String string) {
		String fieldValue = result.get(fieldName).toLowerCase();
		return fieldValue.contains(tags.open() + string + tags.close());
	}

}
