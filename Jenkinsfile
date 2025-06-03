pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "user-service:${env.BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
               git branch: 'main',
               url: 'https://github.com/Arnold311/user-service.git'
            }
        }

        stage('Build') {
            steps {
                powershell './mvnw clean package'
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
