package tools

import common.Jenkins

class SonarQube implements Serializable {
    def steps

    /**
    * [概要]コンストラクタ
    **/
    SonarQube(steps, version) {
        this.steps = steps
    }

    /**
    * [概要]SonarQubeで静的コード解析を実行する
    * [引数]静的コード解析時のオプションをkey, valueの形で定義
    * [戻り値]なし
    **/
    void scan(def param) {
        def scannerHome = tool name: Jenkins.config.sonarqube.scanner, type: 'hudson.plugins.sonar.SonarRunnerInstallation'
        def projectKey = "${PROJECT_NAME}:${env.BRANCH_NAME.replaceAll('/', '-')}"

        // propertyファイルの読み込み
        def sonarProperties = libraryResource("sonarQube/${PROJECT_NAME}/sonar-project.properties")
        writeFile file: "sonar-project.properties", text: sonarProperties
        def sonarProps = readProperties(file: 'sonar-project.properties')

        def projectVersion = sonarProps['sonar.projectVersion']

        def options = ''
        param.each {
            options += "-D${it.key}=${it.value} "
        }

        this.steps.withSonarQubeEnv('sonar') {
            this.steps.sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=${projectKey} -Dsonar.projectName=${PROJECT_NAME} -Dsonar.projectVersion=${projectVersion} ${options}"
        }
    }
}