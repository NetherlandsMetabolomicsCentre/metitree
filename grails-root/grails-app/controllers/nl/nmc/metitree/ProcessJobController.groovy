package nl.nmc.metitree

class ProcessJobController {

    def details = {	
		def processJob = ProcessJob.get(params.id)
		
		if (!processJob || processJob.member.usergroup.id != session.member.usergroup.id){
			//return to where you came from...
			redirect(controller: 'page')
		}
		
		[ processjob: processJob ]			
	}
	
	def runningjobs = {
		
		def out = ""
					
		def processJobsRunning = ProcessJob.findAllByLastStatus(1)

		out += '<div style="padding:10px; margin:5px;">'
		if (processJobsRunning){
			out += processJobsRunning.size() + ' job(s) running: '
			processJobsRunning.eachWithIndex { job, i ->
				if (job.member.id == session.member.id){
					out += g.link(action:"details", controller:"processJob", id: job.id) { job.name }
				} else {
					out += "<i><font color=\"#dcdcdc\">${job.name}</font></i>"
				}
				if (i < (processJobsRunning.size()-1)) { out += ", " }
			}
		} else {
			out += '0 job(s) running'
		}
			
		def activeProcessJobsWaiting = ProcessJob.findAll("	from ProcessJob as pj \
															where pj.lastStatus 	= :laststatus \
																AND pj.active 		= :active",
															[laststatus: 0, active: true]
														  )
		
		if (activeProcessJobsWaiting){
			out += '<br />' + activeProcessJobsWaiting.size() + ' job(s) waiting: '
			activeProcessJobsWaiting.eachWithIndex { job, i ->
				if (job.active){
					if (job.member.id == session.member.id){
						out += g.link(action:"details", controller:"processJob", id: job.id) { job.name }
					} else {
						out += "<i><font color=\"#dcdcdc\">${job.name}</font></i>"
					}
					if (i < (activeProcessJobsWaiting.size()-1)) { out += ", " }
				}
			}
		} else {
			out += '<br />0 job(s) waiting'
		}
		out += '</div>'
		
		render ("${out}")
    }
				
	
	def processjobstatus = {
		
		def out = ""
					
		def processJob = ProcessJob.get(params.id)
		def compound = null
				
		if (processJob && processJob.member.usergroup.id == session.member.usergroup.id){
			out += "<h2>"
			out += "Status ${processJob.name} ("
			if (processJob.lastStatus == 0){
				out += "...new!"
			} else if (processJob.lastStatus == 1) {
				out += "<img style=\"borderless\" src=\"${resource(dir:'images',file:'spinner.gif')}\" alt=\"metItreeDB\" /> running..."			
			} else {
				out += "...finished!"
			}
			out += ")</h2>"
			out += "<br />"
					
			/*
			 * Job Files
			 */
			if (processJob.msnfiles){
								
				out += "<h3>Output</h3>"
				out += "<table width=\"100%\" style=\"padding-right: 15px;\" border=\"0\">"
				out += "	<tr>"
				out += "		<th valign=\"bottom\" nowrap>name</th>"
				out += "		<th valign=\"bottom\" nowrap>compound</th>"
				out += "		<th valign=\"bottom\" nowrap>mzxml " + tt.simple(code:'MzXML file format is the original imported data. It contains the mass spectral data.') + "</th>"
				out += "		<th valign=\"bottom\" nowrap>cml " + tt.simple(code:'Cml file format is a Chemical Markup Language. It contains the fragmentation tree data.') + "</th>"
				out += "		<th valign=\"bottom\" nowrap>export (csv/tab)" + tt.simple(code:'Tab is the Space Separated Value file format and csv is the Comma Separated Value file format</div>') + "</th>"
				out += "		<th valign=\"bottom\" nowrap>visuals" + tt.simple(code:'The spectral tree viewer and PDF is the portable document format file') + "</th>"
				out += "		<th valign=\"bottom\" nowrap>created</th>"
				out += "		<th valign=\"bottom\" nowrap></th>"
				out += "	</tr>"
				processJob.msnfiles.sort{ a,b -> b.compound?.id <=> a.compound?.id }.collect{ it }.each { msnfile ->											
										
					//see if there are files produced from the original mzXML
					def outputFiles = [:]
					
					if (msnfile?.children){
						//xcms
						msnfile.getChildren(processJob).each { xcmsFile ->
							outputFiles['xcms'] = xcmsFile
							
							//cml
							xcmsFile.getChildren(processJob).each { cmlFile ->
								outputFiles['cml'] = cmlFile
								//json and json.master
								cmlFile.getChildren(processJob).each { jsonFile ->
									if (jsonFile.name.contains('master')){
										outputFiles['jsonMaster'] = jsonFile
									} else {
										outputFiles['jsonRepetitions'] = jsonFile
									}
									//csv and tab
									jsonFile.getChildren(processJob).each { exportFile ->
										if (exportFile.name.contains('tab')){
											outputFiles['tab'] = exportFile
										}
										if (exportFile.name.contains('csv')){
											outputFiles['csv'] = exportFile
											exportFile.getChildren(processJob).each { pdfFile ->
												if (pdfFile.name.contains('pdf')){
													outputFiles['pdf'] = pdfFile
												}
											}
										}
									}
								}
							}
						}
					}
					
					//display the output
					out += "<tr>"
					out += "	<td valign=\"bottom\" nowrap>" + dir.downloadMsnFile(msnfile: msnfile, label: msnfile?.name) + "</td>"
					out += "<td valign=\"bottom\" nowrap>"
					if(msnfile.compound()?.inchi){
						out += "${msnfile.compound()?.name}"
					}
					out += "</td>"
					
					if (msnfile?.location && (new File(msnfile.location as String).exists())){
						out += "	<td nowrap>" + dir.downloadMsnFile(msnfile: msnfile, label: "<img class=\"borderless\" width=\"17px\" src=\"${resource(dir:'images',file:'page_white_code.png')}\" title=\"mzxml\" alt=\"mzxml\" />") + "</td>"
					} else if (processJob.lastStatus > 1){
						out += "	<td nowrap><img class=\"borderless\" width=\"17px\" src=\"${resource(dir:'images',file:'error.png')}\" title=\"error: was unable to process this file.\" alt=\"error: was unable to process this file.\" /></td>"
					} else {
						out += "	<td nowrap> </td>"
					}
					
					if (outputFiles['cml']?.location && (new File(outputFiles['cml'].location as String).exists())){
						out += "	<td nowrap>" + dir.downloadMsnFile(msnfile: outputFiles['cml'], label: "<img class=\"borderless\" width=\"17px\" src=\"${resource(dir:'images',file:'page_white_code.png')}\" title=\"cml\" alt=\"cml\" />") + "</td>"
					} else if (processJob.lastStatus > 1){
						out += "	<td nowrap><img class=\"borderless\" width=\"17px\" src=\"${resource(dir:'images',file:'error.png')}\" title=\"error: was unable to process this file.\" alt=\"error: was unable to process this file.\" /></td>"
					} else {
						out += "	<td nowrap> </td>"
					}
					
					out += "	<td nowrap>" + dir.downloadMsnFile(msnfile: outputFiles['csv'], label: "<img class=\"borderless\" width=\"17px\" src=\"${resource(dir:'images',file:'table.png')}\" title=\"csv\" alt=\"csv\" />") + " " + dir.downloadMsnFile(msnfile: outputFiles['tab'], label: "<img class=\"borderless\" width=\"17px\" src=\"${resource(dir:'images',file:'text_align_justify.png')}\" title=\"tab\" alt=\"tab\" />") + "</td>"
					out += "	<td nowrap>"
					if (outputFiles['jsonMaster']?.location && (new File(outputFiles['jsonMaster'].location as String).exists())){
						out += g.link(action:"preview", controller:"msnfile", id: outputFiles['jsonMaster'].id) { "<img class=\"borderless\" width=\"17px\" src=\"${resource(dir:'images',file:'chart_organisation.png')}\" title=\"tree\" alt=\"tree\" />" } + " " + dir.downloadMsnFile(msnfile: outputFiles['pdf'], label: "<img class=\"borderless\" width=\"17px\" src=\"${resource(dir:'images',file:'pdf.gif')}\" title=\"pdf\" alt=\"pdf\" />")
					}
					out += "</td>"
					out += "	<td valign=\"bottom\" nowrap>" + g.formatDate(format:"yyyy-MM-dd", date:msnfile.dateCreated) + "</td>"
					out += "	<td width=\"100%\">&nbsp;</td>"
					out += "</tr>"
				}
				out += "</table>"
				out += "<br />"
			}
			
			/*
			* Job Settings
			*/
		   if (processJob?.settings){
			   out += "<h3>Settings</h3>"
			   out += "<table border=\"0\">"
			   out += "	<tr>"
			   out += "		<th></th>"
			   out += "		<th>setting</th>"
			   out += "		<th></th>"
			   out += "		<th>value</th>"
			   out += "	</tr>"
			   processJob.settings.settingsmap.each { smap ->
				   out += "<tr>"
				   out += "	<td></td>"
				   out += "	<td nowrap>${smap.key}</td>"
				   out += "	<td>&nbsp;</td>"
				   out += "	<td nowrap>${smap.value}</td>"
				   out += "</tr>"
			   }
			   out += "</table>"
		   }
				
			/*
			* Job Log
			*/
		   def outLog = ""
				   
		   if (processJob.report != ""){
			   outLog += processJob.report?.replace('ERROR:','<font color="red"><b>ERROR:</b></font>') ?: ""
			   outLog = "<pre>${outLog}</pre>"
			   out += "<br />"
			   out += "<h3>Job Log</h3>${outLog}"
		   }
			
			render ("${out}")
		} else {
			render ("please wait...")
		}
	}
	
	def deactivate = {
		def processJob = ProcessJob.findByIdAndMember(params.id, session.member)
		if (processJob?.lastStatus != 1){ //cannot delete a running job. That would violate the transaction of the Jobrunner
			processJob.active = false
			processJob.save()
		}
			
		//return to where you came from...
		redirect(url: request.getHeader('Referer'))
    }
}