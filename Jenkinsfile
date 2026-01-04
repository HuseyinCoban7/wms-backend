pipeline {
    agent any
    
    stages {
        stage('1 - Checkout') {
            steps {
                echo '========== 1. Git Checkout =========='
                checkout scm
            }
        }

        stage('2 - Build') {
            steps {
                echo '========== 2. Maven Build =========='
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('3 - Unit Tests') {
            steps {
                echo '========== 3. Unit Tests =========='
                sh '''
                    mvn test \
                    -Dtest=*ServiceTest \
                    -Dspring.profiles.active=test
                '''
            }
            post {
                always {
                    sh 'mkdir -p custom-reports/unit && cp target/surefire-reports/*.xml custom-reports/unit/ || true'
                }
            }
        }

        stage('4 - Integration Tests') {
            steps {
                echo '========== 4. Integration Tests =========='
                sh '''
                    mvn test \
                    -Dtest=*IntegrationTest \
                    -Dspring.profiles.active=ci
                '''
            }
            post {
                always {
                    sh 'mkdir -p custom-reports/integration && cp target/surefire-reports/*.xml custom-reports/integration/ || true'
                }
            }
        }

        stage('5 - Run System in Docker') {
            steps {
                echo '========== 5. Docker Compose ile Sistem Ayağa Kaldırılıyor =========='
                sh '''
                    docker rm -f wms-postgres wms-backend selenium-chrome || true
                    docker-compose down -v || true
                    docker-compose up -d wms-postgres backend
                    
                    echo "⏳ PostgreSQL hazır olana kadar bekleniyor..."
                    for i in {1..30}; do
                        docker exec wms-postgres pg_isready -U wmsuser && break || sleep 2
                    done
                    echo "✅ PostgreSQL hazır!"
                    
                    echo "⏳ Backend hazır olana kadar bekleniyor..."
                    for i in {1..30}; do
                        curl -f http://host.docker.internal:8089/actuator/health && break || sleep 2
                    done
                    echo "✅ Backend hazır!"
                    
                    docker-compose up -d selenium-chrome
                    echo "⏳ Selenium hazır olana kadar bekleniyor..."
                    for i in {1..20}; do
                        curl -f http://host.docker.internal:4444/wd/hub/status && break || sleep 2
                    done
                    echo "✅ Selenium hazır!"
                '''
            }
        }

        stage('6.1 - E2E Test: Login') {
            steps {
                echo '========== 6.1. E2E Test: Login =========='
                sh '''
                    mvn test \
                    -Dtest=LoginE2ETest \
                    -Dspring.profiles.active=test \
                    -Dapp.url=http://host.docker.internal:8089 \
                    -Dselenium.remote.url=http://host.docker.internal:4444
                '''
            }
            post {
                always {
                    sh 'mkdir -p custom-reports/e2e && cp target/surefire-reports/*.xml custom-reports/e2e/ || true'
                }
            }
        }

        stage('6.2 - E2E Test: Product CRUD') {
            steps {
                echo '========== 6.2. E2E Test: Product CRUD =========='
                sh '''
                    mvn test \
                    -Dtest=ProductE2ETest \
                    -Dspring.profiles.active=test \
                    -Dapp.url=http://host.docker.internal:8089 \
                    -Dselenium.remote.url=http://host.docker.internal:4444
                '''
            }
            post {
                always {
                    sh 'mkdir -p custom-reports/e2e && cp target/surefire-reports/*.xml custom-reports/e2e/ || true'
                }
            }
        }

        stage('6.4 - E2E Test: User Logout') {
            when {
                expression {
                    return fileExists('src/test/java/com/wms/e2e/LogoutE2ETest.java')
                }
            }
            steps {
                echo '========== 6.4. E2E Test: User Logout =========='
                sh '''
                    mvn test \
                    -Dtest=LogoutE2ETest \
                    -Dspring.profiles.active=test \
                    -Dapp.url=http://host.docker.internal:8089 \
                    -Dselenium.remote.url=http://host.docker.internal:4444
                '''
            }
            post {
                always {
                    sh 'mkdir -p custom-reports/e2e && cp target/surefire-reports/*.xml custom-reports/e2e/ || true'
                }
            }
        }

        stage('6.5 - E2E Test: Product Search') {
            when {
                expression {
                    return fileExists('src/test/java/com/wms/e2e/ProductSearchE2ETest.java')
                }
            }
            steps {
                echo '========== 6.5. E2E Test: Product Search =========='
                sh '''
                    mvn test \
                    -Dtest=ProductSearchE2ETest \
                    -Dspring.profiles.active=test \
                    -Dapp.url=http://host.docker.internal:8089 \
                    -Dselenium.remote.url=http://host.docker.internal:4444
                '''
            }
            post {
                always {
                    sh 'mkdir -p custom-reports/e2e && cp target/surefire-reports/*.xml custom-reports/e2e/ || true'
                }
            }
        }
    }

    post {
        always {
            echo '========== Pipeline Tamamlandı =========='
            
            // Tüm test raporlarını custom-reports klasöründen topla
            junit allowEmptyResults: true, testResults: 'custom-reports/**/*.xml'
            
            sh 'docker-compose down -v || true'
            sh 'rm -rf custom-reports || true'
        }
        success {
            echo '✅ Build başarılı!'
        }
        failure {
            echo '❌ Build başarısız!'
        }
    }
}
