pipeline {
    agent any
    environment {
        KOYEB_API_KEY = credentials('koyeb-api')
        KOYEB_REDEPLOYMENT_URL = credentials('koyeb-deployment-url')
        CLOUDINARY_API_SECRET = credentials('CLOUDINARY_API_SECRET')
        CLOUDINARY_API_KEY = credentials('CLOUDINARY_API_KEY')
        CLOUDINARY_CLOUD_NAME = credentials('CLOUDINARY_CLOUD_NAME')
        CLOUDINARY_URL = credentials('CLOUDINARY_URL')
        JWT_EXPIRATION_TIME = credentials('JWT_EXPIRATION_TIME')
        JWT_SECRET_KEY = credentials('JWT_SECRET_KEY')
        MONGO_URI = credentials('MONGO_URI')
        MONGO_DB = credentials('MONGO_DB')
        PORT = credentials('PORT')
        SOCKET_PORT = credentials('SOCKET_PORT')
        SOCKET_ORIGIN = credentials('SOCKET_ORIGIN')
        SERVER_ORIGIN = credentials('SERVER_ORIGIN')
    }
    options {
        skipDefaultCheckout()
    }
    tools {
        maven "mvn"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'dev-deployment', url: 'https://github.com/itSarthak/chat-app-backend.git'
            }
        }
        stage ('Build') {
            steps {
                sh 'mvn clean install'
            }
        }
        stage('Test') {
            steps {
                script {
                    sh 'mvn test'
                }
            }
        }
        stage('Sonar') {
            steps {
                withSonarQubeEnv('MySonarServer') {
                    sh 'mvn sonar:sonar'
                }
            }
            post {
                success {
                    script {
                        timeout(time: 2, unit: 'MINUTES') {
                            def qualityGate = waitForQualityGate()
                            if (qualityGate.status != 'OK') {
                                error "SonarQube Quality Gate failed: ${qualityGate.status}"
                            } else {
                                echo "SonarQube analysis passed."
                            }
                        }
                    }
                }
                failure {
                    echo "SonarQube analysis failed during execution."
                }
            }
        }
        stage('Deploy to Koyeb') {
            steps {
                script {
                    echo "Deploying Backend..."
                    def backendResponse = httpRequest(
                        url: "${KOYEB_REDEPLOYMENT_URL}",
                        httpMode: 'POST',
                        customHeaders: [[name: 'Authorization', value: "Bearer ${KOYEB_API_KEY}"]],
                        validResponseCodes: '200:299'
                    )
                    echo "Koyeb Backend Deployment Response: ${backendResponse}"
                }
            }
        }
    }
    post {
        success {
            echo 'Build was successful!'
        }
        failure {
            echo 'Build failed. Check logs.'
        }
    }
}
