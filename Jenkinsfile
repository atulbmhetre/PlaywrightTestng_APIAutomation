pipeline {
    agent any

    parameters {
        choice(name: 'env', choices: ['dev', 'qa', 'prod'], description: 'Environment')
        string(name: 'groups', defaultValue: 'smoke', description: 'TestNG groups')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                echo "Running tests for env=${params.env}, groups=${params.groups}"
                sh "mvn clean test -Denv=${params.env} -Dgroups=${params.groups}"
            }
        }
    }
}