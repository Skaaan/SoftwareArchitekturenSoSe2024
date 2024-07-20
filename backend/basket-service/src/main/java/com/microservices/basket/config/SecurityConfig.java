package com.microservices.basket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/baskets/**").authenticated()
                                .anyRequest().permitAll()
                )
                .oauth2ResourceServer(oauth2ResourceServer ->
                        oauth2ResourceServer.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                )
                .logout(logout ->
                        logout
                                .logoutUrl("/logout")
                                .invalidateHttpSession(true)
                                .deleteCookies("JSESSIONID")
                                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
                );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() throws Exception {
        RSAPublicKey publicKey1 = loadPublicKey("-----BEGIN CERTIFICATE-----\nMIIDHTCCAgWgAwIBAgIJAMfzGQUnE/2GMA0GCSqGSIb3DQEBBQUAMDExLzAtBgNV\nBAMMJnNlY3VyZXRva2VuLnN5c3RlbS5nc2VydmljZWFjY291bnQuY29tMB4XDTI0\nMDcxMTA3MzIzMFoXDTI0MDcyNzE5NDczMFowMTEvMC0GA1UEAwwmc2VjdXJldG9r\nZW4uc3lzdGVtLmdzZXJ2aWNlYWNjb3VudC5jb20wggEiMA0GCSqGSIb3DQEBAQUA\nA4IBDwAwggEKAoIBAQC//i5yMxd2NYI7tKqr20kS2ZTJ33BgLI+GEd05taMne8CN\n4MfjgJAdW3kEjIvo/Kxc/8fgEhUIxIMTkWQMAG3jYppSprW0hdPBgruEpFuT4NT8\nNgwWy1WEkO8vDXKCF0Zxg3tyVhgNlrx1Nn77PhTT/bDjRh9QyvtIgGmJ6qlIsSLz\nVZS3SAZiJFAwo2jiLFAqPvVxvb4VO0YFn9MPXOEtUxJeNNJ+OJxaMx0RreMfrnoV\naLVtSCt3sb7i8BnkMVKViLpwT1wLw430GCIiigOmss4/kVu9GbBbDO1U1cVYbEh/\nd+Yi4byRMoLr+NymK4xaeI6fQf7aW+KCyaKabcwtAgMBAAGjODA2MAwGA1UdEwEB\n/wQCMAAwDgYDVR0PAQH/BAQDAgeAMBYGA1UdJQEB/wQMMAoGCCsGAQUFBwMCMA0G\nCSqGSIb3DQEBBQUAA4IBAQB0wzW9Bl5e1pT4ujxepVlk/SJ/sBl5RsAQaxOAtjJ9\n5avJe0JOflN9ssSDmRartC++TM2r3LdUgQ5fCTs/426TbiIjvfFqQf4TFoLBLRaX\nRHUlaLqBDEbitzbFh1+Vitnps9czJVN/fKCDyqt/3dzPiMjXgssN69jH0oL0QSgb\nWDvv5D9gi2gQgGeFscCCbgH2+DLdP/ztuUnZvbA8lQ5nYUcYOu++OMZCY1p5zmgA\nbD0sjNw/n5QARdfbkEWtlrx7uqN014DV7qMvxGZCzm0MR4yn4CoUp9aHJgfLI1uH\nmS5VG2SdROcOKQilMr23F7C4OVk8B82xhc0FU/YqArHG\n-----END CERTIFICATE-----\n");
        RSAPublicKey publicKey2 = loadPublicKey("-----BEGIN CERTIFICATE-----\nMIIDHTCCAgWgAwIBAgIJAKGV2cCEDbyQMA0GCSqGSIb3DQEBBQUAMDExLzAtBgNV\nBAMMJnNlY3VyZXRva2VuLnN5c3RlbS5nc2VydmljZWFjY291bnQuY29tMB4XDTI0\nMDcwMzA3MzIyOVoXDTI0MDcxOTE5NDcyOVowMTEvMC0GA1UEAwwmc2VjdXJldG9r\nZW4uc3lzdGVtLmdzZXJ2aWNlYWNjb3VudC5jb20wggEiMA0GCSqGSIb3DQEBAQUA\nA4IBDwAwggEKAoIBAQCeU/3qKeOGKkkQbsV44VDpas2YuO7L143OyKj7rSkHtzK7\nQSUZKhf60VtUj/Rnxq+gQgKupNx2PxIv0Aa6Uwiluim9Wd5acJYe4A7jxBM1YLMz\nYVJqJlDNrA0qYvE7woBUAeMgfpHU5s908E1J1145HCRMG91PF6EMzrlZEcsVnBMd\nf15X5FCfq0uFQ2t6F7RkboSBh7MeNIlgHvqLheVkafaGMD/2zzN4TYBPHfFQubcq\njuTDjrMJZyWEEA7W8U4TIjalkecKDLDoc9fV2gQtHOiXkjSHk+ReqAY+1fF05t8G\n5xDjivWacidQ4hdvrhlGuDuWyTnYczXJXSlAJOKPAgMBAAGjODA2MAwGA1UdEwEB\n/wQCMAAwDgYDVR0PAQH/BAQDAgeAMBYGA1UdJQEB/wQMMAoGCCsGAQUFBwMCMA0G\nCSqGSIb3DQEBBQUAA4IBAQA8IHMtaPPP3wtsSMri8F4asdIwTBuLS/bwAn1/Qs8J\nZNA2q5r88BXPBHU1me44xYeIYAlbmfq85oCeZIrIK9R5x+HSqp1xG7kxbeQLq+qY\noBqZzYztB5aA9djCq3vxxIREHKr1jzFWcjiKt97CtyMSquT/JhBm2RkguwGpr0Vw\nX+pV9WNPJYTjOGb0er1ks+WN2PBLs5/Fc0KJxOAggAOzWdeouqKjWTLS4DK8IGnJ\n5/26xQ9tYBt8oiKUErU/pwjob260aR20em7vIE46WpZVkQ+7oh+j1MD4zUoix/s7\nMGtrrIHtru1+4IiUVxOV//jxJGUVSlAkxPLxqWGHmIYh\n-----END CERTIFICATE-----\n");

        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(publicKey1).build();

        return jwtDecoder;
    }

    private RSAPublicKey loadPublicKey(String certificate) throws Exception {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        X509Certificate x509Certificate = (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(certificate.getBytes(StandardCharsets.UTF_8)));
        PublicKey publicKey = x509Certificate.getPublicKey();
        return (RSAPublicKey) publicKey;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}
