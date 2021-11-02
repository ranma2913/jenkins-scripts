import hudson.model.Job
import jenkins.model.Jenkins

def MAX_BUILDS = 4
//def itemNameMatcher = "/"
//def itemNameMatcher = "OpenShift_UserSync/"
//def itemNameMatcher = "Fortify_Scan/"
def itemNameMatcher = "Sonar_Scan/"
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