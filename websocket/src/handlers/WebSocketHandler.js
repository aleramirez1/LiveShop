const ProductService = require('../services/ProductService');

class WebSocketHandler {
    constructor(clients) {
        this.clients = clients;
    }

    async handleMessage(ws, message) {
        try {
            const data = JSON.parse(message);
            console.log(' Mensaje recibido:', data.type);

            switch (data.type) {
                case 'subscribe_products':
                    await this.handleSubscribeProducts(ws);
                    break;
                case 'subscribe_category':
                    await this.handleSubscribeCategory(ws, data);
                    break;
                case 'search_products':
                    await this.handleSearchProducts(ws, data);
                    break;
                case 'product_created':
                    await this.broadcastProductUpdate('product_created', data.product);
                    break;
                case 'product_updated':
                    await this.broadcastProductUpdate('product_updated', data.product);
                    break;
                case 'product_deleted':
                    await this.broadcastProductUpdate('product_deleted', data.product);
                    break;
                case 'ping':
                    ws.send(JSON.stringify({ type: 'pong', timestamp: new Date().toISOString() }));
                    break;
                default:
                    console.log(' Tipo de mensaje desconocido:', data.type);
            }
        } catch (error) {
            console.error('Error procesando mensaje:', error);
            ws.send(JSON.stringify({
                type: 'error',
                message: 'Error procesando mensaje'
            }));
        }
    }

    async handleSubscribeProducts(ws) {
        try {
            const products = await ProductService.getAllProducts();
            ws.send(JSON.stringify({
                type: 'products_list',
                products: products.map(p => p.toJSON()),
                timestamp: new Date().toISOString()
            }));
            console.log(' Productos enviados');
        } catch (error) {
            console.error('Error:', error);
            ws.send(JSON.stringify({
                type: 'error',
                message: 'Error obteniendo productos'
            }));
        }
    }

    async handleSubscribeCategory(ws, data) {
        try {
            const products = await ProductService.getProductsByCategory(data.categoria);
            ws.send(JSON.stringify({
                type: 'products_by_category',
                categoria: data.categoria,
                products: products.map(p => p.toJSON()),
                timestamp: new Date().toISOString()
            }));
            console.log(`✓ Productos de ${data.categoria} enviados`);
        } catch (error) {
            console.error('Error:', error);
            ws.send(JSON.stringify({
                type: 'error',
                message: 'Error obteniendo productos'
            }));
        }
    }

    async handleSearchProducts(ws, data) {
        try {
            const products = await ProductService.searchProducts(data.query);
            ws.send(JSON.stringify({
                type: 'search_results',
                query: data.query,
                products: products.map(p => p.toJSON()),
                timestamp: new Date().toISOString()
            }));
            console.log(` Resultados de búsqueda enviados`);
        } catch (error) {
            console.error('Error:', error);
            ws.send(JSON.stringify({
                type: 'error',
                message: 'Error buscando productos'
            }));
        }
    }

    async broadcastProductUpdate(type, product) {
        const message = JSON.stringify({
            type: type,
            product: product,
            timestamp: new Date().toISOString()
        });

        this.clients.forEach((client) => {
            if (client.readyState === 1) { 
                client.send(message);
            }
        });

        console.log(`📢 Broadcast ${type} a ${this.clients.size} clientes`);
    }
}

module.exports = WebSocketHandler;
