package com.example.contactservice.controller;

import com.example.contactservice.entity.Contact;
import com.example.contactservice.repository.ContactRepository;
import com.example.contactservice.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * Controller
 */
@RestController
@RequestMapping("/v1/contact")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @Autowired
    private ContactRepository contactRepository;

    /**
     * Create or Update Contact
     *
     * @param request the request contain email and phonenumber
     * @return Contact details
     */
    @PostMapping("/identify")
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
    @GetMapping
    public ResponseEntity<?> getAllContacts(@RequestParam(name = "id", required = false) Long contactID,
                                            @RequestParam(name = "email", required = false) String email,
                                            @RequestParam(name = "phoneNumber", required = false) String phoneNumber) {
        try {
            return ResponseEntity.ok(contactService.findByIdOrEmailOrPhoneNumber(contactID,email,phoneNumber ));
        }catch (Exception e){
            return ResponseEntity.status(500).body( Map.of("error", "Internal Server Error"));
        }
    }
    /**
     * Delete BY ID
     *
     * @param id the unique ID
     * @return result
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id){
        try{
            Optional<Contact> existingContact = this.contactRepository.findById(id);
            if(existingContact.isPresent()){
                this.contactService.delete(existingContact.get());
                return ResponseEntity.ok().body("Successfully delete contact Id:"+id);
            }else{
                return ResponseEntity.status(400).body("Record not found");
            }
        }catch (Exception e){
            return ResponseEntity.status(500).body("Internal Server Error");
        }
    }
}
