/**
 * Cliente WebSocket para LiveShop
 * Maneja conexión en tiempo real con el servidor
 */

class LiveShopWebSocketClient {
    constructor(url = 'ws://localhost:8081') {
        this.url = url;
        this.ws = null;
        this.reconnectAttempts = 0;
        this.maxReconnectAttempts = 5;
        this.reconnectDelay = 3000;
        this.listeners = {};
        this.isConnected = false;
    }

    /**
     * Conectar al servidor WebSocket
     */
    connect() {
        return new Promise((resolve, reject) => {
            try {
                this.ws = new WebSocket(this.url);

                this.ws.onopen = () => {
                    console.log('✓ Conectado al servidor WebSocket');
                    this.isConnected = true;
                    this.reconnectAttempts = 0;
                    this.emit('connected');
                    resolve();
                };

                this.ws.onmessage = (event) => {
                    try {
                        const data = JSON.parse(event.data);
                        console.log('📨 Mensaje recibido:', data.type);
                        this.emit(data.type, data);
                    } catch (error) {
                        console.error('Error procesando mensaje:', error);
                    }
                };

                this.ws.onerror = (error) => {
                    console.error('✗ Error WebSocket:', error);
                    this.emit('error', error);
                    reject(error);
                };

                this.ws.onclose = () => {
                    console.log('✗ Desconectado del servidor');
                    this.isConnected = false;
                    this.emit('disconnected');
                    this.attemptReconnect();
                };
            } catch (error) {
                console.error('Error al conectar:', error);
                reject(error);
            }
        });
    }

    /**
     * Intentar reconectar
     */
    attemptReconnect() {
        if (this.reconnectAttempts < this.maxReconnectAttempts) {
            this.reconnectAttempts++;
            console.log(`⏳ Intentando reconectar (${this.reconnectAttempts}/${this.maxReconnectAttempts})...`);
            
            setTimeout(() => {
                this.connect().catch(error => {
                    console.error('Error en reconexión:', error);
                });
            }, this.reconnectDelay);
        } else {
            console.error('✗ No se pudo reconectar después de varios intentos');
            this.emit('reconnect_failed');
        }
    }

    /**
     * Suscribirse a actualizaciones de productos
     */
    subscribeToProducts() {
        if (this.isConnected) {
            this.send({
                type: 'subscribe_products'
            });
            console.log('✓ Suscrito a actualizaciones de productos');
        } else {
            console.warn('⚠️ No conectado al servidor');
        }
    }

    /**
     * Suscribirse a datos de usuario
     */
    subscribeToUser(userId) {
        if (this.isConnected) {
            this.send({
                type: 'subscribe_user',
                userId: userId
            });
            console.log(`✓ Suscrito a datos del usuario ${userId}`);
        } else {
            console.warn('⚠️ No conectado al servidor');
        }
    }

    /**
     * Notificar creación de producto
     */
    notifyProductCreated(product) {
        this.send({
            type: 'product_created',
            product: product
        });
        console.log('📢 Producto creado notificado');
    }

    /**
     * Notificar actualización de producto
     */
    notifyProductUpdated(product) {
        this.send({
            type: 'product_updated',
            product: product
        });
        console.log('📢 Producto actualizado notificado');
    }

    /**
     * Notificar eliminación de producto
     */
    notifyProductDeleted(product) {
        this.send({
            type: 'product_deleted',
            product: product
        });
        console.log('📢 Producto eliminado notificado');
    }

    /**
     * Enviar mensaje al servidor
     */
    send(data) {
        if (this.isConnected && this.ws.readyState === WebSocket.OPEN) {
            this.ws.send(JSON.stringify(data));
        } else {
            console.warn('⚠️ WebSocket no está conectado');
        }
    }

    /**
     * Registrar listener para un tipo de evento
     */
    on(eventType, callback) {
        if (!this.listeners[eventType]) {
            this.listeners[eventType] = [];
        }
        this.listeners[eventType].push(callback);
    }

    /**
     * Desregistrar listener
     */
    off(eventType, callback) {
        if (this.listeners[eventType]) {
            this.listeners[eventType] = this.listeners[eventType].filter(
                cb => cb !== callback
            );
        }
    }

    /**
     * Emitir evento
     */
    emit(eventType, data) {
        if (this.listeners[eventType]) {
            this.listeners[eventType].forEach(callback => {
                try {
                    callback(data);
                } catch (error) {
                    console.error(`Error en listener de ${eventType}:`, error);
                }
            });
        }
    }

    /**
     * Desconectar
     */
    disconnect() {
        if (this.ws) {
            this.ws.close();
            this.isConnected = false;
            console.log('✓ Desconectado');
        }
    }

    /**
     * Verificar conexión (ping)
     */
    ping() {
        this.send({ type: 'ping' });
    }

    /**
     * Obtener estado de conexión
     */
    getStatus() {
        return {
            isConnected: this.isConnected,
            url: this.url,
            readyState: this.ws ? this.ws.readyState : null
        };
    }
}

// Exportar para Node.js
if (typeof module !== 'undefined' && module.exports) {
    module.exports = LiveShopWebSocketClient;
}
