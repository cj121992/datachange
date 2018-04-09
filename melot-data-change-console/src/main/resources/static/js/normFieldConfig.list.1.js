$(function() {
	// init date tables
	var normFieldConfigTable = $("#normFieldConfig_list").dataTable({
		"deferRender": true,
		"processing" : true,
		"serverSide": true,
		"ajax": {
			url: "/normConfig/normFieldConfigList",
			type:"get",
			data : function ( d ) {
				var obj = {};
				obj.normId = normId;
				obj.start = d.start;
				obj.length = d.length;
				return obj;
			}
		},
		"searching": false,
		"ordering": false,
		//"scrollX": true,	// X轴滚动条，取消自适应
		"columns": [
			{ "data": 'fieldId', "bSortable": false, "visible" : false},
			{ "data": 'fieldType', "bSortable": false, "width":'10%',
				"render": function ( data, type, row ) {
					var htm = '';
					if(data == 1){
						htm += '榜单列表GroupBy字段';
					}else if(data == 2){
						htm += '生成榜单数据的条件';
					}else if(data == 6){
						htm += '榜单排序算法所需字段';
					}else if(data == 3){
						htm += 'Date类型的时间字段';
					}else if(data == 4){
						htm += 'String类型的时间字段';
					}else if(data == 5){
						htm += 'timestamp类型的时间字段';
					}else if(data == 0){
						htm += '生成榜单名称的Target字段';
					}else{
						htm += '未知类型字段';
					}
					return htm;
				}
			},
			{ "data": 'fieldName', "visible" : true, "width":'10%'},
			{ "data": 'fieldAlgorithm', "visible" : true, "width":'10%'},
			{ "data": 'fieldMatchValue', "visible" : true, "width":'10%'},
			{ "data": 'fieldPath', "visible" : true, "width":'10%'},
			{
				"data": '操作' ,
				"width":'10%',
				"render": function ( data, type, row ) {
					return function(){

						// html
						var html = '<p fieldId="'+ row.fieldId +'" '+
								' fieldType="'+ row.fieldType +'" '+
								' fieldName="'+ row.fieldName +'" '+
								' fieldAlgorithm="'+ row.fieldAlgorithm +'" '+
								' fieldMatchValue="'+ row.fieldMatchValue +'" '+
								' fieldPath="'+ row.fieldPath +'" '+
								'>'+
							'<button class="btn btn-warning btn-xs update" type="button">编辑</button>  '+
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
		normFieldConfigTable.fnDraw();
	});

	// job operate
	$("#normFieldConfig_list").on('click', '.delete',function() {
		var fieldId = $(this).parent('p').attr("fieldId");
		ComConfirm.show("确认删除该项目?", function(){
			$.ajax({
				type : 'DELETE',
				url :  "/normConfig/normField?fieldId="+fieldId,
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
		$('#fieldType').trigger("change");
		$('#addModal').modal({backdrop: false, keyboard: false}).modal('show');
	});
	$('#fieldType').on("change",function(){ 
		var fieldType = $('#fieldType').val();
		$('#algorithFieldDiv').hide();
		$('#matchFieldDiv').hide();
		if(fieldType == 6){
			$('#algorithFieldDiv').show();
		}else if(fieldType == 2){
			$('#matchFieldDiv').show();
		}
	});
	$('#fieldTypeE').on("change",function(){ 
		var fieldType = $('#fieldTypeE').val();
		$('#algorithFieldDivE').hide();
		$('#matchFieldDivE').hide();
		if(fieldType == 6){
			$('#algorithFieldDivE').show();
		}else if(fieldType == 2){
			$('#matchFieldDivE').show();
		}
	});
	var addModalValidate = $("#addModal .form").validate({
		errorElement : 'span',  
        errorClass : 'help-block',
        focusInvalid : true,  
        rules : {
        	fieldType : {
				required : true
			},
			fieldName : {
				required : true
			},
			fieldPath : {
				required : true
			}
        }, 
        messages : {
        	fieldType : {
            	required :"请选择“字段类型”"
            },
            fieldName : {
            	required :"请输入“字段名称”"
            },
            fieldPath : {
            	required :"请输入“字段路径”"
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
        	$.post("/normConfig/normField",  $("#addModal .form").serialize(), function(data, status) {
    			if (data.code == "200") {
					$('#addModal').modal('hide');
					setTimeout(function () {
						ComAlert.show(1, "新增成功", function(){
							window.location.reload();
						});
					}, 315);
    			}else if (data.code == "300") {
					$('#addModal').modal('hide');
					setTimeout(function () {
						ComAlert.show(2, (data.msg || "该类型字段已经存在!!!") );
					}, 315);
    			} else {
					$('#addModal').modal('hide');
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
	$("#normFieldConfig_list").on('click', '.update',function() {
		$("#updateModal .form input[name='fieldIdE']").val($(this).parent('p').attr("fieldId"));
		$("#fieldTypeE").val($(this).parent('p').attr("fieldType"));
		$("#updateModal .form input[name='fieldNameE']").val($(this).parent('p').attr("fieldName"));
		$("#fieldAlgorithmE").val($(this).parent('p').attr("fieldAlgorithm"));
		$("#updateModal .form input[name='fieldMatchValueE']").val($(this).parent('p').attr("fieldMatchValue")==null?"":$(this).parent('p').attr("fieldMatchValue"));
		$("#updateModal .form input[name='fieldPathE']").val($(this).parent('p').attr("fieldPath"));

		$('#fieldTypeE').trigger("change");
		// show
		$('#updateModal').modal({backdrop: false, keyboard: false}).modal('show');
	});
	var updateModalValidate = $("#updateModal .form").validate({
		errorElement : 'span',  
        errorClass : 'help-block',
        focusInvalid : true,
        rules : {
        	fieldTypeE : {
				required : true
			},
			fieldNameE : {
				required : true
			},
			fieldPathE : {
				required : true
			}
        }, 
        messages : {
        	fieldTypeE : {
            	required :"请选择“字段类型”"
            },
            fieldNameE : {
            	required :"请输入“字段名称”"
            },
            fieldPathE : {
            	required :"请输入“字段路径”"
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
				url : "/normConfig/normField",
				data : {
					fieldId : $('#fieldIdE').val(),
					fieldType : $('#fieldTypeE').val(),
					fieldName : $('#fieldNameE').val(),
					fieldAlgorithm : $('#fieldAlgorithmE').val(),
					fieldMatchValue : $('#fieldMatchValueE').val(),
					fieldPath : $('#fieldPathE').val(),
					normId : normId,
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
