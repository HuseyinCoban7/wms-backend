-- Kullanıcı oluştur
CREATE USER wmsuser WITH PASSWORD 'wms';

-- wmsdb'ye bağlan (zaten POSTGRES_DB ile oluşturulmuş)
\c wmsdb

-- İzinleri ver
GRANT ALL ON SCHEMA public TO wmsuser;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO wmsuser;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO wmsuser;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO wmsuser;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO wmsuser;