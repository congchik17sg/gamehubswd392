package com.example.GameHub.service;


import com.example.GameHub.entities.User;
import com.example.GameHub.exception.AppException;
import com.example.GameHub.exception.ErrorCode;
import com.example.GameHub.model.request.AuthenticationRequest;
import com.example.GameHub.model.response.AuthenticationResponse;
import com.example.GameHub.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    UserRepository userRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;


//    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
//        var token = request.getToken();
//        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
//        SignedJWT signedJWT = SignedJWT.parse(token);
//        Date expiredTime = signedJWT.getJWTClaimsSet().getExpirationTime();
//        var verified = signedJWT.verify(verifier);
//        return IntrospectResponse.builder()
//                .valid(verified && expiredTime.after(new Date()))
//                .build();
//    }

    public AuthenticationResponse authenticated(AuthenticationRequest request) {
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!user.isStatus()) {
            throw new AppException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassword(),
                user.getPassword());
        if (!authenticated)
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        var token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .isAuthenticated(true)
                .build();
    }

    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
//                .subject(user.getUsername())
//                .issuer("congchi.deptrai")// ten nguoi doamain
                .issuer("gamehub.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                ))
                .claim("username", user.getUsername())
                .claim("role", buildRole(user))
                .claim("userId", user.getId())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    private String buildRole(User user) {
        if (user.getUserRoles() == null) return "";
        return user.getUserRoles().stream()
                .map(userRole -> userRole.getRole().getRole()) // Lấy tên vai trò từ Role
                .collect(Collectors.joining(" "));
    }

}
