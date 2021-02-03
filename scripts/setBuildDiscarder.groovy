import hudson.model.Job
import hudson.tasks.LogRotator
import jenkins.model.BuildDiscarderProperty
import jenkins.model.Jenkins

// NOTES:
// dryRun: to only list the jobs which would be changed
// daysToKeep:  If not -1, history is only kept up to this day.
// numToKeep: If not -1, only this number of build logs are kept.
// artifactDaysToKeep: If not -1 nor null, artifacts are only kept up to this day.
// artifactNumToKeep: If not -1 nor null, only this number of builds have their artifacts kept.

def dryRun = true
def daysToKeep = -1
def numToKeep = -1
def artifactDaysToKeep = -1
def artifactNumToKeep = -1

Jenkins.instanceOrNull.allItems(Job).each { job ->
    if (job.isBuildable() && job.supportsLogRotator() && job.getProperty(BuildDiscarderProperty) == null) {
        println "Processing \"${job.fullDisplayName}\""
        if (!"true".equals(dryRun)) {
            // adding a property implicitly saves so no explicit one
            try {
                job.setBuildDiscarder(new LogRotator(daysToKeep, numToKeep, artifactDaysToKeep, artifactNumToKeep))
                println "${job.fullName} is updated"
            } catch (Exception e) {
                // Some implementation like for example the hudson.matrix.MatrixConfiguration supports a LogRotator but not setting it
                println "[WARNING] Failed to update ${job.fullName} of type ${job.class} : ${e}"
            }
        }
    }
}
return