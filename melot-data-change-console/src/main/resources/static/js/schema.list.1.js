var selectNormConfigs;
$(function() {
	// init date tables
	var schemaTable = $("#schema_list").dataTable({
		"deferRender": true,
		"processing" : true,
		"serverSide": true,
		"ajax": {
			url:  "/schema/list",
			type:"post",
			data : function ( d ) {
				var obj = {};
				obj.schemaName = $('#name').val();
				obj.start = d.start;
				obj.length = d.length;
				return obj;
			}
		},
		"searching": false,
		"ordering": false,
		//"scrollX": true,	// X轴滚动条，取消自适应
		"columns": [
			{ "data": 'schema_id', "bSortable": false, "visible" : false},
			{ "data": 'schema_name', "bSortable": false, "width":'10%'},
			{ "data": 'version', "bSortable": false,"width":'10%'},
			{ "data": 'value', "bSortable": false,"width":'62%'},
			{
				"data": '操作' ,
				"width":'8%',
				"render": function ( data, type, row ) {
					return function(){
						// html
						var html = '<p schema_id="'+ row.schema_id +'" '+
								' schema_name="'+ row.schema_name +'" >'+
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
		schemaTable.fnDraw();
	});

	// job operate
	$("#schema_list").on('click', '.delete',function() {
		var schemaId = $(this).parent('p').attr("schema_id");
		alert(schemaId);
		ComConfirm.show("确认删除该schema?", function(){
			$.ajax({
				type : 'DELETE',
				url :  "/schema/remove?schemaId="+schemaId,
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
	var addModalValidate = $("#addModal .form").validate({
		errorElement : 'span',  
        errorClass : 'help-block',
        focusInvalid : true,  
        rules : {
        	schema_name : {
				required : true
			},
			version : {
				required : true
			},
			value : {
				required : true
			}
        }, 
        messages : {
        	schema_name : {
            	required :"请输入“schema名称”"
            },
            version : {
            	required :"请输入“版本”"
            },
            value : {
            	required :"请输入“schema内容”"
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
    		var schema_name = $('#schema_name').val();
    		var version = $('#version').val();
    		var value = $('#value').val();
        	$.post("/schema/add",  
        			{
        				schema_name : schema_name,
        				version : version,
        				value : value
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
	});
	$("#addModal").on('hide.bs.modal', function () {
		$("#addModal .form")[0].reset();
		addModalValidate.resetForm();
		$("#addModal .form .form-group").removeClass("has-error");
		$(".remote_panel").show();	// remote
	});

});
