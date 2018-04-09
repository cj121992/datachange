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
	<@netCommon.commonLeft "workList" />

	<!-- Content Wrapper. Contains page content -->
	<div class="content-wrapper">
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<h1>worker管理<small>开放数据管理平台</small></h1>
		</section>

		<!-- Main content -->
	    <section class="content">

            <div class="row">
                <div class="col-xs-4">
                    <div class="input-group">
                        <span class="input-group-addon">worker名称</span>
                        <input type="text" class="form-control" id="name" autocomplete="on" >
                    </div>
                </div>
                <div class="col-xs-2">
                    <button class="btn btn-block btn-info" id="search">搜索</button>
                </div>
            </div>

			<div class="row">
				<div class="col-xs-12">
                    <div class="box">
                        <!-- /.box-header -->
                        <div class="box-body">
                            <table id="worker_list" class="table table-bordered table-striped">
                                <thead>
									<tr>
										<th>jobId</th>
										<th>worker名称</th>
										<th>运行的任务类型</th>
										<th>运行的任务名称</th>
										<th>worker状态</th>
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

<!-- 更新.模态框 -->
<div class="modal fade" id="updateModal" tabindex="-1" role="dialog"  aria-hidden="true">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
            	<h4 class="modal-title" >更新worker</h4>
         	</div>
         	<div class="modal-body">
                <form class="form-horizontal form" role="form" >
                    <div class="form-group">
                        <label for="workerName" class="col-sm-4 control-label">worker名称<font color="red">*</font></label>
                        <div class="col-sm-8"><input type="text" class="form-control" id="workerName" name="workerName" placeholder="请输入“worker名称”" maxlength="50" ></div>
                    </div>
                    <div class="form-group">
                        <label for="jobType" class="col-sm-4 control-label">任务类型<font color="red">*</font></label>
                    	<div class="col-sm-8">
	                        <select class="form-control select2_tag_new key" id="jobType" name="jobType" >
	                        	<option value=''></option>
	                        	<option value='norm'>榜单任务</option>
                        		<option value='timed_update'>定时任务</option>
	                        </select>
                        </div>    
                    </div>
                    <div class="form-group">
                        <label for="jobName" class="col-sm-4 control-label">任务名称<font color="red">*</font></label>
                        <div class="col-sm-8">
	                        <select class="form-control select2_tag_new key" id="jobName" name="jobName" >
	                        	<option value=''></option>
	                        </select>
                        </div>  
                    </div>
                    <div class="form-group">
                        <label for="status" class="col-sm-4 control-label">状态变更<font color="red">*</font></label>
                        <div class="col-sm-8">
	                        <select class="form-control select2_tag_new key" id="status" name="status" >
	                        </select>
                        </div>  
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

<script src="/js/worker.list.1.js"></script>
</body>
</html>
