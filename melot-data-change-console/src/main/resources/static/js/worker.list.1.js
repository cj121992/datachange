var currentWorkStatus;
$(function() {
	// init date tables
	var workerTable = $("#worker_list").dataTable({
		"deferRender": true,
		"processing" : true,
		"serverSide": true,
		"ajax": {
			url:  "/worker/pageList",
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
		    { "data": 'jobId', "visible" : false, "width":'25%'},
			{ "data": 'workerName', "bSortable": false, "width":'10%'},
			{ "data": 'jobType', "bSortable": false, "width":'10%'},
			{ "data": 'jobName', "visible" : true, "width":'25%'},
			{ "data": 'status', "visible" : true, "width":'10%'},
			{
				"data": '操作' ,
				"width":'10%',
				"render": function ( data, type, row ) {
					return function(){
						var status = 0;
						if(row.status == 'STOP'){
							status = -1;
						}else if(row.status == 'RUNNING'){
							status = 1;
						}else if(row.status == 'START'){
							status = 2;
						}else if(row.status == 'UNKNOWN'){
							status = 3;
						}
						// html
						var html = '<p workerName="'+ row.workerName +'" '+
								' jobType="'+ row.jobType +'" '+
								' jobId="'+ row.jobId +'" '+
								' status="'+ status +'" '+
								'>'+
							'<button class="btn btn-warning btn-xs update" type="button">编辑</button>  '+
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
		workerTable.fnDraw();
	});
	$('#jobType').on("change",function(){ 
		var jobType = $('#jobType').val();
		$('#jobName').empty();
		if(jobType == 'norm'){
			$.ajax({
				type : 'GET',
				url :  "/job/normJobList",
				dataType : "json",
				success : function(data){
					$('#jobName').append("<option value=''></option>");
					for(var i=0;i<data.length;i++){
						$('#jobName').append("<option value='"+data[i].jobId+"'>"+data[i].jobName+"</option>");
					}
				}
			});
		}else if(jobType == 'timed_update'){
			$.ajax({
				type : 'GET',
				url :  "/job/timedUpdateJobList",
				dataType : "json",
				success : function(data){
					$('#jobName').append("<option value=''></option>");
					for(var i=0;i<data.length;i++){
						$('#jobName').append("<option value='"+data[i].jobId+"'>"+data[i].jobName+"</option>");
					}
				}
			});
		}
	});
	// 更新
	$("#worker_list").on('click', '.update',function() {
		// base data
		
		$("#updateModal .form input[name='workerName']").val($(this).parent('p').attr("workerName"));
		$("#jobType").val($(this).parent('p').attr("jobType"));
		$("#jobType").trigger("change");
		var jobId = $(this).parent('p').attr("jobId");
		currentWorkStatus = $(this).parent('p').attr("status");
		$('#status').empty();
		if(currentWorkStatus == '-1' ||  currentWorkStatus == '0'){
			$('#status').append("<option value=''></option>");
			$('#status').append("<option value='2'>启动</option>");
		}else{
			$('#status').append("<option value=''></option>");
			$('#status').append("<option value='-1'>停止</option>");
		}
		
		setTimeout(function () {
			$("#jobName").val(jobId);
		},300);
		// show
		$('#updateModal').modal({backdrop: false, keyboard: false}).modal('show');
	});
	var updateModalValidate = $("#updateModal .form").validate({
		errorElement : 'span',  
        errorClass : 'help-block',
        focusInvalid : true,
		rules : {
			workerName : {
				required : true
			}
		},
		messages : {
			workerName : {
				required :"请输入“worker名称”"
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
        	var status = $('#status').val().trim();
        	if(status==''){
        		status = currentWorkStatus;
        	}
        	// post
        	$.ajax({
				type : 'POST',
				url : "/worker/workerConfig",
				data : 
				{	
					workerName:$('#workerName').val(),
					jobId:$('#jobName').val(),
					jobType:$('#jobType').val(),
					status:status,
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
	});
	$("#updateModal").on('hide.bs.modal', function () {
		$("#updateModal .form")[0].reset();
		updateModalValidate.resetForm();
		$("#updateModal .form .form-group").removeClass("has-error");
		$(".remote_panel").show();	// remote
	});
});
