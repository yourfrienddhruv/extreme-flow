/*
 * This Jenkinsfile is intended to run on on pre-setup-ed Jenkins2.0 and may fail anywhere else.
 * It makes assumptions about plugins being installed, labels mapping to nodes that can build what is needed, etc.
 *
 * The required labels are "java" and "docker" - "java" would be any node that can run Java builds. It doesn't need
 * to have Java installed, but some setups may have nodes that shouldn't have heavier builds running on them, so we
 * make this explicit. "docker" would be any node with docker installed.
 */

// Only keep the most recent builds.
properties([[$class: 'jenkins.model.BuildDiscarderProperty', strategy: [$class: 'LogRotator', numToKeepStr: '10',
                                                                        artifactNumToKeepStr: '20']]])
def PRE_CONFIGURED_GLOBAL_TOOL_JDK_ID   = 'Jdk_8u40'

node{
        //prints timestamps
        wrap([$class: 'TimestamperBuildWrapper']) {
            // The names here are currently aligned with Ambari default of version 2.4. This needs to be made more flexible.
            // Using the "tool" Workflow call automatically installs those tools on the node.
            String jdktool = tool PRE_CONFIGURED_GLOBAL_TOOL_JDK_ID

            // Set JAVA_HOME, MAVEN_HOME and special PATH variables for the tools we're  using.
            List buildEnv = ["PATH+JDK=${jdktool}/bin", "JAVA_HOME=${jdktool}",
                        "JAVA_OPTS=-Xmx1536m -Xms512m -XX:MaxPermSize=1024m", "MAVEN_OPTS=-Xmx1536m -Xms512m -XX:MaxPermSize=1024m "]

            // First stage is actually checking out the source. Since we're using Multibranch currently, we can use "checkout scm".
            stage "Get Latest Source"

            checkout scm

            //@TODO if(currentBuild.rawBuild.getChangeSets().empty()

            // Now run the actual build.
            stage "Build, Test & Package"

            timeout(time: 15, unit: 'MINUTES') {
                // See below for what this method does - we're passing an arbitrary environment
                // variable to it so that JAVA_OPTS and MAVEN_OPTS are set correctly.
                withEnv(buildEnv) {
                    // Actually run Maven!
                    // The -Dmaven.repo.local=${pwd()}/.repository means that Maven will create a
                    // .repository directory at the root of the build (which it gets from the
                    // pwd() Workflow call) and use that for the local Maven repository.
                    sh "./mvnw  clean install  -Dmaven.test.failure.ignore=true -V -B " //-Dmaven.repo.local=${pwd()}/.repository"
                }
            }

            // Once we've built, archive the artifacts and the test results.
            stage "Archive Artifacts"

            step([$class: 'ArtifactArchiver', artifacts: '**/target/*.jar, **/target/*.tar.gz', fingerprint: true])
            step([$class: 'JUnitResultArchiver', healthScaleFactor: 20.0, testResults: '**/target/surefire-reports/*.xml'])

            stage "Quality Assurance"
            timeout(time: 15, unit: 'MINUTES') {
                withEnv(buildEnv) {
                    sh "./mvnw sonar:sonar -V -B " //-Dmaven.repo.local=${pwd()}/.repository"
                }
            }
            if (currentBuild.result == null) {
                //Build yet not failed
                stage "Release Actions"
                timeout(time:5, unit:'MINUTES') {
                    def v = versionOfProject()

                    //@TODO input should be done out-side of node to not to block other builds
                    withEnv(buildEnv) {
                        if (env.BRANCH_NAME.startsWith("develop")) {
                            echo "Developer can : start a Feature manually using :  ./mvnw clean jgitflow:feature-start -V -B"
                            input message: v + ' : Want to star new Release ?'
                            sh "./mvnw clean jgitflow:release-start -V -B"
                        } else if (env.BRANCH_NAME.startsWith("release")) {
                            input message: v + ' : Finish Release ?'
                            sh "./mvnw clean jgitflow:release-finish -V -B " //-Dmaven.repo.local=${pwd()}/.repository"
                        } else if (env.BRANCH_NAME.startsWith("hotfix")) {
                            input message: v + ' : Finish Hotfix ?'
                            sh "./mvnw clean jgitflow:hotfix-finish  -V -B " //-Dmaven.repo.local=${pwd()}/.repository"
                        } else if (env.BRANCH_NAME.startsWith("feature")) {
                            input message: v + ' : Finish Feature ?'
                            sh "./mvnw clean jgitflow:feature-finish  -V -B " //-Dmaven.repo.local=${pwd()}/.repository"
                        } else if (env.BRANCH_NAME.startsWith("master")) {
                            echo "You can :  Hotfix start manually using :  ./mvnw clean jgitflow:hotfix-start -V -B"
                        } else if (env.BRANCH_NAME.startsWith("support")) {
                             echo "You can :  Hotfix start manually using :  ./mvnw clean jgitflow:hotfix-start -V -B"
                        } else{
                            echo "Non-standard Git-Flow Branch, can't suggest any release actions."
                        }
                    }
                }
            }else{
                echo 'No release steps can be done on failed build.'
            }
            final def RECIPIENTS = emailextrecipients([ [$class: 'DevelopersRecipientProvider'], [$class: 'CulpritsRecipientProvider'] ])
            step([$class: 'Mailer', notifyEveryUnstableBuild: true, sendToIndividuals: true, recipients: RECIPIENTS])
        }
}

def versionOfProject() {
  def matcher = readFile('pom.xml') =~ '<version>(.+)</version>'
  matcher ? matcher[0][1] : null
}