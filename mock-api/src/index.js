const express = require('express');
const { Pool } = require('pg');
const cors = require('cors');
const multer = require('multer');
const { v4: uuidv4 } = require('uuid');
const jwt = require('jsonwebtoken');

const app = express();
const port = 8080;
const SECRET = 'mock-secret';

// Configuração do Multer para uploads
const storage = multer.memoryStorage();
const upload = multer({ storage });

// Configuração do PostgreSQL
const pool = new Pool({
  user: process.env.DB_USER || 'postgres',
  host: process.env.DB_HOST || 'db',
  database: process.env.DB_NAME || 'libera_db',
  password: process.env.DB_PASSWORD || 'Admin@123',
  port: 5432,
});

// Inicialização da Base de Dados
const initDB = async () => {
  try {
    await pool.query(`
      CREATE TABLE IF NOT EXISTS parceiros (
        id SERIAL PRIMARY KEY,
        public_id UUID DEFAULT gen_random_uuid() UNIQUE,
        nome VARCHAR(255) NOT NULL,
        nif VARCHAR(50),
        iban VARCHAR(100),
        telefone VARCHAR(50),
        email VARCHAR(255),
        endereco TEXT,
        foto_url TEXT,
        documento_url TEXT,
        provincia_nome VARCHAR(100),
        tipo_parceiro_nome VARCHAR(100),
        password VARCHAR(255) DEFAULT 'password',
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      )
    `);

    // Migrações parceiros
    await pool.query("ALTER TABLE parceiros ADD COLUMN IF NOT EXISTS documento_url TEXT");
    await pool.query("ALTER TABLE parceiros ADD COLUMN IF NOT EXISTS foto_url TEXT");
    await pool.query(`
      CREATE TABLE IF NOT EXISTS leads (
        id SERIAL PRIMARY KEY,
        public_id UUID DEFAULT gen_random_uuid() UNIQUE,
        nome VARCHAR(255) NOT NULL,
        email VARCHAR(255),
        telemovel VARCHAR(50),
        nif VARCHAR(50),
        estado VARCHAR(50) DEFAULT 'LEAD',
        pacote_nome VARCHAR(100),
        pacote_preco DECIMAL(15,2),
        comprovativo_url TEXT,
        parceiro_public_id UUID,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      )
    `);
    await pool.query(`
      CREATE TABLE IF NOT EXISTS lead_notas (
        id SERIAL PRIMARY KEY,
        public_id UUID DEFAULT gen_random_uuid() UNIQUE,
        lead_public_id UUID REFERENCES leads(public_id) ON DELETE CASCADE,
        nota TEXT NOT NULL,
        usuario_nome VARCHAR(255),
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      )
    `);

    // Limpar base de dados para um estado inicial vazio
    console.log('Limpando base de dados para estado inicial...');
    await pool.query('TRUNCATE TABLE lead_notas CASCADE');
    await pool.query('TRUNCATE TABLE leads CASCADE');
    await pool.query('TRUNCATE TABLE parceiros CASCADE');
    await pool.query('TRUNCATE TABLE materiais_apoio CASCADE');

    await pool.query(`
      CREATE TABLE IF NOT EXISTS materiais_apoio (
        id SERIAL PRIMARY KEY,
        public_id UUID DEFAULT gen_random_uuid() UNIQUE,
        nome VARCHAR(255) NOT NULL,
        arquivo_url TEXT,
        tipo_conteudo VARCHAR(50), -- PROMOCIONAL, EDUCATIVO
        tipos_parceiro TEXT[], -- Array de tipos que podem ver
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      )
    `);
    
    await pool.query(`
      CREATE TABLE IF NOT EXISTS tickets (
        id SERIAL PRIMARY KEY,
        public_id UUID DEFAULT gen_random_uuid() UNIQUE,
        conteudo TEXT NOT NULL,
        tipo VARCHAR(50),
        estado VARCHAR(50) DEFAULT 'ABERTO',
        publicado_por_public_id UUID,
        publicado_por_nome VARCHAR(255),
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      )
    `);

    // Migrations para adicionar colunas se não existirem
    await pool.query(`
      ALTER TABLE materiais_apoio ADD COLUMN IF NOT EXISTS tag_promocional VARCHAR(100);
      ALTER TABLE materiais_apoio ADD COLUMN IF NOT EXISTS tag_educativo VARCHAR(100);
      ALTER TABLE parceiros ADD COLUMN IF NOT EXISTS password VARCHAR(255) DEFAULT 'password';
    `);

    console.log('Limpando base de dados para estado inicial...');
    await pool.query('TRUNCATE TABLE lead_notas CASCADE');
    await pool.query('TRUNCATE TABLE leads CASCADE');
    await pool.query('TRUNCATE TABLE parceiros CASCADE');
    await pool.query('TRUNCATE TABLE materiais_apoio CASCADE');
    await pool.query('TRUNCATE TABLE tickets CASCADE');

    console.log('Tabelas inicializadas com sucesso.');
  } catch (err) {
    console.error('Erro ao inicializar base de dados:', err);
  }
};
initDB();

app.use(cors());
app.use(express.json());

// Log de pedidos para depuração
app.use((req, res, next) => {
  console.log(`${new Date().toISOString()} - ${req.method} ${req.url}`);
  next();
});

// Middleware de Autenticação (Simulado)
const authenticate = (req, res, next) => {
  const authHeader = req.headers.authorization;
  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return res.status(401).json({ message: 'Não autorizado' });
  }
  
  const token = authHeader.split(' ')[1];
  let role = 'PARCEIRO';
  let publicId = '00000000-0000-0000-0000-000000000000'; // Default valid UUID

  if (token.includes('admin')) {
    role = 'ADMIN';
    publicId = '00000000-0000-0000-0000-000000000001'; 
  } else if (token.startsWith('mock-parceiro-token-')) {
    publicId = token.replace('mock-parceiro-token-', '');
  }

  req.user = { role, publicId };
  next();
};

// 2. Autenticação
app.post('/api/v1/auth/login', async (req, res) => {
  const { username, password } = req.body;

  // Admin Hardcoded
  if (username === 'admin@teste.com' && password === '345rfvc') {
    return res.json({
      access_token: 'mock-admin-token',
      refresh_token: 'mock-refresh-token',
      id: 1,
      public_id: '00000000-0000-0000-0000-000000000001',
      nome: 'Administrador',
      username: 'admin@teste.com',
      role: 'ADMIN'
    });
  }

  // Verificar na base de dados para parceiros criados
  try {
    const result = await pool.query('SELECT * FROM parceiros WHERE email = $1 AND password = $2', [username, password]);
    if (result.rows.length > 0) {
      const p = result.rows[0];
      return res.json({
        access_token: `mock-parceiro-token-${p.public_id}`,
        refresh_token: 'mock-refresh-token',
        id: p.id,
        public_id: p.public_id,
        nome: p.nome,
        username: p.email,
        role: 'PARCEIRO'
      });
    }
  } catch (err) {
    console.error('Erro login:', err);
  }

  res.status(401).json({ message: 'Credenciais inválidas' });
});

app.post('/api/v1/auth/refresh-token', (req, res) => {
  res.json({
    access_token: 'mock-refreshed-token-' + Date.now(),
    refresh_token: 'mock-refresh-token'
  });
});

// 5. Leads
app.post('/api/v1/leads', authenticate, async (req, res) => {
  try {
    const { nome, email, telemovel, nif, pacotePublicId, estado } = req.body;
    const query = `
      INSERT INTO leads (nome, email, telemovel, nif, pacote_nome, pacote_preco, estado)
      VALUES ($1, $2, $3, $4, $5, $6, $7)
      RETURNING public_id
    `;
    const values = [nome, email, telemovel, nif, 'Pacote Teste', 35000.00, estado || 'LEAD'];
    const result = await pool.query(query, values);
    res.status(201).send(result.rows[0].public_id);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.get('/api/v1/leads', authenticate, async (req, res) => {
  try {
    const { estado, pagina = 0, tamanho = 10 } = req.query;
    let query = 'SELECT * FROM leads';
    let params = [];
    if (estado) {
      query += ' WHERE estado = $1';
      params.push(estado);
    }
    query += ' ORDER BY created_at DESC';

    const result = await pool.query(query, params);
    const content = result.rows.map(row => ({
      publicId: row.public_id,
      nome: row.nome,
      pacoteNome: row.pacote_nome,
      pacotePreco: row.pacote_preco,
      estado: row.estado,
      createdAt: row.created_at
    }));

    res.json({
      content: content,
      number: parseInt(pagina),
      size: parseInt(tamanho),
      totalElements: content.length,
      totalPages: 1,
      last: true
    });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.get('/api/v1/leads/admin/todos', authenticate, async (req, res) => {
  // Para o admin, reaproveitamos a listagem mas permitindo ver todos
  return app._router.handle({ method: 'GET', url: '/api/v1/leads', headers: req.headers, query: req.query }, res);
});

app.get('/api/v1/leads/overview', authenticate, async (req, res) => {
  try {
    const totalResult = await pool.query('SELECT COUNT(*) as total FROM leads');
    const convertidosResult = await pool.query("SELECT COUNT(*) as total FROM leads WHERE estado = 'CONVERTIDO'");
    const faturacaoResult = await pool.query("SELECT SUM(pacote_preco) as total FROM leads WHERE estado = 'CONVERTIDO'");
    res.json({
      totalLeads: parseInt(totalResult.rows[0].total) || 0,
      leadsConvertidos: parseInt(convertidosResult.rows[0].total) || 0,
      faturacaoTotal: parseFloat(faturacaoResult.rows[0].total) || 0
    });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.get('/api/v1/leads/:publicId', authenticate, async (req, res) => {
  try {
    const leadRes = await pool.query('SELECT * FROM leads WHERE public_id = $1', [req.params.publicId]);
    if (leadRes.rows.length === 0) return res.status(404).json({ message: 'Lead não encontrado' });

    const notasRes = await pool.query('SELECT * FROM lead_notas WHERE lead_public_id = $1 ORDER BY created_at DESC', [req.params.publicId]);

    const row = leadRes.rows[0];
    res.json({
      publicId: row.public_id,
      nome: row.nome,
      email: row.email,
      telemovel: row.telemovel,
      nif: row.nif,
      estado: row.estado,
      comprovantivoUrl: row.comprovativo_url,
      pacoteNome: row.pacote_nome,
      pacotePreco: row.pacote_preco,
      createdAt: row.created_at,
      notas: notasRes.rows.map(n => ({
        publicId: n.public_id,
        nota: n.nota,
        usuarioNome: n.usuario_nome,
        createdAt: n.created_at
      }))
    });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.put('/api/v1/leads/:publicId', authenticate, upload.single('comprovativo'), async (req, res) => {
  try {
    const { publicId } = req.params;
    let dados = req.body;
    if (req.body.dados) {
      dados = JSON.parse(req.body.dados);
    }
    
    let query = `
      UPDATE leads 
      SET nome = $1, email = $2, telemovel = $3, nif = $4, pacote_nome = $5, estado = $6, updated_at = NOW()
    `;
    let values = [
      dados.nome, dados.email, dados.telemovel, dados.nif, 'Pacote Teste', dados.estado || 'LEAD'
    ];
    
    if (req.file) {
      const fileUrl = `http://localhost:8080/files/comprovativos/${req.file.filename}`;
      query += `, comprovativo_url = $7 WHERE public_id = $8`;
      values.push(fileUrl, publicId);
    } else {
      query += ` WHERE public_id = $7`;
      values.push(publicId);
    }
    
    await pool.query(query, values);
    res.status(202).send({ message: 'Lead atualizado com sucesso' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.delete('/api/v1/leads/:publicId', authenticate, async (req, res) => {
  try {
    const { publicId } = req.params;
    await pool.query('DELETE FROM leads WHERE public_id = $1', [publicId]);
    res.status(204).send();
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.patch('/api/v1/leads/:publicId/comprovativo', authenticate, upload.single('file'), async (req, res) => {
  try {
    if (!req.file) return res.status(400).json({ error: 'Ficheiro não fornecido' });
    const { publicId } = req.params;
    const fileUrl = `http://localhost:8080/files/comprovativos/${req.file.filename}`;
    await pool.query('UPDATE leads SET comprovativo_url = $1, updated_at = NOW() WHERE public_id = $2', [fileUrl, publicId]);
    res.status(202).send(fileUrl);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.post('/api/v1/leads/:leadPublicId/notas', authenticate, async (req, res) => {
  try {
    const { leadPublicId } = req.params;
    const { nota } = req.body;
    const usuarioNome = req.user && req.user.role === 'ADMIN' ? 'Admin' : 'Parceiro';
    
    const query = `
      INSERT INTO lead_notas (lead_public_id, nota, usuario_nome)
      VALUES ($1, $2, $3)
      RETURNING public_id
    `;
    const result = await pool.query(query, [leadPublicId, nota, usuarioNome]);
    res.status(201).send(result.rows[0].public_id);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.put('/api/v1/leads/:leadPublicId/notas/:notaPublicId', authenticate, async (req, res) => {
  try {
    const { notaPublicId } = req.params;
    const { nota } = req.body;
    const query = `
      UPDATE lead_notas SET nota = $1, updated_at = NOW()
      WHERE public_id = $2
    `;
    await pool.query(query, [nota, notaPublicId]);
    res.status(202).send();
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.delete('/api/v1/leads/:leadPublicId/notas/:notaPublicId', authenticate, async (req, res) => {
  try {
    const { notaPublicId } = req.params;
    const query = `DELETE FROM lead_notas WHERE public_id = $1`;
    await pool.query(query, [notaPublicId]);
    res.status(204).send();
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// 4. Parceiros (Admin)
app.post('/api/v1/admin/parceiros', authenticate, upload.fields([{ name: 'foto', maxCount: 1 }, { name: 'documento', maxCount: 1 }]), async (req, res) => {
  try {
    let dados = req.body.dados;
    if (typeof dados === 'string') dados = JSON.parse(dados);

    console.log('Persistindo parceiro no DB:', dados.nome);

    const query = `
      INSERT INTO parceiros (nome, nif, iban, telefone, email, provincia_nome, tipo_parceiro_nome, password)
      VALUES ($1, $2, $3, $4, $5, $6, $7, $8)
      RETURNING public_id
    `;

    // Mapear UUIDs fictícios para nomes para exibição na lista
    const provincia = dados.provinciaPublicId === 'luanda-uuid' ? 'Luanda' : 'Benguela';
    const tipo = dados.tipoParceiroPublicId === 'tipo-partner-uuid' ? 'PARCEIRO' : 'AGENTE';

    const values = [
      dados.nome,
      dados.nif,
      dados.iban,
      dados.telefone,
      dados.email,
      provincia,
      tipo,
      dados.password || 'password'
    ];

    const result = await pool.query(query, values);
    res.status(201).send(result.rows[0].public_id);
  } catch (err) {
    console.error('Erro ao salvar parceiro:', err);
    res.status(500).json({ error: err.message });
  }
});

app.get('/api/v1/admin/parceiros', authenticate, async (req, res) => {
  try {
    const result = await pool.query('SELECT * FROM parceiros ORDER BY created_at DESC');

    const content = result.rows.map(row => ({
      publicId: row.public_id,
      nome: row.nome,
      telefone: row.telefone,
      email: row.email,
      nif: row.nif,
      provinciaNome: row.provincia_nome,
      tipoParceiroNome: row.tipo_parceiro_nome,
      createdAt: row.created_at
    }));

    res.json({
      content: content,
      number: 0,
      size: 10,
      totalElements: content.length,
      totalPages: 1,
      last: true
    });
  } catch (err) {
    console.error('Erro ao listar parceiros:', err);
    res.status(500).json({ error: err.message });
  }
});

app.get('/api/v1/admin/parceiros/:publicId', authenticate, async (req, res) => {
  const { publicId } = req.params;
  try {
    const result = await pool.query('SELECT * FROM parceiros WHERE public_id = $1', [publicId]);
    if (result.rows.length === 0) {
      return res.status(404).json({ message: 'Não encontrado' });
    }
    const row = result.rows[0];
    res.json({
      publicId: row.public_id,
      nome: row.nome,
      nif: row.nif,
      iban: row.iban,
      telefone: row.telefone,
      email: row.email,
      endereco: row.endereco,
      fotoUrl: row.foto_url,
      documentoUrl: row.documento_url,
      provinciaNome: row.provincia_nome,
      tipoParceiroNome: row.tipo_parceiro_nome,
      createdAt: row.created_at
    });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.put('/api/v1/admin/parceiros/:publicId', authenticate, upload.fields([{ name: 'foto', maxCount: 1 }, { name: 'documento', maxCount: 1 }]), async (req, res) => {
  const { publicId } = req.params;
  try {
    let dados = req.body.dados;
    if (typeof dados === 'string') dados = JSON.parse(dados);

    console.log('Atualizando parceiro no DB:', publicId);

    const provincia = dados.provinciaPublicId === 'luanda-uuid' ? 'Luanda' : 'Benguela';
    const tipo = dados.tipoParceiroPublicId === 'tipo-partner-uuid' ? 'PARCEIRO' : (dados.tipoParceiroPublicId === 'tipo-agente-uuid' ? 'AGENTE' : 'REPRESENTANTE');

    let query = `
      UPDATE parceiros 
      SET nome = $1, nif = $2, iban = $3, telefone = $4, email = $5, provincia_nome = $6, tipo_parceiro_nome = $7
    `;
    let values = [dados.nome, dados.nif, dados.iban, dados.telefone, dados.email, provincia, tipo];

    if (req.files && req.files['foto']) {
      query += `, foto_url = $${values.length + 1}`;
      values.push(`http://localhost:8080/files/parceiros/${req.files['foto'][0].originalname}`);
    }
    if (req.files && req.files['documento']) {
      query += `, documento_url = $${values.length + 1}`;
      values.push(`http://localhost:8080/files/parceiros/${req.files['documento'][0].originalname}`);
    }

    query += ` WHERE public_id = $${values.length + 1}`;
    values.push(publicId);

    await pool.query(query, values);

    res.status(200).json({ message: 'Atualizado com sucesso' });
  } catch (err) {
    console.error('Erro ao atualizar:', err);
    res.status(500).json({ error: err.message });
  }
});

app.delete('/api/v1/admin/parceiros/:publicId', authenticate, async (req, res) => {
  const { publicId } = req.params;
  try {
    await pool.query('DELETE FROM parceiros WHERE public_id = $1', [publicId]);
    res.status(204).send();
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// 5. Leads (Parceiro)
app.get('/api/v1/leads/overview', authenticate, (req, res) => {
  res.json({
    totalLeads: 42,
    totalFaturacao: 1500000.00
  });
});

app.get('/api/v1/leads/recentes', authenticate, (req, res) => {
  res.json([
    {
      publicId: uuidv4(),
      nome: "Cliente Recente 1",
      pacoteNome: "Pacote Ouro",
      pacotePreco: "50000.00",
      estado: "LEAD",
      createdAt: new Date().toISOString()
    },
    {
      publicId: uuidv4(),
      nome: "Cliente Recente 2",
      pacoteNome: "Pacote Prata",
      pacotePreco: "25000.00",
      estado: "PENDENTE",
      createdAt: new Date().toISOString()
    }
  ]);
});

app.get('/api/v1/leads', authenticate, (req, res) => {
  res.json({
    content: [],
    number: 0,
    size: 10,
    totalElements: 0,
    totalPages: 0,
    last: true
  });
});

// 7. Materiais de Apoio
// 8. Materiais de Apoio (Admin)
app.post('/api/v1/admin/materiais-apoio', authenticate, upload.single('arquivo'), async (req, res) => {
  try {
    let dados = req.body.dados;
    if (typeof dados === 'string') dados = JSON.parse(dados);

    const query = `
      INSERT INTO materiais_apoio (nome, tipo_conteudo, tag_promocional, tag_educativo, tipos_parceiro, arquivo_url)
      VALUES ($1, $2, $3, $4, $5, $6)
      RETURNING public_id
    `;
    const values = [
      dados.nome,
      dados.tipoConteudo || 'PROMOCIONAL',
      dados.tagPromocional,
      dados.tagEducativo,
      dados.tiposParceiroPublicIds || [],
      req.file ? `http://localhost:8080/files/materiais/${req.file.originalname}` : null
    ];
    const result = await pool.query(query, values);
    res.status(201).send(result.rows[0].public_id);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.get('/api/v1/admin/materiais-apoio', authenticate, async (req, res) => {
  try {
    const result = await pool.query('SELECT * FROM materiais_apoio ORDER BY created_at DESC');
    res.json({
      content: result.rows.map(row => ({
        publicId: row.public_id,
        nome: row.nome,
        arquivoUrl: row.arquivo_url,
        tipoConteudo: row.tipo_conteudo,
        tagPromocional: row.tag_promocional,
        tagEducativo: row.tag_educativo,
        createdAt: row.created_at
      })),
      page: 0,
      size: 10,
      totalElements: result.rows.length,
      totalPages: 1,
      last: true
    });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.get('/api/v1/admin/materiais-apoio/:publicId', authenticate, async (req, res) => {
  try {
    const result = await pool.query('SELECT * FROM materiais_apoio WHERE public_id = $1', [req.params.publicId]);
    if (result.rows.length === 0) return res.status(404).json({ error: 'Material não encontrado' });

    const row = result.rows[0];
    res.json({
      publicId: row.public_id,
      nome: row.nome,
      arquivoUrl: row.arquivo_url,
      tipoConteudo: row.tipo_conteudo,
      tagPromocional: row.tag_promocional,
      tagEducativo: row.tag_educativo,
      tiposParceiroPublicIds: row.tipos_parceiro,
      createdAt: row.created_at
    });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.put('/api/v1/admin/materiais-apoio/:publicId', authenticate, upload.single('arquivo'), async (req, res) => {
  try {
    let dados = req.body.dados;
    if (typeof dados === 'string') dados = JSON.parse(dados);

    let query = `
      UPDATE materiais_apoio 
      SET nome = $1, tipo_conteudo = $2, tag_promocional = $3, tag_educativo = $4, tipos_parceiro = $5
    `;
    let values = [
      dados.nome,
      dados.tipoConteudo,
      dados.tagPromocional,
      dados.tagEducativo,
      dados.tiposParceiroPublicIds || []
    ];

    if (req.file) {
      query += `, arquivo_url = $${values.length + 1}`;
      values.push(`http://localhost:8080/files/materiais/${req.file.originalname}`);
    }

    query += ` WHERE public_id = $${values.length + 1}`;
    values.push(req.params.publicId);

    await pool.query(query, values);
    res.status(200).send();
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.delete('/api/v1/admin/materiais-apoio/:publicId', authenticate, async (req, res) => {
  try {
    await pool.query('DELETE FROM materiais_apoio WHERE public_id = $1', [req.params.publicId]);
    res.status(204).send();
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Admin Overview
app.get('/api/v1/admin/overview', authenticate, async (req, res) => {
  try {
    const parceirosCount = await pool.query('SELECT COUNT(*) FROM parceiros');
    const leadsCount = await pool.query('SELECT COUNT(*) FROM leads');
    const ticketsCount = await pool.query('SELECT COUNT(*) FROM tickets');
    const ticketsAbertosCount = await pool.query("SELECT COUNT(*) FROM tickets WHERE estado = 'ABERTO'");

    res.json({
      totalParceiros: parseInt(parceirosCount.rows[0].count) || 0,
      totalParceirosTrend: 8.26, // Mantendo exemplo
      totalLeads: parseInt(leadsCount.rows[0].count) || 0,
      totalLeadsTrend: -2.5,
      totalTickets: parseInt(ticketsCount.rows[0].count) || 0,
      ticketsAbertos: parseInt(ticketsAbertosCount.rows[0].count) || 0,
      ticketsAbertosTrend: 1.2
    });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Partner Overview (Novo endpoint para separar responsabilidades)
app.get('/api/v1/parceiro/overview', authenticate, async (req, res) => {
  try {
    const leadsCount = await pool.query('SELECT COUNT(*) FROM leads WHERE parceiro_public_id = $1', [req.user.publicId]);
    const leadsConvertidos = await pool.query("SELECT COUNT(*) FROM leads WHERE parceiro_public_id = $1 AND estado = 'CONVERTIDO'", [req.user.publicId]);
    
    res.json({
      totalLeads: parseInt(leadsCount.rows[0].count),
      totalLeadsTrend: 12.5,
      leadsConvertidos: parseInt(leadsConvertidos.rows[0].count),
      faturacaoTotal: 500000.00, // Mock valor
      faturacaoTotalTrend: 5.8
    });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Materiais de Apoio (Parceiro)
app.get('/api/v1/materiais-apoio', authenticate, async (req, res) => {
  // Simplificado: retorna todos por agora
  return app._router.handle({ method: 'GET', url: '/api/v1/admin/materiais-apoio', headers: req.headers, query: req.query }, res);
});
// Tickets (Parceiro)
app.get('/api/v1/tickets', authenticate, async (req, res) => {
  try {
    const page = parseInt(req.query.pagina) || 0;
    const size = parseInt(req.query.tamanho) || 10;
    
    const result = await pool.query('SELECT * FROM tickets ORDER BY created_at DESC');
    const content = result.rows.map(row => ({
      publicId: row.public_id,
      conteudo: row.conteudo,
      tipo: row.tipo,
      estado: row.estado,
      publicadoPorNome: row.publicado_por_nome,
      createdAt: row.created_at
    }));

    res.json({
      content: content,
      number: page,
      size: size,
      totalElements: content.length,
      totalPages: 1,
      last: true,
      first: true
    });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.get('/api/v1/tickets/:publicId', authenticate, async (req, res) => {
  try {
    const result = await pool.query('SELECT * FROM tickets WHERE public_id = $1', [req.params.publicId]);
    if (result.rows.length === 0) return res.status(404).json({ error: 'Ticket não encontrado' });
    
    const row = result.rows[0];
    res.json({
      publicId: row.public_id,
      conteudo: row.conteudo,
      tipo: row.tipo,
      estado: row.estado,
      publicadoPorPublicId: row.publicado_por_public_id,
      publicadoPorNome: row.publicado_por_nome,
      createdAt: row.created_at,
      updatedAt: row.updated_at
    });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.post('/api/v1/tickets', authenticate, async (req, res) => {
  try {
    const { tipo, conteudo } = req.body;
    const publicadoPorNome = req.user.role === 'ADMIN' ? 'Admin' : 'Parceiro Mock';
    
    const query = `
      INSERT INTO tickets (tipo, conteudo, publicado_por_nome, publicado_por_public_id)
      VALUES ($1, $2, $3, $4)
      RETURNING public_id
    `;
    const result = await pool.query(query, [tipo, conteudo, publicadoPorNome, req.user.publicId]);
    res.status(201).json({ publicId: result.rows[0].public_id, message: 'Ticket criado com sucesso' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.put('/api/v1/tickets/:publicId', authenticate, async (req, res) => {
  try {
    const { publicId } = req.params;
    const { tipo, conteudo, estado } = req.body;
    const query = `
      UPDATE tickets 
      SET tipo = $1, conteudo = $2, estado = $3, updated_at = NOW()
      WHERE public_id = $4
    `;
    await pool.query(query, [tipo, conteudo, estado, publicId]);
    res.status(202).json({ message: 'Ticket atualizado com sucesso' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.delete('/api/v1/tickets/:publicId', authenticate, async (req, res) => {
  try {
    await pool.query('DELETE FROM tickets WHERE public_id = $1', [req.params.publicId]);
    res.status(204).send();
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.listen(port, () => {
  console.log(`Mock API rodando em http://localhost:${port}`);
});
