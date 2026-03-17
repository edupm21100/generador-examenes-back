pipeline {
    agent any

    tools {
        maven 'maven3'
    }

    stages {
        stage('Construir (Maven)') {
            steps {
                dir('examen-backend') {
                    echo 'Compilando el proyecto...'
                    sh 'mvn clean verify' 
                }
            }
        }
        
        stage('Auditoría de Calidad (SonarQube)') {
            environment {
                scannerHome = tool 'sonar-scanner'
            }
            steps {
                dir('examen-backend') {
                    echo 'Pasando el escáner de SonarQube...'
                    withSonarQubeEnv('sonar') {
                        sh """
                        ${scannerHome}/bin/sonar-scanner \
                          -Dsonar.projectKey=examen-backend \
                          -Dsonar.projectName="Examen Backend" \
                          -Dsonar.java.binaries=target/classes
                        """
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Despliegue Automático en Docker') {
            steps {
                sh '''
                    if ! command -v docker &> /dev/null; then
                        echo "Docker no está instalado. Instalando cliente de Docker..."
                        curl -fsSL https://get.docker.com -o get-docker.sh
                        sh get-docker.sh
                    fi
                '''
                
                dir('examen-backend') { 
                    echo '¡Aprobado! Desplegando la nueva versión...'
                    sh 'docker compose down'
                    sh 'docker compose up -d --build'
                }
            }
        }
    }
    
    post {
        success {
            echo 'Despliegue completado con éxito. El código está en producción.'
        }
        failure {
            echo 'El pipeline ha fallado.'
        }
    }
}