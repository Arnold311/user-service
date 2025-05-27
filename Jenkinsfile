pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "user-service:${env.BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps { checkout scm }
        }

        stage('Build') {
            steps {
                powershell './mvnw clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                powershell './mvnw test -Dspring.profiles.active=test'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    powershell "docker build -t ${env.DOCKER_IMAGE} ."
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    powershell "docker-compose down"
                    powershell "docker-compose up -d --build"
                }
            }
        }
    }
}