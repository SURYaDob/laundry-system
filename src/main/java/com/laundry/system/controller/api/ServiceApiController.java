package com.laundry.system.controller.api;

import com.laundry.system.entity.Service;
import com.laundry.system.exception.ErrorResponse;
import com.laundry.system.service.ServiceCrudService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@Tag(name = "Services", description = "REST API for laundry service catalog management")
public class ServiceApiController {

    private final ServiceCrudService serviceCrudService;

    public ServiceApiController(ServiceCrudService serviceCrudService) {
        this.serviceCrudService = serviceCrudService;
    }

    @GetMapping
    @Operation(summary = "Get all services", description = "Retrieves the list of all available laundry services")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved services",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    public ResponseEntity<List<Service>> getAllServices() {
        return ResponseEntity.ok(serviceCrudService.getAllServices());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get service by ID", description = "Retrieves a specific service by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service found"),
            @ApiResponse(responseCode = "404", description = "Service not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Service> getServiceById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceCrudService.getServiceById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new service", description = "Adds a new laundry service to the catalog (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Service created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Service with this name already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Service> createService(@Valid @RequestBody Service service) {
        Service created = serviceCrudService.createService(service);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a service", description = "Updates an existing service's details (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service updated"),
            @ApiResponse(responseCode = "404", description = "Service not found"),
            @ApiResponse(responseCode = "409", description = "Service name already in use")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Service> updateService(@PathVariable Long id, @Valid @RequestBody Service service) {
        return ResponseEntity.ok(serviceCrudService.updateService(id, service));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a service", description = "Deletes a service from the catalog (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Service deleted"),
            @ApiResponse(responseCode = "404", description = "Service not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceCrudService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}
