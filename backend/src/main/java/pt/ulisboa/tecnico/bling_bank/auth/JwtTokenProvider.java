package pt.ulisboa.tecnico.bling_bank.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.bling_bank.auth.domain.AuthHolder;
import pt.ulisboa.tecnico.bling_bank.auth.repository.AuthHolderRepository;
import pt.ulisboa.tecnico.bling_bank.account.repository.AccountHolderRepository;

import javax.servlet.http.HttpServletRequest;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private AuthHolderRepository authHolderRepository;
    private static PublicKey publicKey;
    private static PrivateKey privateKey;

    public JwtTokenProvider(AuthHolderRepository authHolderRepository) {
        this.authHolderRepository = authHolderRepository;
    }

    public static void generateKeys() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (Exception e) {
            logger.error("Unable to generate keys");
        }
    }

    static String generateToken(AuthHolder authHolder, AccountHolderRepository accountholderRepository) {
        if (publicKey == null) {
            generateKeys();
        }

        Claims claims = Jwts.claims().setSubject(String.valueOf(authHolder.getId()));

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 1000*60*60*24);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(privateKey)
                .compact();
    }

    static String getToken(HttpServletRequest req) {
        String authHeader = req.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        } else if (authHeader != null && authHeader.startsWith("AUTH")) {
            return authHeader.substring(4);
        } else if (authHeader != null) {
            return authHeader;
        }
        return "";
    }

    private static Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token).getBody();
    }

    Authentication getAuthentication(String token) {
        Claims tokenClaims = getAllClaimsFromToken(token);
        int authHolderId = Integer.parseInt(tokenClaims.getSubject());
        AuthHolder authHolder = this.authHolderRepository.findById(authHolderId).orElseThrow(() -> new Exception("AuthHolder not found"));
        return new UsernamePasswordAuthenticationToken(authHolder, "", new ArrayList<>());
    }
}