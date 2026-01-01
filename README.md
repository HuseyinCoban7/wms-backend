# Warehouse Management System (WMS)

Kapsamlı, production-ready Depo Yönetim Sistemi.

## Teknoloji Stack

- **Backend**: Spring Boot 3.2.0, Java 17, Maven
- **Database**: PostgreSQL 15
- **Migration**: Flyway
- **Security**: Spring Security + JWT
- **Testing**: JUnit 5, Mockito, Testcontainers, Selenium
- **CI/CD**: Jenkins
- **Containerization**: Docker, Docker Compose

## Domain Model

- Users (ADMIN/MANAGER/WORKER)
- Warehouses
- Locations (depo içi lokasyonlar)
- Products (SKU, barcode, min stok)
- Inventory (product + location bazlı)
- Suppliers
- PurchaseOrders + Items
- Orders + Items
- StockMovements (IN/OUT/TRANSFER/ADJUSTMENT)
- StockReservations

## İş Kuralları

- Stok negatif olamaz
- Transfer işlemleri transactional
- PurchaseOrder onaylanmadan stok girişi yok
- Order oluştururken yeterli stok kontrolü
- Inventory (product, location) unique
- Soft delete + audit fields

## Lokalde Çalıştırma

### Gereksinimler

- Java 17
- Maven 3.8+
- Docker & Docker Compose

### Adımlar

1. **Clone repository**
```bash
git clone <repo-url>
cd warehouse-management-system