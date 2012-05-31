package nl.nmc.metitree

class ProcessJobQueue {
	
	ProcessJob	processjob
	int			status // 0 = new, 1 = running, 2 = finished, 99 = error 
	Date		dateCreated
	Date		lastUpdated

    static constraints = {
    }
	
	static transients = ['new', 'running', 'finished', 'error', 'stopped', 'cancelled', 'done']

	boolean isNew(){
		this.status == 0 ? true : false
	}
		
	boolean isRunning(){	
		this.status == 1 ? true : false
	}
	
	boolean isFinished(){
		this.status == 2 ? true : false
	}
	
	boolean isStopped(){
		this.status == 10 ? true : false
	}
	
	boolean isCancelled(){
		this.status == 11 ? true : false
	}
	
	boolean isError(){
		this.status == 99 ? true : false
	}
	
	boolean isDone(){
		this.status >= 2 ? true : false
	}
}