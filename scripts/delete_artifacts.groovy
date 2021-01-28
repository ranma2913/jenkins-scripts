import hudson.*
import hudson.model.*
import jenkins.*
import jenkins.model.*
import org.jenkinsci.plugins.workflow.job.*

// Delete old artifacts that fills up the disk on the master node.
// Run this from the Jenkins console (Manage Jenkins, Manage Nodes, master, Script Console)

//def itemNameMatcher = "/"
//def itemNameMatcher = "Fortify_Scan/covid-portal/"
def itemNameMatcher = "Sonar_Scan/"
def numBuildsKeepArtifacts = 2

def totalSize = 0
println("************************************************** START ***************************************************************")
Jenkins.instance.getAllItems(Job.class).each { job ->
    if ((job.fullName =~ itemNameMatcher).find()) {
        def recent = job.builds.limit(numBuildsKeepArtifacts)
        for (build in job.builds) {
            if (!recent.contains(build)) {
                def artifacts = build.artifacts
                artifacts.each { artifact ->
                    totalSize += artifact.getFileSize()
                    println("$artifact, ${artifact.getFileSize()}")
                }
                build.deleteArtifacts()
            }
        }
    }
}
println "Total size: ${totalSize}"
return "************************************************* END ******************************************************************"