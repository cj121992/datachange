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
	<@netCommon.commonLeft "JobList" />

	<!-- Content Wrapper. Contains page content -->
	<div class="content-wrapper">
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<h1>schema管理<small>数据变更流管理平台</small></h1>
		</section>

		<!-- Main content -->
	    <section class="content">

            <div class="row">
                <div class="col-xs-4">
                    <div class="input-group">
                        <span class="input-group-addon">schema名称</span>
                        <input type="text" class="form-control" id="name" autocomplete="on" >
                    </div>
                </div>
                <div class="col-xs-2">
                    <button class="btn btn-block btn-info" id="search">搜索</button>
                </div>
                <div class="col-xs-2 pull-right">
                    <button class="btn btn-block btn-success" type="button" id="add" >+新增schema</button>
                </div>
            </div>

			<div class="row">
				<div class="col-xs-12">
                    <div class="box">
                        <!-- /.box-header -->
                        <div class="box-body">
                            <table id="schema_list" class="table table-bordered table-striped">
                                <thead>
									<tr>
										<th>schemaId</th>
										<th>schema名称(库名.表名)</th>
										<th>schema版本</th>
										<th>schema内容</th>
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
		<div class="modal-content" style="width: 720px;">
			<div class="modal-header">
            	<h4 class="modal-title" >新增schema</h4>
         	</div>
         	<div class="modal-body">
				<form class="form-horizontal form" role="form" >
					<div class="form-group">
                        <label for="schema_name" class="col-sm-4 control-label">schema名称<font color="red">*</font></label>
                        <div class="col-sm-8"><input type="text" class="form-control" id="schema_name" name="schema_name" placeholder="请输入“schema名称”" maxlength="50" ></div>
					</div>
					<div class="form-group">
                        <label for="version" class="col-sm-4 control-label">version<font color="red">*</font></label>
                        <div class="col-sm-8"><input type="text" class="form-control" id="version" name="version" placeholder="请输入“版本”" maxlength="50" ></div>
					</div>
                    <div class="form-group">
                        <label for="value" class="col-sm-4 control-label">schema内容<font color="red">*</font></label>
                        <div class="col-sm-8">
                        	
                        	<textarea id="value" name="value" rows="20" cols="71">
							</textarea>
                        </div>
					</div>
                    
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


	<@netCommon.commonScript />
<!-- DataTables -->
<script src="/adminlte/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="/adminlte/plugins/datatables/dataTables.bootstrap.min.js"></script>
<script src="/plugins/jquery/jquery.validate.min.js"></script>

<script src="/js/schema.list.1.js"></script>
</body>
</html>
