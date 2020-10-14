import jenkins.model.Jenkins
import hudson.model.Job

MAX_BUILDS = 5
Jenkins.instance.getAllItems(Job.class).each { job ->
    println job.name
    def recent = job.builds.limit(MAX_BUILDS)
    for (build in job.builds) {
        if (!recent.contains(build)) {
            println "Preparing to delete: $build"
            try{
                build.delete()
            }catch(e){
                println "Unable to delete build: $build"
                println e
            }
        }
    }
}