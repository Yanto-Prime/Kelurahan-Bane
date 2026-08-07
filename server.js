const express = require('express');
const path = require('path');
const app = express();
const PORT = process.env.PORT || 19132;

// 1. Body Parser Middleware
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// 2. Serve Static Files dari folder public
app.use(express.static(path.join(__dirname, 'public')));

// 3. Endpoint API (Contoh)
app.get('/api/status', (req, res) => {
    res.json({ message: "Server Lapau Siantar Aktif!" });
});

// 4. Fallback ke index.html untuk semua route
app.get('*', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

app.listen(PORT, () => {
    console.log(`Server berjalan di port ${PORT}`);
});