pipeline {

    agent any

    environment {
        DOCKER_USERNAME = 'YOUR_DOCKERHUB_USERNAME'

        APP_IMAGE = "${DOCKER_USERNAME}/springboot-app"

        IMAGE_TAG = "v${BUILD_NUMBER}"
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/abhijeet-salunke-in/springboot-mongodb-k8s.git'
            }
        }

        stage('Build Application') {
            steps {
                sh '''
                    chmod +x mvnw || true
                    ./mvnw clean package -DskipTests
                '''
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
                    '''

                    sh '''
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

        stage('Verify Deployment') {
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
            echo 'Spring Boot pipeline completed successfully.'
        }

        failure {
            echo 'Spring Boot pipeline failed.'
        }
    }
}
