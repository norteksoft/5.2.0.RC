package com.norteksoft.wf.base.utils;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;


public class DocumentThreadParameters{
		private Map<String,Document> documents = new HashMap<String, Document>();
		
		public DocumentThreadParameters(Map<String, Document> documents) {
			super();
			this.documents = documents;
		}
		
		public DocumentThreadParameters() {
			super();
		}
		
		public Map<String, Document> getDocuments() {
			return documents;
		}

		public void setDocuments(Map<String, Document> documents) {
			this.documents = documents;
		}

	}