package nl.nmc.metitree

import java.util.logging.Logger;

class EveryMinuteJob {
	
	def sessionRequired = true
	def concurrent = true
		
	static triggers = {
		simple name: 'CleanupEveryMinute', startDelay: 6000, repeatInterval: 6000 //delay 60sec, run every 60sec
	}
	
	def mconfService
	def processService
	
    def execute() {
		
		/**
		 * find out of control jobs to stop
		 * 
		 * - job should have status 1 (running)
		 * - jobs last update should have been over x second. x = metitree.processing.jobs.max.execution
		 */
		def runningJobs = ProcessJobQueue.findAllByStatus(1)
		
		runningJobs.each { processJobQueue ->
			
			def currentExecTime = new Date().getTime() - processJobQueue.lastUpdated.getTime()
			def maxExecTime = mconfService.fromLabel("metitree.processing.jobs.max.execution") as int
			
			if (currentExecTime >= maxExecTime){
				try {
					def processJob = processJobQueue.processjob
						processJob.lastStatus = 99
						processJob.save()
					
					processJobQueue.status = 99
					processJobQueue.save()
				} catch (e) {
					//this gives errors ofter, can be ignored
				}
			}
		}
	}
}
