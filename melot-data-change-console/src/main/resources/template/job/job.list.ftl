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
                            <table id="job_list" class="table table-bordered table-striped">
                                <thead>
									<tr>
<<<<<<< HEAD:melot-data-change-console/src/main/resources/template/job/job.list.ftl
										<th>jobId</th>
										<th>任务名称</th>
										<th>任务类型</th>
										<th>任务匹配榜单</th>
										<th>任务详情</th>
										<th>创建时间</th>
										<th>更新时间</th>
                                        <th>操作</th>
=======
										<th>schemaId</th>
										<th>schema名称(库名.表名)</th>
										<th>schema版本</th>
										<th>schema内容</th>
>>>>>>> 模块添加:melot-data-change-console/target/classes/template/schema/schema.list.ftl
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
<<<<<<< HEAD:melot-data-change-console/src/main/resources/template/job/job.list.ftl
                        <label for="jobType" class="col-sm-4 control-label">任务类型<font color="red">*</font></label>
                        <div class="col-sm-8">
                        <select class="form-control select2_tag_new key" id="jobType" name="jobType" >
                        	<option value='' select></option>
                        	<option value='norm'>榜单任务</option>
                        	<option value='timed_update'>定时任务</option>
                        </select>
                        </div>
                    </div>
                    <div id="normConfigDiv">
						<div class="form-group">
	                        <label for="topic" class="col-sm-4 control-label">topic<font color="red">*</font></label>
	                        <div class="col-sm-8">
	                        <select class="form-control select2_tag_new key" id="topic" name="topic" >
	                        </select>
	                        </div>
	                    </div>
	                     <div class="form-group">
	                        <label for="tag" class="col-sm-4 control-label">tag<font color="red">*</font></label>
	                        <div class="col-sm-8">
	                        <select class="form-control select2_tag_new key" id="tag" name="tag" >
	                        </select>
	                        </div>
	                    </div>
	                    <div class="form-group">
	                        <label for="configIds" class="col-sm-4 control-label">子任务<font color="red">*</font></label>
	                        <div class="col-sm-8">
		                        <select class="form-control select2_tag_new key" id="configIds" multiple="multiple" name="configIds" >
		                        </select>
							</div>
	                    </div>
                    </div>
                    
                    <div id="timeJobDiv" style="display:none">
	                    <div class="form-group">
	                        <label for="dailyCron" class="col-sm-4 control-label">日榜定时任务Cron表达式</label>
	                        <div class="col-sm-8"><input type="text" class="form-control" id="dailyCron" name="dailyCron" placeholder="请输入“日榜定时任务Cron表达式”" maxlength="50" ></div>
						</div>
						<div class="form-group">
	                        <label for="weeklyCron" class="col-sm-4 control-label">周榜定时任务Cron表达式</label>
	                        <div class="col-sm-8"><input type="text" class="form-control" id="weeklyCron" name="weeklyCron" placeholder="请输入“周榜定时任务Cron表达式”" maxlength="50" ></div>
						</div>
						<div class="form-group">
	                        <label for="monthlyCron" class="col-sm-4 control-label">月榜定时任务Cron表达式</label>
	                        <div class="col-sm-8"><input type="text" class="form-control" id="monthlyCron" name="monthlyCron" placeholder="请输入“月榜定时任务Cron表达式”" maxlength="50" ></div>
						</div>
						<div class="form-group">
	                        <label for="yearlyCron" class="col-sm-4 control-label">年榜定时任务Cron表达式</label>
	                        <div class="col-sm-8"><input type="text" class="form-control" id="yearlyCron" name="yearlyCron" placeholder="请输入“年榜定时任务Cron表达式”" maxlength="50" ></div>
						</div>
=======
                        <label for="value" class="col-sm-4 control-label">schema内容<font color="red">*</font></label>
                        <div class="col-sm-8"><input type="text" class="form-control" id="value" name="value" placeholder="请输入“内容”" maxlength="50" ></div>
>>>>>>> 模块添加:melot-data-change-console/target/classes/template/schema/schema.list.ftl
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
            	<h4 class="modal-title" >更新任务</h4>
         	</div>
         	<div class="modal-body">
                <form class="form-horizontal form" role="form" >
					<input type="hidden"  id="jobIdE" name="jobIdE" >
                    <div class="form-group">
                        <label for="schema_name" class="col-sm-4 control-label">schema名称<font color="red">*</font></label>
                        <div class="col-sm-8"><input type="text" class="form-control" id="schema_name" name="schema_name" placeholder="请输入“schema名称”" maxlength="50" ></div>
					</div>
					<div class="form-group">
                        <label for="version" class="col-sm-4 control-label">version<font color="red">*</font></label>
                        <div class="col-sm-8"><input type="text" class="form-control" id="version" name="version" placeholder="请输入“版本”" maxlength="50" ></div>
					</div>
                    <div class="form-group">
<<<<<<< HEAD:melot-data-change-console/src/main/resources/template/job/job.list.ftl
                        <label for="jobTypeE" class="col-sm-4 control-label">任务类型<font color="red">*</font></label>
                        <div class="col-sm-8">
                        <select class="form-control select2_tag_new key" id="jobTypeE" name="jobTypeE" >
                        	<option value='' select></option>
                        	<option value='norm'>榜单任务</option>
                        	<option value='timed_update'>定时任务</option>
                        </select>
                        </div>
                    </div>
                    <div id="normConfigEDiv">
	                    <div class="form-group">
	                        <label for="topicE" class="col-sm-4 control-label">topic<font color="red">*</font></label>
	                        <div class="col-sm-8">
	                        <select class="form-control select2_tag_new key" id="topicE" name="topicE" >
	                        </select>
	                        </div>
	                    </div>
	                     <div class="form-group">
	                        <label for="tagE" class="col-sm-4 control-label">tag<font color="red">*</font></label>
	                        <div class="col-sm-8">
	                        <select class="form-control select2_tag_new key" id="tagE" name="tagE" >
	                        </select>
	                        </div>
	                    </div>
	                    <div class="form-group">
	                        <label for="configIdsE" class="col-sm-4 control-label">子任务<font color="red">*</font></label>
	                        <div class="col-sm-8">
		                        <select class="form-control select2_tag_new key" id="configIdsE" multiple="multiple" name="configIdsE" >
		                        </select>
							</div>
	                    </div>
                    </div>
                    <div id="timeJobEDiv">
	                    <div class="form-group">
	                        <label for="dailyCronE" class="col-sm-4 control-label">日榜统计任务的Cron表达式</label>
	                        <div class="col-sm-8"><input type="text" class="form-control" id="dailyCronE" name="dailyCronE" placeholder="请输入“日榜统计时间的Cron表达式”" maxlength="50" ></div>
						</div>
						<div class="form-group">
	                        <label for="weeklyCronE" class="col-sm-4 control-label">周榜统计任务的Cron表达式</label>
	                        <div class="col-sm-8"><input type="text" class="form-control" id="weeklyCronE" name="weeklyCronE" placeholder="请输入“日榜统计时间的Cron表达式”" maxlength="50" ></div>
						</div>
						<div class="form-group">
	                        <label for="monthlyCronE" class="col-sm-4 control-label">月榜统计任务的Cron表达式</label>
	                        <div class="col-sm-8"><input type="text" class="form-control" id="monthlyCronE" name="monthlyCronE" placeholder="请输入“日榜统计时间的Cron表达式”" maxlength="50" ></div>
						</div>
						<div class="form-group">
	                        <label for="yearlyCronE" class="col-sm-4 control-label">年榜统计任务的Cron表达式</label>
	                        <div class="col-sm-8"><input type="text" class="form-control" id="yearlyCronE" name="yearlyCronE" placeholder="请输入“日榜统计时间的Cron表达式”" maxlength="50" ></div>
						</div>
=======
                        <label for="value" class="col-sm-4 control-label">schema内容<font color="red">*</font></label>
                        <div class="col-sm-8"><input type="text" class="form-control" id="value" name="value" placeholder="请输入“内容”" maxlength="50" ></div>
>>>>>>> 模块添加:melot-data-change-console/target/classes/template/schema/schema.list.ftl
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

<script src="/js/schema.list.1.js"></script>
</body>
</html>
