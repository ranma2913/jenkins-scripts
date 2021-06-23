import hudson.model.Run
import hudson.model.User
import jenkins.model.Jenkins

def abortJobs = true
def itemsNotProcessed = [:]
def jobsFoundMatchingSearch = 0
def jobsFoundInClassesToAbort = 0
def jobsAborted = 0

def vo = false

def printHelper(def verboseOutput, def string) {
    if (verboseOutput) {
        println(string)
    }
}

println("************************************************** START ***************************************************************" +
        "\nabortJobs: ${abortJobs}" +
        "\njobsFoundMatchingSearch: ${jobsFoundMatchingSearch}" +
        "\njobsFoundInClassesToAbort: ${jobsFoundInClassesToAbort}" +
        "\njobsAborted: ${jobsAborted}"
)
println("****************************************** Processing Started **********************************************************" +
        "\nSearching through hudson.model.Job(s) ... \n")
Jenkins.instance.getAllItems(hudson.model.Job).each { item ->
    println("Class: ${item.getClass()} fullName: ${item.fullName}")
    def jobBuildsMap = item.getBuildsAsMap()
    printHelper(vo, "Class: ${jobBuildsMap.getClass()}\n jobBuildsMap.size() = ${jobBuildsMap.size()}")
    jobBuildsMap.each() { int id, Run run ->


        printHelper(vo, "\t- runId: ${id}" +
                "\n\t  BuildStatusSumamry.message: ${run.getBuildStatusSummary().message}" +
                "\n\t  run.getClass(): ${run.getClass()}" +
                "\n\t  run.hasntStartedYet(): ${run.hasntStartedYet()}" +
                "\n\t  run.isBuilding(): ${run.isBuilding()}" +
                "\n\t  run.isInProgress(): ${run.isInProgress()}" +
                "\n\t  run.isLogUpdated(): ${run.isLogUpdated()}" +
                "\n\t  run: ${run}\n")


        if (abortJobs) {
            if (org.jenkinsci.plugins.workflow.job.WorkflowRun == run.getClass()) {
                run.finish(hudson.model.Result.ABORTED,
                        new IOException(
                                "\nBuild Aborted by Jenkins Script Console" +
                                        "\nUser: ${User.current().getId()} (${User.current()})" +
                                        "\nBuild: ${run}" +
                                        "\nStack Trace:\n"
                        )
                )
            } else {
                def executor = run.getExecutor()
                if (executor && executor.isActive()) {
                    printHelper(vo, "$executor")
                    executor.interrupt(hudson.model.Result.ABORTED)
                }
            }
            jobsAborted++
        }

        jobsFoundMatchingSearch++
    }
}


println("***************************************** Processing Complete **********************************************************" +
        "\nabortJobs: ${abortJobs}" +
        "\njobsFoundMatchingSearch: ${jobsFoundMatchingSearch}" +
        "\njobsFoundInClassesToAbort: ${jobsFoundInClassesToAbort}" +
        "\njobsAborted: ${jobsAborted}"
)

return "************************************************* END ******************************************************************"