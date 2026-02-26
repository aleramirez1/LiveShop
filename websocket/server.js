const WebSocket = require('ws');
const express = require('express');
const cors = require('cors');
const mysql = require('mysql2/promise');
require('dotenv').config();

const app = express();
app.use(cors());
app.use(express.json());

const dbConfig = {
    host: process.env.DB_HOST || 'localhost',
    user: process.env.DB_USER || 'root',
    password: process.env.DB_PASSWORD || '',
    database: process.env.DB_NAME || 'liveshop',
    port: process.env.DB_PORT || 3306
};

let pool;

async function initializePool() {
    try {
        pool = mysql.createPool(dbConfig);
        console.log('✓ Conexión a base de datos establecida');
    } catch (error) {
        console.error('✗ Error al conectar a la base de datos:', error);
        process.exit(1);
    }
}

const clients = new Set();

const server = require('http').createServer(app);

const wss = new WebSocket.Server({ server });

wss.on('connection', (ws) => {
    console.log('✓ Cliente conectado. Total:', clients.size + 1);
    clients.add(ws);

    ws.send(JSON.stringify({
        type: 'connection',
        message: 'Conectado al servidor WebSocket de LiveShop',
        timestamp: new Date().toISOString()
    }));

    ws.on('message', async (message) => {
        try {
            const data = JSON.parse(message);
            console.log('📨 Mensaje recibido:', data.type);

            switch (data.type) {
                case 'subscribe_products':
                    await handleSubscribeProducts(ws, data);
                    break;
                case 'subscribe_user':
                    await handleSubscribeUser(ws, data);
                    break;
                case 'product_created':
                    await broadcastProductUpdate('product_created', data.product);
                    break;
                case 'product_updated':
                    await broadcastProductUpdate('product_updated', data.product);
                    break;
                case 'product_deleted':
                    await broadcastProductUpdate('product_deleted', data.product);
                    break;
                case 'ping':
                    ws.send(JSON.stringify({ type: 'pong', timestamp: new Date().toISOString() }));
                    break;
                default:
                    console.log('Tipo de mensaje desconocido:', data.type);
            }
        } catch (error) {
            console.error('Error procesando mensaje:', error);
            ws.send(JSON.stringify({
                type: 'error',
                message: 'Error procesando mensaje'
            }));
        }
    });

    ws.on('close', () => {
        clients.delete(ws);
        console.log('✗ Cliente desconectado. Total:', clients.size);
    });

    ws.on('error', (error) => {
        console.error('Error WebSocket:', error);
    });
});

async function handleSubscribeProducts(ws, data) {
    try {
        const connection = await pool.getConnection();
        const [products] = await connection.query(
            `SELECT p.*, u.nombre as nombre_vendedor, u.telefono as telefono_vendedor
             FROM productos p
             LEFT JOIN usuarios u ON p.id_vendedor = u.id
             WHERE p.disponible = TRUE
             ORDER BY p.created_at DESC`
        );
        connection.release();

        ws.send(JSON.stringify({
            type: 'products_list',
            products: products,
            timestamp: new Date().toISOString()
        }));

        console.log('✓ Productos enviados:', products.length);
    } catch (error) {
        console.error('Error obteniendo productos:', error);
        ws.send(JSON.stringify({
            type: 'error',
            message: 'Error obteniendo productos'
        }));
    }
}

async function handleSubscribeUser(ws, data) {
    try {
        const connection = await pool.getConnection();
        const [user] = await connection.query(
            'SELECT id, nombre, email, telefono FROM usuarios WHERE id = ?',
            [data.userId]
        );
        connection.release();

        if (user.length > 0) {
            ws.send(JSON.stringify({
                type: 'user_data',
                user: user[0],
                timestamp: new Date().toISOString()
            }));
            console.log('✓ Datos de usuario enviados');
        } else {
            ws.send(JSON.stringify({
                type: 'error',
                message: 'Usuario no encontrado'
            }));
        }
    } catch (error) {
        console.error('Error obteniendo usuario:', error);
        ws.send(JSON.stringify({
            type: 'error',
            message: 'Error obteniendo usuario'
        }));
    }
}

async function broadcastProductUpdate(type, product) {
    const message = JSON.stringify({
        type: type,
        product: product,
        timestamp: new Date().toISOString()
    });

    clients.forEach((client) => {
        if (client.readyState === WebSocket.OPEN) {
            client.send(message);
        }
    });

    console.log(`📢 Broadcast ${type} a ${clients.size} clientes`);
}

// Rutas HTTP
app.get('/health', (req, res) => {
    res.json({
        status: 'ok',
        message: 'WebSocket server funcionando',
        connectedClients: clients.size
    });
});

app.get('/stats', (req, res) => {
    res.json({
        connectedClients: clients.size,
        timestamp: new Date().toISOString()
    });
});

// Iniciar servidor
const PORT = process.env.PORT || 8081;

async function start() {
    await initializePool();
    
    server.listen(PORT, () => {
        console.log(`
╔════════════════════════════════════════╗
║   WebSocket Server - LiveShop         ║
╚════════════════════════════════════════╝

✓ Servidor escuchando en puerto ${PORT}
✓ WebSocket: ws://localhost:${PORT}
✓ Health check: http://localhost:${PORT}/health

Esperando conexiones...
        `);
    });
}

start().catch(error => {
    console.error('Error iniciando servidor:', error);
    process.exit(1);
});

// Manejar cierre graceful
process.on('SIGINT', () => {
    console.log('\n✓ Cerrando servidor...');
    wss.close();
    server.close();
    process.exit(0);
});
