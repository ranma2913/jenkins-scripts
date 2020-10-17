import hudson.model.*

disableChildren(Hudson.instance.items)

def disableChildren(items) {
    for (item in items) {
        if (item.class.canonicalName == 'com.cloudbees.hudson.plugins.folder.Folder') {
            disableChildren(((com.cloudbees.hudson.plugins.folder.Folder) item).getItems())
        } else if (item.class.canonicalName != 'org.jenkinsci.plugins.workflow.job.WorkflowJob') {
            try {
                println(item.name)
                item.disabled = false
                item.save()
            } catch (e) {
                println(e)
            }
        }
    }
}