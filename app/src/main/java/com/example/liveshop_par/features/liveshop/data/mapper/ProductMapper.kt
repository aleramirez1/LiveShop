package com.example.liveshop_par.features.liveshop.data.mapper

import com.example.liveshop_par.data.model.Product as ProductEntity
import com.example.liveshop_par.features.liveshop.domain.entities.Product

object ProductMapper {
    fun toDomain(entity: ProductEntity): Product {
        return Product(
            id = entity.id,
            name = entity.name,
            price = entity.price,
            description = entity.description,
            category = entity.category,
            imageUri = entity.imageUri,
            availableUnits = entity.availableUnits,
            sellerId = entity.sellerId,
            createdAt = entity.createdAt
        )
    }

    fun toEntity(domain: Product): ProductEntity {
        return ProductEntity(
            id = domain.id,
            name = domain.name,
            price = domain.price,
            description = domain.description,
            category = domain.category,
            imageUri = domain.imageUri,
            availableUnits = domain.availableUnits,
            sellerId = domain.sellerId,
            createdAt = domain.createdAt
        )
    }

    fun toDomainList(entities: List<ProductEntity>): List<Product> {
        return entities.map { toDomain(it) }
    }
}
