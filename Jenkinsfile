pipeline {
    agent any
    environment {
        KOYEB_API_KEY = credentials('koyeb-api')
        KOYEB_REDEPLOYMENT_URL = credentials('koyeb-deployment-url')
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
                withSonarQubeEnv('sonarqube-25.5.0.107428') {
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
