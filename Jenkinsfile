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
                    sh 'mkdir -p custom-reports/unit && cp target/surefire-reports/*.xml custom-reports/unit/ || true'
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
                    sh 'mkdir -p custom-reports/integration && cp target/surefire-reports/*.xml custom-reports/integration/ || true'
                    echo '📊 Entegrasyon test raporları toplandı'
                }
            }
        }

        // ============================================================
        // 5. SİSTEMİ DOCKER'DA AYAĞA KALDIR (5 puan)
        // ============================================================
        stage('5 - Run System in Docker') {
            steps {
                script {
                    try {
                        echo '🐳 Eski container\'ları temizleniyor...'
                        sh '''
                            docker ps -a --filter "name=selenium-chrome" -q | xargs -r docker rm -f || true
                            docker ps -a --filter "name=wms-backend" -q | xargs -r docker rm -f || true
                            docker ps -a --filter "name=wms-postgres" -q | xargs -r docker rm -f || true
                        '''
                        sh 'docker-compose down -v || true'

                        echo '🐘 PostgreSQL, Backend ve Selenium ayağa kaldırılıyor...'
                        sh '''
                            set -e
                            docker-compose build --no-cache backend
                            docker-compose up -d
                            echo "👉 docker-compose ps çıktısı:"
                            docker-compose ps
                        '''

                        echo '⏳ Backend hazır olana kadar bekleniyor...'
                        sh '''
                            set -e
                            TIMEOUT=180
                            ELAPSED=0
                            
                            while [ $ELAPSED -lt $TIMEOUT ]; do
                                echo "---- CURL TRY (ELAPSED=$ELAPSED) ----"
                                curl -sS http://host.docker.internal:8089/actuator/health || echo "curl FAILED"
                                echo "-------------------------------------"
                                
                                if curl -sf http://host.docker.internal:8089/actuator/health > /dev/null 2>&1; then
                                    echo "✅ Backend hazır! ($ELAPSED saniye)"
                                    exit 0
                                fi
                                
                                echo "⏳ Backend henüz hazır değil... ($ELAPSED/$TIMEOUT saniye)"
                                sleep 5
                                ELAPSED=$((ELAPSED + 5))
                            done
                            
                            echo "❌ Backend $TIMEOUT saniye içinde hazır OLAMADI!"
                            echo "👉 Backend logları:"
                            docker-compose logs --tail=200 backend || true
                            exit 1
                        '''

                        echo '⏳ Selenium hazır olana kadar bekleniyor...'
                        sh '''
                            set -e
                            TIMEOUT=90
                            ELAPSED=0

                            while [ $ELAPSED -lt $TIMEOUT ]; do
                                if curl -sSf http://host.docker.internal:4444/wd/hub/status > /dev/null 2>&1; then
                                    echo "✅ Selenium hazır! ($ELAPSED saniye)"
                                    exit 0
                                fi
                                echo "⏳ Selenium henüz hazır değil... ($ELAPSED/$TIMEOUT saniye)"
                                sleep 3
                                ELAPSED=$((ELAPSED + 3))
                            done

                            echo "❌ Selenium $TIMEOUT saniye içinde hazır OLAMADI!"
                            echo "👉 Selenium logları:"
                            docker-compose logs --tail=200 selenium-chrome || true
                            exit 1
                        '''

                        echo '✅ Tüm servisler hazır, 6. stage\'e geçiliyor.'
                    } catch (err) {
                        echo "❌ '5 - Run System in Docker' stage BAŞARISIZ: ${err}"
                        error("Backend veya Selenium ayağa kalkamadığı için pipeline durduruldu.")
                    }
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
                    -Dapp.url=http://host.docker.internal:8089 \
                    -Dselenium.remote.url=http://host.docker.internal:4444
                '''
            }
            post {
                always {
                    sh 'mkdir -p custom-reports/e2e && cp target/surefire-reports/*.xml custom-reports/e2e/ || true'
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
                    -Dapp.url=http://host.docker.internal:8089 \
                    -Dselenium.remote.url=http://host.docker.internal:4444
                '''
            }
            post {
                always {
                    sh 'mkdir -p custom-reports/e2e && cp target/surefire-reports/*.xml custom-reports/e2e/ || true'
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
                    -Dapp.url=http://host.docker.internal:8089 \
                    -Dselenium.remote.url=http://host.docker.internal:4444
                '''
            }
            post {
                always {
                    sh 'mkdir -p custom-reports/e2e && cp target/surefire-reports/*.xml custom-reports/e2e/ || true'
                    echo '📊 E2E Test 3 raporu toplandı'
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
                echo '========== 6.4. E2E Senaryo: Kullanıcı Logout =========='
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
                    echo '📊 E2E Test 4 raporu toplandı'
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
                echo '========== 6.5. E2E Senaryo: Product Search =========='
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
                    echo '📊 E2E Test (Product Search) raporu toplandı'
                }
            }
        }
    }

    post {
        always {
            echo '========== Pipeline tamamlandı =========='
            script {
                // Tüm test raporlarını custom-reports klasöründen topla
                junit allowEmptyResults: true, testResults: 'custom-reports/**/*.xml'
                
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

                // Container'ları ve custom-reports klasörünü temizle
                sh 'docker-compose down -v || true'
                sh 'rm -rf custom-reports || true'
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
