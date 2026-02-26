const ProductRepository = require('../repositories/ProductRepository');

class ProductService {
    async getAllProducts() {
        try {
            console.log(' Obteniendo todos los productos...');
            const products = await ProductRepository.getAllProducts();
            console.log(`✓ ${products.length} productos obtenidos`);
            return products;
        } catch (error) {
            console.error('Error en servicio:', error);
            throw error;
        }
    }

    async getProductById(productId) {
        try {
            console.log(` Obteniendo producto ${productId}...`);
            const product = await ProductRepository.getProductById(productId);
            if (product) {
                console.log(`✓ Producto ${productId} obtenido`);
            } else {
                console.log(`Producto ${productId} no encontrado`);
            }
            return product;
        } catch (error) {
            console.error('Error en servicio:', error);
            throw error;
        }
    }

    async getProductsByCategory(categoria) {
        try {
            console.log(` Obteniendo productos de categoría: ${categoria}`);
            const products = await ProductRepository.getProductsByCategory(categoria);
            console.log(`✓ ${products.length} productos de ${categoria} obtenidos`);
            return products;
        } catch (error) {
            console.error('Error en servicio:', error);
            throw error;
        }
    }

    async searchProducts(query) {
        try {
            console.log(` Buscando productos: ${query}`);
            const products = await ProductRepository.searchProducts(query);
            console.log(`✓ ${products.length} productos encontrados`);
            return products;
        } catch (error) {
            console.error('Error en servicio:', error);
            throw error;
        }
    }
}

module.exports = new ProductService();
