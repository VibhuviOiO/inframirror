import express from 'express';
import { Client } from 'pg';

const router = express.Router();

// POST /api/postgresops/tables
// Body: { host, port, database, user?, password? }
router.post('/tables', async (req, res) => {
  const { host, port, database, user, password } = req.body;
  if (!host || !port || !database) {
    return res.status(400).json({ error: 'host, port, and database are required' });
  }
  const client = new Client({
    host,
    port,
    database,
    user: user || undefined,
    password: password || undefined,
  });
  try {
    await client.connect();
    const result = await client.query(`SELECT tablename FROM pg_catalog.pg_tables WHERE schemaname NOT IN ('pg_catalog', 'information_schema')`);
    await client.end();
    res.json({ tables: result.rows.map(r => r.tablename) });
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});

export default router;
