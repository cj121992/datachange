$(function() {
	var groupConfigListTable = $("#groupConfigList").dataTable({
		"deferRender": true,
		"processing" : true,
		"serverSide": true,
		"ajax": {
			url: base_url + "/config/groupPageList",
			type:"post",
			data : function ( d ) {
				var obj = {};
				obj.configName = '';
				obj.groupName = groupName;
				obj.start = d.start;
				obj.length = d.length;
				return obj;
			}
		},
		"searching": false,
		"ordering": false,
		"searching": false,
		"columns": [
			{ "data": 'configDes', "visible" : true, "bSortable": false},
			{ "data": 'configName', "visible" : false, "bSortable": false},
			{ "data": 'attrName', "visible" : true, "bSortable": false},
			{ "data": 'attrDes', "visible" : true, "bSortable": false},
			{ "data": 'attrValue', "visible" : true, "bSortable": false},
			{ "data": 'currentObject', "visible" : true, "bSortable": false},
			{
				"data": '操作' ,
				"bSortable": false,
				"render": function ( data, type, row ) {
					return function(){

						// html
						var html = '<p configName="'+ row.configName +'" '+
							' configValue="'+ row.configValue +'" '+
							' attrName="'+ row.attrName +'" '+
							' attrValue="'+ row.attrValue +'" '+
							' attrDes="'+ row.attrDes +'" '+
							'>'+
							'<button class="btn btn-warning btn-xs update" >编辑</button>&nbsp;'+
							'<button class="btn btn-danger btn-xs delete" type="button">删除</button> '+
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
	/**
	 * 关键字搜索
	 */
	$("#searchGroupConfigName").bind('input porpertychange',function(){
		var searchGroupConfigName = $("#searchGroupConfigName").val();
		$('#groupConfigList').find('tbody tr').each(function(){
			var nameItem = $(this).children().eq(1).html();
			if (searchGroupConfigName) {
				if (nameItem.toLowerCase().indexOf(searchGroupConfigName.toLowerCase()) != -1) {
					$(this).show();
				} else {
					$(this).hide();
				}

			} else {
				$(this).show();
			}
		});

	});
	// 新增
	$("#addGroupConfig").click(function(){
		$('#addGroupConfigModal').modal({backdrop: false, keyboard: false}).modal('show');
	});
	//选择配置名称 加载配置属性名称
	$("#configName").change(function () {
		var $this = $(this);
		$.post(base_url + "/config/getAttrNames",  
    			{
					configName : $this.val()
    			},
    			function(data, status) {
    				$("#attrName").empty();
    				for(var i=0;i<data.length;i++){
    					$("#attrName").append("<option value='"+data[i].attrName+"'>"+data[i].attrDes+"</option>");
    				}
					$('#addModal').modal('hide');
	    			
	    	});
	});
	$("#configName").trigger('change');
	var addModalValidate = $("#addGroupConfigModal .form").validate({
		errorElement : 'span',  
        errorClass : 'help-block',
        focusInvalid : true,  
        rules : {
        	attrValue : {
            	required : true
            }
        }, 
        messages : {
        	attrValue : {
            	required :"请输入“属性值”"
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
        	if($('#attrName').find("option:selected").text().indexOf('是否') != -1){
        		if($('#attrValue').val() != 'true' && $('#attrValue').val() != 'false'){
        			ComAlert.show(2, "参数值输入错误" );
        			return false;
        		}
        	}
        	$.post(base_url + "/config/add",  
    			{
        			attrName : $('#attrName').val(),
        			attrValue : $('#attrValue').val(),
    				groupName : groupName
    			},
    			function(data, status) {
	    			if (data.code == "200") {
						$('#addGroupConfigModal').modal('hide');
						setTimeout(function () {
							ComAlert.show(1, "新增成功", function(){
								groupConfigListTable.fnDraw();
							});
						}, 115);
	    			} else {
						ComAlert.show(2, (data.msg || "新增失败") );
	    			}
	    	});
			}
	});
	$("#addGroupConfigModal").on('hide.bs.modal', function () {
		$("#addGroupConfigModal .form")[0].reset();
		addModalValidate.resetForm();
		$("#addGroupConfigModal .form .form-group").removeClass("has-error");
		$(".remote_panel").show();	// remote
	});
	
	// delete
	$("#groupConfigList").on('click', '.delete',function() {
		var attrName = $(this).parent('p').attr("attrName");
		ComConfirm.show("确认删除该配置?", function(){
			$.ajax({
				type : 'POST',
				url : base_url + "/config/delete",
				data : {
					"attrName" : attrName,
    				"groupName" : groupName
				},
				dataType : "json",
				success : function(data){
					if (data.code == 200) {
						ComAlert.show(1, "删除成功", function(){
							groupConfigListTable.fnDraw();
						});
					} else {
						ComAlert.show(2, (data.msg || "删除失败") );
					}
				},
			});
		});
	});
	
	// 更新
	//选择配置名称 加载配置属性名称
	$("#configNameE").change(function () {
		var $this = $(this);
		$.ajax({
			type : 'POST',
			url : base_url + "/config/getAttrNames",  
			async: false,
			data : {
				configName : $this.val()
			},
			success : function(data, status) {
				$("#attrNameE").empty();
				for(var i=0;i<data.length;i++){
					$("#attrNameE").append("<option value='"+data[i].attrName+"'>"+data[i].attrDes+"</option>");
				}
				$('#addModal').modal('hide');
    			
			}
		});
	});
	$("#groupConfigList").on('click', '.update',function() {
		// base data
		$("#configNameE").val($(this).parent('p').attr("configName"));
		$("#configNameE").trigger('change');
		$("#updateGroupConfigModal .form input[name='attrValueE']").val($(this).parent('p').attr("attrValue"));

		var attrName = $(this).parent('p').attr("attrName");
		$("#attrNameE").val(attrName);
		
	
		// show
		$('#updateGroupConfigModal').modal({backdrop: false, keyboard: false}).modal('show');
	});
	var updateModalValidate = $("#updateGroupConfigModal .form").validate({
		errorElement : 'span',  
        errorClass : 'help-block',
        focusInvalid : true,  
        rules : {
        	attrValueE : {
            	required : true
            }
        }, 
        messages : {
        	attrValueE : {
            	required :"请输入“属性值”"
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

        	if($('#attrNameE').find("option:selected").text().indexOf('是否') != -1){
        		if($('#attrValueE').val() != 'true' && $('#attrValueE').val() != 'false'){
        			ComAlert.show(2, "参数值输入错误" );
        			return false;
        		}
        	}
        	$.post(base_url + "/config/update",  
    			{
        			attrName : $('#attrNameE').val(),
        			attrValue : $('#attrValueE').val(),
    				groupName : groupName
    			},
    			function(data, status) {
	    			if (data.code == "200") {
						$('#updateGroupConfigModal').modal('hide');
						setTimeout(function () {
							ComAlert.show(1, "修改成功", function(){
								groupConfigListTable.fnDraw();
							});
						}, 115);
	    			} else {
						ComAlert.show(2, (data.msg || "修改失败") );
	    			}
	    	});
			}
	});
	$("#updateGroupConfigModal").on('hide.bs.modal', function () {
		$("#updateGroupConfigModal .form")[0].reset();
		updateModalValidate.resetForm();
		$("#updateGroupConfigModal .form .form-group").removeClass("has-error");
		$(".remote_panel").show();	// remote
	});
	bindTypeRadios();
});

function bindTypeRadios() {
    $("#Jhds1").on("click", function(e) {
    	$("#groupConfigListDiv").hide();
    	$("#apiGroupConfigListDiv").hide();
    	$("#documentListDiv").show();
    	
    });
	
    $("#Jhds2").on("click", function(e) {
    	$("#documentListDiv").hide();
    	$("#apiGroupConfigListDiv").hide();
    	$("#groupConfigListDiv").show();
    });
    
    $("#Jhds3").on("click", function(e) {
    	$("#documentListDiv").hide();
    	$("#groupConfigListDiv").hide();
    	$("#apiGroupConfigListDiv").show();
    	apiConfigListTable.fnDraw();
    });
}