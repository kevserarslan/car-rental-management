# 🚀 Postman ile API Test Adımları

**API Base URL:** `http://localhost:8080/api`

---

## 1️⃣ STEP 1: Register (Kayıt Ol)

**Endpoint:** `POST http://localhost:8080/api/auth/register`

**Body (JSON):**
```json
{
  "name": "Ahmet Yılmaz",
  "email": "ahmet@example.com",
  "password": "password123",
  "phone": "05551234567",
  "address": "İstanbul, Türkiye"
}
```

**Beklenen Yanıt:**
```json
{
  "success": true,
  "message": "Kullanıcı başarıyla kaydedildi",
  "data": {
    "id": 1,
    "name": "Ahmet Yılmaz",
    "email": "ahmet@example.com"
  }
}
```

---

## 2️⃣ STEP 2: Login (Giriş Yap)

**Endpoint:** `POST http://localhost:8080/api/auth/login`

**Body (JSON):**
```json
{
  "email": "ahmet@example.com",
  "password": "password123"
}
```

**Beklenen Yanıt:**
```json
{
  "success": true,
  "message": "Giriş başarılı",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": 1,
      "name": "Ahmet Yılmaz",
      "email": "ahmet@example.com"
    }
  }
}
```

⚠️ **ÖNEMLİ:** Token'ı kopyala! Sonraki istekler için kullanacaksın.

---

## 3️⃣ STEP 3: Arabaları Listele (Public - Token gerekmiyor)

**Endpoint:** `GET http://localhost:8080/api/cars`

**Headers:**
```
Content-Type: application/json
```

**Beklenen Yanıt:** 200 OK
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "brand": "BMW",
      "model": "X5",
      "year": 2023,
      "dailyRate": 500.00,
      "status": "AVAILABLE"
    }
  ]
}
```

---

## 4️⃣ STEP 4: Kategorileri Listele (Public - Token gerekmiyor)

**Endpoint:** `GET http://localhost:8080/api/categories`

**Headers:**
```
Content-Type: application/json
```

**Beklenen Yanıt:** 200 OK

---

## 5️⃣ STEP 5: Authenticated İstek - Rezervasyon (Token Gerekli)

**Endpoint:** `POST http://localhost:8080/api/reservations`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer YOUR_TOKEN_HERE
```

⚠️ **Adım 2'den aldığın token'ı bu şekilde koy:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Body (JSON):**
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

## ✅ Postman Ayarları

### Authorization Tab'ı Kullan (Önerilir):

1. **Type:** Select "Bearer Token"
2. **Token:** Adım 2'den aldığın token'ı yapıştır
3. Postman otomatik olarak `Authorization: Bearer TOKEN` header'ı ekleyecek

### Manual Header Yöntemi:

1. **Headers** tab'ına git
2. **New Header** ekle:
   - Key: `Authorization`
   - Value: `Bearer YOUR_TOKEN`

---

## 🔍 Hata Çözmesi

| Hata | Çözüm |
|------|-------|
| 403 Forbidden | Token eksik veya geçersiz → Adım 2'deki Login'i tekrar yap |
| 400 Bad Request | JSON formatı yanlış → Body'ni kontrol et |
| 401 Unauthorized | Token geçersiz/süresi dolmuş → Login yap ve yeni token al |
| 404 Not Found | Endpoint yanlış → URL'yi kontrol et |

---

## 📝 Postman Environment Dosyasını Oluştur

Dosya: `CarRental_Environment.postman_environment.json`

```json
{
  "id": "car-rental-env",
  "name": "Car Rental API",
  "values": [
    {
      "key": "base_url",
      "value": "http://localhost:8080/api",
      "enabled": true
    },
    {
      "key": "token",
      "value": "",
      "enabled": true
    }
  ],
  "_postman_variable_scope": "environment",
  "_postman_exported_at": "2025-12-03T00:00:00.000Z",
  "_postman_exported_format": 2
}
```

Bu dosyayı Postman'da import et:
1. Sağ üstte **Environments** klik
2. **Import** tıkla
3. Dosyayı seç
4. **Register** isteğinin sonunda, **Tests** tab'ına git ve şunu ekle:

```javascript
if (pm.response.code === 200) {
    pm.environment.set("token", pm.response.json().data.token);
}
```

Bu token'ı sonraki isteklerde `{{token}}` olarak kullanabilirsin!

---

## 🎯 Test Sırası:
1. ✅ Register
2. ✅ Login (token al)
3. ✅ Get Cars (public)
4. ✅ Get Categories (public)
5. ✅ Create Reservation (authenticated)

Başarılar! 🚀

