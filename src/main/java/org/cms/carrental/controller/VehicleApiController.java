package org.cms.carrental.controller;

import lombok.RequiredArgsConstructor;
import org.cms.carrental.dto.ApiResponse;
import org.cms.carrental.dto.VehicleMetadata;
import org.cms.carrental.service.VehicleApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VehicleApiController {

    private final VehicleApiService vehicleApiService;

    @GetMapping("/metadata")
    public ResponseEntity<ApiResponse<VehicleMetadata>> getVehicleMetadata(
            @RequestParam String make,
            @RequestParam String model,
            @RequestParam Integer year) {

        VehicleMetadata metadata = vehicleApiService.getVehicleMetadata(make, model, year);
        return ResponseEntity.ok(ApiResponse.success("Vehicle metadata retrieved", metadata));
    }

    @GetMapping("/makes")
    public ResponseEntity<ApiResponse<List<String>>> getAllMakes() {
        List<String> makes = vehicleApiService.getAllMakes();
        return ResponseEntity.ok(ApiResponse.success("Vehicle makes retrieved", makes));
    }

    @GetMapping("/models/{make}")
    public ResponseEntity<ApiResponse<List<String>>> getModelsForMake(@PathVariable String make) {
        List<String> models = vehicleApiService.getModelsForMake(make);
        return ResponseEntity.ok(ApiResponse.success("Models retrieved for make: " + make, models));
    }
}

