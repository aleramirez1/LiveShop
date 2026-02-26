const WebSocket = require('ws');
const express = require('express');
const cors = require('cors');
require('dotenv').config();

const { initializePool, closePool } = require('./config/database');
const WebSocketHandler = require('./handlers/WebSocketHandler');

const app = express();
app.use(cors());
app.use(express.json());

const clients = new Set();

const server = require('http').createServer(app);


const wss = new WebSocket.Server({ server });

let wsHandler;

wss.on('connection', (ws) => {
    console.log('✓ Cliente conectado. Total:', clients.size + 1);
    clients.add(ws);

    ws.send(JSON.stringify({
        type: 'connection',
        message: 'Conectado al servidor WebSocket de LiveShop',
        timestamp: new Date().toISOString()
    }));

    ws.on('message', (message) => {
        wsHandler.handleMessage(ws, message);
    });

    ws.on('close', () => {
        clients.delete(ws);
        console.log('✗ Cliente desconectado. Total:', clients.size);
    });

    ws.on('error', (error) => {
        console.error('Error WebSocket:', error);
    });
});

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

const PORT = process.env.PORT || 8081;

async function start() {
    try {
       
        await initializePool();
        
        wsHandler = new WebSocketHandler(clients);
        
        server.listen(PORT, () => {
            console.log(`
╔════════════════════════════════════════╗
║   WebSocket Server - LiveShop         ║
║   (Arquitectura en Capas)             ║
╚════════════════════════════════════════╝

✓ Servidor escuchando en puerto ${PORT}
✓ WebSocket: ws://localhost:${PORT}
✓ Health check: http://localhost:${PORT}/health
✓ Stats: http://localhost:${PORT}/stats

Esperando conexiones...
            `);
        });
    } catch (error) {
        console.error('Error iniciando servidor:', error);
        process.exit(1);
    }
}

// Manejar cierre graceful
process.on('SIGINT', async () => {
    console.log('\n✓ Cerrando servidor...');
    wss.close();
    server.close();
    await closePool();
    process.exit(0);
});

start();
