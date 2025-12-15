pipeline {
    agent any

    tools {
        maven 'Maven-3.9.11'
        jdk 'JDK-11'
    }

    environment {
        SONAR_HOST_URL = 'http://localhost:9000'
        MAVEN_OPTS = '-Xmx1024m'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
    }

    stages {
        stage('Checkout') {
            steps {
                echo '=== Stage: Récupération du code ==='
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo '=== Stage: Compilation ==='
                sh 'mvn clean compile'
            }
        }

        stage('Unit Tests') {
            steps {
                echo '=== Stage: Tests Unitaires ==='
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Code Coverage') {
            steps {
                echo '=== Stage: Couverture de Code ==='
                sh 'mvn jacoco:report'
            }
            post {
                always {
                    jacoco(
                        execPattern: '**/target/jacoco.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java'
                    )
                }
            }
        }

        stage('Quality Analysis') {
            steps {
                echo '=== Stage: Analyse de Qualité (SonarQube) ==='
                script {
                    withSonarQubeEnv('SonarQube') {
                        sh 'mvn sonar:sonar'
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                echo '=== Stage: Vérification Quality Gate ==='
                script {
                    timeout(time: 5, unit: 'MINUTES') {
                        waitForQualityGate abortPipeline: false
                    }
                }
            }
        }

        stage('Package') {
            steps {
                echo '=== Stage: Packaging ==='
                sh 'mvn package -DskipTests'
            }
            post {
                success {
                    archiveArtifacts artifacts: '**/target/*.jar',
                                   fingerprint: true
                }
            }
        }
    }

    post {
        always {
            echo '=== Pipeline terminé ==='
            cleanWs()
        }

        success {
            echo '✓ Build réussi!'
            emailext(
                subject: "✓ Build réussi - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """
                    Le build du projet ${env.JOB_NAME} a réussi.
                    Build: #${env.BUILD_NUMBER}
                    Voir: ${env.BUILD_URL}
                """,
                to: 'm.elmouktadir@gmail.com'
            )
        }

        failure {
            echo '✗ Build échoué!'
            emailext(
                subject: "✗ Build échoué - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """
                    Le build du projet ${env.JOB_NAME} a échoué.
                    Build: #${env.BUILD_NUMBER}
                    Logs: ${env.BUILD_URL}console
                """,
                to: 'elmelssez@gmail.com'
            )
        }
    }
}