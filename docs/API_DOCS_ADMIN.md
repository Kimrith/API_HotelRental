# 📘 Hotel Rental API Documentation — Admin Portal

This API reference is crafted for frontend developers integrating the **Admin Portal / Dashboard**. The Admin role possesses full system authority to manage users, hotels, room inventory, booking approvals, payment tracking, audit records, and reviews.

---

## 🌐 General Configuration

- **Base URL**: `http://localhost:8030`
- **Default Port**: `8030`
- **Swagger UI (Interactive)**: `http://localhost:8030/swagger-ui/index.html`
- **OpenAPI JSON**: `http://localhost:8030/v3/api-docs`
- **Content-Type**: `application/json` (unless uploading files, which uses `multipart/form-data`)

---

## 🔐 Authentication & Authorization

All protected endpoints require the JWT access token in the `Authorization` HTTP header:

```http
Authorization: Bearer <ACCESS_TOKEN>
```

### Roles & Access
- **Required Role**: `ADMIN`
- Tokens expire according to JWT configuration. Refresh or re-login upon receiving `401 Unauthorized`.

---

## 📋 Common Enums & Status Codes

| Enum Type | Allowed Values |
| :--- | :--- |
| **Role** | `ADMIN`, `Vendor`, `CUSTOMER` |
| **HotelStatus** | `PENDING`, `APPROVED`, `SUSPENDED` |
| **RoomStatus** | `AVAILABLE`, `BOOKED`, `OCCUPIED`, `MAINTENANCE`, `CLEANING` |
| **BookingStatus** | `PENDING`, `APPROVED`, `REJECTED`, `FINISHED`, `CONFIRMED`, `CHECKED_IN`, `COMPLETED`, `CANCELLED` |
| **PaymentMethod** | `KHQR`, `CASH` |
| **PaymentStatus** | `PENDING`, `PAID`, `FAILED` |

---

## 📑 Table of Contents

1. [Authentication & User Management](#1-authentication--user-management)
2. [Hotel Management](#2-hotel-management)
3. [Hotel Images Management](#3-hotel-images-management)
4. [Room Types Management](#4-room-types-management)
5. [Room Type Images Management](#5-room-type-images-management)
6. [Room Inventory Management](#6-room-inventory-management)
7. [Booking Management & Approval](#7-booking-management--approval)
8. [Booking Audit Logs](#8-booking-audit-logs)
9. [Payment & Transactions Tracking](#9-payment--transactions-tracking)
10. [Review Moderation](#10-review-moderation)

---

## 1. Authentication & User Management

### 1.1 Admin Login
Authenticate credentials and obtain JWT tokens.

- **Method**: `POST`
- **Endpoint**: `/api/v1/users/login`
- **Auth**: Public

#### Request Body
```json
{
  "username": "admin",
  "password": "Password@123"
}
```

#### Response (`200 OK`)
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsIn...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsIn...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "role": "ADMIN"
}
```

---

### 1.2 Register New User / Hotel Owner (Vendor)
Admin can provision accounts for customers, hotel owners (`Vendor`), or other admins.

- **Method**: `POST`
- **Endpoint**: `/api/v1/users/register`
- **Auth**: Public / Admin

#### Request Body
```json
{
  "username": "owner_sokha",
  "email": "owner@sokhahotel.com",
  "password": "SecurePassword@123",
  "role": "Vendor",
  "phoneNumber": "+85512345678"
}
```
> Password must have $\ge 8$ characters, at least 1 uppercase, 1 lowercase, 1 number, and 1 special character.

#### Response (`201 Created`)
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsIn...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsIn...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "role": "Vendor"
}
```

---

### 1.3 Get All Users
Fetch list of all registered users across the platform.

- **Method**: `GET`
- **Endpoint**: `/api/v1/users`
- **Auth**: Bearer Token (`ADMIN`)

#### Response (`200 OK`)
```json
[
  {
    "id": 1,
    "username": "admin",
    "email": "admin@hotel.com",
    "role": "ADMIN",
    "phoneNumber": "+85598765432",
    "createdAt": "2026-08-27T10:15:30"
  },
  {
    "id": 2,
    "username": "hotel_owner",
    "email": "owner@hotel.com",
    "role": "Vendor",
    "phoneNumber": "+85512345678",
    "createdAt": "2026-08-28T14:20:00"
  }
]
```

---

### 1.4 Get User by ID
- **Method**: `GET`
- **Endpoint**: `/api/v1/users/{id}`
- **Auth**: Bearer Token

#### Response (`200 OK`)
```json
{
  "id": 2,
  "username": "hotel_owner",
  "email": "owner@hotel.com",
  "role": "Vendor",
  "phoneNumber": "+85512345678",
  "createdAt": "2026-08-28T14:20:00"
}
```

---

## 2. Hotel Management

### 2.1 Get All Hotels
- **Method**: `GET`
- **Endpoint**: `/api/v1/hotels`
- **Auth**: Public / Admin

#### Response (`200 OK`)
```json
[
  {
    "id": 1,
    "name": "Luxury Palace Hotel",
    "description": "5-star luxury hotel in downtown Phnom Penh",
    "address": "Preah Monivong Blvd, Sangkat Boeung Keng Kang 1",
    "city": "Phnom Penh",
    "latitude": 11.5564,
    "longitude": 104.9282,
    "depositPercentage": 20,
    "status": "APPROVED",
    "createdAt": "2026-08-27T08:00:00",
    "images": [
      {
        "id": 10,
        "imageUrl": "/uploads/hotel/banner.jpg",
        "isBanner": true,
        "orderIndex": 0
      }
    ]
  }
]
```

---

### 2.2 Get Hotel by ID
- **Method**: `GET`
- **Endpoint**: `/api/v1/hotels/{id}`
- **Auth**: Public / Admin

#### Response (`200 OK`)
```json
{
  "id": 1,
  "name": "Luxury Palace Hotel",
  "description": "5-star luxury hotel in downtown Phnom Penh",
  "address": "Preah Monivong Blvd",
  "city": "Phnom Penh",
  "latitude": 11.5564,
  "longitude": 104.9282,
  "depositPercentage": 20,
  "status": "APPROVED",
  "createdAt": "2026-08-27T08:00:00",
  "images": []
}
```

---

### 2.3 Create Hotel
- **Method**: `POST`
- **Endpoint**: `/api/v1/hotels`
- **Auth**: Bearer Token (`ADMIN`)

#### Request Body
```json
{
  "name": "Royal Riverside Resort",
  "description": "Riverside boutique hotel with sunset view",
  "address": "Riverside Walk, Sisowath Quay",
  "city": "Phnom Penh",
  "latitude": 11.5713,
  "longitude": 104.9312,
  "depositPercentage": 30,
  "status": "APPROVED"
}
```

#### Response (`201 Created`)
```json
{
  "id": 2,
  "name": "Royal Riverside Resort",
  "description": "Riverside boutique hotel with sunset view",
  "address": "Riverside Walk, Sisowath Quay",
  "city": "Phnom Penh",
  "latitude": 11.5713,
  "longitude": 104.9312,
  "depositPercentage": 30,
  "status": "APPROVED",
  "createdAt": "2026-11-06T19:00:00",
  "images": []
}
```

---

### 2.4 Delete Hotel
- **Method**: `DELETE`
- **Endpoint**: `/api/v1/hotels/{id}`
- **Auth**: Bearer Token (`ADMIN`)
- **Response**: `204 No Content`

---

## 3. Hotel Images Management

### 3.1 Upload Hotel Image / Banner
Upload an image file using `multipart/form-data`.

- **Method**: `POST`
- **Endpoint**: `/api/v1/hotel-images`
- **Content-Type**: `multipart/form-data`
- **Auth**: Bearer Token (`ADMIN`)

#### Form Data Parameters
| Field | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `hotelId` | `Integer` | Yes | Target Hotel ID |
| `file` | `Binary File` | Yes | Image file (.png, .jpg, .webp) |
| `isBanner` | `Boolean` | No | Set as primary banner (default: `false`) |
| `orderIndex`| `Integer` | No | Display sorting index |

#### Response (`201 Created`)
```json
{
  "id": 15,
  "hotelId": 1,
  "imageUrl": "/uploads/hotels/hotel_1_banner.jpg",
  "isBanner": true,
  "orderIndex": 0
}
```

---

### 3.2 Get Images by Hotel
- **Method**: `GET`
- **Endpoint**: `/api/v1/hotel-images/hotel/{hotelId}`
- **Auth**: Public / Admin

#### Response (`200 OK`)
```json
[
  {
    "id": 15,
    "hotelId": 1,
    "imageUrl": "/uploads/hotels/hotel_1_banner.jpg",
    "isBanner": true,
    "orderIndex": 0
  }
]
```

---

### 3.3 Delete Hotel Image
- **Method**: `DELETE`
- **Endpoint**: `/api/v1/hotel-images/{imageId}`
- **Auth**: Bearer Token (`ADMIN`)
- **Response**: `204 No Content`

---

## 4. Room Types Management

### 4.1 Create Room Type
- **Method**: `POST`
- **Endpoint**: `/api/v1/room-types`
- **Auth**: Bearer Token (`ADMIN` / Manager)

#### Request Body
```json
{
  "hotelId": 1,
  "name": "Deluxe King Suite",
  "basePrice": 120.00,
  "maxGuests": 2,
  "status": "AVAILABLE",
  "description": "Spacious city-view room with king bed, bathtub and private balcony",
  "searchKeywords": "king bed, bathtub, balcony, ocean view"
}
```

#### Response (`201 Created`)
```json
{
  "id": 5,
  "hotelId": 1,
  "name": "Deluxe King Suite",
  "basePrice": 120.00,
  "maxGuests": 2,
  "status": "AVAILABLE",
  "description": "Spacious city-view room with king bed, bathtub and private balcony",
  "searchKeywords": "king bed, bathtub, balcony, ocean view",
  "totalRooms": 0,
  "availableRooms": 0
}
```

---

### 4.2 Get All Room Types
Supports dynamic date availability check.

- **Method**: `GET`
- **Endpoint**: `/api/v1/room-types`
- **Query Params (Optional)**:
  - `checkInDate`: `YYYY-MM-DD` (e.g. `2026-12-01`)
  - `checkOutDate`: `YYYY-MM-DD` (e.g. `2026-12-05`)

#### Response (`200 OK`)
```json
[
  {
    "id": 5,
    "hotelId": 1,
    "name": "Deluxe King Suite",
    "basePrice": 120.00,
    "maxGuests": 2,
    "status": "AVAILABLE",
    "description": "Spacious room",
    "totalRooms": 10,
    "availableRooms": 7
  }
]
```

---

### 4.3 Update Room Type
- **Method**: `PUT`
- **Endpoint**: `/api/v1/room-types/{id}`
- **Auth**: Bearer Token (`ADMIN`)

#### Request Body
```json
{
  "hotelId": 1,
  "name": "Deluxe King Suite Premium",
  "basePrice": 135.00,
  "maxGuests": 3,
  "status": "AVAILABLE",
  "description": "Renovated with modern amenities",
  "searchKeywords": "renovated, king bed, wifi"
}
```

#### Response (`200 OK`)
```json
{
  "id": 5,
  "hotelId": 1,
  "name": "Deluxe King Suite Premium",
  "basePrice": 135.00,
  "maxGuests": 3,
  "status": "AVAILABLE",
  "description": "Renovated with modern amenities"
}
```

---

### 4.4 Patch Room Type Status
- **Method**: `PATCH`
- **Endpoint**: `/api/v1/room-types/{id}/status?status=AVAILABLE`
- **Query Param**: `status` (`AVAILABLE`, `BOOKED`, `OCCUPIED`, `MAINTENANCE`, `CLEANING`)
- **Response (`200 OK`)**: Updated room type object.

---

### 4.5 Delete Room Type
- **Method**: `DELETE`
- **Endpoint**: `/api/v1/room-types/{id}`
- **Auth**: Bearer Token (`ADMIN`)
- **Response**: `204 No Content`

---

## 5. Room Type Images Management

### 5.1 Batch Upload Images for Room Type
- **Method**: `POST`
- **Endpoint**: `/api/v1/room-type-images/upload`
- **Content-Type**: `multipart/form-data`
- **Auth**: Bearer Token (`ADMIN`)

#### Form Data Parameters
| Field | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `roomTypeId` | `Long` | Yes | Target Room Type ID |
| `files` | `List<File>` | Yes | Array of image files |
| `primaryIndex`| `Integer`| No | Zero-based index of cover photo (default: `0`) |

#### Response (`201 Created`)
```json
[
  {
    "id": 101,
    "roomTypeId": 5,
    "imageUrl": "/uploads/rooms/room_5_1.jpg",
    "isPrimary": true
  },
  {
    "id": 102,
    "roomTypeId": 5,
    "imageUrl": "/uploads/rooms/room_5_2.jpg",
    "isPrimary": false
  }
]
```

---

### 5.2 Set Primary Cover Photo
- **Method**: `PATCH`
- **Endpoint**: `/api/v1/room-type-images/{id}/primary`
- **Auth**: Bearer Token (`ADMIN`)
- **Response**: `204 No Content`

---

### 5.3 Delete Room Type Image
- **Method**: `DELETE`
- **Endpoint**: `/api/v1/room-type-images/{id}`
- **Auth**: Bearer Token (`ADMIN`)
- **Response**: `204 No Content`

---

## 6. Room Inventory Management

Manage physical room units (e.g. Room 101, Room 102).

### 6.1 Get All Rooms (Paginated & Filterable)
- **Method**: `GET`
- **Endpoint**: `/api/v1/rooms`
- **Query Parameters**:
  - `hotelId` (optional): Filter by Hotel ID
  - `roomTypeId` (optional): Filter by Room Type ID
  - `status` (optional): `AVAILABLE`, `BOOKED`, `OCCUPIED`, `MAINTENANCE`, `CLEANING`
  - `roomNumber` (optional): Filter by specific number (e.g., `302`)
  - `page` (default: `0`): Page number (0-based)
  - `size` (default: `10`): Items per page
  - `sortBy` (default: `id`): Sort field
  - `sortDir` (default: `asc`): `asc` or `desc`

#### Response (`200 OK`)
```json
{
  "content": [
    {
      "id": 20,
      "hotelId": 1,
      "roomTypeId": 5,
      "roomNumber": "101",
      "status": "AVAILABLE"
    },
    {
      "id": 21,
      "hotelId": 1,
      "roomTypeId": 5,
      "roomNumber": "102",
      "status": "OCCUPIED"
    }
  ],
  "pageNo": 0,
  "pageSize": 10,
  "totalElements": 2,
  "totalPages": 1,
  "last": true
}
```

---

### 6.2 Create Physical Room
- **Method**: `POST`
- **Endpoint**: `/api/v1/rooms`
- **Auth**: Bearer Token (`ADMIN`)

#### Request Body
```json
{
  "hotelId": 1,
  "roomTypeId": 5,
  "roomNumber": "103",
  "status": "AVAILABLE"
}
```

#### Response (`201 Created`)
```json
{
  "id": 22,
  "hotelId": 1,
  "roomTypeId": 5,
  "roomNumber": "103",
  "status": "AVAILABLE"
}
```

---

### 6.3 Update Room Details
- **Method**: `PUT`
- **Endpoint**: `/api/v1/rooms/{id}`
- **Auth**: Bearer Token (`ADMIN`)

#### Request Body
```json
{
  "hotelId": 1,
  "roomTypeId": 5,
  "roomNumber": "103-B",
  "status": "MAINTENANCE"
}
```

#### Response (`200 OK`)
```json
{
  "id": 22,
  "hotelId": 1,
  "roomTypeId": 5,
  "roomNumber": "103-B",
  "status": "MAINTENANCE"
}
```

---

### 6.4 Patch Room Status (Quick Status Change)
Used by reception and housekeeping.

- **Method**: `PATCH`
- **Endpoint**: `/api/v1/rooms/{id}/status`
- **Auth**: Bearer Token (`ADMIN`)

#### Request Body
```json
{
  "status": "CLEANING"
}
```

#### Response (`200 OK`)
```json
{
  "id": 22,
  "hotelId": 1,
  "roomTypeId": 5,
  "roomNumber": "103-B",
  "status": "CLEANING"
}
```

---

### 6.5 Soft Delete Room
- **Method**: `DELETE`
- **Endpoint**: `/api/v1/rooms/{id}`
- **Auth**: Bearer Token (`ADMIN`)
- **Response**: `204 No Content`

---

## 7. Booking Management & Approval

### 7.1 Get All Bookings
- **Method**: `GET`
- **Endpoint**: `/api/v1/bookings`
- **Auth**: Bearer Token (`ADMIN`)

#### Response (`200 OK`)
```json
[
  {
    "id": 1001,
    "customerId": 3,
    "hotelId": 1,
    "roomTypeId": 5,
    "assignedRoomId": null,
    "checkInDate": "2026-12-10",
    "checkOutDate": "2026-12-15",
    "totalPrice": 600.00,
    "depositAmount": 120.00,
    "status": "PENDING",
    "createdAt": "2026-11-06T12:00:00"
  }
]
```

---

### 7.2 Get Booking Details
- **Method**: `GET`
- **Endpoint**: `/api/v1/bookings/{id}`
- **Auth**: Bearer Token

#### Response (`200 OK`)
```json
{
  "id": 1001,
  "customerId": 3,
  "hotelId": 1,
  "roomTypeId": 5,
  "assignedRoomId": 20,
  "checkInDate": "2026-12-10",
  "checkOutDate": "2026-12-15",
  "totalPrice": 600.00,
  "depositAmount": 120.00,
  "status": "APPROVED",
  "createdAt": "2026-11-06T12:00:00"
}
```

---

### 7.3 Approve Booking & Assign Specific Room
Approves a pending booking and attaches a concrete room unit number.

- **Method**: `PATCH`
- **Endpoint**: `/api/v1/bookings/{id}/approve?roomId={roomId}`
- **Query Parameter**: `roomId` (Physical room ID, e.g. `20`)
- **Auth**: Bearer Token (`ADMIN`)

#### Response (`200 OK`)
```json
{
  "id": 1001,
  "customerId": 3,
  "hotelId": 1,
  "roomTypeId": 5,
  "assignedRoomId": 20,
  "checkInDate": "2026-12-10",
  "checkOutDate": "2026-12-15",
  "totalPrice": 600.00,
  "depositAmount": 120.00,
  "status": "APPROVED",
  "createdAt": "2026-11-06T12:00:00"
}
```

---

### 7.4 Update Booking Status
- **Method**: `PATCH`
- **Endpoint**: `/api/v1/bookings/{id}/status?status={status}`
- **Query Parameter**: `status` (`CONFIRMED`, `CHECKED_IN`, `COMPLETED`, `CANCELLED`, `REJECTED`)
- **Auth**: Bearer Token (`ADMIN`)

#### Response (`200 OK`)
```json
{
  "id": 1001,
  "customerId": 3,
  "hotelId": 1,
  "roomTypeId": 5,
  "assignedRoomId": 20,
  "checkInDate": "2026-12-10",
  "checkOutDate": "2026-12-15",
  "totalPrice": 600.00,
  "depositAmount": 120.00,
  "status": "CONFIRMED",
  "createdAt": "2026-11-06T12:00:00"
}
```

---

## 8. Booking Audit Logs

Audit trailing allows admins to trace who approved, cancelled, or modified bookings.

### 8.1 Get All Audit Logs (System-Wide)
- **Method**: `GET`
- **Endpoint**: `/api/v1/booking-audits`
- **Auth**: Bearer Token (`ADMIN`)
- **Query Parameters**:
  - `page` (default: `0`)
  - `size` (default: `10`)

#### Response (`200 OK`)
```json
{
  "content": [
    {
      "id": 501,
      "bookingId": 1001,
      "action": "APPROVED",
      "previousStatus": "PENDING",
      "newStatus": "APPROVED",
      "performedBy": "admin",
      "notes": "Approved booking and assigned room 101",
      "createdAt": "2026-11-06T14:32:00"
    }
  ],
  "pageNo": 0,
  "pageSize": 10,
  "totalElements": 1,
  "totalPages": 1,
  "last": true
}
```

---

### 8.2 Get Logs for a Single Booking
- **Method**: `GET`
- **Endpoint**: `/api/v1/booking-audits/booking/{bookingId}`
- **Auth**: Bearer Token (`ADMIN`, `STAFF`)

#### Response (`200 OK`)
```json
{
  "content": [
    {
      "id": 501,
      "bookingId": 1001,
      "action": "APPROVED",
      "previousStatus": "PENDING",
      "newStatus": "APPROVED",
      "performedBy": "admin",
      "notes": "Booking approved",
      "createdAt": "2026-11-06T14:32:00"
    }
  ],
  "pageNo": 0,
  "pageSize": 10,
  "totalElements": 1,
  "totalPages": 1,
  "last": true
}
```

---

## 9. Payment & Transactions Tracking

### 9.1 Get All Payments
- **Method**: `GET`
- **Endpoint**: `/api/v1/payments`
- **Auth**: Bearer Token (`ADMIN`)

#### Response (`200 OK`)
```json
[
  {
    "id": 301,
    "bookingId": 1001,
    "paymentMethod": "KHQR",
    "paymentStatus": "PAID",
    "amount": 120.00,
    "createdAt": "2026-11-06T12:05:00"
  },
  {
    "id": 302,
    "bookingId": 1002,
    "paymentMethod": "CASH",
    "paymentStatus": "PENDING",
    "amount": 480.00,
    "createdAt": "2026-11-06T13:10:00"
  }
]
```

---

### 9.2 Get Payment by ID
- **Method**: `GET`
- **Endpoint**: `/api/v1/payments/{id}`
- **Auth**: Bearer Token

#### Response (`200 OK`)
```json
{
  "id": 301,
  "bookingId": 1001,
  "paymentMethod": "KHQR",
  "paymentStatus": "PAID",
  "amount": 120.00,
  "createdAt": "2026-11-06T12:05:00"
}
```

---

### 9.3 Get Payments by Booking ID
- **Method**: `GET`
- **Endpoint**: `/api/v1/payments/booking/{bookingId}`
- **Auth**: Bearer Token

#### Response (`200 OK`)
```json
[
  {
    "id": 301,
    "bookingId": 1001,
    "paymentMethod": "KHQR",
    "paymentStatus": "PAID",
    "amount": 120.00,
    "createdAt": "2026-11-06T12:05:00"
  }
]
```

---

## 10. Review Moderation

### 10.1 Delete Inappropriate Review
- **Method**: `DELETE`
- **Endpoint**: `/api/v1/reviews/{id}`
- **Auth**: Bearer Token (`ADMIN`)
- **Response**: `204 No Content`

---

## ⚠️ Standard Error Response Model

When an error occurs, the server returns the standard error format:

```json
{
  "timestamp": "2026-11-06T19:00:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Field 'name' must not be blank",
  "path": "/api/v1/hotels"
}
```
Common status codes:
- `400 Bad Request`: Validation failure or missing required fields.
- `401 Unauthorized`: Token missing or expired.
- `403 Forbidden`: Insufficient role permissions (`ADMIN` required).
- `404 Not Found`: Resource ID not found.
- `409 Conflict`: Duplicate key or constraint violation.
