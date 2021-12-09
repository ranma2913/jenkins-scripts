/**
 * @see https://newbedev.com/cancel-queued-builds-and-aborting-executing-builds-using-groovy-for-jenkins
 */
import jenkins.model.*

Jenkins.get().queue.clear()
