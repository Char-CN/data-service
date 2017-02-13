<%@page import="java.util.concurrent.ConcurrentLinkedQueue"%>
<%@page import="org.blazer.scheduler.util.DateUtil"%>
<%@page import="org.blazer.scheduler.core.JobServer"%>
<%@page import="org.blazer.scheduler.core.TaskServer"%>
<%@page import="org.blazer.scheduler.model.ProcessModel"%>
<%@page import="java.io.PrintWriter"%>
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="shortcut icon" type="image/ico" href="../images/favicon.png">
<link href="../css/jquery.dataTables.min.css" rel="stylesheet">
<script type="text/javascript" src="../js/jquery.min.js"></script>
<script type="text/javascript" src="../js/jquery.easyui.commons.js"></script>
<title></title>
</head>
<body style="padding: 0px; margin: 0px;">
<div>正在执行的任务个数：<c:out value="${TaskServer.taskSize()}"/></div>
<c:forEach var="vv" items="" varStatus="TaskServer.tasks()">
<c:out value="${vv}"/>
</c:forEach>
<div>下一分钟的进程个数：<%=(TaskServer.getTimeToProcess(DateUtil.newDateStrNextMinute()) == null ? 0 : TaskServer.getTimeToProcess(DateUtil.newDateStrNextMinute()).size()) %></div>
<c:if test="${DateUtil.newDateStrNextMinute() != null}">
	<c:forEach var="item" items="${TaskServer.getTimeToProcess(DateUtil.newDateStrNextMinute())}" varStatus="vv">
	${item}<br><br>
	</c:forEach>
</c:if>
<!-- 	<table style="width:100%"> -->
<!-- 		<tr> -->
<!-- 			<td style="width:50%">indexTaskNameToTask</td> -->
<%-- 			<td style="width:50%"><c:out value="${TaskServer.taskSize()}" /></td> --%>
<!-- 		</tr> -->
<!-- 	</table> -->
	<%--
		PrintWriter o = response.getWriter();
		o.println("indexTaskNameToTask        size : " + TaskServer.taskSize());
		for (ProcessModel pm : TaskServer.tasks()) {
			o.println("                | process model : " + pm);
		}
		o.println("waitSpawnTaskJobIdQueue    size : " + JobServer.waitSpawnSize());
		for (Integer jobId : JobServer.waitSpawns()) {
			o.println("                |     job model : " + JobServer.getJobById(jobId));
		}
		String nextMinuteTime = DateUtil.newDateStrNextMinute();
		ConcurrentLinkedQueue<ProcessModel> nextMinuteTimeProcessQueue = TaskServer.getTimeToProcess(nextMinuteTime);
		o.println("nextMinuteTimeProcessQueue size : " + (nextMinuteTimeProcessQueue == null ? 0 : nextMinuteTimeProcessQueue.size()));
		if (nextMinuteTimeProcessQueue != null) {
			for (ProcessModel pm : nextMinuteTimeProcessQueue) {
				o.println("                | process model : " + pm);
			}
		}
		o.println("jobIdToJobMap              size : " + JobServer.jobSize());
	--%>
</body>
</html>
