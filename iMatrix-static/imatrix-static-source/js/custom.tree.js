var _obj,_node_id,_node_title,_operate,_url_prn="",_onsuccess=function(){},_crn_obj;
/**
 * 页面中只有一颗树
 * @param obj:{url(必填):'',inputObj:'',multiple(默认false):,height:,width:,title:'',nodeInfo:'',postData:{name:vlaue,name:value,...},onsuccess:function(){},tree:{}}
 * @return
 * 用法:
 * 		1 sales系统中给所在系统加资源,编号:IS_AUTHENTICATED_ANONYMOUSLY,名称:自定义标签树,路径:/custom-tree.action
 * 		2 action中树节点id的拼接规则:以"-"隔开多个值,如：值1-值2-值3-值4...
 *      3 js调用规则:
 *      	参数说明:1)必填的参数url
 *      			 2)inputObj: inputObj的值为保存title的input的id,该参数和onsuccess两者中必须填一个,且若两者都填了，则以onsuccess为主
 *      			 3)onsuccess:为确定时关闭该窗口时回调的方法,该参数和inputObj两者中必须填一个,且若两者都填了，则以onsuccess为主
 *      			 4)multiple:是否是多选树，默认为false，单选树
 *      			 5)nodeInfo:['','',....],数组值为树节点id各部分代表的意义,如树节点id为:值1-值2-值3-值4,则nodeInfo的值应为['a','b','c','d']
 *      				调用方法getSelectValue('a')得到"值1",getSelectValue('b')得到"值2",getSelectValue('c')得到"值3",getSelectValue('d')得到"值4"等，当然a b c d可以为任意字符串
 *      			 6)postData:拼树的action中需要的参数,其格式为{name:value,.....}
 *      			 7)width:窗口宽度,默认300
 *     				 8)height:窗口高度,默认400
 *      			 9)title:窗口的标题,
 *      			10)tree:如果页面为带有页签的树，其值为object对象{'标签树名称':{treeobj},'标签树名称':{treeobj}},treeobj的配置参照以上9条规则
 *      
 *      	页签树配置说明:1)页签树中treeobj没有配inputObj和onsuccess,则obj必须配置其中一项
 *      				  2)页签树中treeobj有配inputObj或onsuccess,则以treeobj的配置为准，除了obj配置onsuccess,而treeobj配置inputObj,则以obj配置的onsuccess方法为准
 *      				  3)postData:以页签树treeobj中配的为准，如果页签树没配则以obj配的为准
 *      
 *      	API方法：1)getSelectValue(nodeInfo中单个值),其用法参照'参数说明/nodeInfo';
 *     	 			 2)getSelectNodeId() ，获得选中节点的id,选中单个节点返回id字符串，选中多个返回id数组
 *      			 3)getSelectNodeTitle(),获得选中节点的title,选中单个节点返回title字符串，选中多个返回title数组
 *      			 4)getSelectValueByIndex(index),获得节点的值,如节点id的值为：值1-值2-值3-值4,则getSelectValueByIndex(1)的值为"值1",getSelectValueByIndex(2)的值为"值2",依次类推
 * 用法例如：1 单颗树:custom_tree({
							url:webRoot+'/vehicle/vehicle-choose-all-unused-car-tree.htm',
							onsuccess:function(){closeFun();},
							inputObj:'sendCarName',
							width:500,
							height:600,
							title:'选择车辆',
							postData:{startDate:startDate,endDate:endDate},
							nodeInfo:['type','id'],
							multiple:true
						});
			2 页签树:custom_tree({tree:{'标签1':{url:webRoot+'/vehicle/vehicle-choose-all-unused-car-tree.htm',
												  onsuccess:function(){closeFun1();},
												  nodeInfo:['type','id'],
										  		  postData:{startDate:startDate,endDate:endDate}},
										'标签2':{url:webRoot+'/vehicle/vehicle-choose-all-unused-car-tree.htm?startDate='+startDate+'&endDate='+endDate,
												 multiple:true}
									   },
							onsuccess:function(){closeFun();},
							inputObj:'sendCarName',
							postData:{startDate:startDate,endDate:endDate},
							width:500,
							height:600,
							title:'选择车辆'
						});
 */
function custom_tree(obj){
	var rootUrl=webRoot;
	_obj=obj;
	if(typeof(obj.url)!="undefined"){
		obj=_get_url(obj);
	}
	if(typeof(obj.multiple)=="undefined"){
		obj.multiple=false;
	}
	if(typeof(obj.width)=="undefined"){
		obj.width=300;
	}
	if(typeof(obj.height)=="undefined"){
		obj.height=400;
	}
	if(typeof(obj.onsuccess)!="undefined"){
		_onsuccess=obj.onsuccess;
	}
	if(typeof(obj.webRoot)!="undefined"){
		rootUrl=obj.webRoot;
	}
	$.colorbox({href:rootUrl+"/portal/custom-tree.action",
		iframe:true, 
		innerWidth:obj.width, 
		innerHeight:obj.height,
		overlayClose:false,
		title:obj.title,
		onClosed:function(){
			if(_operate=="ok"){
				_onsuccess.call();
			}
			_clear_data();
		}
	});
}

function _get_url(obj){
	var _url=obj.url;
	//参数以get方式传过来处理
	//${ctx}/vehicle/vehicle-choose-all-unused-car-tree.htm?startDate='+startDate+'&endDate='+endDate
	if(_url.indexOf("?")>=0){
		var urlArr=obj.url.split("?");
		obj.url=urlArr[0];//${ctx}/vehicle/vehicle-choose-all-unused-car-tree.htm
		_url_prn=urlArr[1];//'startDate='+startDate+'&endDate='+endDate
	}
	var paraObj=obj.postData;
	if(typeof(paraObj)!="undefined"){
		for(var p in paraObj){
			// 方法
			if(typeof(paraObj[p])!="function"){
				// p 为属性名称，obj[p]为对应属性的值 
				if(_url_prn==""){
					_url_prn=p+"="+paraObj[p];
				}else{
					_url_prn=_url_prn+"&"+p+"="+paraObj[p];
				}
			}
		}
	}
	
	return obj;
}

function _clear_data(){
	_node_id="";
	_node_title="";
	_operate="";
	_url_prn="";
}
/**
 * 获得某参数名称的值
 * @param prname 对应参数名称
 * @return 单选树时，返回相应的字符串
 * 			多选树时，返回相应值的数组
 */
function getSelectValue(prname){
	if(typeof(_node_id)=="undefined"||_node_id=="")return "";
	if(typeof(_crn_obj.nodeInfo)!="undefined"){
		var n=0;
		var arr=_crn_obj.nodeInfo;
		for(var i=0;i<arr.length;i++){
			if(arr[i]==prname){
				n=i;
				break;
			}else{
				n=-1;
				continue;
			}
		}
		if(n==-1){//n==-1说明nodeInfo中没有prname该参数名
			return "";
		}else{
			var valArr=new Array();
			var j=0;
			if(_node_id.indexOf(",")<0){//单选树或多选树中只选了一个节点
				var node_id_vals=_node_id.split("-");//将node的id以"-"截取
				if(node_id_vals.length>n){
					if(node_id_vals[n]!=''&&typeof(node_id_vals[n])!='undefined'){
						valArr[0]=node_id_vals[n];
						return valArr;
					}
				}
			}else{//多选树中选中了多个节点
				var node_ids=_node_id.split(",");
				for(var i=0;i<node_ids.length;i++){
					var node_id_vals=node_ids[i].split("-");
					if(node_id_vals[n]!=''&&typeof(node_id_vals[n])!='undefined'){
						valArr[j]=node_id_vals[n];
						j++;
					}					
				}
				return valArr;
			}
		}
	}
	return "";
}

/**
 * 获得选中节点的id
 * @return 
 */
function getSelectNodeId(){
	if(typeof(_node_id)!="undefined"){
		if(_node_id.indexOf(",")<0){//单选树或多选树中只选了一个节点
			var valArr=new Array();
			valArr[0]=_node_id;
			return valArr;
		}else{//多选树中选中了多个节点
			return _node_id.split(",");
		}
	}
	return "";
}
/**
 * 获得选中节点的title
 * @return
 */
function getSelectNodeTitle(){
	if(typeof(_node_title)!="undefined"){
		if(_node_title.indexOf(",")<0){//单选树或多选树中只选了一个节点
			var valArr=new Array();
			valArr[0]=_node_title;
			return valArr;
		}else{//多选树中选中了多个节点
			return _node_title.split(",");
		}
	}
	return "";
}

/**
 * 获得节点id中某位置的值
 * @param index 索引位置
 * @return 单选树时，返回相应的字符串
 * 			多选树时，返回相应值的数组
 */
function getSelectValueByIndex(index){
	if(typeof(_node_id)=="undefined"||_node_id==""||typeof(index)=="undefined")return "";
	if(index<=0){
		return "";
	}
	var n=index-1;
	var valArr=new Array();
	if(_node_id.indexOf(",")<0){//单选树或多选树中只选了一个节点
		var node_id_vals=_node_id.split("-");//将node的id以"-"截取
		if(node_id_vals.length>n){
			valArr[0]=node_id_vals[n];
			return valArr;
		}
	}else{//多选树中选中了多个节点
		var node_ids=_node_id.split(",");
		for(var i=0;i<node_ids.length;i++){
			var node_id_vals=node_ids[i].split("-");
			valArr[i]=node_id_vals[n];
		}
		return valArr;
	}
	return "";
}
