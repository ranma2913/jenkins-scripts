import hudson.model.Run
import hudson.model.User
import jenkins.model.Jenkins

def abortJobs = true
def itemsNotProcessed = [:]
def jobsFoundMatchingSearch = 0
def jobsFoundInClassesToAbort = 0
def jobsAborted = 0

Boolean vo = false

def printHelper(Boolean verboseOutput, def string) {
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
Jenkins.instance.getAllItems(hudson.model.Job).each { job ->
  println("Class: ${job.getClass()} fullName: ${job.fullName}")
  Map<Integer, Run> jobBuilds = job.getBuildsAsMap()
  printHelper(verboseOutput, "Class: ${jobBuilds.getClass()}\n jobBuilds.size() = ${jobBuilds.size()}")
  jobBuilds.each() { int id, Run run ->


    printHelper(verboseOutput, "\t- runId: ${id}" +
        "\n\t  BuildStatusSumamry.message: ${run.getBuildStatusSummary().message}" +
        "\n\t  run.getClass(): ${run.getClass()}" +
        "\n\t  run.hasntStartedYet(): ${run.hasntStartedYet()}" +
        "\n\t  run.isBuilding(): ${run.isBuilding()}" +
        "\n\t  run.isInProgress(): ${run.isInProgress()}" +
        "\n\t  run.isLogUpdated(): ${run.isLogUpdated()}" +
        "\n\t  run: ${run}\n")


    if (abortJobs) {
      if (run instanceof org.jenkinsci.plugins.workflow.job.WorkflowRun) {
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
          printHelper(verboseOutput, "executor = $executor. Interruptuing with hudson.model.Result.ABORTED")
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