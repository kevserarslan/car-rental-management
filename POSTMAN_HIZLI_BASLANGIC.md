# 🎯 Hızlı Başlangıç - API Test Rehberi

## ⚙️ Sunucuyu Başlat

IntelliJ IDEA'da `CarRentalApplication` sınıfını çalıştır veya:

```bash
cd C:\Users\Kevser\CarRental
mvnw.cmd spring-boot:run
```

✅ Bekle: `Tomcat started on port 8080 (http) with context path '/api'`

---

## 📱 Postman Kurulumu (Hızlı)

### 1. Postman Dosyalarını Import Et

**Postman açınca:**
1. Sol üstte **Import** butonuna tıkla
2. `CarRental_API.postman_collection.json` dosyasını seç
3. **Import** tıkla

### 2. Environment Dosyasını Import Et

1. Sağ üstte **Environments** (dişli çark yanında) klik
2. **Import** tıkla
3. `CarRental_Environment.postman_environment.json` dosyasını seç

### 3. Environment'i Seç

Sağ üstte açılan dropdown'da `CarRental Environment` seç.

---

## 🚀 Test Adımları (Sırasıyla)

### ✅ 1. Register (Kayıt Ol)

**Postman Collection'da:** Auth → Register

Otomatik gelmesi gereken body:
```json
{
  "name": "Ahmet Yılmaz",
  "email": "ahmet@example.com",
  "password": "password123",
  "phone": "05551234567",
  "address": "İstanbul, Türkiye"
}
```

**Sonuç Beklenir:** ✅ 200 OK
```json
{
  "success": true,
  "message": "Kullanıcı başarıyla kaydedildi"
}
```

---

### ✅ 2. Login (Giriş Yap)

**Postman Collection'da:** Auth → Login

Body:
```json
{
  "email": "ahmet@example.com",
  "password": "password123"
}
```

**Sonuç Beklenir:** ✅ 200 OK

**Yanıtta göreceklerin:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": 1,
    "name": "Ahmet Yılmaz",
    "email": "ahmet@example.com"
  }
}
```

⚠️ **ÖNEMLİ:** 

**Otomatik Token Kaydı:**
- **Tests** tab'ında bu kod var:
```javascript
if (pm.response.code === 200) {
    pm.environment.set("token", pm.response.json().data.token);
}
```
- Yapıştır ve **Send**'e tıkla
- Token otomatik olarak `{{token}}` variable'ına kaydedilecek

---

### ✅ 3. Arabaları Listele (Herkes için - Token YOK)

**Postman Collection'da:** Cars → Get All Cars

✅ **Expected:** 200 OK

Hiçbir Header gerekmiyor. Direkt çalışacak!

---

### ✅ 4. Kategorileri Listele (Herkes için - Token YOK)

**Postman Collection'da:** Categories → Get All Categories

✅ **Expected:** 200 OK

---

### ✅ 5. Rezervasyon Oluştur (Token GEREKLI)

**Postman Collection'da:** Reservations → Create Reservation

⚠️ **Önemli:** Authorization Header'ında token olmalı!

**Collection'da önceden ayarlanmış:**
```
Authorization: Bearer {{token}}
```

Body:
```json
{
  "carId": 1,
  "startDate": "2025-12-10",
  "endDate": "2025-12-15",
  "pickupLocation": "İstanbul Havalimanı",
  "returnLocation": "Taksim"
}
```

---

## 🛠️ Manual Test (Token olmadan)

Eğer Collection import etmek istemezsen:

### Register
```
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "name": "Ahmet Yılmaz",
  "email": "ahmet@example.com",
  "password": "password123",
  "phone": "05551234567",
  "address": "İstanbul, Türkiye"
}
```

### Login
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "ahmet@example.com",
  "password": "password123"
}
```

**Yanıttan token'ı kopyala!**

### Get Cars (Public - Token YOK)
```
GET http://localhost:8080/api/cars
Content-Type: application/json
```

### Authenticated Request (Token GEREKLI)
```
POST http://localhost:8080/api/reservations
Content-Type: application/json
Authorization: Bearer <BURAYA_TOKEN_YAPISTIR>

{
  "carId": 1,
  "startDate": "2025-12-10",
  "endDate": "2025-12-15",
  "pickupLocation": "İstanbul Havalimanı",
  "returnLocation": "Taksim"
}
```

---

## ❌ Hata Çözmesi

| Error | Çözüm |
|-------|-------|
| **403 Forbidden** | Token eksik veya yanlış. Register → Login yap ve token al |
| **400 Bad Request** | JSON formatında hata. Body'yi kontrol et |
| **401 Unauthorized** | Token geçersiz/süresi dolmuş. Yeniden Login yap |
| **404 Not Found** | Endpoint yanlış. URL'yi kontrol et (`/api` olmalı) |
| **500 Internal Server Error** | Server hatası. Console'u kontrol et |

---

## ✨ İpuçları

1. **Token'ı test et:** Token aldıktan sonra, Reservations isteğine token'ı koydun mu kontrol et
2. **Environment'i seç:** Sağ üstte env seçmeyi unutma, yoksa `{{base_url}}` ve `{{token}}` çalışmaz
3. **Veri Güncelle:** Farklı email/password ile yeni user oluşturabilirsin
4. **Tarayıcıda test:** `http://localhost:8080/api/cars` ziyaret et (herkes görebilir)

---

## 📊 API Endpoints Özeti

| Metot | Endpoint | Auth? | Açıklama |
|-------|----------|-------|----------|
| POST | `/api/auth/register` | ❌ | Kayıt ol |
| POST | `/api/auth/login` | ❌ | Giriş yap |
| GET | `/api/cars` | ❌ | Tüm arabaları listele |
| GET | `/api/cars/{id}` | ❌ | Araba detayı |
| GET | `/api/categories` | ❌ | Kategorileri listele |
| POST | `/api/reservations` | ✅ | Rezervasyon yap |
| GET | `/api/reservations` | ✅ | Rezervasyonlarım |

---

**Başarıyla tamamlanırsa, tüm endpoint'ler çalışıyor demektir! 🎉**

