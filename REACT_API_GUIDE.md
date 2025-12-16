# 🚗 CarRental API - React Frontend Entegrasyonu

## 📍 Base URL
```
http://localhost:8080/api
```

## 🔐 Authentication Endpoints

### Register (Kayıt Ol)
```http
POST /auth/register
Content-Type: application/json

{
  "name": "Ahmet Yılmaz",
  "email": "ahmet@example.com",
  "password": "password123",
  "phone": "05551234567",
  "address": "İstanbul, Türkiye"
}
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "type": "Bearer",
    "user": {
      "id": 1,
      "name": "Ahmet Yılmaz",
      "email": "ahmet@example.com",
      "role": "USER"
    }
  }
}
```

### Login (Giriş Yap)
```http
POST /auth/login
Content-Type: application/json

{
  "email": "ahmet@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "type": "Bearer",
    "user": {
      "id": 1,
      "name": "Ahmet Yılmaz",
      "email": "ahmet@example.com",
      "role": "USER"
    }
  }
}
```

---

## 👤 User Endpoints

### Get Current User Profile (Kendi Profilim)
```http
GET /users/me
Authorization: Bearer <token>
```

### Update Current User Profile
```http
PUT /users/me
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Ahmet Yılmaz",
  "phone": "05559876543",
  "address": "Ankara, Türkiye"
}
```

---

## 🚙 Car Endpoints (PUBLIC - Token Gerekmez)

### Get All Cars
```http
GET /cars
```

### Get Car by ID
```http
GET /cars/{id}
```

### Get Available Cars
```http
GET /cars/available
```

### Get Cars by Category
```http
GET /cars/category/{categoryId}
```

---

## 📂 Category Endpoints (PUBLIC - Token Gerekmez)

### Get All Categories
```http
GET /categories
```

### Get Category by ID
```http
GET /categories/{id}
```

---

## 📅 Reservation Endpoints (Token Gerekli)

### Create Reservation
```http
POST /reservations
Authorization: Bearer <token>
Content-Type: application/json

{
  "carId": 1,
  "startDate": "2025-12-15",
  "endDate": "2025-12-20",
  "notes": "Havaalanında teslim"
}
```

### Get My Reservations
```http
GET /reservations/my
Authorization: Bearer <token>
```

### Cancel Reservation
```http
PUT /reservations/{id}/cancel
Authorization: Bearer <token>
```

---

## 💱 Currency API (PUBLIC - External API)

### Convert Currency
```http
GET /currency/convert?amount=100&from=USD&to=TRY
```

### Get Exchange Rates
```http
GET /currency/rates?base=USD
```

---

## 🔧 React'te Kullanım Örneği

### API Service (src/services/api.js)
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
      ...(token && { Authorization: `Bearer ${token}` }),
      ...options.headers,
    },
  };

  const response = await fetch(`${API_BASE_URL}${endpoint}`, config);
  const data = await response.json();
  
  if (!response.ok) {
    throw new Error(data.message || 'API Error');
  }
  
  return data;
};

// Auth API
export const authAPI = {
  login: (credentials) => apiRequest('/auth/login', {
    method: 'POST',
    body: JSON.stringify(credentials),
  }),
  
  register: (userData) => apiRequest('/auth/register', {
    method: 'POST',
    body: JSON.stringify(userData),
  }),
};

// User API
export const userAPI = {
  getProfile: () => apiRequest('/users/me'),
  updateProfile: (data) => apiRequest('/users/me', {
    method: 'PUT',
    body: JSON.stringify(data),
  }),
};

// Cars API
export const carsAPI = {
  getAll: () => apiRequest('/cars'),
  getById: (id) => apiRequest(`/cars/${id}`),
  getAvailable: () => apiRequest('/cars/available'),
  getByCategory: (categoryId) => apiRequest(`/cars/category/${categoryId}`),
};

// Categories API
export const categoriesAPI = {
  getAll: () => apiRequest('/categories'),
  getById: (id) => apiRequest(`/categories/${id}`),
};

// Reservations API
export const reservationsAPI = {
  create: (data) => apiRequest('/reservations', {
    method: 'POST',
    body: JSON.stringify(data),
  }),
  getMy: () => apiRequest('/reservations/my'),
  cancel: (id) => apiRequest(`/reservations/${id}/cancel`, {
    method: 'PUT',
  }),
};

// Currency API
export const currencyAPI = {
  convert: (amount, from, to) => 
    apiRequest(`/currency/convert?amount=${amount}&from=${from}&to=${to}`),
  getRates: (base = 'USD') => 
    apiRequest(`/currency/rates?base=${base}`),
};
```

### Login Component Örneği (src/pages/Login.jsx)
```jsx
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authAPI } from '../services/api';

function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await authAPI.login({ email, password });
      
      // Token'ı kaydet
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('user', JSON.stringify(response.data.user));
      
      // Dashboard'a yönlendir
      navigate('/dashboard');
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input 
        type="email" 
        value={email} 
        onChange={(e) => setEmail(e.target.value)}
        placeholder="E-posta"
      />
      <input 
        type="password" 
        value={password} 
        onChange={(e) => setPassword(e.target.value)}
        placeholder="Şifre"
      />
      {error && <p className="error">{error}</p>}
      <button type="submit">Giriş Yap</button>
    </form>
  );
}

export default Login;
```

---

## 📋 React Route Yapısı Önerisi

```jsx
// App.jsx
import { BrowserRouter, Routes, Route } from 'react-router-dom';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public Routes */}
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        
        {/* Protected Routes */}
        <Route path="/dashboard" element={<PrivateRoute><Dashboard /></PrivateRoute>} />
        <Route path="/profile" element={<PrivateRoute><Profile /></PrivateRoute>} />
        <Route path="/cars" element={<PrivateRoute><Cars /></PrivateRoute>} />
        <Route path="/cars/:id" element={<PrivateRoute><CarDetail /></PrivateRoute>} />
        <Route path="/reservations" element={<PrivateRoute><MyReservations /></PrivateRoute>} />
        <Route path="/reservations/new/:carId" element={<PrivateRoute><NewReservation /></PrivateRoute>} />
        
        {/* Admin Routes */}
        <Route path="/admin/cars" element={<AdminRoute><AdminCars /></AdminRoute>} />
        <Route path="/admin/categories" element={<AdminRoute><AdminCategories /></AdminRoute>} />
        <Route path="/admin/users" element={<AdminRoute><AdminUsers /></AdminRoute>} />
        <Route path="/admin/reservations" element={<AdminRoute><AdminReservations /></AdminRoute>} />
      </Routes>
    </BrowserRouter>
  );
}
```

---

## 🎯 Thymeleaf → React Yönlendirme

Thymeleaf'deki butonlar şu adreslere yönlendirir:
- **Giriş** → `http://localhost:3000/login`
- **Kayıt Ol** → `http://localhost:3000/register`

Başarılı login sonrası React'te:
- **Dashboard** → `http://localhost:3000/dashboard`

---

## ✅ Backend Hazır!

Spring Boot backend tamamen hazır. React projenizi oluşturup `localhost:3000`'de çalıştırabilirsiniz.

**React Projesini Başlatmak:**
```bash
npx create-react-app carrental-frontend
cd carrental-frontend
npm install react-router-dom axios
npm start
```

veya Vite ile:
```bash
npm create vite@latest carrental-frontend -- --template react
cd carrental-frontend
npm install react-router-dom axios
npm run dev
```

