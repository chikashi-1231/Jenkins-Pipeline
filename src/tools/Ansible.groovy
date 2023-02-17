package tools

class Ansible implements Serializable {
    def steps

    /**
    * [概要]コンストラクタ
    **/
    Ansible(steps) {
        this.steps = steps
    }

    /**
    * [概要]Ansibleを用いて、Inventoryファイルに定義されたサーバーへ資材を配置
    * [引数1]Inventoryファイル内に置ける、配置先のホストが定義されている項目名
    * [引数2]配置する対象のファイル
    * [引数3]配置先のディレクトリPath
    * [戻り値]なし
    **/
    void deploy(String host, String copySrc, String destSrc) {

        // Load playbook
        def playbook = libraryResource("${PROJECT_NAME}/ansible/linux-deploy.yml")
        writeFile file: "deploy.yml", text: playbook

        // Load inventory
        def hosts = libraryResource("ansible/hosts")
        writeFile file: "hosts", text: hosts

        this.steps.sh "ansible-playbook -i hosts deploy.yml --extra-vars 'host=${host} copy_src=${copySrc} dest_src=${destSrc}'"
    }
}