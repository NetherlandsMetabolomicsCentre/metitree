<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

		<script language="JavaScript">
			<!-- Begin
			function checkAll(field){ for (i = 0; i < field.length; i++) field[i].checked = true ; }
			function uncheckAll(field){ for (i = 0; i < field.length; i++) field[i].checked = false ; }
			//  End -->
		</script>

    <meta name="layout" content="metitree" />
  </head>
  <body>
	<div class="thin_green_border">
		<h2><g:message code="page.process.jobactivity" />:</h2>
		<div class="bordered">
			<g:render template="/common/running_jobs" />
		</div>

		<h2><g:message code="page.process.selecttoprocess" />:</h2>
		<table width="100%" border="0">
			<tr>
				<td valign="top" style="padding: 0px 10px;">
					<div class="bordered" style="height: 100%;">
						<b><g:message code="page.process.onefileordirectory" /></b><br /><br />
						<!-- Choose a directory to select files from -->
						<g:form method="POST" controller="page" action="process" name="select_directory">
							<g:select style="width: 350px; background: #fff;" value="${directorySelected?.id}" name="directorySelected" from="${directories}" optionKey="id" optionValue="name" onchange="this.form.submit()" />
							<g:submitButton name="submit" value="select file(s) from directory to process" /> or <g:submitButton name="submit" value="process directory" />
						</g:form>
					</div>

					<g:if test="${directorySelected != null}">
						<!-- A directory was selected, show file out of which you can choose one or more -->
						<div class="bordered">
							<b><g:message code="page.process.filesindirectory" /> <i>${directorySelected?.name}</i></b>

								<g:form controller="msnfile" action="settings" name="process_msnfiles" method="POST">
								    <g:submitButton name="submit" value="Process files" />
								    <table style="padding: 5px;" width="100%">
								            <tr>
							                    <th width="150px"><input style="font-size:1em; width:20px;" type=button name="CheckAll" value="+" onClick="checkAll(document.process_msnfiles.files)"><input style="font-size:1em; width:20px;" type=button name="UnCheckAll" value="-" onClick="uncheckAll(document.process_msnfiles.files)"></th>
							                    <th nowrap><g:message code="common.label.file" /></th>
							                    <th nowrap><g:message code="common.label.comments" /></th>
							                    <th nowrap><g:message code="common.label.created" /></th>
								            </tr>

										<g:each in="${directorySelected.toplevelmsnfiles.sort { a,b -> a.name <=> b.name }}" var="msnfile">

								            <tr>
												<td valign="top" nowrap style="padding: 0 5px;" align="middle" width="150px"><input type="checkbox" name="files" value="${msnfile.id}" /></td>
							                    <td valign="top" nowrap style="padding: 0 5px;">${msnfile?.name}</td>
							                    <td valign="top" width="100%" style="padding: 0 5px;">${msnfile?.comments}</td>
							                    <td valign="top" nowrap style="padding: 0 5px;"><g:formatDate format="yyyy-MM-dd" date="${msnfile.dateCreated}"/></td>
								            </tr>
								    	</g:each>
								    </table>
								    <g:submitButton name="submit" value="Process files" />
						    	</g:form>
						</div>
					</g:if>
				</td>
			</tr>
		</table>

		<h2><g:message code="page.process.jobhistory" />:</h2>
		<div class="bordered">

			<table width="100%" border="0">
				<tr>
					<th></th>
					<th><g:message code="common.label.name" /></th>
					<th><g:message code="common.label.files" /></th>
					<th><g:message code="common.label.created" /></th>
					<th></th>
					<th></th>
				</tr>
			<g:each in="${processjobs.sort { a,b -> b.dateCreated <=> a.dateCreated }}" var="${processjob}">
				<g:if test="${processjob.active}">
					<tr>
						<td nowrap valign="top" align="right" style="padding: 0px; width: 25px;"><g:link controller="processJob" action="details" id="${processjob.id}"><img class="borderless" width="20px" src="${resource(dir:'images',file:'view.png')}" alt="view" /></g:link></td>
						<td nowrap valign="top" style="padding: 0px 10px;">${processjob?.name}</td>
						<td width="100%" valign="top" style="padding: 0px 10px;">
							<g:each status="i" in="${processjob.msnfiles}" var="msnfile">
								<dir:downloadMsnFile msnfile="${msnfile}" label="${msnfile?.name}" />
								<g:if test="${processjob.msnfiles.size()-1 > i}">,</g:if>
							</g:each>
						</td>
						<td nowrap valign="top" style="padding: 0px 10px;"><g:formatDate format="yyyy-MM-dd" date="${processjob.dateCreated}"/></td>
						<td valign="top" nowrap align="right" style="padding: 0px; width: 25px;">
							<g:if test="${processjob.lastStatus != 1}">
								<g:link controller="processJob" action="deactivate" id="${processjob.id}"><img class="borderless" width="20px" src="${resource(dir:'images',file:'delete.png')}" alt="delete" /></g:link>
							</g:if>
							<g:else>
								<img class="borderless" width="20px" src="${resource(dir:'images',file:'cancel_grey.png')}" alt="delete" />
							</g:else>
						</td>
						<td width="100%"></td>
					</tr>
				</g:if>
			</g:each>
			</table>
		</div>
	</div>

  </body>
</html>
