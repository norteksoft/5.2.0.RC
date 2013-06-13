	<#if taskId==0||workflow.processState.code=='process.unsubmit'>
	<#if taskId==0>
		<button href="#" class='btn' id="${workflowButtonGroupName}_btnStartWorkflow" onclick="${workflowButtonGroupName}.btnStartWorkflow.click(#{taskId})" ></button>
		<button href="#" class='btn' id="${workflowButtonGroupName}_btnSubmitWorkflow"  onclick="${workflowButtonGroupName}.btnSubmitWorkflow.click(#{taskId})" ></button>
		<script type="text/javascript">
				$("#${workflowButtonGroupName}_btnStartWorkflow").html('<span><span>'+${workflowButtonGroupName}.btnStartWorkflow.name+'</span></span>');
				$("#${workflowButtonGroupName}_btnSubmitWorkflow").html('<span><span>'+${workflowButtonGroupName}.btnSubmitWorkflow.name+'</span></span>');
		</script>
	<#else>
		<#if task.active == 0 >
			<button href="#" class='btn' id="${workflowButtonGroupName}_btnStartWorkflow" onclick="${workflowButtonGroupName}.btnStartWorkflow.click(#{taskId})" ></button>
			<button href="#" class='btn' id="${workflowButtonGroupName}_btnSubmitWorkflow"  onclick="${workflowButtonGroupName}.btnSubmitWorkflow.click(#{taskId})" ></button>
			<script type="text/javascript">
					$("#${workflowButtonGroupName}_btnStartWorkflow").html('<span><span>'+${workflowButtonGroupName}.btnStartWorkflow.name+'</span></span>');
					$("#${workflowButtonGroupName}_btnSubmitWorkflow").html('<span><span>'+${workflowButtonGroupName}.btnSubmitWorkflow.name+'</span></span>');
			</script>
		</#if>
		<#if task.active == 1>
			<#--转向指定办理人页面按钮-->
			<button href="#"  id="${workflowButtonGroupName}_btnAssignTransactor"  class="btn" onclick="${workflowButtonGroupName}.btnAssignTransactor.click(#{taskId})"></button>
			<script type="text/javascript">
				$("#${workflowButtonGroupName}_btnAssignTransactor").html('<span><span>'+${workflowButtonGroupName}.btnAssignTransactor.name+'</span></span>');<#--${workflowButtonGroupName}.btnAssignTransactor.name-->
			</script>
		</#if>
		<#if task.active == 6>
			<#--转向选择环节页面按钮-->
			<button href="#" class='btn' id="${workflowButtonGroupName}_btnSaveForm"    onclick="${workflowButtonGroupName}.btnSaveForm.click(#{taskId})" ></button>
			<script type="text/javascript">
				$("#${workflowButtonGroupName}_btnSaveForm").html('<span><span>'+${workflowButtonGroupName}.btnSaveForm.name+'</span></span>');<#--${workflowButtonGroupName}.btnSaveForm.name-->
			</script>
			<button href="#" class='btn' id="${workflowButtonGroupName}_btnChoiceTache"   onclick="${workflowButtonGroupName}.btnChoiceTache.click(#{taskId})"></button>
			<script type="text/javascript">
				$("#${workflowButtonGroupName}_btnChoiceTache").html('<span><span>'+${workflowButtonGroupName}.btnChoiceTache.name+'</span></span>');<#--${workflowButtonGroupName}.btnAssignTransactor.name-->
			</script>
		</#if>
	</#if>
	<#else>
		<#if task.active == 0  && task.processingMode.condition!="阅">
			
			<#--保存表单信息按钮-->
			<button href="#" class='btn' id="${workflowButtonGroupName}_btnSaveForm"    onclick="${workflowButtonGroupName}.btnSaveForm.click(#{taskId})" ></button>
			<script type="text/javascript">
				$("#${workflowButtonGroupName}_btnSaveForm").html('<span><span>'+${workflowButtonGroupName}.btnSaveForm.name+'</span></span>');<#--${workflowButtonGroupName}.btnSaveForm.name-->
			</script>
		</#if>
		<#if task.active == 1>
			<#--转向指定办理人页面按钮-->
			<button href="#"  id="${workflowButtonGroupName}_btnAssignTransactor"  class="btn" onclick="${workflowButtonGroupName}.btnAssignTransactor.click(#{taskId})"></button>
			<script type="text/javascript">
				$("#${workflowButtonGroupName}_btnAssignTransactor").html('<span><span>'+${workflowButtonGroupName}.btnAssignTransactor.name+'</span></span>');<#--${workflowButtonGroupName}.btnAssignTransactor.name-->
			</script>
		</#if>
		<#if task.active == 6>
			<#--转向选择环节页面按钮-->
			<button href="#" class='btn' id="${workflowButtonGroupName}_btnSaveForm"    onclick="${workflowButtonGroupName}.btnSaveForm.click(#{taskId})" ></button>
			<script type="text/javascript">
				$("#${workflowButtonGroupName}_btnSaveForm").html('<span><span>'+${workflowButtonGroupName}.btnSaveForm.name+'</span></span>');<#--${workflowButtonGroupName}.btnSaveForm.name-->
			</script>
			<button href="#" class='btn' id="${workflowButtonGroupName}_btnChoiceTache"   onclick="${workflowButtonGroupName}.btnChoiceTache.click(#{taskId})"></button>
			<script type="text/javascript">
				$("#${workflowButtonGroupName}_btnChoiceTache").html('<span><span>'+${workflowButtonGroupName}.btnChoiceTache.name+'</span></span>');<#--${workflowButtonGroupName}.btnAssignTransactor.name-->
			</script>
		</#if>
		
		<#if task.active==4>
			<#--领取任务-->
			<button href="#" class='btn' id="${workflowButtonGroupName}_btnDrawTask"  class="btnStyle " onclick="${workflowButtonGroupName}.btnDrawTask.click(#{taskId});"></button>
			<script type="text/javascript">
				$("#${workflowButtonGroupName}_btnDrawTask").html('<span><span>'+${workflowButtonGroupName}.btnDrawTask.name+'</span></span>');
			</script>
		</#if>
		
		<#if task['drawTask'] && task.active == 0>
			<button href="#" class='btn' id="${workflowButtonGroupName}_btnAbandonTask"   onclick="${workflowButtonGroupName}.btnAbandonTask.click(#{taskId});" ></button>
			<script type="text/javascript">
				$("#${workflowButtonGroupName}_btnAbandonTask").html('<span><span>'+${workflowButtonGroupName}.btnAbandonTask.name+'</span></span>');
			</script>
		</#if>
		
		<#if task.active == 0 && "阅" == task.processingMode.condition>
			<#--完成阅办任务-->
			<button href="#" class='btn' id="${workflowButtonGroupName}_btnReadTask"  onclick="${workflowButtonGroupName}.btnReadTask.click(#{taskId});" class="btnStyle "></button>
			<script type="text/javascript">
				$("#${workflowButtonGroupName}_btnReadTask").html('<span><span>'+${workflowButtonGroupName}.btnReadTask.name+'</span></span>');
			</script>
		</#if>	
		
		
		<#if task.active == 2 
				&& task.processingMode.condition != "阅" 
				&& task.processingMode.condition !=  "会签式" 
				&& task.processingMode.condition !=  "投票式" 
				&&task.processingMode.condition !=  "分发" >
			<button href="#" class='btn' id="${workflowButtonGroupName}_btnGetBackTask"  onclick="${workflowButtonGroupName}.btnGetBackTask.click(#{taskId})" class="btnStyle "></button>
			<script type="text/javascript">
				$("#${workflowButtonGroupName}_btnGetBackTask").html('<span><span>'+${workflowButtonGroupName}.btnGetBackTask.name+'</span></span>');
			</script>
		</#if>
		<#if task.active == 0 &&task.processingMode.condition == "编辑式">
			<button href="#" class='btn'  id="${workflowButtonGroupName}_btnSubmitTask"   onclick="${workflowButtonGroupName}.btnSubmitTask.click(#{taskId})" ></button>
			<script type="text/javascript">
				$("#${workflowButtonGroupName}_btnSubmitTask").html('<span><span>'+'${task.submitButton}'+'</span></span>');<#--${workflowButtonGroupName}.btnSubmitTask.name-->
			</script>
		</#if>
		<#if task.active == 0 
			 && (task.processingMode.condition == "审批式" 
			 || task.processingMode.condition == "会签式")>
				<button href="#" class='btn' id="${workflowButtonGroupName}_btnApproveTask"   onclick="${workflowButtonGroupName}.btnApproveTask.click(#{taskId})"></button>
				<button href="#" class='btn' id="${workflowButtonGroupName}_btnRefuseTask"   onclick="${workflowButtonGroupName}.btnRefuseTask.click(#{taskId})"></button>
				<script type="text/javascript">
					$("#${workflowButtonGroupName}_btnApproveTask").html('<span><span>'+'${task.agreeButton}'+'</span></span>');<#--${workflowButtonGroupName}.btnApproveTask.name-->
					$("#${workflowButtonGroupName}_btnRefuseTask").html('<span><span>'+'${task.disagreeButton}'+'</span></span>');<#--${workflowButtonGroupName}.btnRefuseTask.name-->
				</script>
			<#if task.processingMode.condition == '会签式'>
				<button href="#" class='btn' id="${workflowButtonGroupName}_btnAddCountersign"  onclick='${workflowButtonGroupName}.btnAddCountersign.click(#{taskId})' class="btnStyle "></button>
				<button href="#" class='btn' id="${workflowButtonGroupName}_btnDeleteCountersign"  onclick='${workflowButtonGroupName}.btnDeleteCountersign.click(#{taskId})' class="btnStyle "></button>
				<script type="text/javascript">
					$("#${workflowButtonGroupName}_btnDeleteCountersign").html('<span><span>'+'${task.removeSignerButton}'+'</span></span>');<#--${workflowButtonGroupName}.btnDeleteCountersign.name-->
					$("#${workflowButtonGroupName}_btnAddCountersign").html('<span><span>'+'${task.addSignerButton}'+'</span></span>');<#--${workflowButtonGroupName}.btnAddCountersign.name-->
				</script>
			</#if>
		</#if>
		<#if task.active == 0 &&task.processingMode.condition == '签收式'>
			<button href="#" class='btn' id="${workflowButtonGroupName}_btnSignoffTask"   onclick="${workflowButtonGroupName}.btnSignoffTask.click(#{taskId})"></button>
			<script type="text/javascript">
				$("#${workflowButtonGroupName}_btnSignoffTask").html('<span><span>'+'${task.signForButton}'+'</span></span>');<#--${workflowButtonGroupName}.btnSignoffTask.name-->
			</script>
		</#if>
		<#if task.active == 0 &&task.processingMode.condition == '投票式'>
			<button href="#" class='btn' id="${workflowButtonGroupName}_btnVoteAgreement"   onclick="${workflowButtonGroupName}.btnVoteAgreement.click(#{taskId})"></button>
			<button href="#" class='btn' id="${workflowButtonGroupName}_btnVoteOppose"   onclick="${workflowButtonGroupName}.btnVoteOppose.click(#{taskId})"></button>
			<button href="#" class='btn' id="${workflowButtonGroupName}_btnVoteKiken"   onclick="${workflowButtonGroupName}.btnVoteKiken.click(#{taskId})"></button>
			<script type="text/javascript">
				$("#${workflowButtonGroupName}_btnVoteAgreement").html('<span><span>'+'${task.approveButton}'+'</span></span>');<#--${workflowButtonGroupName}.btnVoteAgreement.name-->
				$("#${workflowButtonGroupName}_btnVoteOppose").html('<span><span>'+'${task.opposeButton}'+'</span></span>');<#--${workflowButtonGroupName}.btnVoteOppose.name-->
				$("#${workflowButtonGroupName}_btnVoteKiken").html('<span><span>'+'${task.abstainButton}'+'</span></span>');<#--${workflowButtonGroupName}.btnVoteKiken.name-->
			</script>
		</#if>
		<#if task.active == 0 &&task.processingMode.condition == "交办式">
			<button href="#" class='btn' id="${workflowButtonGroupName}_btnAssignTask"  onclick="${workflowButtonGroupName}.btnAssignTask.click(#{taskId});" class="btnStyle "></button>
			<script type="text/javascript">
				$("#${workflowButtonGroupName}_btnAssignTask").html('<span><span>'+'${task.assignButton}'+'</span></span>');<#--${workflowButtonGroupName}.btnAssignTask.name-->
			</script>
		</#if>
		<#if task.active == 0 &&task.processingMode.condition == "分发">
			<button href="#" class='btn' id="${workflowButtonGroupName}_btnDistributeTask"   onclick="${workflowButtonGroupName}.btnDistributeTask.click(#{taskId});" ></button>
			<script type="text/javascript">
				$("#${workflowButtonGroupName}_btnDistributeTask").html('<span><span>'+'${task.submitButton}'+'</span></span>');<#--${workflowButtonGroupName}.btnDistributeTask.name-->
			</script>
		</#if>
		<#if showOtherButton>
			<#if task.active == 0 || task.active == 1||task.active == 6>
				<button href="#" class='btn' id="${workflowButtonGroupName}_btnCopyTache"   onclick="${workflowButtonGroupName}.btnCopyTache.click(#{taskId});" ></button>
				<script type="text/javascript">
					$("#${workflowButtonGroupName}_btnCopyTache").html('<span><span>'+${workflowButtonGroupName}.btnCopyTache.name+'</span></span>');
				</script>
			</#if>
			<#if (task.active == 0||task.active == 4)&& task.processingMode.condition !="阅">
				<button href="#" class='btn' id="${workflowButtonGroupName}_btnAssign"   onclick="${workflowButtonGroupName}.btnAssign.click(#{taskId});" ></button>
				<script type="text/javascript">
					$("#${workflowButtonGroupName}_btnAssign").html('<span><span>'+${workflowButtonGroupName}.btnAssign.name+'</span></span>');
				</script>
			</#if>
		</#if>
	</#if>
