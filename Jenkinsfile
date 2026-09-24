pipeline {

    agent any

    environment {
        DOCKER_USERNAME = 'abhisalunke16'

        APP_IMAGE = 'abhisalunke16/springboot-mongodb-app'

        IMAGE_TAG = "v${BUILD_NUMBER}"
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/abhijeet-salunke-in/springboot-mongodb-k8s.git'
            }
        }

        stage('Maven Build') {
            steps {
                sh '''
                    chmod +x mvnw || true

                    ./mvnw clean package -DskipTests
                '''
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh '''
                        ./mvnw sonar:sonar \
                        -Dsonar.projectKey=springboot-mongodb-k8s \
                        -Dsonar.projectName=springboot-mongodb-k8s
                    '''
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh '''
                    docker build \
                    -t ${APP_IMAGE}:${IMAGE_TAG} .
                '''
            }
        }

        stage('Docker Login & Push') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'docker_hub',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASSWORD'
                    )
                ]) {

                    sh '''
                        echo "$DOCKER_PASSWORD" | docker login \
                        -u "$DOCKER_USER" \
                        --password-stdin

                        docker push ${APP_IMAGE}:${IMAGE_TAG}
                    '''
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                sh '''
                    kubectl apply -f k8s/
                '''
            }
        }

        stage('Verify Kubernetes Deployment') {
            steps {
                sh '''
                    kubectl get pods
                    kubectl get deployments
                    kubectl get services
                '''
            }
        }
    }

    post {

        success {
            echo 'Spring Boot CI/CD pipeline completed successfully.'
        }

        failure {
            echo 'Spring Boot CI/CD pipeline failed.'
        }
    }
}
