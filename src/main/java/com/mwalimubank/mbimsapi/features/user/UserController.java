package com.mwalimubank.mbimsapi.features.user;

import com.mwalimubank.mbimsapi.core.dto.ApiResponse;
import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.core.dto.PaginationResponse;
import com.mwalimubank.mbimsapi.features.user.dto.CreateUserDTO;
import com.mwalimubank.mbimsapi.features.user.dto.UserResponseDTO;
import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalAwareDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping
    public PagedResponse<UserResponseDTO> findAll(
            PaginationRequest pagination,
            @RequestParam(required = false) String search
    ) {
                return service.findAll(pagination, search);
    }

    @PostMapping
    public UserResponseDTO  create(
            @RequestBody CreateUserDTO request
    ) {
        return service.create(request);
    }

     @GetMapping("/{id}")
        public ApprovalAwareDTO<UserResponseDTO> findOne(
                @PathVariable Long id
        ) {
            return service.findOne(id);
        }

    @PatchMapping("/{id}")
    public UserResponseDTO update(
            @PathVariable Long id,
            @RequestBody CreateUserDTO request
    ) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @PathVariable Long id,
            @RequestParam(name = "soft", defaultValue = "false") boolean soft
    ) {
        service.delete(id, soft);
        return ApiResponse.success(null);
    }
}
