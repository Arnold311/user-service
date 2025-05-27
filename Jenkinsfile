pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                powershell './mvnw clean package'
            }
        }

        stage('Test') {
            steps {
                powershell './mvnw test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    def imageTag = "user-service:${env.BUILD_NUMBER}"
                    powershell "docker build -t ${imageTag} ."
                }
            }
        }

        stage('Deploy') {
                    steps {
                        powershell '''
                            docker-compose down
                            docker-compose up -d --build
                        '''
                    }
                }
        }
    }
}
