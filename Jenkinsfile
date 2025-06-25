
pipeline {
    agent any

    tools {
        jdk 'jdk17'
        gradle 'gradle'
    }

    environment {
        ENV_FILE_CONTENT = credentials('ENV_FILE_CONTENT')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Prepare for Build') {
            steps {
                sh 'echo "$ENV_FILE_CONTENT" > .env'
                sh 'chmod +x ./gradlew'
            }
        }

        stage('Test & Coverage') {
            steps {
                sh './gradlew clean test'
            }
        }
    }

    post {
        always {
            junit 'build/test-results/test/*.xml'

            recordIssues(
                id: 'coverage',
                name: 'Code Coverage',
                tools: [[
                    parser: 'JACOCO',
                    pattern: 'build/reports/jacoco/test/jacocoTestReport.xml'
                ]]
            )
        }
    }
}