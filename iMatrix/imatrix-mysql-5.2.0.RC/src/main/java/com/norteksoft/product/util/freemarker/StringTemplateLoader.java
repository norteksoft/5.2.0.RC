package com.norteksoft.product.util.freemarker;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import freemarker.cache.TemplateLoader;

public class StringTemplateLoader implements TemplateLoader {
	public void closeTemplateSource(Object templateSource) throws IOException {
		return;
	}

	public Object findTemplateSource(String name) throws IOException {
		return name;
	}

	public long getLastModified(Object templateSource) {
		return System.currentTimeMillis();
	}

	public Reader getReader(Object templateSource, String encoding)
			throws IOException {
		String ftlString = (String) templateSource;
		return new StringReader(ftlString);
	}
}