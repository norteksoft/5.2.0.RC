package com.norteksoft.wf.base.utils;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class Dom4jUtils {

    /**
     * 将file文件解析成Dom4j的文档
     * @param file
     * @return
     */
    public static Document getDocument(String file){
        SAXReader reader = new SAXReader();
        try {
            Document doc = reader.read(new ByteArrayInputStream(file.getBytes("UTF-8")));
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
    
    /**
     * 根据子元素名称获取子元素
     * @param parentElement
     * @param tagName
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Element getSubElementByName(Element parentElement, String tagName){
    	Element subElement = null;
    	if(parentElement != null){
            Iterator<Element> it = parentElement.elementIterator();
            while(it.hasNext()){
                subElement = it.next();
                if(subElement.getName().equals(tagName)){
                    break;
                }else{
                    subElement = null;
                }
            }
    	}
        return subElement;
    }
    
    /**
     * 根据子元素名称获取子元素集合
     * @param tagName
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Element> getSubElementsByName(Element parentElement, String tagName){
        Iterator<Element> it = parentElement.elementIterator();
        List<Element> subElements = new ArrayList<Element>();
        Element subElement = null;
        while(it.hasNext()){
            subElement = it.next();
            if(subElement.getName().equals(tagName)){
                subElements.add(subElement);
            }
        }
        return subElements;
    }
    
    /**
     * 移除root元素中所有的Tag名称为tagName元素
     * @param root
     * @param tagName
     */
    @SuppressWarnings("unchecked")
    public static void removeAllElementsByName(Element root, String tagName){
        Iterator<Element> it = root.elementIterator();
        Element subElement = null;
        while(it.hasNext()){
            subElement = it.next();
            if(subElement.getName().equals(tagName)){
                root.remove(subElement);
            }else if(!subElement.isTextOnly()){
                removeAllElementsByName(subElement, tagName);
            }
        }
    }
    
    /**
     * 从XML文档中根据路径获取元素值(从根路径开始，可以含有元素属性值)
     * @param xmlFile
     * @param path "/process/task[@name='name']"
     * @return
     */
    public static String getSingleElementValueByPath(String processId, String path){
    	String result = null;
    	if(StringUtils.isNotEmpty(WorkflowMemcachedUtil.getDefinitionFile(processId)) && StringUtils.isNotEmpty(path)){
    		path = path.replaceFirst("/", "");
    		String[] tags = path.split("/");
    		Element root = (DocumentParameterUtils.getDocument(processId)).getRootElement();
    		if(tags.length == 1){
    			result = root.getText();
    		}else{
        		Element element = getElementByPathAndProp(root, tags, 1);
        		if(element != null){
        			result = element.getText();
        		}
    		}
    	}
    	return result;
    }
    /**
     * 从XML文档中根据路径获取元素值(从根路径开始，可以含有元素属性值)
     * @param xmlFile
     * @param path "/process/task[@name='name']"
     * @return
     */
    public static String getSingleElementPropByPath(String processId, String path, String propName){
    	String result = null;
    	if(StringUtils.isNotEmpty(WorkflowMemcachedUtil.getDefinitionFile(processId)) && StringUtils.isNotEmpty(path)){
    		path = path.replaceFirst("/", "");
    		String[] tags = path.split("/");
    		Element root = (DocumentParameterUtils.getDocument(processId)).getRootElement();
    		if(tags.length == 1){
    			result = root.attributeValue(propName);
    		}else{
        		Element element = getElementByPathAndProp(root, tags, 1);
        		if(element != null){
        			result = element.attributeValue(propName);
        		}
    		}
    	}
    	return result;
    }
    @SuppressWarnings("unchecked")
	private static Element getElementByPathAndProp(Element root, String[] path, int pathIndex){
    	Element result = null;
    	if(path[pathIndex].contains("[@")){
			int propNameStart = path[pathIndex].indexOf("[@");
    		List<Element> subElements = root.elements(path[pathIndex].substring(0, propNameStart));
    		for(Element e : subElements){
    			int propNameEnd = path[pathIndex].indexOf("=");
    			String propName = path[pathIndex].substring(propNameStart + 2, propNameEnd);
    			int propValueStart = path[pathIndex].indexOf("'");
    			int propValueEnd = path[pathIndex].lastIndexOf("']");
    			String propValue = path[pathIndex].substring(propValueStart + 1, propValueEnd);
    			if(propValue.equals(e.attributeValue(propName))){
    				result = e;
    				break;
    			}
    		}
    	}else{
    		result = root.element(path[pathIndex]);
    	}
    	if(result != null && ++pathIndex < path.length){
    		result = getElementByPathAndProp(result, path, pathIndex);
    	}
    	return result;
    }
    
    /**
     * 根据元素层级结构获取所需要的元素(path不包含root元素)
     * @param root 
     * @param path parent:sub
     * @return
     */
    public static Element getElementByPath(Element root, String path){
        String[] tagNames = path.split(":");
        Element result = getElementByPath(root, tagNames, 0);
        if(result != null && !result.getName().equals(tagNames[tagNames.length -1])){
            result = null;
        }
        return result;
    }
    private static Element getElementByPath(Element root, String[] path, int index){
        Element result = getSubElementByName(root, path[index]);
        if(result != null && ++index < path.length){
            result = getElementByPath(result, path, index);
        }
        return result;
    }
   
}
