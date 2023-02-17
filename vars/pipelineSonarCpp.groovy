import tools.Ansible
import tools.CppCheck
import tools.GitLab
import tools.SonarQube

def call() {
    pipeline {
        agent { label "master" }
        environment {
            PROJECT_VERSION = "0.0.1"
        }

        stages {
            stage('Check out') {
                steps {
                    checkout scm
                    BRANCH_NAME = ${GIT_BRANCH.replace('origin/', '')}
                    echo "Branch : ${BRANCH_NAME}"
                }
            }

            stage('Sonar Analysis') {
                when{ expression { reuturn false } }
                steps {
                    cppCheck = new CppCheck(this)
                    cppCheck.analyze()
                    Map map = [sonar.cppcheck.reportPath:cppCheck.getReportFileName()]
                    sonarQube = new SonarQube(this)
                    sonarQube.scan(map)
                }
            }
        }
        post {
            always { deleteDir() }
        }
    }
}