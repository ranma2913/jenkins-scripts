import hudson.*
import hudson.model.*
import jenkins.*
import jenkins.model.*
import org.jenkinsci.plugins.workflow.job.*

def MAX_BUILDS = 5
def itemNameMatcher = "/"
//def itemNameMatcher = "Fortify_Scan/covid-portal/"
//def itemNameMatcher = "Fortify_Scan/"
def jobsToDelete = []

println("************************************************** START ***************************************************************")
Jenkins.instance.getAllItems(Job.class).each { job ->
    if ((job.fullName =~ itemNameMatcher).find()) {
        def recent = job.builds.limit(MAX_BUILDS)
        for (build in job.builds) {
            if (!recent.contains(build)) {
                jobsToDelete.add(build)
            }
        }
    }
}
println "${jobsToDelete.size()} Jobs to Delete..."
jobsToDelete.each() { build ->
    try {
        println("Deleting Build: ${build}")
        build.delete()
    } catch (e) {
        println "Unable to delete build: $build"
        println e
    }
}
return "************************************************* END ******************************************************************"