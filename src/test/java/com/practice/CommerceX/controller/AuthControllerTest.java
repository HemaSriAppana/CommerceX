package com.practice.CommerceX.controller;
import static org.mockito.ArgumentMatchers.anyString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.practice.CommerceX.AuthService.controller.AuthController;
import com.practice.CommerceX.config.SecurityConfig;
import com.practice.CommerceX.dto.RegisterUser;
import com.practice.CommerceX.entity.RefreshToken;
import com.practice.CommerceX.entity.User;
import com.practice.CommerceX.exception.UserAlreadyExistsException;
import com.practice.CommerceX.security.CommerceAuthenticationProvider;
import com.practice.CommerceX.security.CommerceUserDetailsService;
import com.practice.CommerceX.security.JwtService;
import com.practice.CommerceX.service.ChangePasswordService;
import com.practice.CommerceX.service.RefreshTokenService;
import com.practice.CommerceX.service.UserService;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private RefreshTokenService refreshTokenService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ChangePasswordService changePasswordService;
    @MockitoBean
    private CommerceUserDetailsService commerceUserDetailsService;
    @MockitoBean
    private CommerceAuthenticationProvider authenticationProvider;


    @Test
    void shouldCreateUser() throws Exception {

        // Arrange
        String requestBody = """
        {
            "name": "Hema",
            "email": "hema@gmail.com",
            "password": "password123",
            "age": 25,
            "phoneNumber":"2345678901"
        }
        """;


        // Act + Assert
        mockMvc.perform(
                post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody)
        )
        .andExpect(status().isCreated())
        .andExpect(
                content().string(
                        "User registered successfully"
                )
        );


        // Verify service was called
        ArgumentCaptor<RegisterUser> captor =
                ArgumentCaptor.forClass(RegisterUser.class);

        verify(userService)
                .saveUser(captor.capture());


        // Get captured DTO - Now we're testing that the JSON was correctly converted into your RegisterUser DTO.
        RegisterUser capturedRequest =
                captor.getValue();


        // Verify request data
        assertEquals(
                "Hema",
                capturedRequest.getName()
        );

        assertEquals(
                "hema@gmail.com",
                capturedRequest.getEmail()
        );

        assertEquals(
                "password123",
                capturedRequest.getPassword()
        );

        
    }
    
    @Test
    void shouldRejectInvalidEmail() throws Exception {

        String requestBody = """
        {
            "name": "Hema",
            "email": "wrong-email", 
            "password": "password123",
            "age": 25,
            "phoneNumber": "9876543210"
        }
        """;

        mockMvc.perform(
                post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody)
        )
        .andExpect(status().isBadRequest());

        verify(userService, never())
                .saveUser(any(RegisterUser.class));  //never make sures This method must NOT have been called.
    }
    
    @Test
    void shouldRejectBlankName() throws Exception {

        String requestBody = """
        {
            "name": "",
            "email": "hema@gmail.com",
            "password": "password123",
            "age": 25,
            "phoneNumber": "9876543210"
        }
        """;

        mockMvc.perform(
                post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody)
        )
        .andExpect(status().isBadRequest());

        verify(userService, never())
                .saveUser(any(RegisterUser.class));
    }
    @Test
    void shouldRejectUnderAgeUser() throws Exception {

        String requestBody = """
        {
            "name": "Hema",
            "email": "hema@gmail.com",
            "password": "password123",
            "age": 17,
            "phoneNumber": "9876543210"
        }
        """;

        mockMvc.perform(
                post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody)
        )
        .andExpect(status().isBadRequest());

        verify(userService, never())
                .saveUser(any(RegisterUser.class));
    }
    @Test
    void shouldRejectInvalidPhoneNumber() throws Exception {

        String requestBody = """
        {
            "name": "Hema",
            "email": "hema@gmail.com",
            "password": "password123",
            "age": 25,
            "phoneNumber": "987654"
        }
        """;

        mockMvc.perform(
                post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody)
        )
        .andExpect(status().isBadRequest());

        verify(userService, never())
                .saveUser(any(RegisterUser.class));
    }
    
    //handling exceptions--Testing Exception Handling with MockMvc
    @Test
    void shouldReturnConflictWhenUserAlreadyExists()
            throws Exception {

        // Arrange
        doThrow(
            new UserAlreadyExistsException(
                "User with this email already exists"
            )
        )
        .when(userService)
        .saveUser(any(RegisterUser.class));

        String requestBody = """
        {
            "name": "Hema",
            "email": "hema@gmail.com",
            "password": "password123",
            "age": 25,
            "phoneNumber": "9876543210"
        }
        """;

        // Act + Assert
        mockMvc.perform(
                post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody)
        )
        .andExpect(status().isConflict())
        .andExpect(
        		 jsonPath("$.message")
                 .value(
                     "User with this email already exists"
                 )
        );
    }
    
    @Test
    void shouldLogoutUser() throws Exception {

        String requestBody = """
        {
            "refreshToken": "refresh-token-123"
        }
        """;

        mockMvc.perform(
                post("/auth/logout")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody)
        )
        .andExpect(status().isOk())
        .andExpect(
                content().string(
                        "Logout successful"
                )
        );

        verify(refreshTokenService)
                .logout("refresh-token-123");
    }
    
    @Test
    void shouldChangePassword() throws Exception {

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "hema",
                        null,
                        List.of(
                            new SimpleGrantedAuthority("ROLE_ADMIN")
                        )
                );   // this creates an authenticated user with role Admin

        String requestBody = """
        {
            "oldPassword": "oldPassword",
            "newPassword": "newPassword123"
        }
        """;

        mockMvc.perform(
                post("/auth/change-password")
                    .with(authentication(authentication))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody)
        )
        .andExpect(status().isOk())
        .andExpect(
                content().string(
                        "Password changed successfully"
                )
        );

        verify(changePasswordService)
                .changePassword(
                        "hema",
                        "oldPassword",
                        "newPassword123"
                );
    }
    @Test
    void shouldLoginSuccessfully() throws Exception {

        Authentication authentication =
                mock(Authentication.class);

        User user = new User();

        user.setUsername("hema");
        user.setTokenVersion(1L);

        RefreshToken refreshToken =
                new RefreshToken();

        refreshToken.setToken("refresh-token-123");

        when(
            authenticationManager.authenticate(any())
        )
        .thenReturn(authentication);

        when(
            authentication.getName()
        )
        .thenReturn("hema");

        when(
            userService.findUser("hema")
        )
        .thenReturn(user);

        when(
            jwtService.generateAccessToken(
                    "hema",
                    1L
            )
        )
        .thenReturn("access-token-123");

        when(
            refreshTokenService.createRefreshToken(
                    "hema",
                    1L
            )
        )
        .thenReturn(refreshToken);

        String requestBody = """
        {
            "username": "hema",
            "password": "password123"
        }
        """;

        mockMvc.perform(
                post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody)
        )
        .andExpect(status().isOk())
        .andExpect(
                jsonPath("$.accessToken")
                        .value("access-token-123")
        )
        .andExpect(
                jsonPath("$.refreshToken")
                        .value("refresh-token-123")
        );
    }
    
    @Test
    void shouldRejectInvalidLoginCredentials()
            throws Exception {

        when(
            authenticationManager.authenticate(any())
        )
        .thenThrow(
            new BadCredentialsException(
                "Bad credentials"
            )
        );

        String requestBody = """
        {
            "username": "hema",
            "password": "wrongPassword"
        }
        """;

        mockMvc.perform(
                post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody)
        )
        .andExpect(status().isUnauthorized())
        .andExpect(
                content().string(
                        "Invalid username or password"
                )
        );
    }
    
//    @Test
//    @WithMockUser(
//            username = "hema",
//            roles = "USER"
//    )
//    void customerShouldNotChangePassword() throws Exception {
//
//        String requestBody = """
//        {
//            "oldPassword": "oldPassword",
//            "newPassword": "newPassword123"
//        }
//        """;
//
//        mockMvc.perform(
//                post("/auth/change-password")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(requestBody)
//        )
//        .andExpect(status().isForbidden());
//
//        verify(changePasswordService, never())
//                .changePassword(
//                        anyString(),
//                        anyString(),
//                        anyString()
//                );
//    }
//    
    @Test
    void customerShouldNotChangePassword1() throws Exception {

        String requestBody = """
        {
            "oldPassword": "oldPassword",
            "newPassword": "newPassword123"
        }
        """;

        mockMvc.perform(
                post("/auth/change-password")
                    .with(user("hema").roles("CUSTOMER"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody)
        )
        .andExpect(status().isForbidden());

        verify(changePasswordService, never())
                .changePassword(
                        anyString(),
                        anyString(),
                        anyString()
                );
    }
}