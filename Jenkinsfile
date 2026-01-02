pipeline {
    agent any

    

    environment {
        APP_NAME = "wms-backend"
        APP_PORT = "8089"
        COMPOSE_PROJECT_NAME = "wms-jenkins-${BUILD_NUMBER}"
    }

    options {
        timestamps()
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }


    stages {

        // ============================================================
        // 1. GITHUB'DAN KODLARI ÇEK (5 puan)
        // ============================================================
        stage('1 - Checkout from GitHub') {
            steps {
                echo '========== 1. GitHub\'dan kodlar çekiliyor =========='
                checkout scm
            }
        }

        // ============================================================
        // 2. KODLARI BUILD ET (5 puan)
        // ============================================================
        stage('2 - Build') {
            steps {
                echo '========== 2. Proje build ediliyor =========='
                sh 'mvn clean package -DskipTests'
            }
            post {
                success {
                    echo '✅ Build başarılı'
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
                failure {
                    echo '❌ Build başarısız'
                }
            }
        }

        // ============================================================
        // 3. BİRİM TESTLERİ ÇALIŞTIR VE RAPORLA (15 puan)
        // ============================================================
        stage('3 - Unit Tests') {
            steps {
                echo '========== 3. Birim testleri çalıştırılıyor =========='
                sh '''
                    mvn test \
                    -Dtest=*ServiceTest \
                    -Dspring.profiles.active=test
                '''
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                    echo '📊 Birim test raporları toplandı'
                }
            }
        }

        // ============================================================
        // 4. ENTEGRASYON TESTLERİ ÇALIŞTIR VE RAPORLA (15 puan)
        // ============================================================
        stage('4 - Integration Tests') {
    steps {
        echo '========== 4. Entegrasyon testleri çalıştırılıyor =========='
        sh '''
            mvn test \
            -Dtest=*IntegrationTest \
            -Dspring.profiles.active=ci
        '''
    }
    post {
        always {
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
            echo '📊 Entegrasyon test raporları toplandı'
        }
    }
}

       // ============================================================
        // 5. SİSTEMİ DOCKER CONTAINER'DA ÇALIŞTIR (5 puan)
        // ============================================================
        stage('5 - Run System in Docker') {
            steps {
                echo '========== 5. Docker Compose ile sistem ayağa kaldırılıyor =========='
                script {
                    // Eski container'ları zorla temizle (çakışma önlemi)
                    sh '''
                        docker rm -f wms-postgres backend selenium-chrome || true
                        docker-compose down -v || true
                    '''

                    // PostgreSQL ve Backend'i ayağa kaldır
                    sh 'docker-compose up -d wms-postgres backend'

                    // PostgreSQL'in hazır olmasını bekle
                    echo 'PostgreSQL hazır olması bekleniyor...'
                    sh '''
                        for i in {1..30}; do
                            if docker exec wms-postgres pg_isready -U wmsuser -d wmsdb > /dev/null 2>&1; then
                                echo "✅ PostgreSQL hazır!"
                                break
                            fi
                            echo "Bekleniyor... ($i/30)"
                            sleep 2
                        done
                    '''

                    // Backend'in hazır olmasını bekle
                    echo 'Backend uygulaması hazır olması bekleniyor...'
                    sh '''
                        for i in {1..60}; do
                            if curl -sSf http://localhost:8089/actuator/health > /dev/null 2>&1; then
                                echo "✅ Backend hazır!"
                                break
                            fi
                            echo "Bekleniyor... ($i/60)"
                            sleep 2
                        done
                    '''

                    // Selenium container'ı ayağa kaldır
                    sh 'docker-compose up -d selenium-chrome'

                    echo 'Selenium hazır olması bekleniyor...'
                    sh '''
                        for i in {1..20}; do
                            if curl -sSf http://localhost:4444/wd/hub/status > /dev/null 2>&1; then
                                echo "✅ Selenium hazır!"
                                break
                            fi
                            echo "Bekleniyor... ($i/20)"
                            sleep 2
                        done
                    '''

                    // Container durumlarını göster
                    sh 'docker-compose ps'
                }
            }
        }

        // ============================================================
        // 6. ÇALIŞAN SİSTEM ÜZERİNDE E2E TEST SENARYOLARI (55 puan)
        // ============================================================

        stage('6.1 - E2E Test: Admin Login & Redirect') {
            steps {
                echo '========== 6.1. E2E Senaryo: Admin Login ve Dashboard Redirect =========='
                sh '''
                    mvn test \
                    -Dtest=LoginE2ETest#testLogin_Success_AdminRedirectsToAdminDashboard \
                    -Dspring.profiles.active=test \
                    -Dapp.url=http://localhost:8089 \
                    -Dselenium.remote.url=http://localhost:4444
                '''
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                    echo '📊 E2E Test 1 raporu toplandı'
                }
            }
        }

        stage('6.2 - E2E Test: Invalid Login Error') {
            steps {
                echo '========== 6.2. E2E Senaryo: Geçersiz Login Hata Mesajı =========='
                sh '''
                    mvn test \
                    -Dtest=LoginE2ETest#testLogin_InvalidCredentials_ShowsError \
                    -Dspring.profiles.active=test \
                    -Dapp.url=http://localhost:8089 \
                    -Dselenium.remote.url=http://localhost:4444
                '''
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                    echo '📊 E2E Test 2 raporu toplandı'
                }
            }
        }

        stage('6.3 - E2E Test: Product CRUD Operations') {
            steps {
                echo '========== 6.3. E2E Senaryo: Ürün CRUD İşlemleri =========='
                sh '''
                    mvn test \
                    -Dtest=ProductE2ETest \
                    -Dspring.profiles.active=test \
                    -Dapp.url=http://localhost:8089 \
                    -Dselenium.remote.url=http://localhost:4444
                '''
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                    echo '📊 E2E Test 3 raporu toplandı'
                }
            }
        }

        // ============================================================
        // EK SENARYOLAR (Her biri +2 puan, max 10 senaryo)
        // ============================================================

        stage('6.4 - E2E Test: Standard User Login & Redirect') {
            when {
                expression {
                    // Eğer bu test metodu varsa çalıştır
                    return sh(
                        script: 'grep -r "testLogin_Success_StandardUserRedirectsToUserDashboard" src/test/java/com/wms/e2e/ || true',
                        returnStatus: true
                    ) == 0
                }
            }
            steps {
                echo '========== 6.4. E2E Senaryo: Standart Kullanıcı Login =========='
                sh '''
                    mvn test \
                    -Dtest=LoginE2ETest#testLogin_Success_StandardUserRedirectsToUserDashboard \
                    -Dspring.profiles.active=test \
                    -Dapp.url=http://localhost:8089 \
                    -Dselenium.remote.url=http://localhost:4444
                '''
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                    echo '📊 E2E Test 4 raporu toplandı'
                }
            }
        }

        stage('6.5 - E2E Test: User Logout') {
            when {
                expression {
                    return fileExists('src/test/java/com/wms/e2e/LogoutE2ETest.java')
                }
            }
            steps {
                echo '========== 6.5. E2E Senaryo: Kullanıcı Logout =========='
                sh '''
                    mvn test \
                    -Dtest=LogoutE2ETest \
                    -Dspring.profiles.active=test \
                    -Dapp.url=http://localhost:8089 \
                    -Dselenium.remote.url=http://localhost:4444
                '''
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                    echo '📊 E2E Test 5 raporu toplandı'
                }
            }
        }

        stage('6.6 - E2E Test: Product Search') {
            when {
                expression {
                    return fileExists('src/test/java/com/wms/e2e/ProductSearchE2ETest.java')
                }
            }
            steps {
                echo '========== 6.6. E2E Senaryo: Ürün Arama =========='
                sh '''
                    mvn test \
                    -Dtest=ProductSearchE2ETest \
                    -Dspring.profiles.active=test \
                    -Dapp.url=http://localhost:8089 \
                    -Dselenium.remote.url=http://localhost:4444
                '''
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                    echo '📊 E2E Test 6 raporu toplandı'
                }
            }
        }

        stage('6.7 - E2E Test: Stock Management') {
            when {
                expression {
                    return fileExists('src/test/java/com/wms/e2e/StockE2ETest.java')
                }
            }
            steps {
                echo '========== 6.7. E2E Senaryo: Stok Yönetimi =========='
                sh '''
                    mvn test \
                    -Dtest=StockE2ETest \
                    -Dspring.profiles.active=test \
                    -Dapp.url=http://localhost:8089 \
                    -Dselenium.remote.url=http://localhost:4444
                '''
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                    echo '📊 E2E Test 7 raporu toplandı'
                }
            }
        }

        // 6.8, 6.9, 6.10 için aynı pattern'i kullanabilirsin

    }

    post {
        always {
            echo '========== Pipeline tamamlandı =========='
            script {
                // Container loglarını kaydet
                sh 'docker-compose logs backend > backend.log 2>&1 || true'
                sh 'docker-compose logs wms-postgres > postgres.log 2>&1 || true'
                sh 'docker-compose logs selenium-chrome > selenium.log 2>&1 || true'

                archiveArtifacts artifacts: '*.log', allowEmptyArchive: true

                // Test raporlarını HTML olarak da arşivle
                publishHTML([
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'target/surefire-reports',
                    reportFiles: 'index.html',
                    reportName: 'Test Report'
                ])

                // Container'ları temizle
                sh 'docker-compose down -v || true'
            }
        }
        success {
            echo "✅ Build #${env.BUILD_NUMBER} BAŞARILI - Tüm testler geçti!"
        }
        failure {
            echo "❌ Build #${env.BUILD_NUMBER} BAŞARISIZ - Logları inceleyin"
        }
    }
}
