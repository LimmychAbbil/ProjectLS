pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                bat 'mvn clean package'
            }
        }
        stage('Deploy') {
            when {
                branch 'servlet4/forHeroku'
            }
            steps {
                bat 'cd target; mvn heroku:deploy-war'
            }
        }
    }
}