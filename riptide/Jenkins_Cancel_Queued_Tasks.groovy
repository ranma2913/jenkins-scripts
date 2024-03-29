import hudson.model.Queue
import jenkins.model.Jenkins

Boolean cancelQueuedItems = false
def countCanceled = 0
def countAttemptedCancel = 0
Queue queue = Jenkins.instance.queue
println("Queue Length = ${queue.getItems().size()}")
println("cancelQueuedItems=$cancelQueuedItems")

queue.items.each() {
  println("Task = ${it.task}")
  if (cancelQueuedItems) {
    try {
      countAttemptedCancel++
      queue.cancel(it.task)
      countCanceled++
    } catch (e) {
      println("failed to cancel task = $e")
    }
  }
}