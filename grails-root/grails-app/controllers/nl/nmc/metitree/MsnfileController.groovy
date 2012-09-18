package nl.nmc.metitree

import org.springframework.context.*
import grails.converters.*;
import org.codehaus.groovy.grails.commons.ConfigurationHolder

import net.sf.jniinchi.*;

class MsnfileController {

	def processService

	def config = ConfigurationHolder.config

	def download = {

		def msnFile = Msnfile.get(params.id)

		if (msnFile.member.usergroup.id == session.member.usergroup.id){

			def downloadFile = new File("${msnFile.location}")
			if (downloadFile.exists()){
				byte[] downloadBytes = downloadFile.readBytes()
				response.setContentType("application/download")
				response.setHeader("Content-disposition", "attachment; filename=${msnFile.name}")
				response.setContentLength(downloadBytes.length)
				response.getOutputStream().write(downloadBytes)
			} else {
				render "file not found."
			}
		} else {
			render 'access denied!'
		}
	}

	def add = {

		def uploadedfile = request.getFile('fileUpload')
		if (!uploadedfile.isEmpty()){

			//determine extension of uploaded file
			def filename = uploadedfile.getOriginalFilename().toLowerCase()
			def ext = filename.tokenize(".")[-1]

			// select correct way of processing the file based on extension
			switch (ext) {

				case 'zip' 		:	// uploaded zip
					def zipLocation = "/tmp/${UUID.randomUUID().toString()}"
					def zipLocationFile = new File("${zipLocation}")
					zipLocationFile.mkdirs()

					def zippedFile = new File("${zipLocation}/zippedFile.zip")
					zippedFile.setWritable(true, false) && zippedFile.canWrite()

					uploadedfile.transferTo(zippedFile)

					def ant = new AntBuilder()
					ant.unzip(
							src: zippedFile.canonicalPath,
							dest: zipLocation,
							overwrite: "true"
							)

				//loop over files from zip and add the mzxml files!!!
					zipLocationFile.eachFile{

						//determine extension of unzipped files
						def unzippedFilename = it.absolutePath.toLowerCase()
						def unzippedName = unzippedFilename.tokenize("/")[-1]
						def unzippedExt = unzippedName.tokenize(".")[-1]

						if (unzippedExt == 'mzxml'){
							def unzippedMsnFile = new Msnfile(member: session.member,name: "${unzippedName}", directory: Directory.get(params.directory),filename: "${UUID.randomUUID().toString()}.mzxml").save(flush: true)
							ant.copy(file : it, tofile : new File(unzippedMsnFile.location), overwrite : true)
						}
					}

					break

				case 'mzxml'	:	// uploaded mzXML file
					def newMsnFile = new Msnfile(member: session.member,name: filename,directory: Directory.get(params.directory),filename: "${UUID.randomUUID().toString()}.mzxml").save(flush: true)
					uploadedfile.transferTo(new File(newMsnFile.location))
					break

				default			:	//unsupported file!
					log.error("Unsupported filetype!")
			}
		}

		//return to where you came from...
		redirect(url: request.getHeader('Referer'))
	}

	def delete = {

		if (params.id){
			def msnfile = Msnfile.get(params.id)

			if (session?.member?.id == msnfile.member?.id){
				msnfile.active = false
			}
		}

		//return to where you came from...
		redirect(url: request.getHeader('Referer'))
	}

	def preview = {

		def previewData = [:]

		//read contents of msnFile and pass it to the view
		def msnFile = Msnfile.get(params.id)

		if (session.member.usergroup.id == msnFile.member.usergroup.id){

			previewData['filename'] = "file${msnFile.id}"

			//determine extension if file
			def fileExtension = msnFile.location.tokenize(".")[-1]

			switch (fileExtension) {
				case 'pdf'		:	//set header to PDF output
					def pdfFile = new File("${msnFile.location}")
					byte[] pdfBytes = pdfFile.readBytes()
					response.setContentType("application/pdf")
					response.setHeader("Content-disposition", "attachment; filename=${msnFile.name}")
					response.setContentLength(pdfBytes.length)
					response.getOutputStream().write(pdfBytes)
					break;
				case 'json'		:	//have to show preview in MSnViewer
//					previewData['json'] = []
					previewData['json'] = msnFile.location
//					def jsonArray = JSON.parse(new File(msnFile.location).text)
//					jsonArray.each { j ->
//						previewData['json'].add(j)
//					}
					break;
				case 'cml'		:
				case 'mzxml'	:	previewData['xml'] = new File(msnFile.location).text
				//do some cleaning to present XML correct on HTML page
					previewData['xml'] = previewData['xml'].replace('<', '&lt;')
					previewData['xml'] = previewData['xml'].replace('>', '&gt;')
					break;
				default			:	previewData['text'] = new File(msnFile.location).text
			}

			[ msnFile: msnFile, previewData: previewData, serverUrl: "${config.grails.serverURL}" ]
		} else {
			render ("You are not authorized to view this file!")
		}

	}


	def settings = {

		def settings = [:]

		// check if we are using settings available in the database
		if (params.Submit == 'Load' && params.availableSetting){

			def availableSettings = ProcessSettings.findByMemberAndId(session.member, params.availableSetting)
			if (availableSettings){
				settings = availableSettings.settingsmap
				params.files = session.files
				params.directories = session.directories
			}
		}

		// fetch settings from post/get
		if (params.Submit == 'Process'){

			params.each {
				def paramSplit = it.key.split('_').toList()
				if (paramSplit[0] == "settings"){
					if (it.value != ''){
						settings.put(paramSplit[1], it.value) // add the setting to the list of settings
					}
				}
			}
		}

		if (params.Submit == 'Process' && settings.size() >= 1 ) {
			session.settings = settings
			redirect(action: 'process')

		} else {

			// save the files/directory settings in the session
			if (params.files) { session.files = params.files }
			if (params.directories)	{ session.directories = params.directories }

			if ((session.files || session.directories)){
				return [ settings : settings, snthreshs: 10..1, levels: 1..5, accuracy: 1..50, availableSettings: ProcessSettings.findAllByMember(session.member) ]
			} else {
				// we can only process when there are files or directories selected!/
				redirect(controller: 'page', action: 'process')
			}
		}
	}


	def process = {

		def job
		def jobSettings

		// we need files/directories and settings to process!
		if ((session.files || session.directories) && session.settings){

			/*
			 ****************************************************************************
			 *	JOB SETTINGS
			 ****************************************************************************
			 */
			def jsonSettings 		= (session.settings as JSON).toString()
			def jsonSettingsHash 	= jsonSettings.encodeAsMD5()

			/*
			 * try to find/load existing settings, or create new
			 */
			jobSettings 	= ProcessSettings.findBySettingsHashAndMember(jsonSettingsHash, session.member)
			if (!jobSettings){

				/*
				 * create a new ProcessSettings entry
				 */
				jobSettings 				= new ProcessSettings()
				jobSettings.member			= session.member
				jobSettings.settings		= jsonSettings
				jobSettings.settingsHash	= jsonSettingsHash
				jobSettings.save(flush: true)
			}
			//***************************************************************************


			/*
			 ****************************************************************************
			 *	PROCESSJOB
			 ****************************************************************************
			 */

			job 			= new ProcessJob()
			job.active		= true
			job.member		= session.member
			job.settings	= jobSettings

			/*
			 *  job can now be saved to database
			 */
			job.save(flush: true)

			//***************************************************************************


			/*
			 ****************************************************************************
			 *  PROCESSJOB FILES
			 ****************************************************************************
			 */
			def fileIds = []

			if (session.files?.size()){
				if (session.files instanceof String){
					fileIds.add(session.files)
				}
				else {
					session.files?.each { fileId ->
						fileIds.add(fileId)
					}
				}
			}

			if (session.directories?.size()){
				if (session.directories?.size() && session.directories instanceof String){
					Directory.get(session.directories).toplevelmsnfiles?.each { file -> fileIds.add(file.id) }
				} else {
					session.directories.each { directoryId ->
						Directory.get(directoryId).toplevelmsnfiles?.each { file -> fileIds.add(file.id) }
					}
				}
			}

			job.fids = fileIds.join(', ')
			job.report = "${new Date()} - Job queued\n"
			job.save(flush: true)//force a save to be sure the files are linked!

			//***************************************************************************


			/*
			 ****************************************************************************
			 *  QUEUE PROCESSJOB
			 ****************************************************************************
			 */
			job.queue() //jobs take too long to do it instantly, so we queue it!
			//***************************************************************************

			// clear input from session when job is finished
			session.files			= null
			session.directories		= null
			session.settings		= null

		} else {
			//we should only be able to access this page with the required settings
			redirect(action: 'settings')
		}

		//done... lets see what this job did!
		flash.message = 'Job settings were saved and the processing has been queued. On this page you will be able to track the progress of the job'
		redirect(controller: 'processJob', action: 'details', id: job.id)
	}

	def setinchi = {

		if (params.inchi != null && params.msnfile){
			def msnfile = Msnfile.get(params.msnfile)
			if (msnfile.member.usergroup.id == session.member.usergroup.id)  { //only usergroup members/owner of files can update the compound information of an msnfile

				//fetch compound, or create it
				def compound = Compound.findByInchi(Compound.cleanInchi(params.inchi))
				if (!compound){
					compound = new Compound()
					compound.name = 'unknown'
				}

				compound.inchi = Compound.cleanInchi(params.inchi)
				compound.save()

				//add compound to Msnfile
				msnfile.compound = compound
				msnfile.save()
			}
		}

		//return to where you came from...
		redirect(url: request.getHeader('Referer'))
	}

	def setcomments = {

		if (params.comments && params.msnfile){
			def msnfile = Msnfile.get(params.msnfile)
			if (msnfile.member.usergroup.id == session.usergroup.id)  { //only owner of files can update the information of an msnfile
				msnfile.comments = params.comments
				msnfile.save()
			}
		}

		//return to where you came from...
		redirect(url: request.getHeader('Referer'))
	}
}
