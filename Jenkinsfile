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


node{
    //prints timestamps
    wrap([$class: 'TimestamperBuildWrapper']) {

        // The names here are currently aligned with Ambari default of version 2.4. This needs to be made more flexible.
        // Using the "tool" Workflow call automatically installs those tools on the node.
        String mvntool = tool 'Maven_3.2.2'
        String jdktool = tool 'Jdk_8u40'

        // Set JAVA_HOME, MAVEN_HOME and special PATH variables for the tools we're  using.
        List buildEnv = ["PATH+MVN=${mvntool}/bin", "PATH+JDK=${jdktool}/bin", "JAVA_HOME=${jdktool}", "MAVEN_HOME=${mvntool}",
                    "JAVA_OPTS=-Xmx1536m -Xms512m -XX:MaxPermSize=1024m", "MAVEN_OPTS=-Xmx1536m -Xms512m -XX:MaxPermSize=1024m"]

        // First stage is actually checking out the source. Since we're using Multibranch currently, we can use "checkout scm".
        stage "Checkout source"

        checkout scm

        def v = versionOfProject()
        echo "Building version ${v}"

        // Now run the actual build.
        stage "Build and test"

        timeout(time: 15, unit: 'MINUTES') {
            // See below for what this method does - we're passing an arbitrary environment
            // variable to it so that JAVA_OPTS and MAVEN_OPTS are set correctly.
            withEnv(buildEnv) {
                // Actually run Maven!
                // The -Dmaven.repo.local=${pwd()}/.repository means that Maven will create a
                // .repository directory at the root of the build (which it gets from the
                // pwd() Workflow call) and use that for the local Maven repository.
                //sh "mvn  clean install ${runTests ? '-Dmaven.test.failure.ignore=true -Dconcurrency=1' : '-DskipTests'} -V -B -Dmaven.repo.local=${pwd()}/.repository"
                sh "mvn  clean install ${runTests ? '-Dmaven.test.failure.ignore=true -Dconcurrency=1' : '-DskipTests'} -V -B"
            }
        }

        // Once we've built, archive the artifacts and the test results.
        stage "Archive artifacts and test results"

        step([$class: 'ArtifactArchiver', artifacts: '**/target/*.jar, **/target/*.tar.gz', fingerprint: true])
        if (runTests) {
            step([$class: 'JUnitResultArchiver', healthScaleFactor: 20.0, testResults: '**/target/surefire-reports/*.xml'])
        }

        stage "Quality Assurance"
        timeout(time: 15, unit: 'MINUTES') {
            withEnv(buildEnv) {
                //sh "mvn  clean install ${runTests ? '-Dmaven.test.failure.ignore=true -Dconcurrency=1' : '-DskipTests'} -V -B -Dmaven.repo.local=${pwd()}/.repository"
                sh "mvn sonar:sonar"
            }
        }
    }
}

stage "Release Actions"
timeout(time:3, unit:'DAYS') {
    input message:'Do you want to take any Release related actions on version: ${v} ?'

    if (env.BRANCH_NAME.startsWith("develop")) {
        node{
            withEnv(buildEnv) {
                echo "@TODO give option to start release"
                echo "@TODO give option to start features"
                sh "mvn validate"
            }
        }
    } else if (env.BRANCH_NAME.startsWith("release")) {
        node{
            withEnv(buildEnv) {
                echo "@TODO give option to finish release"
                sh "mvn validate"
            }
        }
    } else if (env.BRANCH_NAME.startsWith("hotfix")) {
        node{
            withEnv(buildEnv) {
                echo "@TODO give option to finish hotfix"
                sh "mvn validate"
            }
        }
    } else if (env.BRANCH_NAME.startsWith("feature")) {
        node{
            withEnv(buildEnv) {
                echo "@TODO give option to finish feature"
                sh "mvn validate"
            }
        }
    } else if (env.BRANCH_NAME.startsWith("master")) {
        node{
            withEnv(buildEnv) {
                echo "@TODO give option to start hotfix"
                sh "mvn validate"
            }
        }
    } else if (env.BRANCH_NAME.startsWith("support")) {
         node{
             withEnv(buildEnv) {
                 echo "@TODO give option to start hotfix"
                 sh "mvn validate"
             }
         }
    }  else{
        echo "Non-standard Git-Flow Branch, can't suggest any release actions."
    }
}

def versionOfProject() {
  def matcher = readFile('pom.xml') =~ '<version>(.+)</version>'
  matcher ? matcher[0][1] : null
}