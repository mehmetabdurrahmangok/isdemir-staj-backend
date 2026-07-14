package com.isdemirstaj.backend.service;

import com.isdemirstaj.backend.dto.user.*;
import com.isdemirstaj.backend.entity.UserEntity;
import com.isdemirstaj.backend.repository.UserRepository;
import com.isdemirstaj.backend.util.PasswordUtil;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService; // Token üretmek için servisimizi enjekte ediyoruz

    public UserService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    // 1. KULLANICI GİRİŞİ (LOGIN) MANTIĞI
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        // E-postaya göre kullanıcıyı veritabanında ara
        UserEntity user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new RuntimeException("E-posta adresi veya şifre hatalı!"));

        // Şifreyi SHA-256 formatına çevirip veritabanındaki hash ile kıyasla
        String hashedInputPassword = PasswordUtil.toSHA256(loginRequestDto.getPassword());
        if (!user.getPassword().equals(hashedInputPassword)) {
            throw new RuntimeException("E-posta adresi veya şifre hatalı!");
        }

        // Aktiflik kontrolü (İşten ayrılan/pasif üyelerin girmesini engeller)
        if (Boolean.FALSE.equals(user.getAktifMi())) {
            throw new RuntimeException("Hesabınız pasif durumdadır. Yöneticiyle iletişime geçin!");
        }

        // Giriş başarılıysa JWT Token'larını üret
        String accessToken = jwtService.generateAccessToken(user.getEmail(), user.getRol());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        // Token'ları arayüze dönecek pakete ekle
        return new LoginResponseDto(
                user.getId(),
                user.getEmail(),
                user.getAdSoyad(),
                user.getRol(),
                accessToken,
                refreshToken
        );
    }

    // 2. KULLANICI KAYIT OLMA (REGISTER) MANTIĞI
    public UserResponseDto register(UserCreateDto createDto) {
        // E-posta adresi veritabanında zaten var mı?
        if (userRepository.findByEmail(createDto.getEmail()).isPresent()) {
            throw new RuntimeException("Bu e-posta adresi zaten kullanımda!");
        }

        UserEntity yeniUser = new UserEntity();
        yeniUser.setEmail(createDto.getEmail());
        
        // Şifreyi veritabanına SHA-256 ile hash'leyerek kaydediyoruz!
        String hashedRegisterPassword = PasswordUtil.toSHA256(createDto.getPassword());
        yeniUser.setPassword(hashedRegisterPassword);
        
        yeniUser.setAdSoyad(createDto.getAdSoyad());
        yeniUser.setRol("USER"); // Varsayılan rol USER olur
        yeniUser.setAktifMi(true); // Yeni kayıt olanlar otomatik aktiftir
        yeniUser.setOper(createDto.getOper());

        userRepository.save(yeniUser);

        return new UserResponseDto(
                yeniUser.getId(),
                yeniUser.getEmail(),
                yeniUser.getAdSoyad(),
                yeniUser.getRol(),
                yeniUser.getAktifMi()
        );
    }

    // 3. TOKEN YENİLEME (REFRESH TOKEN) MANTIĞI
    public TokenRefreshResponseDto refreshToken(TokenRefreshRequestDto refreshRequest) {
        String reqRefreshToken = refreshRequest.getRefreshToken();

        // Refresh token'ın imzası ve süresi geçerli mi kontrol et
        if (jwtService.validateToken(reqRefreshToken)) {
            // Token'ın içinden kullanıcının e-postasını çek
            String email = jwtService.extractEmail(reqRefreshToken);
            
            // Kullanıcıyı veritabanında doğrula
            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Geçersiz kullanıcı oturumu!"));

            // Kullanıcı pasif duruma getirilmişse engelle
            if (Boolean.FALSE.equals(user.getAktifMi())) {
                throw new RuntimeException("Hesabınız pasif duruma getirilmiş!");
            }

            // Her şey geçerliyse yepyeni bir Access ve Refresh Token seti oluştur
            String newAccessToken = jwtService.generateAccessToken(user.getEmail(), user.getRol());
            String newRefreshToken = jwtService.generateRefreshToken(user.getEmail());

            return new TokenRefreshResponseDto(newAccessToken, newRefreshToken);
        } else {
            throw new RuntimeException("Oturum süresi dolmuş. Lütfen tekrar giriş yapın!");
        }
    }
}