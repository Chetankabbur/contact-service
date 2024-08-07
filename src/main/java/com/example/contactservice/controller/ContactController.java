package com.example.contactservice.controller;

import com.example.contactservice.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller
 */
@RestController
@RequestMapping("/contact/v1/identify")
public class ContactController {

    @Autowired
    private ContactService contactService;

    /**
     * Create or Update Contact
     * @param request the request contain email and phonenumber
     * @return Contact details
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> processContact(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String phoneNumber = request.get("phoneNumber");

            if (email == null || phoneNumber == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid input"));
            }

            Map<String, Object> response = contactService.processContact(email, phoneNumber);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Log the exception or handle it appropriately
            return ResponseEntity.status(500).body(Map.of("error", "Internal Server Error"));
        }
    }
}
