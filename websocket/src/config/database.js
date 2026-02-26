const mysql = require('mysql2/promise');
require('dotenv').config();

const dbConfig = {
    host: process.env.DB_HOST || 'localhost',
    user: process.env.DB_USER || 'root',
    password: process.env.DB_PASSWORD || '',
    database: process.env.DB_NAME || 'liveshop',
    port: process.env.DB_PORT || 3306,
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0
};

let pool;

async function initializePool() {
    try {
        pool = mysql.createPool(dbConfig);
        console.log('✓ Pool de conexiones creado');
        return pool;
    } catch (error) {
        console.error('✗ Error al crear pool:', error);
        throw error;
    }
}

function getPool() {
    if (!pool) {
        throw new Error('Pool no inicializado');
    }
    return pool;
}

async function closePool() {
    if (pool) {
        await pool.end();
        console.log('✓ Pool cerrado');
    }
}

module.exports = {
    initializePool,
    getPool,
    closePool
};
