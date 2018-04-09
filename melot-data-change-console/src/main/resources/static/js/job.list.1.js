var selectNormConfigs;
$(function() {
	// init date tables
	var jobTable = $("#job_list").dataTable({
		"deferRender": true,
		"processing" : true,
		"serverSide": true,
		"ajax": {
			url:  "/job/pageList",
			type:"post",
			data : function ( d ) {
				var obj = {};
				obj.name = $('#name').val();
				obj.start = d.start;
				obj.length = d.length;
				return obj;
			}
		},
		"searching": false,
		"ordering": false,
		//"scrollX": true,	// X轴滚动条，取消自适应
		"columns": [
			{ "data": 'jobId', "bSortable": false, "visible" : false},
			{ "data": 'jobName', "bSortable": false, "width":'12%'},
			{ "data": 'jobType', "bSortable": false, "width":'8%',
				"render": function ( data, type, row ) {
					var htm = "";
					if(data == "norm"){
						htm += "榜单任务";
					}else if(data == "timed_update"){
						htm += "定时汇聚任务";
					}
					return htm;
				}
			},
			{ "data": 'normConfigs', "visible" : true, "width":'20%',
				"render": function ( data, type, row ) {
					var htm = '';
					if(data != null && data != undefined){
						for(var i=0;i<data.length;i++){
							if(data[i].normFieldConfigs != null && data[i].normFieldConfigs != undefined 
									&& data[i].normFieldConfigs.length > 0){
								for(var j=0;j<data[i].normFieldConfigs.length;j++){
									if(data[i].normFieldConfigs[j].fieldType == 2){
										htm += data[i].normName+"("+data[i].normFieldConfigs[j].fieldName+":"+data[i].normFieldConfigs[j].fieldMatchValue+")";
										break;
									}
								}
							}else{
								htm += data[i].normName;
							}
							if(i!=data.length-1){
								htm += "  /    ";
							}
						}
					}
					return htm;
				}
			},

			{ "data": 'jobDetail', "bSortable": false,"width":'20%',
				"render": function ( data, type, row ) {
					return data;
				}
			},
			{ "data": 'createTime', "visible" : true, "width":'15%',
				"render": function ( data, type, row ) {
					var htm = '';
					htm = new Date(data).Format("yyyy-MM-dd hh:mm:ss");
					return htm;
				}
			},
			{ "data": 'updateTime', "visible" : true, "width":'15%',
				"render": function ( data, type, row ) {
					var htm = '';
					htm = new Date(data).Format("yyyy-MM-dd hh:mm:ss");
					return htm;
				}
			},
			{
				"data": '操作' ,
				"width":'10%',
				"render": function ( data, type, row ) {
					return function(){
						// html
						var html = '<p jobId="'+ row.jobId +'" '+
								' jobName="'+ row.jobName +'" '+
								' jobType="'+ row.jobType +'" ';
								if((row.jobType=="norm" || row.jobType=="timed_update" ) && row.normConfigs.length > 0){
									var configIds = '';
									for(var i=0;i<row.normConfigs.length;i++){
										if(i != row.normConfigs.length -1){
											configIds += row.normConfigs[i].normId +",";
										}else{
											configIds += row.normConfigs[i].normId;
										}
									}
									html += ' topic="'+row.normConfigs[0].topic+'" '+
									' tag="'+row.normConfigs[0].tags+'" '+
									' configIds="'+configIds+'"';
									
								}
								html+=
								'>'+
							'<button  class="btn btn-warning btn-xs update" type="button">编辑</button>  '+
							'<button class="btn btn-danger btn-xs delete" type="button">删除</button>  <br>'+
							'</p>';
						return html;
					};
				}
			}
		],
		"language" : {
			"sProcessing" : "处理中...",
			"sLengthMenu" : "每页 _MENU_ 条记录",
			"sZeroRecords" : "没有匹配结果",
			"sInfo" : "第 _PAGE_ 页 ( 总共 _PAGES_ 页，_TOTAL_ 条记录 )",
			"sInfoEmpty" : "无记录",
			"sInfoFiltered" : "(由 _MAX_ 项结果过滤)",
			"sInfoPostFix" : "",
			"sSearch" : "搜索:",
			"sUrl" : "",
			"sEmptyTable" : "表中数据为空",
			"sLoadingRecords" : "载入中...",
			"sInfoThousands" : ",",
			"oPaginate" : {
				"sFirst" : "首页",
				"sPrevious" : "上页",
				"sNext" : "下页",
				"sLast" : "末页"
			},
			"oAria" : {
				"sSortAscending" : ": 以升序排列此列",
				"sSortdescriptionending" : ": 以降序排列此列"
			}
		}
	});

	$("#search").click(function(){
		jobTable.fnDraw();
	});

	// job operate
	$("#job_list").on('click', '.delete',function() {
		var jobId = $(this).parent('p').attr("jobId");
		ComConfirm.show("确认删除该任务?", function(){
			$.ajax({
				type : 'DELETE',
				url :  "/job/jobConfig?jobId="+jobId,
				dataType : "json",
				success : function(data){
					if (data.code == 200) {
						ComAlert.show(1, "删除成功", function(){
							window.location.reload();
						});
					} else {
						ComAlert.show(2, (data.msg || "删除失败") );
					}
				},
			});
		});
	});

	// 新增
	$("#add").click(function(){
		$('#addModal').modal({backdrop: false, keyboard: false}).modal('show');
	});
	$('#jobType,#jobTypeE').on("change",function(){ 
		var jobType = $(this).val();
		if(jobType == 'norm'){
			$('#timeJobDiv').hide();
			$.ajax({
				type : 'GET',
				url :  "/normConfig/normTopic",
				dataType : "json",
				success : function(data){
					$('#topic').empty();
					$('#topicE').empty();
					$('#tag').empty();
					$('#tagE').empty();
					$('#configIds').empty();
					$('#configIdsE').empty();
					$('#topic').append("<option value='' select></option>");
					$('#topicE').append("<option value='' select></option>");
					for(var i=0;i<data.length;i++){
						$('#topic').append("<option value='"+data[i].trim()+"'>"+data[i].trim()+"</option>");
						$('#topicE').append("<option value='"+data[i].trim()+"'>"+data[i].trim()+"</option>");
					}
				}
			});
		}else if(jobType == 'timed_update'){
			$('#timeJobDiv').show();
			$.ajax({
				type : 'GET',
				url :  "/normConfig/normTopic",
				dataType : "json",
				success : function(data){
					$('#topic').empty();
					$('#topicE').empty();
					$('#tag').empty();
					$('#tagE').empty();
					$('#configIds').empty();
					$('#configIdsE').empty();
					$('#topic').append("<option value='' select></option>");
					$('#topicE').append("<option value='' select></option>");
					for(var i=0;i<data.length;i++){
						$('#topic').append("<option value='"+data[i].trim()+"'>"+data[i].trim()+"</option>");
						$('#topicE').append("<option value='"+data[i].trim()+"'>"+data[i].trim()+"</option>");
					}
				}
			});
		}else{
			$('#timeJobDiv').hide();
		}
	});
	$('#topic,#topicE').on("change",function(){ 
		var topic = $(this).val();
		$.ajax({
			type : 'GET',
			url :  "/normConfig/normTag?topic="+topic,
			dataType : "json",
			success : function(data){
				$('#tag').empty();
				$('#tagE').empty();
				$('#configIds').empty();
				$('#configIdsE').empty();
				$('#tag').append("<option value='' select></option>");
				$('#tagE').append("<option value='' select></option>");
				for(var i=0;i<data.length;i++){
					$('#tag').append("<option  value='"+data[i].trim()+"'>"+data[i].trim()+"</option>");
					$('#tagE').append("<option value='"+data[i].trim()+"'>"+data[i].trim()+"</option>");
				}
			}
		});
		

	});
	$('#tag').on("change",function(){ 
		var jobType = $('#jobType').val();
		var topic = $('#topic').val();
		var tag = $('#tag').val();
		if(jobType == 'norm'){
			$.ajax({
				type : 'GET',
				url :  "/normConfig/normlistnouse?topic="+topic+"&tag="+tag,
				dataType : "json",
				success : function(data){
					$('#configIds').empty();
					for(var i=0;i<data.length;i++){
						for(var j=0;j<data[i].normFieldConfigs.length;j++){
							if(data[i].normFieldConfigs[j].fieldType == 2){
								$('#configIds').append("<option  value='"+data[i].normId+"'>"+data[i].normName+"("+data[i].normFieldConfigs[j].fieldName+":"+data[i].normFieldConfigs[j].fieldMatchValue+")"+"</option>");
								break;
							}
						}
					}
				}
			});
		}else if(jobType == 'timed_update'){
			$.ajax({
				type : 'GET',
				url :  "/normConfig/normlist?topic="+topic+"&tag="+tag,
				dataType : "json",
				success : function(data){
					$('#configIds').empty();
					for(var i=0;i<data.length;i++){
						for(var j=0;j<data[i].normFieldConfigs.length;j++){
							if(data[i].normFieldConfigs[j].fieldType == 2){
								$('#configIds').append("<option  value='"+data[i].normId+"'>"+data[i].normName+"("+data[i].normFieldConfigs[j].fieldName+":"+data[i].normFieldConfigs[j].fieldMatchValue+")"+"</option>");
								break;
							}
						}
					}
				}
			});
		}
	});
	$('#tagE').on("change",function(){ 
		var topic = $('#topicE').val();
		var tag = $('#tagE').val();
		$.ajax({
			type : 'GET',
			url :  "/normConfig/normlist?topic="+topic+"&tag="+tag,
			dataType : "json",
			success : function(data){
				$('#configIdsE').empty();
				for(var i=0;i<data.length;i++){
					for(var j=0;j<data[i].normFieldConfigs.length;j++){
						if(data[i].normFieldConfigs[j].fieldType == 2){
							$('#configIdsE').append("<option  value='"+data[i].normId+"'>"+data[i].normName+"("+data[i].normFieldConfigs[j].fieldName+":"+data[i].normFieldConfigs[j].fieldMatchValue+")"+"</option>");
							break;
						}
					}
				}
			}
		});
	});
	var addModalValidate = $("#addModal .form").validate({
		errorElement : 'span',  
        errorClass : 'help-block',
        focusInvalid : true,  
        rules : {
        	jobName : {
				required : true
			},
			jobType : {
				required : true
			},
			configIds : {
				required : true
			},
			topic : {
				required : true
			},
			tag : {
				required : true
			}
        }, 
        messages : {
        	jobName : {
            	required :"请输入“任务名称”"
            },
            jobType : {
            	required :"请输入“任务类型”"
            },
            configIds : {
            	required :"请选中“子任务”"
            },
            topic : {
            	required :"请选择“topic”"
            },
            tag : {
            	required :"请选择“tag”"
            }
        },
		highlight : function(element) {  
            $(element).closest('.form-group').addClass('has-error');  
        },
        success : function(label) {  
            label.closest('.form-group').removeClass('has-error');  
            label.remove();  
        },
        errorPlacement : function(error, element) {  
            element.parent('div').append(error);  
        },
        submitHandler : function(form) {
    		var jobType = $('#jobType').val();
    		
        	var configIdArray = $("#configIds").val();  
        	if(configIdArray == null || configIdArray.length < 1){
				ComAlert.show(2,"请选择子任务!");
				return;
        	}
        	var configIds = "";
        	for(var i=0;i<configIdArray.length;i++){
        		if(i==configIdArray.length-1){
        			configIds += configIdArray[i];
        		}else{
        			configIds += configIdArray[i]+",";
        		}
        	}
        	if(jobType == 'norm'){
        		var jobJson = JSON.stringify(
					{	
            			jobName:$('#jobName').val(),
            			jobType:$('#jobType').val(),
            			configIds:configIds
            		}
    			);
            	$.post("/job/jobConfig",  
            		{	
            			jobJson : jobJson
            		}
            		, function(data, status) {
    	    			if (data.code == "200") {
    						$('#addModal').modal('hide');
    						setTimeout(function () {
    							ComAlert.show(1, "新增成功", function(){
    								window.location.reload();
    							});
    						}, 315);
    	    			} else {
    						ComAlert.show(2, (data.msg || "新增失败") );
    	    			}
        		});
        	}else if(jobType == 'timed_update'){
        		var dailyCron = $('#dailyCron').val();
        		var weeklyCron = $('#weeklyCron').val();
        		var monthlyCron = $('#monthlyCron').val();
        		var yearlyCron = $('#yearlyCron').val();
//        		if(dailyCron == '' || weeklyCron == '' || monthlyCron == '' || yearlyCron == ''){
//					ComAlert.show(2, "请输入所有榜单的Cron表达式!!!");
//					return;
//        		}
        		var jobJson = JSON.stringify(
					{	
            			jobName:$('#jobName').val(),
            			jobType:$('#jobType').val(),
            			configIds:configIds,
            			timedUpdateJobConfig:{ 
            				'dailyCron' : dailyCron,
            				'weeklyCron' : weeklyCron,
            				'monthlyCron' : monthlyCron,
            				'yearlyCron' : yearlyCron
            			}
            		}
    			);
            	$.post("/job/jobConfig",  
            			{
            				jobJson : jobJson
            			}, function(data, status) {
        	    			if (data.code == "200") {
        						$('#addModal').modal('hide');
        						setTimeout(function () {
        							ComAlert.show(1, "新增成功", function(){
        								window.location.reload();
        							});
        						}, 315);
        	    			} else {
        						ComAlert.show(2, (data.msg || "新增失败") );
        	    			}
        		});
            }
		}
	});
	$("#addModal").on('hide.bs.modal', function () {
		$("#addModal .form")[0].reset();
		addModalValidate.resetForm();
		$("#addModal .form .form-group").removeClass("has-error");
		$(".remote_panel").show();	// remote
	});

	// 更新
	$("#job_list").on('click', '.update',function() {
		// base data
		$("#jobIdE").val($(this).parent('p').attr("jobId"));
		$("#jobTypeE").val($(this).parent('p').attr("jobType"));
		$("#jobTypeE").trigger("change");
		$("#jobNameE").val($(this).parent('p').attr("jobName"));
		var topic = $(this).parent('p').attr("topic");
		var tag = $(this).parent('p').attr("tag");
		var configIds = $(this).parent('p').attr("configIds");

		setTimeout(function () {
			$("#topicE").val(topic);
			$("#topicE").trigger("change");
			setTimeout(function () {
				$("#tagE").val(tag);
				$("#tagE").trigger("change");

				if(configIds != undefined && configIds.length > 1){
					var configIdArray = configIds.split(",");
					if(configIdArray != null && configIdArray != undefined && configIdArray.length > 0){
						setTimeout(function () {
							$("#configIdsE").val(configIdArray);
						},200);
						
					}
				}
			},200);
		},200);
		if($(this).parent('p').attr("jobType") == 'timed_update'){
			$.ajax({
				type : 'GET',
				url : "/job/timedUpdateJobDetail",
				data : 
				{	
					jobId : $(this).parent('p').attr("jobId")
        		},
				dataType : "json",
				success : function(data){
					$("#dailyCronE").val(data.dailyCron);
					$("#weeklyCronE").val(data.weeklyCron);
					$("#monthlyCronE").val(data.monthlyCron);
					$("#yearlyCronE").val(data.yearlyCron);
				},
			});
			$("#timeJobEDiv").show();
		}else{
			$("#timeJobEDiv").hide();
		}
		// show
		$('#updateModal').modal({backdrop: false, keyboard: false}).modal('show');
	});
	var updateModalValidate = $("#updateModal .form").validate({
		errorElement : 'span',  
        errorClass : 'help-block',
        focusInvalid : true,
		rules : {
			jobNameE : {
				required : true
			},
			jobTypeE : {
				required : true
			},
			configIdsE : {
				required : true
			},
			topicE : {
				required : true
			},
			tagE : {
				required : true
			}
		},
		messages : {
			jobNameE : {
            	required :"请输入“任务名称”"
            },
            jobTypeE : {
            	required :"请输入“任务类型”"
            },
            configIdsE : {
            	required :"请选中“子任务”"
            },
            topicE : {
            	required :"请选择“topic”"
            },
            tagE : {
            	required :"请选择“tag”"
            }
		},
		highlight : function(element) {
            $(element).closest('.form-group').addClass('has-error');  
        },
        success : function(label) {  
            label.closest('.form-group').removeClass('has-error');  
            label.remove();  
        },
        errorPlacement : function(error, element) {  
            element.parent('div').append(error);  
        },
        submitHandler : function(form) {

    		var jobType = $('#jobTypeE').val();
        	var configIdArray = $("#configIdsE").val();  
        	if(configIdArray == null || configIdArray.length < 1){
				ComAlert.show(2,"请选择子任务!");
				return;
        	}
        	var configIds = "";
        	for(var i=0;i<configIdArray.length;i++){
        		if(i==configIdArray.length-1){
        			configIds += configIdArray[i];
        		}else{
        			configIds += configIdArray[i]+",";
        		}
        	}
			// post
        	if(jobType == 'norm'){
        		var jobJson = JSON.stringify(
    					{	
    						jobId:$('#jobIdE').val(),
                			jobName:$('#jobNameE').val(),
                			jobType:$('#jobTypeE').val(),
                			configIds:configIds
                		}
        			);
            	$.ajax({
    				type : 'POST',
    				url : "/job/jobConfig",
    				data : 
    				{	
    					jobJson : jobJson,
    					_method : 'put'
            		},
    				dataType : "json",
    				success : function(data){
    					if (data.code == 200) {
    						$('#updateModal').modal('hide');
    						setTimeout(function () {
    							ComAlert.show(1, "更新成功", function(){
    								window.location.reload();
    							});
    						}, 315);
    					} else {
    						ComAlert.show(2, (data.msg || "更新失败") );
    					}
    				},
    			});
        	}else if(jobType == 'timed_update'){
        		var dailyCron = $('#dailyCronE').val();
        		var weeklyCron = $('#weeklyCronE').val();
        		var monthlyCron = $('#monthlyCronE').val();
        		var yearlyCron = $('#yearlyCronE').val();
//        		if(dailyCron == '' || weeklyCron == '' || monthlyCron == '' || yearlyCron == ''){
//					ComAlert.show(2, "请输入所有榜单的Cron表达式!!!");
//					return;
//        		}
        		var jobJson = JSON.stringify(
					{	
    					jobId:$('#jobIdE').val(),
            			jobName:$('#jobNameE').val(),
            			jobType:$('#jobTypeE').val(),
            			configIds:configIds,
            			timedUpdateJobConfig:{ 
            				'dailyCron' : dailyCron,
            				'weeklyCron' : weeklyCron,
            				'monthlyCron' : monthlyCron,
            				'yearlyCron' : yearlyCron
            			}
            		}
    			);
            	$.ajax({
    				type : 'POST',
    				url : "/job/jobConfig",
    				data : 
    				{	
    					jobJson : jobJson,
    					_method : 'put'
            		},
    				dataType : "json",
    				success : function(data){
    					if (data.code == 200) {
    						$('#updateModal').modal('hide');
    						setTimeout(function () {
    							ComAlert.show(1, "更新成功", function(){
    								window.location.reload();
    							});
    						}, 315);
    					} else {
    						ComAlert.show(2, (data.msg || "更新失败") );
    					}
    				},
    			});
        	}
		}
	});
	$("#updateModal").on('hide.bs.modal', function () {
		$("#updateModal .form")[0].reset();
		updateModalValidate.resetForm();
		$("#updateModal .form .form-group").removeClass("has-error");
		$(".remote_panel").show();	// remote
	});

});
