import common.Jenkins

def call(){
    echo "Start ${JOB_NAME}"

    node('master') {
        // Jenkinsの設定ファイルの読み込み
        jenkinsConfig = loadConfig('jenkins-config.yml')
        Jenkins.init(jenkinsConfig)
    }

    // 実行ジョブの選択
    // Jenkinsのジョブ名から、後方一致で検索をする。したがって、Jenkinsのジョブ名は下記のように命名することを想定
    // ・develop-deploy-shellScript
    // ・staging-deploy-shellScript
    // ・production-deploy-shellScript
    switch(JOB_NAME){
        case ~/^.*deploy-shellScript/:
            echo "Start Deploy"
            pipelineDeployShellScript
            break
        case ~/^.*action-something/:
            echo "Some Message"
            // ここに、/vars配下の実行したいパイプラインスクリプト(.groovy)を指定
            break
        default :
            throw new Exception("No pipeline starts")
    }
}

/**
* [概要]Jenkinsの設定ファイルの読み込み
* [引数1]/resourcesディレクトリ配下の設定ファイルの名前
* [戻り値]設定値を変数に持つインスタンス
**/
def loadConfig(String configFileName) {
    def configFile = libraryResources(configFileName)
    writeFile(file: configFileName, text: configFile)
    config = readYaml(file: configFileName)

    return config
}