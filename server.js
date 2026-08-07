const express = require('express');
const path = require('path');
const app = express();
const PORT = process.env.PORT || 19132;

// 1. Body Parser Middleware
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// 2. Serve Static Files dari root folder (karena index.html sejajar sama server.js)
app.use(express.static(__dirname));

// 3. Endpoint API (Contoh)
app.get('/api/status', (req, res) => {
    res.json({ message: "Server Lapau Siantar Aktif!" });
});

// 4. Fallback ke index.html untuk semua route
app.get('*', (req, res) => {
    res.sendFile(path.join(__dirname, 'index.html'));
});

// Jalankan server sendiri cuma kalau dijalankan lokal (bukan di Vercel)
if (require.main === module) {
    app.listen(PORT, () => {
        console.log(`Server berjalan di port ${PORT}`);
    });
}

// Vercel butuh ini buat jalanin app sebagai serverless function
module.exports = app;
