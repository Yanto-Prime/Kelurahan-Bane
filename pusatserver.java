import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.File;
import java.nio.file.Files;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class pusatserver {

    public static void main(String[] args) throws IOException {
        // Menjalankan server lokal pada port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        // 1. Route untuk menyajikan file statis (index.html, gambar, video, lagu .mp3)
        server.createContext("/", new StaticFileHandler());
        
        // 2. Route untuk menerima data form pengajuan surat
        server.createContext("/PusatServer", new FormHandler());
        
        server.setExecutor(null);
        System.out.println("==================================================");
        System.out.println("🚀 Server Kelurahan Bane Berhasil Dijalankan!");
        System.out.println("👉 Silakan buka browser: http://localhost:8080/");
        System.out.println("==================================================");
        server.start();
    }

    // Handler untuk membaca dan mengirimkan file HTML, Gambar, MP3, MP4 ke browser
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            
            // Mengarahkan alamat utama / ke index.html
            if (path.equals("/")) {
                path = "/index.html";
            }

            File file = new File("." + path);
            if (file.exists() && !file.isDirectory()) {
                // Membaca semua byte file secara aman
                byte[] bytes = Files.readAllBytes(file.toPath());
                
                // Menentukan Content-Type berdasarkan jenis file
                String lowerPath = path.toLowerCase();
                if (lowerPath.endsWith(".html")) {
                    exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                } else if (lowerPath.endsWith(".css")) {
                    exchange.getResponseHeaders().set("Content-Type", "text/css");
                } else if (lowerPath.endsWith(".js")) {
                    exchange.getResponseHeaders().set("Content-Type", "application/javascript");
                } else if (lowerPath.endsWith(".mp3")) {
                    exchange.getResponseHeaders().set("Content-Type", "audio/mpeg");
                } else if (lowerPath.endsWith(".mp4")) {
                    exchange.getResponseHeaders().set("Content-Type", "video/mp4");
                } else if (lowerPath.endsWith(".jpg") || lowerPath.endsWith(".jpeg")) {
                    exchange.getResponseHeaders().set("Content-Type", "image/jpeg");
                } else if (lowerPath.endsWith(".png")) {
                    exchange.getResponseHeaders().set("Content-Type", "image/png");
                }

                exchange.sendResponseHeaders(200, bytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
            } else {
                // Respons jika file tidak ditemukan
                String response = "404 Not Found - File tidak ditemukan";
                exchange.sendResponseHeaders(404, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    // Handler untuk menerima submission dari form
    static class FormHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                InputStream is = exchange.getRequestBody();
                String formData = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                Map<String, String> params = parseFormData(formData);

                String nama = params.getOrDefault("nama", "-");
                String nik = params.getOrDefault("nik", "-");
                String jenisSurat = params.getOrDefault("jenisSurat", "");

                String namaSurat;
                switch (jenisSurat) {
                    case "SKU": 
                        namaSurat = "Surat Keterangan Usaha (SKU)"; 
                        break;
                    case "SKD": 
                        namaSurat = "Surat Keterangan Domisili"; 
                        break;
                    case "SPKTP": 
                        namaSurat = "Surat Pengantar KTP/KK"; 
                        break;
                    default: 
                        namaSurat = "Surat Keterangan"; 
                        break;
                }

                // Halaman konfirmasi setelah berhasil kirim form
                String htmlResponse = "<!DOCTYPE html>"
                        + "<html lang='id'>"
                        + "<head>"
                        + "    <meta charset='UTF-8'>"
                        + "    <title>Status Pengajuan - Kelurahan Bane</title>"
                        + "    <style>"
                        + "        body { font-family: 'Segoe UI', sans-serif; background-color: #f1f5f9; padding: 40px; color: #334155; }"
                        + "        .card { max-width: 500px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }"
                        + "        h2 { color: #1e3a8a; margin-bottom: 5px; }"
                        + "        .sub { color: #64748b; margin-bottom: 20px; font-size: 14px; }"
                        + "        .success-box { background: #dcfce7; color: #15803d; padding: 12px; border-radius: 6px; margin-bottom: 20px; font-weight: bold; text-align: center; }"
                        + "        .detail { background: #f8fafc; padding: 15px; border-radius: 6px; border: 1px solid #e2e8f0; margin-bottom: 20px; }"
                        + "        .btn-kembali { display: block; text-align: center; padding: 12px; background: #2563eb; color: white; text-decoration: none; border-radius: 6px; font-weight: bold; transition: 0.3s; }"
                        + "        .btn-kembali:hover { background: #1d4ed8; }"
                        + "    </style>"
                        + "</head>"
                        + "<body>"
                        + "    <div class='card'>"
                        + "        <h2>Kelurahan Bane</h2>"
                        + "        <p class='sub'>Kecamatan Siantar Utara</p>"
                        + "        <div class='success-box'>✓ Pengajuan Surat Berhasil Diterima!</div>"
                        + "        <div class='detail'>"
                        + "            <p><strong>Nama Lengkap:</strong> " + nama + "</p>"
                        + "            <p><strong>NIK:</strong> " + nik + "</p>"
                        + "            <p><strong>Jenis Surat:</strong> " + namaSurat + "</p>"
                        + "        </div>"
                        + "        <p style='font-size: 13px; color: #64748b; margin-bottom: 20px;'>Dokumen Anda sedang diproses oleh petugas. Silakan datang ke kantor kelurahan jika memerlukan konfirmasi lanjutan.</p>"
                        + "        <a href='/' class='btn-kembali'>Kembali ke Beranda</a>"
                        + "    </div>"
                        + "</body>"
                        + "</html>";

                byte[] responseBytes = htmlResponse.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, responseBytes.length);
                
                OutputStream os = exchange.getResponseBody();
                os.write(responseBytes);
                os.close();
            }
        }

        private Map<String, String> parseFormData(String formData) {
            Map<String, String> map = new HashMap<>();
            String[] pairs = formData.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length > 1) {
                    String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                    String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                    map.put(key, value);
                }
            }
            return map;
        }
    }
}