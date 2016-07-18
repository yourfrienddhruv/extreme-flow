# extreme-flow
Extreme Flow = SpringBoot + Jenkinsfile  + gitflow + Ambari



# CI
*   Download latest jenkins : wget http://mirrors.jenkins-ci.org/war-stable/latest/jenkins.war
*   Start Jenkins : java -jar jenkins.war
*   Login in Jenkins : previous command will print initial admin password , use that to login
*   Click `Install suggested Plugins`
*   If you have any errors, go to cli and ^c to stop jenkins and restart again to retry until all plugins are correctly installed.
*   Once all plugins are green, create admin account
*   Go to Jenkins->Config-> Global Tool Configuration
*   Add Maven TOOL with name `Maven_3.2.2`
*   Add JDK  TOOL  with name `Jdk_8u41`
*   Create a Job -> Multibranch pipeline -> Name `spring-boot-workshop`
*   Add git/github url and click save.
