package com.norteksoft.acs.base.utils.log;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class AnalyzeAnnotation {
	
	private static AnalyzeAnnotation analay;
	
	private static final String LOGGERANNOTATION = "com.norteksoft.acs.base.utils.log.Logger";
	
	private static final String METHODSTARTSWITH = "get" ;
	
	private static final String KEY="key";
	
	private  Map logValue;
	
	
	private AnalyzeAnnotation(){
		init();
	}
	
	private void init(){
		logValue = new LinkedHashMap();
		
	}
	
	public static synchronized  AnalyzeAnnotation getInstance(){
		if(analay==null){
			analay = new AnalyzeAnnotation();
		}
		return analay;
	}
	
	
	
	public  Map analyzeAnnotationObject(Object entity) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		
		if(entity!=null){
			   boolean istrue = false;	
				Method[] methods = entity.getClass().getMethods();
				Annotation[] annotation;
				Method mm ;
				for (Method method : methods) {
					if(method.getName().startsWith(METHODSTARTSWITH)){
						
						annotation = method.getDeclaredAnnotations();
						for (Annotation annota : annotation) {
							if(annota.annotationType().getName().equals(LOGGERANNOTATION)){
								 mm = annota.annotationType().getMethod(KEY);
								 istrue = mm.invoke(annota).toString().equals("");
								 
								 if(istrue){
									 analyzeAnnotationObject(method.invoke(entity));
								 }else{
									 logValue.put(mm.invoke(annota), method.invoke(entity));
								 }
								
								
							}
						}
					}
				}
			}
		
  		
		return logValue;
	}
	
	
}
