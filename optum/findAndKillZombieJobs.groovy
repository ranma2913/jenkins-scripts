import hudson.*
import hudson.model.*
import jenkins.*
import jenkins.model.*
import org.jenkinsci.plugins.workflow.job.*

itemNameMatcherList = [
//        /B360_Jobs/,
//        /emr-poca/,
//        /cdb-locked-accounts/,
//        /sox-reporting_Jobs/,
//        /Espresso_Jobs/,
//        /WidgetFactory_Jobs/,
/pafs_ATDD_parallel7$/,
]
def abortJobs = false
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
        "\nitemNameMatcherList: '${itemNameMatcherList}'" +
        "\nabortJobs: ${abortJobs}" +
        "\njobsFoundMatchingSearch: ${jobsFoundMatchingSearch}" +
        "\njobsFoundInClassesToAbort: ${jobsFoundInClassesToAbort}" +
        "\njobsAborted: ${jobsAborted}"
)
println("****************************************** Processing Started **********************************************************" +
        "\nSearching through hudson.model.Job(s) ... \n")
itemNameMatcherList.each() { itemNameMatcher ->
    println("Searching for runs which match $itemNameMatcher & run.isBuilding() || run.isInProgress() || run.hasntStartedYet()")
    Jenkins.instance.getAllItems(hudson.model.Job).each { item ->
        if ((item.fullName =~ itemNameMatcher).find()) {
            println("Class: ${item.getClass()} fullName: ${item.fullName}")
            def jobBuildsMap = item.getBuildsAsMap()
            printHelper(vo, "Class: ${jobBuildsMap.getClass()}\n jobBuildsMap.size() = ${jobBuildsMap.size()}")
            jobBuildsMap.each() { int id, hudson.model.Run run ->


                printHelper(vo, "\t- runId: ${id}" +
                        "\n\t  BuildStatusSumamry.message: ${run.getBuildStatusSummary().message}" +
                        "\n\t  run.getClass(): ${run.getClass()}" +
                        "\n\t  run.hasntStartedYet(): ${run.hasntStartedYet()}" +
                        "\n\t  run.isBuilding(): ${run.isBuilding()}" +
                        "\n\t  run.isInProgress(): ${run.isInProgress()}" +
                        "\n\t  run.isLogUpdated(): ${run.isLogUpdated()}" +
                        "\n\t  run: ${run}\n")


                if (run.isBuilding() || run.isInProgress() || run.hasntStartedYet()) {
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
                            if (executor.isActive()) {
                                printHelper(vo, "$executor")
                                executor.interrupt(hudson.model.Result.ABORTED)
                            }
                        }
                        jobsAborted++
                    }

                    jobsFoundMatchingSearch++
                }
            }
        }
    }
}

println("***************************************** Processing Complete **********************************************************" +
        "\nitemNameMatcherList: '${itemNameMatcherList}'" +
        "\nabortJobs: ${abortJobs}" +
        "\njobsFoundMatchingSearch: ${jobsFoundMatchingSearch}" +
        "\njobsFoundInClassesToAbort: ${jobsFoundInClassesToAbort}" +
        "\njobsAborted: ${jobsAborted}"
)
//println("\nitemsNotProcessed (wrong type or not matching itemNameMatcherList):")
//itemsNotProcessed.each { itemType, itemListForType ->
//    println("itemType: ${itemType}")
//    itemListForType.each { item ->
//        println("\t - ${item}")
//    }
//}

return "************************************************* END ******************************************************************"