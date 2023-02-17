package tools

import common.Jenkins

class GitLab implements Serializable {
    def steps

    /**
    * [概要]コンストラクタ
    **/
    GitLab(steps) {
        this.steps = steps
    }

    /**
    * [概要]Jenkinsジョブの設定画面における"リポジトリURL"に指定されたGitのリポジトリにタグを追加する
    * [引数]付加するGitのタグの名前
    * [戻り値]なし
    **/
    void pushTag(String tagName) {
        this.steps.withCredentials([UsernamePassword(credentialsId: Jenkins.config.gitlab.credential,
        usernameVariable: 'gitUserName', passwordVariable: 'gitPassword']) {

            gitPassword = gitPassword.replace("!", "\\!")

            def remoteUrl = GIT_URL.replace('://', "://${gitUserName}:${gitPassword}@")

            this.steps.sh "git remote set-url origin ${remoteUrl}"
            this.steps.sh "git tag ${tagName}"
            this.steps.sh "git push origin ${tagName}"
        }
    }
}