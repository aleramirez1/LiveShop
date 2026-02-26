package com.example.liveshop.features.liveshop.data.datasources.remote.mapper

import com.example.liveshop.features.liveshop.data.datasources.local.entity.OrderEntity
import com.example.liveshop.features.liveshop.data.datasources.local.entity.ProductEntity
import com.example.liveshop.features.liveshop.data.datasources.local.entity.ReservationEntity
import com.example.liveshop.features.liveshop.data.datasources.remote.model.OrderDto
import com.example.liveshop.features.liveshop.data.datasources.remote.model.ProductDto
import com.example.liveshop.features.liveshop.data.datasources.remote.model.ReservationDto
import com.example.liveshop.features.liveshop.domain.entities.Order
import com.example.liveshop.features.liveshop.domain.entities.OrderStatus
import com.example.liveshop.features.liveshop.domain.entities.Product
import com.example.liveshop.features.liveshop.domain.entities.Reservation

fun ProductDto.toEntity(): ProductEntity = ProductEntity(
    id = id,
    name = name,
    price = price,
    quantity = quantity,
    imageUrl = imageUrl,
    description = description,
    isAvailable = isAvailable,
    sellerId = sellerId,
    sellerName = sellerName,
    sellerPhone = sellerPhone,
    category = category,
    timestamp = timestamp,
    reservedBy = reservedBy,
    reservedAt = reservedAt
)

fun ProductDto.toDomain(): Product = Product(
    id = id,
    name = name,
    price = price,
    quantity = quantity,
    imageUrl = imageUrl,
    description = description,
    isAvailable = isAvailable,
    sellerId = sellerId,
    sellerName = sellerName,
    sellerPhone = sellerPhone,
    category = category,
    timestamp = timestamp,
    reservedBy = reservedBy,
    reservedAt = reservedAt
)

fun ProductEntity.toDomain(): Product = Product(
    id = id,
    name = name,
    price = price,
    quantity = quantity,
    imageUrl = imageUrl,
    description = description,
    isAvailable = isAvailable,
    sellerId = sellerId,
    sellerName = sellerName,
    sellerPhone = sellerPhone,
    category = category,
    timestamp = timestamp,
    reservedBy = reservedBy,
    reservedAt = reservedAt
)

fun Product.toEntity(): ProductEntity = ProductEntity(
    id = id,
    name = name,
    price = price,
    quantity = quantity,
    imageUrl = imageUrl,
    description = description,
    isAvailable = isAvailable,
    sellerId = sellerId,
    sellerName = sellerName,
    sellerPhone = sellerPhone,
    category = category,
    timestamp = timestamp,
    reservedBy = reservedBy,
    reservedAt = reservedAt
)

fun Product.toDto(): ProductDto = ProductDto(
    id = id,
    name = name,
    price = price,
    quantity = quantity,
    imageUrl = imageUrl,
    description = description,
    isAvailable = isAvailable,
    sellerId = sellerId,
    sellerName = sellerName,
    sellerPhone = sellerPhone,
    category = category,
    timestamp = timestamp,
    reservedBy = reservedBy,
    reservedAt = reservedAt
)

fun OrderDto.toEntity(): OrderEntity = OrderEntity(
    id = id,
    productId = productId,
    buyerId = buyerId,
    buyerName = buyerName,
    buyerPhone = buyerPhone,
    quantity = quantity,
    totalPrice = totalPrice,
    status = status,
    timestamp = timestamp
)

fun OrderDto.toDomain(): Order = Order(
    id = id,
    productId = productId,
    buyerId = buyerId,
    buyerName = buyerName,
    buyerPhone = buyerPhone,
    quantity = quantity,
    totalPrice = totalPrice,
    status = OrderStatus.valueOf(status),
    timestamp = timestamp
)

fun OrderEntity.toDomain(): Order = Order(
    id = id,
    productId = productId,
    buyerId = buyerId,
    buyerName = buyerName,
    buyerPhone = buyerPhone,
    quantity = quantity,
    totalPrice = totalPrice,
    status = OrderStatus.valueOf(status),
    timestamp = timestamp
)

fun Order.toEntity(): OrderEntity = OrderEntity(
    id = id,
    productId = productId,
    buyerId = buyerId,
    buyerName = buyerName,
    buyerPhone = buyerPhone,
    quantity = quantity,
    totalPrice = totalPrice,
    status = status.name,
    timestamp = timestamp
)

fun Order.toDto(): OrderDto = OrderDto(
    id = id,
    productId = productId,
    buyerId = buyerId,
    buyerName = buyerName,
    buyerPhone = buyerPhone,
    quantity = quantity,
    totalPrice = totalPrice,
    status = status.name,
    timestamp = timestamp
)

fun ReservationDto.toEntity(): ReservationEntity = ReservationEntity(
    id = id,
    productId = productId,
    userId = userId,
    userName = userName,
    userPhone = userPhone,
    quantity = quantity,
    expiresAt = expiresAt,
    timestamp = timestamp
)

fun ReservationDto.toDomain(): Reservation = Reservation(
    id = id,
    productId = productId,
    userId = userId,
    userName = userName,
    userPhone = userPhone,
    quantity = quantity,
    expiresAt = expiresAt,
    timestamp = timestamp
)

fun ReservationEntity.toDomain(): Reservation = Reservation(
    id = id,
    productId = productId,
    userId = userId,
    userName = userName,
    userPhone = userPhone,
    quantity = quantity,
    expiresAt = expiresAt,
    timestamp = timestamp
)

fun Reservation.toEntity(): ReservationEntity = ReservationEntity(
    id = id,
    productId = productId,
    userId = userId,
    userName = userName,
    userPhone = userPhone,
    quantity = quantity,
    expiresAt = expiresAt,
    timestamp = timestamp
)

fun Reservation.toDto(): ReservationDto = ReservationDto(
    id = id,
    productId = productId,
    userId = userId,
    userName = userName,
    userPhone = userPhone,
    quantity = quantity,
    expiresAt = expiresAt,
    timestamp = timestamp
)
