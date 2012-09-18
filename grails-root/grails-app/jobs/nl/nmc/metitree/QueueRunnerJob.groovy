package nl.nmc.metitree

class QueueRunnerJob {

	def sessionRequired = true
	def concurrent = true

	static triggers = {
		simple name: 'JobRunner', startDelay: 1000, repeatInterval: 10000 //delay 1sec, run every 10sec
	}

	def mconfService
	def processService

    def execute() {

    		//make sure the status is in sync!
    		ProcessJobQueue.list().each { j ->
    			if (j.processjob.lastStatus  != j.status) {
    		    		j.processjob.lastStatus = j.status
    				j.processjob.merge()
    			}
    		}

		//loop over all running jobs to see if the shoud time-out
		ProcessJobQueue.findAllByStatus(1).each { runningJobInQueue ->
			def jobDuration = ((new Date().time - runningJobInQueue.processjob.lastUpdated.time)/1000) as int
			def maxJobDuration = mconfService.fromLabel("metitree.processing.jobs.max.execution") as int
			if (jobDuration > maxJobDuration){
				log.info "Job ${runningJobInQueue} is running longer than allowed (${jobDuration} seconds)."
			}
		}


		//find out if there are jobs queued
		if (ProcessJobQueue.findAllByStatus(0).size() > 0){

			//find out if the max no. of jobs is already runnig
			if (ProcessJobQueue.findAllByStatus(1).size() < (mconfService.fromLabel("metitree.processing.jobs.parallel.max") as int)){

				//fetch the next job to run
				def processJobQueue = ProcessJobQueue.findByStatus(0, [max:1, sort:"dateCreated", order:"asc"] )

				if (processJobQueue){

					// fetch job to process
					def job = ProcessJob.get(processJobQueue.processjob.id)

					if (job.active){

						// change status to running to make sure we only run it once
						processJobQueue.status = 1 // set to running
						processJobQueue = processJobQueue.merge()

						try {
							processService.runJob(job) // RUN !!!
							processJobQueue.status = 2 // set to finished
							processJobQueue.save() // force a save
						} catch (e){
							processJobQueue.status = 99 // set to error
							processJobQueue = processJobQueue.merge()
							log.error("Was unable to execute job (${job})" + e)
						}
					} else {

						// job inactive
						processJobQueue.status = 11 // set to running
						processJobQueue = processJobQueue.merge()
						log.info "Job is in-active, will not process."
					}
				}
			}
		}

		//log.info("Queue runners at ${ProcessJobQueue.findAllByStatus(1).size()} of max ${mconfService.fromLabel('metitree.processing.jobs.parallel.max')} with ${mconfService.fromLabel('metitree.processing.jobs.threads.max')} thread(s) per runner.")
    }
}
