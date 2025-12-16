# 🔧 React Frontend - API Token Düzeltmesi

## Sorun
Backend çalışıyor ama React'ten 403/401 hatası alınıyor.

## Çözüm
React'te API çağrılarında token'ın doğru gönderildiğinden emin olun.

### ✅ Doğru API Service (src/services/api.js)

```javascript
const API_BASE_URL = 'http://localhost:8080/api';

// Token'ı localStorage'dan al
const getToken = () => localStorage.getItem('token');

// API isteği yapan yardımcı fonksiyon
const apiRequest = async (endpoint, options = {}) => {
  const token = getToken();
  
  const config = {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` }),  // ÖNEMLİ: Bearer ve boşluk!
      ...options.headers,
    },
  };

  const response = await fetch(`${API_BASE_URL}${endpoint}`, config);
  
  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    throw new Error(errorData.message || `HTTP Error: ${response.status}`);
  }
  
  return response.json();
};

// Reservations API
export const reservationsAPI = {
  getAll: () => apiRequest('/reservations'),           // Admin için
  getMy: () => apiRequest('/reservations/my'),         // Kullanıcı için
  getById: (id) => apiRequest(`/reservations/${id}`),
  create: (data) => apiRequest('/reservations', {
    method: 'POST',
    body: JSON.stringify(data),
  }),
  cancel: (id) => apiRequest(`/reservations/${id}/cancel`, {
    method: 'PUT',
  }),
  confirm: (id) => apiRequest(`/reservations/${id}/confirm`, {
    method: 'PUT',
  }),
  delete: (id) => apiRequest(`/reservations/${id}`, {
    method: 'DELETE',
  }),
};
```

### ✅ Login'de Token Kaydetme

```javascript
const handleLogin = async (email, password) => {
  const response = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password })
  });
  
  const data = await response.json();
  
  if (data.success) {
    // Token'ı kaydet - ÖNEMLİ!
    localStorage.setItem('token', data.data.token);
    localStorage.setItem('user', JSON.stringify(data.data.user));
  }
};
```

### ✅ AdminReservations Kullanımı

```javascript
import { useEffect, useState } from 'react';
import { reservationsAPI } from '../services/api';

function AdminReservations() {
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchReservations = async () => {
      try {
        const response = await reservationsAPI.getAll();
        setReservations(response.data || []);
      } catch (err) {
        setError(err.message);
        console.error('Rezervasyon hatası:', err);
      } finally {
        setLoading(false);
      }
    };
    
    fetchReservations();
  }, []);

  if (loading) return <div>Yükleniyor...</div>;
  if (error) return <div>Hata: {error}</div>;
  
  return (
    <div>
      <h1>Rezervasyonlar ({reservations.length})</h1>
      {reservations.length === 0 ? (
        <p>Henüz rezervasyon yok.</p>
      ) : (
        reservations.map(res => (
          <div key={res.id}>
            {res.userName} - {res.carBrand} {res.carModel}
          </div>
        ))
      )}
    </div>
  );
}
```

## 🔍 Debug: Token Kontrolü

Browser Console'da token'ı kontrol edin:

```javascript
console.log('Token:', localStorage.getItem('token'));
```

Eğer token `null` veya `undefined` ise, login işlemi token'ı kaydetmemiş demektir.

## ✅ Test Edildi - Çalışan Endpoint'ler

| Endpoint | Method | Durum |
|----------|--------|-------|
| `/auth/login` | POST | ✅ |
| `/auth/check` | GET | ✅ |
| `/auth/check-admin` | GET | ✅ |
| `/reservations` | GET | ✅ (boş array - veri yok) |
| `/cars` | GET | ✅ |
| `/categories` | GET | ✅ |

