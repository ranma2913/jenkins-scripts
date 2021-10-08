# Problem

Jenkins system logs are not being rotated/cleaned up properly.

# Solution

Use a K8s CronJob to delete old files to free up space.

# How to use:

You need the following tools:

- kustomize cli (local build)
- oc cli (deployment)
- kubectl cli (deployment)

## Example Commands

```bash
BUILD_DIR=infrastructure/overlays/cleanup-logs && \
kustomize build $BUILD_DIR >| $BUILD_DIR/kustomize-output.yaml && \
kubectl apply -f $BUILD_DIR/kustomize-output.yaml
```

kub delete -f $BUILD_DIR/kustomize-output.yaml
