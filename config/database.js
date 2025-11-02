const { Pool } = require('pg');

// Create connection pool
const pool = new Pool({
    connectionString: process.env.DATABASE_URL,
    ssl: process.env.NODE_ENV === 'production' ? { rejectUnauthorized: false } : false,
    max: 5, // Reduced for serverless
    idleTimeoutMillis: 10000, // Shorter timeout
    connectionTimeoutMillis: 5000, // Longer timeout for serverless
});

// Handle pool errors
pool.on('error', (err, client) => {
    console.error('Unexpected error on idle client:', err.message);
});

// Test connection on startup
pool.query('SELECT NOW()', (err, res) => {
    if (err) {
        console.error('Database connection test failed:', err.message);
        console.error('Connection string exists:', !!process.env.DATABASE_URL);
    } else {
        console.log('Database connected successfully at:', new Date().toISOString());
    }
});

// Export query function for easier use
const query = (text, params) => {
    const start = Date.now();
    return pool.query(text, params).then(res => {
        const duration = Date.now() - start;
        console.log('Executed query', { text, duration, rows: res.rowCount });
        return res;
    }).catch(err => {
        console.error('Query error:', err.message);
        throw err;
    });
};

module.exports = {
    pool,
    query
};
