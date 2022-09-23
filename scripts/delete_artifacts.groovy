import hudson.*
import hudson.model.*
import jenkins.*
import jenkins.model.*
import org.jenkinsci.plugins.workflow.job.*

// Delete old artifacts that fills up the disk on the master node.
// Run this from the Jenkins console (Manage Jenkins, Manage Nodes, master, Script Console)
def numBuildsKeepArtifacts = 2
def itemNameMatcher=""
try{
    itemNameMatcher=args[0]
}catch(e){
    println ("usage: jcli groovy =< scripts/delete_artifacts.groovy <JenkinsItemName>")
    return 1;
}

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