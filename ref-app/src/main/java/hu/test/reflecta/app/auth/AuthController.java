package hu.test.reflecta.app.auth;

import hu.test.reflecta.app.auth.usecase.RegistrationFacade;
import hu.test.reflecta.app.auth.usecase.RegistrationRequest;
import hu.test.reflecta.auth.dto.LoginRequest;
import hu.test.reflecta.auth.dto.LoginResponse;
import hu.test.reflecta.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication")
public class AuthController {
    private final AuthService authService;
    private final RegistrationFacade registrationFacade;

    @Operation(summary = "Authenticate and return JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful login"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/authenticate")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginRequest authRequest) throws Exception {
        LoginResponse response = authService.login(authRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Register a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful registration"),
            @ApiResponse(responseCode = "401", description = "Invalid input")
    })
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody RegistrationRequest registrationRequest) throws Exception {
        registrationFacade.register(registrationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
