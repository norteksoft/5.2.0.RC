package com.norteksoft.task.webservice;

import java.util.List;

//@WebService(name="TaskWebservice")
public interface TaskWebservice {
	
	String personalTasks(List<String> prmtNames, List<String> prmtValues);
}
