# 🏨 Hotel Rental API Documentation — Hotel Owner Portal

This API reference is designed for frontend developers building the **Hotel Owner Portal / Vendor Dashboard**. 
Hotel Owners (mapped to `role: "Vendor"` in the backend) manage their hotel properties, define room categories and pricing, upload photos, manage physical room inventory, process reservations and walk-in guests, handle payments (Cash & Bakong KHQR), and track guest reviews.

---

## 🌐 General Configuration

- **Base URL**: `http://localhost:8030`
- **Port**: `8030`
- **Swagger UI (Interactive API Explorer)**: `http://localhost:8030/swagger-ui/index.html`
- **OpenAPI Schema**: `http://localhost:8030/v3/api-docs`
- **Standard Request Format**: `application/json`
- **Image Upload Format**: `multipart/form-data`

---

## 🔐 Authentication & Identity

All protected owner endpoints require a valid JWT token sent in the `Authorization` header:

```http
Authorization: Bearer <ACCESS_TOKEN>
Content-Type: application/json
```

### Owner Account Details
- **Role in Backend**: `"Vendor"`
- When an owner registers or logs in, the returned JWT payload contains `role: "Vendor"`.

---

## 📋 Enums & Status References

| Enum | Key Purpose | Values |
| :--- | :--- | :--- |
| **Role** | User permission level | `Vendor` *(Hotel Owner)*, `ADMIN`, `CUSTOMER` |
| **RoomStatus** | Room availability & operational state | `AVAILABLE`, `BOOKED`, `OCCUPIED`, `MAINTENANCE`, `CLEANING` |
| **BookingStatus** | Reservation workflow state | `PENDING`, `APPROVED`, `CONFIRMED`, `CHECKED_IN`, `COMPLETED`, `CANCELLED`, `REJECTED` |
| **PaymentMethod** | Accepted checkout payment methods | `KHQR`, `CASH` |
| **PaymentStatus** | Payment settlement status | `PENDING`, `PAID`, `FAILED` |
| **HotelStatus** | Hotel property status | `PENDING`, `APPROVED`, `SUSPENDED` |

---

## 📑 Table of Contents

1. [Owner Account & Authentication](#1-owner-account--authentication)
2. [Hotel Property Overview](#2-hotel-property-overview)
3. [Room Types & Pricing Management](#3-room-types--pricing-management)
4. [Room Type Images & Cover Photos](#4-room-type-images--cover-photos)
5. [Physical Room Inventory & Housekeeping](#5-physical-room-inventory--housekeeping)
6. [Booking Management & Room Allocation](#6-booking-management--room-allocation)
7. [Cashier & Payments (Cash & Bakong KHQR)](#7-cashier--payments-cash--bakong-khqr)
8. [Booking Action Audit Trail](#8-booking-action-audit-trail)
9. [Customer Reviews Monitoring](#9-customer-reviews-monitoring)
10. [Frontend Integration Guidelines](#10-frontend-integration-guidelines)

---

## 1. Owner Account & Authentication

### 1.1 Owner Registration
Create a new Hotel Owner account.

- **Method**: `POST`
- **Endpoint**: `/api/v1/users/register`
- **Auth**: Public

#### Request Body
```json
{
  "username": "hotel_owner_sokha",
  "email": "owner@sokhahotel.com",
  "password": "Password@123",
  "role": "Vendor",
  "phoneNumber": "+85512345678"
}
```
> **Password Policy**: Minimum 8 characters; must contain at least 1 uppercase letter, 1 lowercase letter, 1 number, and 1 special symbol.

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

### 1.2 Owner Login
- **Method**: `POST`
- **Endpoint**: `/api/v1/users/login`
- **Auth**: Public

#### Request Body
```json
{
  "username": "hotel_owner_sokha",
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
  "role": "Vendor"
}
```

---

### 1.3 Get Owner Profile
- **Method**: `GET`
- **Endpoint**: `/api/v1/users/{id}`
- **Auth**: Bearer Token

#### Response (`200 OK`)
```json
{
  "id": 2,
  "username": "hotel_owner_sokha",
  "email": "owner@sokhahotel.com",
  "role": "Vendor",
  "phoneNumber": "+85512345678",
  "createdAt": "2026-08-28T14:20:00"
}
```

---

## 2. Hotel Property Overview

### 2.1 Get Hotel Property Details
Fetch property information, location coordinates, deposit rate, and photos.

- **Method**: `GET`
- **Endpoint**: `/api/v1/hotels/{hotelId}`
- **Auth**: Bearer Token / Public

#### Response (`200 OK`)
```json
{
  "id": 1,
  "name": "Sokha Grand Palace Hotel",
  "description": "5-star luxury waterfront hotel with river view",
  "address": "Street 178, Riverside Walk",
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
```

---

### 2.2 View Hotel Photos
- **Method**: `GET`
- **Endpoint**: `/api/v1/hotel-images/hotel/{hotelId}`
- **Auth**: Bearer Token / Public

#### Response (`200 OK`)
```json
[
  {
    "id": 10,
    "hotelId": 1,
    "imageUrl": "/uploads/hotels/banner.jpg",
    "isBanner": true,
    "orderIndex": 0
  }
]
```

---

## 3. Room Types & Pricing Management

Hotel Owners can define room models (e.g. Deluxe Suite, Standard Twin), configure pricing per night, guest capacities, and keywords.

### 3.1 Create Room Type
- **Method**: `POST`
- **Endpoint**: `/api/v1/room-types`
- **Auth**: Bearer Token

#### Request Body
```json
{
  "hotelId": 1,
  "name": "Deluxe River View Suite",
  "basePrice": 125.00,
  "maxGuests": 2,
  "status": "AVAILABLE",
  "description": "Luxurious room with panoramic river view, king size bed, bathtub and balcony",
  "searchKeywords": "river view, king bed, balcony, bathtub, wifi, breakfast"
}
```

#### Response (`201 Created`)
```json
{
  "id": 5,
  "hotelId": 1,
  "name": "Deluxe River View Suite",
  "basePrice": 125.00,
  "maxGuests": 2,
  "status": "AVAILABLE",
  "description": "Luxurious room with panoramic river view, king size bed, bathtub and balcony",
  "searchKeywords": "river view, king bed, balcony, bathtub, wifi, breakfast",
  "totalRooms": 0,
  "availableRooms": 0
}
```

---

### 3.2 List All Room Types for This Hotel
- **Method**: `GET`
- **Endpoint**: `/api/v1/room-types/hotel/{hotelId}`
- **Auth**: Bearer Token

#### Response (`200 OK`)
```json
[
  {
    "id": 5,
    "hotelId": 1,
    "name": "Deluxe River View Suite",
    "basePrice": 125.00,
    "maxGuests": 2,
    "status": "AVAILABLE",
    "description": "Luxurious room with panoramic river view",
    "searchKeywords": "river view, king bed, balcony"
  }
]
```

---

### 3.3 Dynamic Room Availability Search for Dates
Returns remaining room counts for specific check-in / check-out dates.

- **Method**: `GET`
- **Endpoint**: `/api/v1/room-types/hotel/{hotelId}/search-availability`
- **Query Parameters**:
  - `checkInDate`: `YYYY-MM-DD` (e.g. `2026-12-01`)
  - `checkOutDate`: `YYYY-MM-DD` (e.g. `2026-12-05`)

#### Response (`200 OK`)
```json
[
  {
    "id": 5,
    "hotelId": 1,
    "name": "Deluxe River View Suite",
    "basePrice": 125.00,
    "maxGuests": 2,
    "status": "AVAILABLE",
    "totalRooms": 10,
    "availableRooms": 7
  }
]
```

---

### 3.4 Update Room Type Details & Pricing
- **Method**: `PUT`
- **Endpoint**: `/api/v1/room-types/{id}`
- **Auth**: Bearer Token

#### Request Body
```json
{
  "hotelId": 1,
  "name": "Deluxe River View Suite Premium",
  "basePrice": 140.00,
  "maxGuests": 3,
  "status": "AVAILABLE",
  "description": "Newly renovated with complimentary breakfast buffet",
  "searchKeywords": "renovated, breakfast, river view"
}
```

#### Response (`200 OK`)
Returns the updated room type object.

---

### 3.5 Change Room Type Status
- **Method**: `PATCH`
- **Endpoint**: `/api/v1/room-types/{id}/status?status=AVAILABLE`
- **Query Parameter**: `status` (`AVAILABLE`, `BOOKED`, `OCCUPIED`, `MAINTENANCE`, `CLEANING`)
- **Response (`200 OK`)**: Updated room type object.

---

### 3.6 Delete Room Type
- **Method**: `DELETE`
- **Endpoint**: `/api/v1/room-types/{id}`
- **Auth**: Bearer Token
- **Response**: `204 No Content`

---

## 4. Room Type Images & Cover Photos

Upload gallery photos and select a primary cover image for each room category.

### 4.1 Batch Upload Room Photos
Upload one or multiple images from the owner's device.

- **Method**: `POST`
- **Endpoint**: `/api/v1/room-type-images/upload`
- **Content-Type**: `multipart/form-data`
- **Auth**: Bearer Token

#### Form Data Parameters
| Parameter | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `roomTypeId` | `Long` | Yes | Target room type ID |
| `files` | `File[]` | Yes | Array of image files (.png, .jpg, .webp) |
| `primaryIndex` | `Integer` | No | 0-based index to set as cover photo (default: `0`) |

#### Response (`201 Created`)
```json
[
  {
    "id": 101,
    "roomTypeId": 5,
    "imageUrl": "/uploads/rooms/room_5_cover.jpg",
    "isPrimary": true
  },
  {
    "id": 102,
    "roomTypeId": 5,
    "imageUrl": "/uploads/rooms/room_5_bathroom.jpg",
    "isPrimary": false
  }
]
```

---

### 4.2 Get Photos for a Room Type
- **Method**: `GET`
- **Endpoint**: `/api/v1/room-type-images/room-type/{roomTypeId}`
- **Auth**: Bearer Token / Public

#### Response (`200 OK`)
```json
[
  {
    "id": 101,
    "roomTypeId": 5,
    "imageUrl": "/uploads/rooms/room_5_cover.jpg",
    "isPrimary": true
  }
]
```

---

### 4.3 Set Primary / Cover Photo
- **Method**: `PATCH`
- **Endpoint**: `/api/v1/room-type-images/{id}/primary`
- **Auth**: Bearer Token
- **Response**: `204 No Content`

---

### 4.4 Delete Room Photo
- **Method**: `DELETE`
- **Endpoint**: `/api/v1/room-type-images/{id}`
- **Auth**: Bearer Token
- **Response**: `204 No Content`

---

## 5. Physical Room Inventory & Housekeeping

Manage individual room numbers (e.g. Room 101, Room 102, Room 201) and monitor real-time housekeeping statuses.

### 5.1 List Rooms with Filters & Pagination
- **Method**: `GET`
- **Endpoint**: `/api/v1/rooms`
- **Query Parameters**:
  - `hotelId`: Target hotel ID
  - `roomTypeId`: (Optional) Filter by room category
  - `status`: (Optional) `AVAILABLE`, `OCCUPIED`, `CLEANING`, `MAINTENANCE`, `BOOKED`
  - `roomNumber`: (Optional) Filter by room number (e.g. `101`)
  - `page`: Page index (default: `0`)
  - `size`: Items per page (default: `10`)
  - `sortBy`: Field name (default: `id`)
  - `sortDir`: `asc` or `desc`

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
    },
    {
      "id": 22,
      "hotelId": 1,
      "roomTypeId": 5,
      "roomNumber": "103",
      "status": "CLEANING"
    }
  ],
  "pageNo": 0,
  "pageSize": 10,
  "totalElements": 3,
  "totalPages": 1,
  "last": true
}
```

---

### 5.2 Add Physical Room Unit
- **Method**: `POST`
- **Endpoint**: `/api/v1/rooms`
- **Auth**: Bearer Token

#### Request Body
```json
{
  "hotelId": 1,
  "roomTypeId": 5,
  "roomNumber": "104",
  "status": "AVAILABLE"
}
```

#### Response (`201 Created`)
```json
{
  "id": 23,
  "hotelId": 1,
  "roomTypeId": 5,
  "roomNumber": "104",
  "status": "AVAILABLE"
}
```

---

### 5.3 Quick Status Change (Housekeeping & Front Desk)
Quickly switch room state between `CLEANING`, `AVAILABLE`, `OCCUPIED`, or `MAINTENANCE`.

- **Method**: `PATCH`
- **Endpoint**: `/api/v1/rooms/{id}/status`
- **Auth**: Bearer Token

#### Request Body
```json
{
  "status": "AVAILABLE"
}
```

#### Response (`200 OK`)
```json
{
  "id": 23,
  "hotelId": 1,
  "roomTypeId": 5,
  "roomNumber": "104",
  "status": "AVAILABLE"
}
```

---

### 5.4 Update Room Details
- **Method**: `PUT`
- **Endpoint**: `/api/v1/rooms/{id}`
- **Auth**: Bearer Token

#### Request Body
```json
{
  "hotelId": 1,
  "roomTypeId": 5,
  "roomNumber": "104-VIP",
  "status": "AVAILABLE"
}
```

---

### 5.5 Delete Room
- **Method**: `DELETE`
- **Endpoint**: `/api/v1/rooms/{id}`
- **Auth**: Bearer Token
- **Response**: `204 No Content`

---

## 6. Booking Management & Room Allocation

Track reservations, assign physical rooms to guests, manage check-in, and record walk-in customers.

### 6.1 View Hotel Reservations
- **Method**: `GET`
- **Endpoint**: `/api/v1/bookings/hotel/{hotelId}`
- **Auth**: Bearer Token

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
    "totalPrice": 625.00,
    "depositAmount": 125.00,
    "status": "PENDING",
    "createdAt": "2026-11-06T12:00:00"
  }
]
```

---

### 6.2 Check Availability Count for Dates
- **Method**: `GET`
- **Endpoint**: `/api/v1/bookings/availability`
- **Query Parameters**:
  - `hotelId`: `1`
  - `roomTypeId`: `5`
  - `checkInDate`: `2026-12-10`
  - `checkOutDate`: `2026-12-15`

#### Response (`200 OK`)
```json
{
  "availableRooms": 4
}
```

---

### 6.3 Approve Booking & Assign Concrete Room Unit
When confirming a reservation or welcoming a guest, assign their physical room number (e.g. Room 101).

- **Method**: `PATCH`
- **Endpoint**: `/api/v1/bookings/{id}/approve?roomId={roomId}`
- **Query Parameter**: `roomId` (e.g. `20`)
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
  "totalPrice": 625.00,
  "depositAmount": 125.00,
  "status": "APPROVED",
  "createdAt": "2026-11-06T12:00:00"
}
```

---

### 6.4 Update Booking Status
Progress booking status through the hospitality cycle:
`PENDING` $\to$ `APPROVED` $\to$ `CONFIRMED` $\to$ `CHECKED_IN` $\to$ `COMPLETED` (or `CANCELLED`).

- **Method**: `PATCH`
- **Endpoint**: `/api/v1/bookings/{id}/status?status={status}`
- **Query Parameter**: `status`
- **Auth**: Bearer Token

#### Example: Check-in Guest
`PATCH /api/v1/bookings/1001/status?status=CHECKED_IN`

#### Response (`200 OK`)
```json
{
  "id": 1001,
  "customerId": 3,
  "hotelId": 1,
  "roomTypeId": 5,
  "assignedRoomId": 20,
  "status": "CHECKED_IN"
}
```

---

### 6.5 Create Walk-In Booking
Reception desk can book on behalf of walk-in customers.

- **Method**: `POST`
- **Endpoint**: `/api/v1/bookings`
- **Auth**: Bearer Token

#### Request Body
```json
{
  "customerId": 3,
  "hotelId": 1,
  "roomTypeId": 5,
  "checkInDate": "2026-11-06",
  "checkOutDate": "2026-11-08"
}
```

#### Response (`201 Created`)
```json
{
  "id": 1005,
  "customerId": 3,
  "hotelId": 1,
  "roomTypeId": 5,
  "checkInDate": "2026-11-06",
  "checkOutDate": "2026-11-08",
  "totalPrice": 250.00,
  "depositAmount": 50.00,
  "status": "PENDING"
}
```

---

## 7. Cashier & Payments (Cash & Bakong KHQR)

Process guest checkouts using Cash or Cambodian National Bank **Bakong KHQR**.

### 7.1 Generate Bakong KHQR for Payment
Generates dynamic QR string and MD5 hash for payment verification.

- **Method**: `POST`
- **Endpoint**: `/api/v1/payments/generate-qr`
- **Auth**: Bearer Token / Public

#### Request Body
```json
{
  "amount": 50.00,
  "currency": "USD",
  "billNumber": "INV-1001"
}
```

#### Response (`200 OK`)
```json
{
  "qr": "00020101021229300018bakong.nbc.gov.kh...",
  "md5": "9b10ea767f7881c0c4eb89e6ebbe4dd1"
}
```

---

### 7.2 Check Bakong Transaction Status
Verifies if the guest paid via mobile banking app using the `md5` hash.

- **Method**: `POST`
- **Endpoint**: `/api/v1/payments/check-transaction`
- **Auth**: Bearer Token / Public

#### Request Body
```json
{
  "md5": "9b10ea767f7881c0c4eb89e6ebbe4dd1"
}
```

#### Response (`200 OK`)
```json
{
  "responseCode": 0,
  "responseMessage": "Success",
  "hash": "9b10ea767f7881c0c4eb89e6ebbe4dd1"
}
```

---

### 7.3 Complete Order & Payment Settlement
- **CASH**: Marks booking confirmed with `PENDING` payment status until staff deposits cash.
- **KHQR**: Verifies payment with Bakong before marking as `PAID`.

- **Method**: `POST`
- **Endpoint**: `/api/v1/payments/order` *(or `/api/v1/payments/checkout`)*
- **Auth**: Bearer Token

#### Request Body (CASH Example)
```json
{
  "customerId": 3,
  "hotelId": 1,
  "roomTypeId": 5,
  "checkInDate": "2026-11-06",
  "checkOutDate": "2026-11-08",
  "paymentMethod": "CASH",
  "amount": 250.00
}
```

#### Request Body (KHQR Example)
```json
{
  "customerId": 3,
  "hotelId": 1,
  "roomTypeId": 5,
  "checkInDate": "2026-11-06",
  "checkOutDate": "2026-11-08",
  "paymentMethod": "KHQR",
  "amount": 250.00,
  "md5": "9b10ea767f7881c0c4eb89e6ebbe4dd1"
}
```

#### Response (`201 Created`)
```json
{
  "bookingId": 1005,
  "paymentId": 305,
  "status": "CONFIRMED",
  "message": "Payment processed and booking confirmed"
}
```

---

### 7.4 Get Payments by Booking ID
- **Method**: `GET`
- **Endpoint**: `/api/v1/payments/booking/{bookingId}`
- **Auth**: Bearer Token

#### Response (`200 OK`)
```json
[
  {
    "id": 305,
    "bookingId": 1005,
    "paymentMethod": "KHQR",
    "paymentStatus": "PAID",
    "amount": 250.00,
    "createdAt": "2026-11-06T15:30:00"
  }
]
```

---

## 8. Booking Action Audit Trail

Record operational notes and maintain historical accountability for room changes.

### 8.1 Add Audit Log / Operational Note
- **Method**: `POST`
- **Endpoint**: `/api/v1/booking-audits`
- **Auth**: Bearer Token

#### Request Body
```json
{
  "bookingId": 1001,
  "action": "ROOM_ASSIGNED",
  "previousStatus": "PENDING",
  "newStatus": "APPROVED",
  "notes": "Assigned Room 101 with river view per guest special request"
}
```

#### Response (`201 Created`)
```json
{
  "id": 502,
  "bookingId": 1001,
  "action": "ROOM_ASSIGNED",
  "previousStatus": "PENDING",
  "newStatus": "APPROVED",
  "performedBy": "hotel_owner_sokha",
  "notes": "Assigned Room 101 with river view per guest special request",
  "createdAt": "2026-11-06T15:35:00"
}
```

---

### 8.2 View Audit History for a Booking
- **Method**: `GET`
- **Endpoint**: `/api/v1/booking-audits/booking/{bookingId}`
- **Auth**: Bearer Token

#### Response (`200 OK`)
```json
{
  "content": [
    {
      "id": 502,
      "bookingId": 1001,
      "action": "ROOM_ASSIGNED",
      "previousStatus": "PENDING",
      "newStatus": "APPROVED",
      "performedBy": "hotel_owner_sokha",
      "notes": "Assigned Room 101 with river view per guest special request",
      "createdAt": "2026-11-06T15:35:00"
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

## 9. Customer Reviews Monitoring

Monitor feedback and guest ratings for your hotel.

### 9.1 Get Reviews for Hotel
- **Method**: `GET`
- **Endpoint**: `/api/v1/reviews/hotel/{hotelId}`
- **Auth**: Bearer Token / Public

#### Response (`200 OK`)
```json
[
  {
    "id": 80,
    "userId": 3,
    "hotelId": 1,
    "bookingId": 1001,
    "rating": 5,
    "comment": "Exceptional hospitality, clean room, and great breakfast buffet!",
    "createdAt": "2026-11-06T18:00:00"
  }
]
```

---

## 10. Frontend Integration Guidelines

1. **Owner Role Identification**:
   - Save the user role in your frontend state (Redux/Zustand/Pinia). Ensure `user.role === 'Vendor'` unlocks the Owner Dashboard navigation.
2. **Date Standards**:
   - Dates must be sent as `YYYY-MM-DD` (ISO local date, e.g. `2026-12-01`).
3. **KHQR Payment Polling**:
   - When displaying the dynamic Bakong QR code modal, poll `/api/v1/payments/check-transaction` every 3 seconds with the `md5` hash.
   - Once verified (`responseCode === 0`), automatically send `/api/v1/payments/order` with `paymentMethod: "KHQR"` and the `md5` hash.
4. **Housekeeping Quick Actions**:
   - Provide rapid button clicks for cleaners and receptionists via `PATCH /api/v1/rooms/{id}/status` to cycle between `CLEANING` and `AVAILABLE`.
5. **Image Uploads**:
   - For `/api/v1/room-type-images/upload`, use `FormData` and append `files` multiple times (`formData.append("files", file)`). Do not set the `Content-Type` header manually.
