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
	<@netCommon.commonLeft "normConfigList" />

	<!-- Content Wrapper. Contains page content -->
	<div class="content-wrapper">
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<h1>榜单管理<small>开放数据管理平台</small></h1>
		</section>

		<!-- Main content -->
	    <section class="content">

            <div class="row">
                <div class="col-xs-4">
                    <div class="input-group">
                        <span class="input-group-addon">榜单名称</span>
                        <input type="text" class="form-control" id="name" autocomplete="on" >
                    </div>
                </div>
                <div class="col-xs-2">
                    <button class="btn btn-block btn-info" id="search">搜索</button>
                </div>
                <div class="col-xs-2 pull-right">
                    <button class="btn btn-block btn-success" type="button" id="add" >+新增榜单</button>
                </div>
            </div>

			<div class="row">
				<div class="col-xs-12">
                    <div class="box">
                        <!-- /.box-header -->
                        <div class="box-body">
                            <table id="normConfig_list" class="table table-bordered table-striped">
                                <thead>
									<tr>
										<th>ID</th>
										<th>榜单名称</th>
										<th>榜单类型</th>
										<th>所需MQ的topic</th>
										<th>所需MQ的tag</th>
										<th>榜单算法</th>
										<th>所加入的任务名</>
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
            	<h4 class="modal-title" >新增榜单</h4>
         	</div>
         	<div class="modal-body">
				<form class="form-horizontal form" role="form" >
					<div class="form-group">
                        <label for="normName" class="col-sm-4 control-label">榜单名称<font color="red">*</font></label>
                        <div class="col-sm-8"><input type="text" class="form-control" name="normName" placeholder="请输入“榜单名称”" maxlength="50" ></div>
					</div>
                    <div class="form-group">
                        <label for="normType" class="col-sm-4 control-label">榜单类型<font color="red">*</font></label>
                        <div class="col-sm-8"><input type="text" class="form-control" name="normType" placeholder="请输入“榜单类型”" maxlength="200" ></div>
                    </div>
					<div class="form-group">
                        <label for="topic" class="col-sm-4 control-label">所需MQ的topic<font color="red">*</font></label>
                        <div class="col-sm-8"><input type="text" class="form-control" id="topic" name="topic" placeholder="请输入“topic”" maxlength="50" ></div>
					</div>
                    <div class="form-group">
                        <label for="tags" class="col-sm-4 control-label">所需MQ的tag<font color="red">*</font></label>
                        <div class="col-sm-8"><input type="text" class="form-control" name="tags" placeholder="请输入“tag”" maxlength="200" ></div>
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

<!-- 更新.模态框 -->
<div class="modal fade" id="updateModal" tabindex="-1" role="dialog"  aria-hidden="true">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
            	<h4 class="modal-title" >更新榜单</h4>
         	</div>
         	<div class="modal-body">
                <form class="form-horizontal form" role="form" >
					<div class="form-group">
						<input type="hidden"  id="normIdE" name="normIdE" >
                        <label for="normNameE" class="col-sm-4 control-label">榜单名称<font color="red">*</font></label>
                        <div class="col-sm-8"><input type="text" class="form-control" id="normNameE" name="normNameE" placeholder="请输入“榜单名称”" maxlength="50" ></div>
					</div>
                    <div class="form-group">
                        <label for="normTypeE" class="col-sm-4 control-label">榜单类型<font color="red">*</font></label>
                        <div class="col-sm-8"><input type="text" class="form-control" id="normTypeE" name="normTypeE" placeholder="请输入“榜单类型”" maxlength="200" ></div>
                    </div>
					<div class="form-group">
                        <label for="topicE" class="col-sm-4 control-label">所需MQ的topic<font color="red">*</font></label>
                        <div class="col-sm-8"><input type="text" class="form-control" id="topicE" name="topicE" placeholder="请输入“topic”" maxlength="50" ></div>
					</div>
                    <div class="form-group">
                        <label for="tagsE" class="col-sm-4 control-label">所需MQ的tag<font color="red">*</font></label>
                        <div class="col-sm-8"><input type="text" class="form-control" id="tagsE" name="tagsE" placeholder="请输入“tag”" maxlength="200" ></div>
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

<script src="/js/normConfig.list.1.js"></script>
</body>
</html>
