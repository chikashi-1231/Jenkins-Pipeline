package tools

import common.Jenkins

class CppCheck implements Serializable {
    def steps
    def remoteUser
    def hostName
    def remoteDir
    def reportFileName = "report.xml"

    /**
    * [概要]コンストラクタ
    **/
    CppCheck(steps) {
        configFileName = "${PROJECT_NAME}/cppcheck/cppcheck-config.yml"
        configFile = libraryResources(configFileName)
        writeFile(file: configFileName, text: configFile)
        this.config = readYaml(file: configFileName)

        this.steps = steps
        this.remoteUser = this.config.remoteUser
        this.hostName = this.config.hostName
        this.remoteDir = this.config.remoteDir
    }

    init(def resource){
        config = resource
    }

    /**
    * [概要]CppCheckで静的コード解析を実行する
    * [引数]静的コード解析時のオプションをkey, valueの形で定義
    * [戻り値]なし
    **/
    void analyze(def param) {
        // CppCheckで解析する対象ディレクトリ。GitLabからcloneしているので、Jenkinsのワークスペースを指定
        def targetDir ${CPP_TARGETPATH} =? "."

        // CppCheckの実行
        this.steps.sshagent(credentials: '12345-6789-0abc-def1-234567') {

            // CppCheckのコンテナでコマンドを実行するため、SSHでディレクトリを初期化しSCPで該当のソースコードをCppCheckのコンテナにコピー
            remoteDir = this.remoteDir =? "/var/src/"
            echo ${remoteDir}
            //sh "ssh -t ${remoteUser}@${remoteHost} rm -rf ${remoteDir}*"
            sh "scp -r ${targetDir} ${this.remoteUser}@${this.remoteHost}:${this.remoteDir} "

            // CppCheckの実行オプション。解析結果で表示するメッセージのレベル(warning, performance, informationなど)を定義
            def enable ${CPP_ENABLE} =? "--enable=all"
            // CppCheckを実行するコマンド。xmlファイルで出力し、SonarQubeで読み取れるように、--xmlのオプションを付加
            def cmd "cppcheck --xml ${enable}"

            // CppCheckzで静的解析を行う際のOptionを設定
            def options = ''
            param.each {
                options += "-D${it.key}=${it.value} "
            }

            // CppCheckの解析対象のディレクトリ
            this.steps.sh "ssh -t ${this.remoteUser}@${this.remoteHost} ${cmd} ${options} ${this.remoteDir} 2> ${this.reportFileName}"
        }
    }
    
    /**
    * [概要]CppCheckでコード解析した結果のファイル名を取得
    * [引数]なし
    * [戻り値]CppCheckでコード解析した結果のファイル名（String）
    **/
    def getReportFileName() {
        return this.reportFileName
    }
}