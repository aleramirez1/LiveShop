const { getPool } = require('../config/database');
const Product = require('../models/Product');

class ProductRepository {
    async getAllProducts() {
        try {
            const pool = getPool();
            const connection = await pool.getConnection();
            
            const [products] = await connection.query(
                `SELECT p.*, u.nombre as nombre_vendedor, u.telefono as telefono_vendedor
                 FROM productos p
                 LEFT JOIN usuarios u ON p.id_vendedor = u.id
                 WHERE p.disponible = TRUE
                 ORDER BY p.created_at DESC`
            );
            
            connection.release();
            
            return products.map(p => new Product(p));
        } catch (error) {
            console.error('Error obteniendo productos:', error);
            throw error;
        }
    }

    async getProductById(productId) {
        try {
            const pool = getPool();
            const connection = await pool.getConnection();
            
            const [products] = await connection.query(
                `SELECT p.*, u.nombre as nombre_vendedor, u.telefono as telefono_vendedor
                 FROM productos p
                 LEFT JOIN usuarios u ON p.id_vendedor = u.id
                 WHERE p.id = ? AND p.disponible = TRUE`,
                [productId]
            );
            
            connection.release();
            
            if (products.length > 0) {
                return new Product(products[0]);
            }
            return null;
        } catch (error) {
            console.error('Error obteniendo producto:', error);
            throw error;
        }
    }

    async getProductsByCategory(categoria) {
        try {
            const pool = getPool();
            const connection = await pool.getConnection();
            
            const [products] = await connection.query(
                `SELECT p.*, u.nombre as nombre_vendedor, u.telefono as telefono_vendedor
                 FROM productos p
                 LEFT JOIN usuarios u ON p.id_vendedor = u.id
                 WHERE p.categoria = ? AND p.disponible = TRUE
                 ORDER BY p.created_at DESC`,
                [categoria]
            );
            
            connection.release();
            
            return products.map(p => new Product(p));
        } catch (error) {
            console.error('Error obteniendo productos por categoría:', error);
            throw error;
        }
    }

    async searchProducts(query) {
        try {
            const pool = getPool();
            const connection = await pool.getConnection();
            
            const searchTerm = `%${query}%`;
            const [products] = await connection.query(
                `SELECT p.*, u.nombre as nombre_vendedor, u.telefono as telefono_vendedor
                 FROM productos p
                 LEFT JOIN usuarios u ON p.id_vendedor = u.id
                 WHERE (p.nombre LIKE ? OR p.descripcion LIKE ? OR p.categoria LIKE ?)
                 AND p.disponible = TRUE
                 ORDER BY p.created_at DESC`,
                [searchTerm, searchTerm, searchTerm]
            );
            
            connection.release();
            
            return products.map(p => new Product(p));
        } catch (error) {
            console.error('Error buscando productos:', error);
            throw error;
        }
    }
}

module.exports = new ProductRepository();
