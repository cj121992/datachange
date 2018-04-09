$(function() {
	// init date tables
	var normConfigTable = $("#normConfig_list").dataTable({
		"deferRender": true,
		"processing" : true,
		"serverSide": true,
		"ajax": {
			url:  "/normConfig/pageList",
			type:"get",
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
			{ "data": 'normId', "bSortable": false, "visible" : false},
			{ "data": 'normName', "bSortable": false, "width":'10%'},
			{ "data": 'normType', "bSortable": false, "width":'10%'},
			{ "data": 'topic', "visible" : true, "width":'10%'},
			{ "data": 'tags', "visible" : true, "width":'10%'},
			{ "data": 'normFieldConfigs', "bSortable": false, "width":'30%',
				"render": function ( data, type, row ) {
					var htm = '';
					var algorithmFiled = '';
					for(var i=0;i<data.length;i++){
						if(data[i].fieldType == 2){
							htm = data[i].fieldName+"/"+data[i].fieldMatchValue;
						}else if(data[i].fieldType == 6){
							algorithmFiled = "("+data[i].fieldName+"/"+data[i].fieldAlgorithm+")";
						}
					}
					return htm+algorithmFiled;
				}
			},
			{ "data": 'jobName', "visible" : true, "width":'10%'},
			{
				"data": '操作' ,
				"width":'10%',
				"render": function ( data, type, row ) {
					return function(){

						// 详情页
						var goUrl =  '/normConfig/normFieldConfig?normId='+row.normId+"&normName="+row.normName;

						// html
						var html = '<p normId="'+ row.normId +'" '+
								' normName="'+ row.normName +'" '+
								' normType="'+ row.normType +'" '+
								' topic="'+ row.topic +'" '+
								' tags="'+ row.tags +'" '+
								'>'+
							'<button class="btn btn-warning btn-xs update" type="button">编辑</button>  '+
							'<button class="btn btn-danger btn-xs delete" type="button">删除</button>  <br>'+
							'<a class="btn btn-info btn-xs" type="button" target="_blank" href="'+goUrl+'" >榜单字段配置</button>  '+
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
		normConfigTable.fnDraw();
	});

	$("#normConfig_list").on('click', '.delete',function() {
		var normId = $(this).parent('p').attr("normId");
		ComConfirm.show("确认删除该榜单?", function(){
			$.ajax({
				type : 'DELETE',
				url :  "/normConfig/norm?normId="+normId,
				dataType : "json",
				success : function(data){
					if (data.code == 200) {
						ComAlert.show(1, "删除成功", function(){
							window.location.reload();
						});
					} else if (data.code == 300) {
						ComAlert.show(2, "请先删除使用该榜单的任务!!!", function(){
							window.location.reload();
						});
					}else{
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
	var addModalValidate = $("#addModal .form").validate({
		errorElement : 'span',  
        errorClass : 'help-block',
        focusInvalid : true,  
        rules : {
        	normName : {
				required : true
			},
			normType : {
				required : true
			},
			topic : {
				required : true
			},
			tags : {
				required : true
			}
        }, 
        messages : {
        	normName : {
            	required :"请输入“榜单名称”"
            },
            normType : {
            	required :"请输入“版本类型”"
            },
            topic : {
            	required :"请输入“topic”"
            },
            tags : {
            	required :"请输入“tag”"
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
        	$.post("/normConfig/norm",  $("#addModal .form").serialize(), function(data, status) {
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
	});
	$("#addModal").on('hide.bs.modal', function () {
		$("#addModal .form")[0].reset();
		addModalValidate.resetForm();
		$("#addModal .form .form-group").removeClass("has-error");
		$(".remote_panel").show();	// remote
	});

	// 更新
	$("#normConfig_list").on('click', '.update',function() {
		// base data
		
		$("#updateModal .form input[name='normIdE']").val($(this).parent('p').attr("normId"));
		$("#updateModal .form input[name='normNameE']").val($(this).parent('p').attr("normName"));
		$("#updateModal .form input[name='normTypeE']").val($(this).parent('p').attr("normType"));
		$("#updateModal .form input[name='topicE']").val($(this).parent('p').attr("topic")).click();
		$("#updateModal .form input[name='tagsE']").val($(this).parent('p').attr("tags")).click();

		// show
		$('#updateModal').modal({backdrop: false, keyboard: false}).modal('show');
	});
	var updateModalValidate = $("#updateModal .form").validate({
		errorElement : 'span',  
        errorClass : 'help-block',
        focusInvalid : true,
        rules : {
        	normNameE : {
				required : true
			},
			normTypeE : {
				required : true
			},
			topicE : {
				required : true
			},
			tagsE : {
				required : true
			}
        }, 
        messages : {
        	normNameE : {
            	required :"请输入“榜单名称”"
            },
            normTypeE : {
            	required :"请输入“版本类型”"
            },
            topicE : {
            	required :"请输入“toptic”"
            },
            tagsE : {
            	required :"请输入“tags”"
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
        	$.ajax({
				type : 'POST',
				url : "/normConfig/norm",
				data : {
					normId : $('#normIdE').val(),
					normName : $('#normNameE').val(),
					normType : $('#normTypeE').val(),
					topic : $('#topicE').val(),
					tags : $('#tagsE').val(),
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
