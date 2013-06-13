package com.norteksoft.wf.base.utils;

import java.util.Map;

import org.dom4j.Document;


public class DocumentParameterUtils {
	private static ThreadLocal<DocumentThreadParameters> threadParameters=new ThreadLocal<DocumentThreadParameters>();
	
	public static void setParameters(DocumentThreadParameters parameters){
		threadParameters.set(parameters);
	}
	
	public static Document getDocument(String processId){
		DocumentThreadParameters parameter=threadParameters.get();
		if(parameter==null){
			return Dom4jUtils.getDocument(WorkflowMemcachedUtil.getDefinitionFile(processId));
		}
		Map<String,Document> documents=parameter.getDocuments();
		if(documents==null)return null;
		Document document= documents.get(processId);
		if(document==null){
			document=Dom4jUtils.getDocument(WorkflowMemcachedUtil.getDefinitionFile(processId));
			documents.put(processId, document);
		}
		return document;
	}
	
	public static void clearParameter(){
		DocumentThreadParameters parameter=threadParameters.get();
		if(parameter!=null){
			Map<String,Document> documents=parameter.getDocuments();
			documents.clear();
		}
	}
	
}
