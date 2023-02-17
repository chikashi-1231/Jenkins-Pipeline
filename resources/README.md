# Jenkins Resource
## jenkins-config.yml
各項目の指定内容は下記の通り

### gitlab
JenkinsとGitLabを連携する際の設定値を定義する
- credential<br>
    Jenkinsにログインし、下記のように遷移した先にある設定値を指定
    1. `Jenkinsの管理`
    2. `Manage Credential`
    3. GitLab接続用のCredential情報における`ID`欄の値を指定すること

### sonarqube
Jenkinsとsonarqubeを連携する際の設定値を定義する
- scanner<br>
    Jenkinsにログインし、下記のように遷移した先にある設定値を指定
    1. `Jenkinsの管理`
    2. `Global Tool Configuration`
    3. `SonarQube Scanner`
    4. `インストール済のSonarQube Scanner` ⇒ `Name`欄にある値を指定すること