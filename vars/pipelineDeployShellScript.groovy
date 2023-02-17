import tools.Ansible
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

            stage('Tar') {
                when{ expression { reuturn false } }
                steps {
                    script {
                        sh "tar -C ${SOURCE_DIR} -czvf ${TAR_NAME} ."
                    }
                }
            }

            stage('Sonar Analysis') {
                when{ expression { reuturn false } }
                steps {
                    sonarQube = new SonarQube(this)
                    sonarQube.scan()
                }
            }

            stage('Git Tag') {
                when{ expression { reuturn false } }
                steps {
                    script {
                        DEPLOY_DATE = sh(returnStdout: true, script: "date '+%Y%m%d_%H%M%S'").trim()
                        TAG_NAME = "${BRANCH_NAME}_${DEPLOY_DATE}"
                        gitLab = new GitLab(this)
                        gitLab.pushTag(TAG_NAME)
                    }
                }
            }

            stage('Deploy') {
                when {
                    // developブランチの場合のみデプロイを行う
                    branch 'develop'
                }
                steps {
                    ansible = new Ansible(this)
                    ansible.deploy("dev_mg", "${TAR_NAME}", "${DEPLOY_DIR}")
                }
            }
        }
        post {
            always { deleteDir() }
        }
    }
}