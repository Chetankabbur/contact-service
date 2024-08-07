package com.example.contactservice;

import com.example.contactservice.entity.Contact;
import com.example.contactservice.repository.ContactRepository;
import com.example.contactservice.service.ContactService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ContactServiceTests {

    @Autowired
    private ContactService contactService;

    @MockBean
    private ContactRepository contactRepository;

    @Test
    public void testProcessContactExistingEntry() {
        // Setup
        // Creating a mock existing contact
        Contact existingContact = new Contact();
        existingContact.setId(1L);
        existingContact.setEmail("existing@example.com");
        existingContact.setPhoneNumber("1234567890");
        existingContact.setLinkPrecedence(Contact.LinkPrecedence.PRIMARY);

        // Mocking the behavior of the contactRepository to return the existing contact
        when(contactRepository.findByEmailOrPhoneNumber("existing@example.com", "1234567890"))
                .thenReturn(Collections.singletonList(existingContact));

        // Action
        Map<String, Object> response = contactService.processContact("existing@example.com", "1234567890");

        // Assertions
        assertThat(response).containsKeys("primaryContactId", "emails", "phoneNumbers", "secondaryContactIds");
    }
}
