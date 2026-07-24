package com.isdemirstaj.backend.config;

import com.isdemirstaj.backend.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtService jwtService;

    public JwtInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        // 1. Tarayıcıların CORS ön kontrol (OPTIONS) isteklerine koşulsuz izin verilir
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();

        // 2. YAPAY ZEKA (MCP) VE RAPOR EKRANI İÇİN BYPASS EKLENDİ!
        if (path.startsWith("/api/ai") || path.startsWith("/api/malzemeler") || path.startsWith("/api/reports")) {
            return true;
        }

        // 2. Giriş, Kayıt ve Token Yenileme endpoint'leri güvenlik kontrolünden muaf tutulur
        if (path.contains("/api/users/login") || 
            path.contains("/api/users/register") || 
            path.contains("/api/users/refresh")) {
            return true;
        }

                // 3. İstekteki "Authorization" başlığını oku
        String token = null;
        String authHeader = request.getHeader("Authorization");

        // Önce Header'a bak
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // "Bearer " sonrasını al
        } 
        // Eğer Header boşsa (Chrome gibi linkten gelmişse), URL parametresine bak
        else {
            token = request.getParameter("token");
        }

        // Token hiçbir şekilde bulunamadıysa isteği reddet
        if (token == null || token.trim().isEmpty()) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            response.getWriter().write("{\"message\": \"Yetki anahtarı (Token) bulunamadı!\"}");
            return false; // İsteği durdur
        }

        // 4. Bulunan Token'ı doğrula
        if (!jwtService.validateToken(token)) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            response.getWriter().write("{\"message\": \"Oturum süresi dolmuş veya geçersiz token!\"}");
            return false; // İsteği durdur
        }

        // 5. YETKİLENDİRME (ROLE-BASED CONTROL):
        // Eğer istek yazma/güncelleme/silme isteğiyse (POST, PUT, DELETE) ve kullanıcı ADMIN değilse engelle!
        String method = request.getMethod();
        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method)) {
            String role = jwtService.extractRole(token);
            if (!"ADMIN".equalsIgnoreCase(role)) {
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 Forbidden (Yasaklandı)
                response.getWriter().write("{\"message\": \"Bu işlemi yapabilmek için YÖNETİCİ yetkisine sahip olmalısınız!\"}");
                return false; // İsteği durdur
            }
        }

        return true; // İstek geçerli, controller sınıfına geçebilir.
    }
}