package homerep.homerepoauth;

import homerep.homerepoauth.controllers.SecurityController;
import homerep.homerepoauth.models.*;
import homerep.homerepoauth.repositories.RefreshTokenRepository;
import homerep.homerepoauth.repositories.UserRepository;
import homerep.homerepoauth.security.JwtCore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SecurityControllerTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtCore jwtCore;

    @InjectMocks
    private SecurityController securityController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSignup_Success() {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("testuser");
        signupRequest.setEmail("test@example.com");
        signupRequest.setPhone("+79882578790");
        signupRequest.setPassword("password");

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setUsername("testuser");
        savedUser.setEmail("test@example.com");
        savedUser.setPassword("encodedPassword");
        savedUser.setPhone("+79882578790");
        savedUser.setRole("USER");
        System.out.println(savedUser.toString());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        ResponseEntity<?> response = securityController.signup(signupRequest);
        System.out.println(response.getBody());
        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedUser, response.getBody());
    }

    @Test
    void testSignup_UsernameAlreadyExists() {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("existinguser");
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("password");

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        // Act
        ResponseEntity<?> response = securityController.signup(signupRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username already exists", response.getBody());
    }

    @Test
    void testSignup_EmailAlreadyExists() {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("testuser");
        signupRequest.setEmail("existing@example.com");
        signupRequest.setPassword("password");

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // Act
        ResponseEntity<?> response = securityController.signup(signupRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email already exists", response.getBody());
    }
    @Test
    void testSignin_Success() {
        // Arrange
        SigninRequest signinRequest = new SigninRequest();
        signinRequest.setUsername("testuser");
        signinRequest.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtCore.generateToken(authentication)).thenReturn("jwtToken");
        when(jwtCore.generateRefreshToken("testuser")).thenReturn("refreshToken");

        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setUsername("testuser");
        refreshTokenEntity.setToken("refreshToken");
        refreshTokenEntity.setExpiryDate(new Date(System.currentTimeMillis() + 100000));

        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshTokenEntity);

        // Act
        ResponseEntity<?> response = securityController.signin(signinRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof AuthResponse);

        AuthResponse authResponse = (AuthResponse) response.getBody();
        assertEquals("jwtToken", authResponse.getAccessToken());
        assertEquals("refreshToken", authResponse.getRefreshToken());

        // Проверяем, что refresh token был сохранен
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    void testSignin_InvalidCredentials() {
        // Arrange
        SigninRequest signinRequest = new SigninRequest();
        signinRequest.setUsername("testuser");
        signinRequest.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act
        ResponseEntity<?> response = securityController.signin(signinRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid username or password", response.getBody());
    }

    @Test
    void testRefreshToken_InvalidToken() {
        // Arrange
        String refreshToken = "invalid-token";
        when(refreshTokenRepository.findByToken(refreshToken)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            securityController.refreshToken(refreshToken);
        });
        assertEquals("Invalid refresh token", exception.getMessage());

        // Проверяем, что методы не были вызваны
        verify(userRepository, never()).findByUsername(any());
        verify(jwtCore, never()).generateToken(any(Authentication.class));
    }

    @Test
    void testRefreshToken_ExpiredToken() {
        // Arrange
        String refreshToken = "expired-token";
        RefreshToken storedToken = new RefreshToken();
        storedToken.setToken(refreshToken);
        storedToken.setUsername("testuser");
        storedToken.setExpiryDate(new Date(System.currentTimeMillis() - 1000)); // Истекший токен

        when(refreshTokenRepository.findByToken(refreshToken)).thenReturn(Optional.of(storedToken));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            securityController.refreshToken(refreshToken);
        });
        assertEquals("Refresh token expired", exception.getMessage());

        // Проверяем, что refresh token был удален
        verify(refreshTokenRepository, times(1)).delete(storedToken);
    }

    @Test
    void testRefreshToken_UserNotFound() {
        // Arrange
        String refreshToken = "valid-refresh-token";
        RefreshToken storedToken = new RefreshToken();
        storedToken.setToken(refreshToken);
        storedToken.setUsername("testuser");
        storedToken.setExpiryDate(new Date(System.currentTimeMillis() + 100000));

        when(refreshTokenRepository.findByToken(refreshToken)).thenReturn(Optional.of(storedToken));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            securityController.refreshToken(refreshToken);
        });
        assertEquals("User not found exception", exception.getMessage());
    }
}