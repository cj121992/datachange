<!DOCTYPE html>
<html>
<head>
  	<title>开放数据管理平台</title>
  	<#import "/common/common.macro.ftl" as netCommon>
	<@netCommon.commonStyle />
	<!-- DataTables -->
  	<link rel="stylesheet" href="/adminlte/plugins/datatables/dataTables.bootstrap.css">

</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && "off" == cookieMap["adminlte_settings"].value >sidebar-collapse</#if>">
<div class="wrapper">
	<!-- header -->
	<@netCommon.commonHeader />
	<!-- left -->
	<@netCommon.commonLeft "normJobConfigList" />

	<!-- Content Wrapper. Contains page content -->
	<div class="content-wrapper">
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<h1>${normName }字段配置</h1>
		</section>

		<!-- Main content -->
	    <section class="content">

            <div class="row">
                <div class="col-xs-4">
                    <div class="input-group">
                        <span class="input-group-addon">字段名称</span>
                        <input type="text" class="form-control" id="name" autocomplete="on" >
                    </div>
                </div>
                <div class="col-xs-2">
                    <button class="btn btn-block btn-info" id="search">搜索</button>
                </div>
                <div class="col-xs-2 pull-right">
                    <button class="btn btn-block btn-success" type="button" id="add" >+新增字段配置</button>
                </div>
            </div>

			<div class="row">
				<div class="col-xs-12">
                    <div class="box">
                        <!-- /.box-header -->
                        <div class="box-body">
                            <table id="normFieldConfig_list" class="table table-bordered table-striped">
                                <thead>
									<tr>
										<th>fieldId</th>
										<th>字段类型</th>
										<th>字段名称</th>
										<th>字段算法</th>
										<th>字段匹配值</th>
										<th>所需字段路径</th>
                                        <th>操作</th>
									</tr>
                                </thead>
                            </table>
                        </div>
                        <!-- /.box-body -->
                    </div>
                    <!-- /.box -->

				</div>
			</div>
	    </section>
	</div>

	<!-- footer -->
	<@netCommon.commonFooter />
</div>

<!-- 新增.模态框 -->
<div class="modal fade" id="addModal" tabindex="-1" role="dialog"  aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
            	<h4 class="modal-title" >新增字段</h4>
         	</div>
         	<div class="modal-body">
				<form class="form-horizontal form" role="form" >
				
                    <div class="form-group">
                        <label for="fieldType" class="col-sm-4 control-label">字段类型<font color="red">*</font></label>
                        <div class="col-sm-8">
	                        <select class="form-control select2_tag_new key" id="fieldType" name="fieldType" >
	                        	<option value='0'>生成榜单名称的Target字段</option>
	                        	<option value='1'>榜单列表GroupBy字段</option>
	                        	<option value='2'>生成榜单数据的条件</option>
	                        	<option value='3'>Date类型的时间字段</option>
	                        	<option value='4'>String类型的时间字段</option>
	                        	<option value='5'>timestamp类型的时间字段</option>
	                        	<option value='6'>榜单排序算法所需字段</option>
	                        </select>
						</div>
                    </div>
					<div class="form-group">
                        <label for="fieldName" class="col-sm-4 control-label">字段名称<font color="red">*</font></label>
                        <div class="col-sm-8"><input type="text" class="form-control" name="fieldName" placeholder="请输入“字段名称”" maxlength="50" ></div>
					</div>
					<div id="algorithFieldDiv">
						<div class="form-group">
	                        <label for="fieldAlgorithm" class="col-sm-4 control-label">字段算法<font color="black">*</font></label>
							<div class="col-sm-8">
		                        <select class="form-control select2_tag_new key" id="fieldAlgorithm" name="fieldAlgorithm" >
		                        	<option value=''></option>
		                        	<option value='count'>count</option>
		                        	<option value='sum'>sum</option>
		                        </select>
							</div>
						</div>
					</div>
					<div id="matchFieldDiv">
						<div class="form-group">
	                        <label for="fieldMatchValue" class="col-sm-4 control-label">字段匹配值<font color="black">*</font></label>
	                        <div class="col-sm-8"><input type="text" class="form-control" name="fieldMatchValue" placeholder="请输入“字段匹配值”" maxlength="50" ></div>
						</div>
						<div class="form-group">
							<label for="fieldAlgorithm" class="col-sm-4 control-label">条件连接算法<font color="black">*</font></label>
	                        <select class="form-control select2_tag_new key" id="fieldAlgorithm" name="fieldAlgorithm" >
		                        	<option value='' select></option>
		                        	<option value='and' select>and</option>
		                        	<option value='or'>or</option>
		                    </select>
		                </div>
					</div>
                    <div class="form-group">
                        <label for="fieldPath" class="col-sm-4 control-label">字段路径<font color="red">*</font></label>
                        <div class="col-sm-8"><input type="text" class="form-control" name="fieldPath" placeholder="请输入“字段路径”" maxlength="200" ></div>
                    </div>

					<input type="hidden"  id="normId" name="normId" value="${normId}">
					<div class="form-group">
						<div class="col-sm-offset-3 col-sm-6">
							<button type="submit" class="btn btn-primary"  >保存</button>
							<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
						</div>
					</div>
				</form>
         	</div>
		</div>
	</div>
</div>

<!-- 更新.模态框 -->
<div class="modal fade" id="updateModal" tabindex="-1" role="dialog"  aria-hidden="true">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
            	<h4 class="modal-title" >更新任务</h4>
         	</div>
         	<div class="modal-body">
                <form class="form-horizontal form" role="form" >
                
					<input type="hidden"  id="fieldIdE" name="fieldIdE" >
                    <div class="form-group">
                        <label for="fieldTypeE" class="col-sm-4 control-label">字段类型<font color="red">*</font></label>
                        <div class="col-sm-8">
	                        <select class="form-control select2_tag_new key" id="fieldTypeE" name="fieldTypeE" disabled="true">
	                        	<option value='0'>生成榜单名称的Target字段</option>
	                        	<option value='1'>榜单列表GroupBy字段</option>
	                        	<option value='2'>生成榜单数据的条件</option>
	                        	<option value='3'>Date类型的时间字段</option>
	                        	<option value='4'>String类型的时间字段</option>
	                        	<option value='5'>timestamp类型的时间字段</option>
	                        	<option value='6'>榜单排序算法所需字段</option>
	                        </select>
						</div>
                    </div>
					<div class="form-group">
                        <label for="fieldNameE" class="col-sm-4 control-label">字段名称<font color="red">*</font></label>
                        <div class="col-sm-8"><input type="text" class="form-control" id="fieldNameE" name="fieldNameE" placeholder="请输入“字段名称”" maxlength="50" ></div>
					</div>
					<div id="algorithFieldDivE">
						<div class="form-group">
	                        <label for="fieldAlgorithmE" class="col-sm-4 control-label">字段算法<font color="black">*</font></label>
							<div class="col-sm-8">
		                        <select class="form-control select2_tag_new key" id="fieldAlgorithmE" name="fieldAlgorithmE" >
		                        	<option value='' select></option>
		                        	<option value='count'>count</option>
		                        	<option value='sum'>sum</option>
		                        </select>
							</div>
						</div>
					</div>
					<div id="matchFieldDivE">
						<div class="form-group">
	                        <label for="fieldMatchValueE" class="col-sm-4 control-label">字段匹配值<font color="black">*</font></label>
	                        <div class="col-sm-8"><input type="text" class="form-control" id="fieldMatchValueE" name="fieldMatchValueE" placeholder="请输入“字段匹配值”" maxlength="50" ></div>
						</div>
						<div class="form-group">
							<label for="fieldAlgorithmE" class="col-sm-4 control-label">条件连接算法<font color="black">*</font></label>
	                        <select class="form-control select2_tag_new key" id="fieldAlgorithmE" name="fieldAlgorithmE" >
		                        	<option value='and' select>and</option>
		                        	<option value='or'>or</option>
		                    </select>
		                </div>
					</div>
                    <div class="form-group">
                        <label for="fieldPathE" class="col-sm-4 control-label">字段路径<font color="black">*</font></label>
                        <div class="col-sm-8"><input type="text" class="form-control" id="fieldPathE" name="fieldPathE" placeholder="请输入“字段路径”" maxlength="200" ></div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-offset-3 col-sm-6">
                            <button type="submit" class="btn btn-primary"  >保存</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>

                            <input type="hidden" name="id" >
                        </div>
                    </div>
                </form>
         	</div>
		</div>
	</div>
</div>

<@netCommon.commonScript />
<!-- DataTables -->
<script src="/adminlte/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="/adminlte/plugins/datatables/dataTables.bootstrap.min.js"></script>
<script src="/plugins/jquery/jquery.validate.min.js"></script>

<script src="/js/normFieldConfig.list.1.js"></script>

</script>

<script script type="text/javascript">
	var normId = '${normId}';
</script>
</body>
</html>
