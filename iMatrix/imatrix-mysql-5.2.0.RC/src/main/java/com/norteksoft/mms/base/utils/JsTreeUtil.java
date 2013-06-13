package com.norteksoft.mms.base.utils;

/**
 *  已经被 com.norteksoft.product.util.JsTreeUtils 替代，请更换
 */
@Deprecated
public class JsTreeUtil {
	public static String treeAttrBefore="\"id\" : \"";
	public static String treeAttrMiddle="\" ,\"rel\":\"";
	public static String treeAttrAfter="\"";
	/**
	 * 生成JSON格式的树节点
	 * @param id      id
	 * @param state   open or closed
	 * @param data    显示数据
	 * @return
	 */
	public static String generateJsTreeNode(String id, String state, String data){
		StringBuilder node = new StringBuilder();
		node.append("{ attributes: { id : \"").append(id).append("\" }");
		if(state != null && !"".equals(state.trim())){
			node.append(",state : \"").append(state).append("\"");
		}
		node.append(", data: \"").append(data).append("\" }");
		return node.toString();
	}
	
	/** 
	 * 生成JSON格式的树节点(带子节点)
	 * @param id      id
	 * @param state   open or closed
	 * @param data    显示数据
	 * @param children 子节点
	 * @return
	 */
	public static String generateJsTreeNode(String id, String state, String data, String children){
		StringBuilder node = new StringBuilder();
		node.append("{ attributes: { id : \"").append(id).append("\" }");
		if(state != null && !"".equals(state.trim())){
			node.append(",state : \"").append(state).append("\"");
		}
		node.append(", data: \"").append(data).append("\" ");
		if(children != null && !"".equals(children.trim())){
			node.append(", children : [").append(children).append("]");
		}
		node.append("}");
		return node.toString();
	}
	/**
	 * jstree 1.0
	 * 生成JSON格式的树节点
	 * @param id      id
	 * @param state   open or closed
	 * @param data    显示数据
	 * @return
	 */
	public static String generateJsTreeNodeDefault(String id, String state, String data){
		StringBuilder node = new StringBuilder();
		node.append("{ \"attr\": { \"id\" : \"").append(id).append("\" }");
		if(state != null && !"".equals(state.trim())){
			node.append(",\"state\" : \"").append(state).append("\"");
		}
		node.append(", \"data\": \"").append(data).append("\" }");
		return node.toString();
	}
	
	/**
	 * jstree 1.0 
	 * 生成JSON格式的树节点(带子节点)
	 * @param id      id
	 * @param state   open or closed
	 * @param data    显示数据
	 * @param children 子节点
	 * @return
	 */
	public static String generateJsTreeNodeDefault(String id, String state, String data, String children){
		StringBuilder node = new StringBuilder();
		node.append("{ \"attr\": { \"id\" : \"").append(id).append("\" }");
		if(state != null && !"".equals(state.trim())){
			node.append(",\"state\" : \"").append(state).append("\"");
		}
		node.append(", \"data\": \"").append(data).append("\" ");
		if(children != null && !"".equals(children.trim())){
			node.append(", \"children\" : [").append(children).append("]");
		}
		node.append("}");
		return node.toString();
	}
	/**
	 * jstree 1.0
	 * 生成JSON格式的树节点
	 * @param id      id
	 * @param state   open or closed
	 * @param data    显示数据
	 * @return
	 */
	public static String generateJsTreeNodeNew(String id, String state, String data,String rel){
		StringBuilder node = new StringBuilder();
		node.append("{ \"attr\": { ").append(treeAttrBefore).append(id).append(treeAttrMiddle).append(rel).append(treeAttrAfter).append("}");
		if(state != null && !"".equals(state.trim())){
			node.append(",\"state\" : \"").append(state).append("\"");
		}
		node.append(", \"data\": \"").append(data).append("\" }");
		return node.toString();
	}
	
	/**
	 * 生成JSON格式的树节点(带子节点)
	 * @param id      id
	 * @param state   open or closed
	 * @param data    显示数据
	 * @param children 子节点
	 * @return
	 */
	public static String generateJsTreeNodeNew(String id, String state, String data, String children,String rel){
		StringBuilder node = new StringBuilder();
		node.append("{\"attr\": { ").append(treeAttrBefore).append(id).append(treeAttrMiddle).append(rel).append(treeAttrAfter).append("}");
		if(state != null && !"".equals(state.trim())){
			node.append(",\"state\" : \"").append(state).append("\"");
		}
		node.append(", \"data\": \"").append(data).append("\" ");
		if(children != null && !"".equals(children.trim())){
			node.append(", \"children\" : [").append(children).append("]");
		}
		node.append("}");
		return node.toString();
	}
	
	/**
	 * 替换掉最后一个逗号
	 * @param nodes
	 */
	public static void removeLastComma(StringBuilder nodes){
		if(nodes.lastIndexOf(",") != -1 && nodes.lastIndexOf(",") == nodes.length()-1){
			nodes.replace(nodes.length()-1, nodes.length(), "");
		}
	}
}
