package com.norteksoft.wf.base.enumeration;

public enum ProcessProperties {
	
	 WF_ADMIN("admin"),
     WF_FORM("form-name"),
     WF_FORM_VERSION("form-version"),
     WF_FORM_CODE("form-code"),
     WF_CREATOR("creator"),
     WF_CREATED_TIME("created-time"),
     WF_STATE("state"),
     WF_TYPE("process-type-id"),
     WF_TYPE_CODE("process-type-code"),
     WF_CODE("process-code");
     String tagName;
     ProcessProperties(String tagName){
         this.tagName = tagName;
     }

     @Override
     public String toString() {
         return this.tagName;
     }
}
