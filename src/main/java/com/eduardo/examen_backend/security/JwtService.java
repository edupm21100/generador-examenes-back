package com.eduardo.examen_backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

/**
 * Servicio central encargado de la gestión de JSON Web Tokens (JWT).
 * Proporciona la lógica criptográfica para generar tokens de sesión, 
 * firmarlos digitalmente y extraer la información de su payload.
 * * @author Eduardo
 * @version 1.0
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * Genera un nuevo token JWT para un usuario autenticado.
     * El token incluye el correo como "Subject" (Sujeto), la fecha de emisión
     * y una fecha de expiración configurada.
     *
     * @param correoUsuario El correo electrónico del usuario logueado.
     * @return Una cadena de texto que representa el token JWT firmado.
     */
    public String generateToken(String correoUsuario) {
        return Jwts.builder()
                .setSubject(correoUsuario)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // Nota: Configurado actualmente para una duración extendida
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) 
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrae la clave de firma criptográfica a partir del secreto codificado en Base64.
     * Esta clave se utiliza tanto para firmar nuevos tokens como para verificar los recibidos.
     *
     * @return Objeto Key compatible con el algoritmo HMAC-SHA.
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extrae el nombre de usuario (correo) almacenado en el token JWT.
     *
     * @param token El token JWT enviado por el cliente en la cabecera HTTP.
     * @return El correo del usuario si el token es válido y no ha expirado.
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Desencripta y valida el token JWT utilizando la clave secreta del servidor.
     * Si el token ha sido modificado, está mal formado o ha expirado, 
     * este método lanzará una excepción de la librería io.jsonwebtoken.
     *
     * @param token El token JWT a procesar.
     * @return Los Claims (datos del payload) contenidos en el token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}