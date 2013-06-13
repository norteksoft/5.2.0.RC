package ${packageName};
<#list importList?if_exists as importName>
${importName}
</#list>
import com.norteksoft.product.orm.IdEntity;
import javax.persistence.Entity;
import javax.persistence.Table;
@Entity
@Table(name="${tableName}")
public class ${entityName}  extends IdEntity <#if processFlag?if_exists=="true">implements FormFlowable </#if>{
	private static final long serialVersionUID = 1L;
<#list attrList?if_exists as attrName>
	<#list attrName?split('_') as value>
		<#if value_index==0>
			<#assign dataType="${value}">
		<#elseif value_index==1>
			<#assign name="${value}">
		<#elseif value_index==2>
			<#assign persistedFlag="${value}">
		</#if>
	</#list>
	<#if persistedFlag?if_exists=="true">
	@Transient
	</#if>
	<#if dataType?if_exists=='CLOB'>
	@Lob
    @Column(columnDefinition="${clob}", nullable=true)
    private String ${name};
	<#elseif dataType?if_exists=='BLOB'>
	@Lob 
	@Basic(fetch = FetchType.LAZY) 
	@Column(name = "FILEBODY", columnDefinition = "${blob}",nullable=true)
	private byte[] ${name}; 
	<#else>
	private ${dataType} ${name};
	</#if>
</#list>
<#if processFlag?if_exists=="true">
	@Embedded
	private WorkflowInfo workflowInfo;

	@Embedded
	private ExtendField extendField;
</#if>
	
<#list methodList?if_exists as method>
		<#list method?split('_') as value>
			<#if value_index==0>
				<#assign dataType="${value}">
			<#elseif value_index==1>
				<#assign methodName="${value}">
			<#elseif value_index==2>
				<#assign attrName="${value}">
			</#if>
		</#list>
	public ${dataType } get${methodName }() {
		return ${attrName };
	}
	public void set${methodName }(${dataType } ${attrName }) {
		this.${attrName } = ${attrName };
	}
</#list>
<#if processFlag?if_exists=="true">
	public WorkflowInfo getWorkflowInfo() {
		return workflowInfo;
	}

	public void setWorkflowInfo(WorkflowInfo workflowInfo) {
		this.workflowInfo = workflowInfo;
	}

	public ExtendField getExtendField() {
		return extendField;
	}

	public void setExtendField(ExtendField extendField) {
		this.extendField = extendField;
	}
</#if>
}
