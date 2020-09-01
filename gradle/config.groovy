// See /global-pipeline-library/src/com/optum/jenkins/pipeline/library/security/Contrast.groovy
// for context & history on this configuration option. 
withConfig(configuration) {
  configuration.setDisabledGlobalASTTransformations(['groovy.grape.GrabAnnotationTransformation'] as Set)
}
