# Jenkins Cleanup Jobs Utility

This project creates a Job to delete all job runs matching a regex.

## Prerequisites:

- Install Homebrew <https://docs.brew.sh/Installation>
- Install Tools: `brew install kubernetes-cli openshift-cli kustomize gnu-sed`

## How to use

1. Create an overlay for your jenkins environment
2. Update the Resource Patch files to suite your requirement
3. Update the `jenkinsCleanupJobHistory.sh` file with your parameters, then execute the same script.
