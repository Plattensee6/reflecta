package hu.test.reflecta.app.user.controller;

import hu.test.reflecta.auth.dto.AppUserRolesRequest;
import hu.test.reflecta.auth.service.AppUserService;
import hu.test.reflecta.user.data.dto.UserRequest;
import hu.test.reflecta.user.data.dto.UserResponse;
import hu.test.reflecta.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "CRUD operations for users")
public class UserController {
    private final UserService userService;
    private final AppUserService appUserService;

    @PreAuthorize("hasRole('READ')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "ID of the user") @PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200", description = "List of users")
    @PreAuthorize("hasRole('READ') and hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.DEFAULT_DIRECTION);
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @Operation(summary = "Update a user by ID")
    @ApiResponse(responseCode = "200", description = "User updated successfully")
    @ApiResponse(responseCode = "204", description = "User not found")
    @PreAuthorize("hasRole('WRITE')")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "ID of the user") @PathVariable Long id,
            @RequestBody @Valid UserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @Operation(summary = "Delete a user by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasRole('WRITE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of the user") @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('WRITE') and hasRole('ADMIN')")
    @PutMapping("/roles/add")
    public ResponseEntity<Void> addRoles(
            @RequestBody @Valid AppUserRolesRequest request) {
        appUserService.addRoles(request);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('WRITE') and hasRole('ADMIN')")
    @PutMapping("/roles/revoke")
    public ResponseEntity<Void> revokeRoles(
            @RequestBody @Valid AppUserRolesRequest request) {
        appUserService.revokeRoles(request);
        return ResponseEntity.noContent().build();
    }
}
