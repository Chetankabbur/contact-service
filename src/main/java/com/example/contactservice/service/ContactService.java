package com.example.contactservice.service;

import com.example.contactservice.entity.Contact;
import com.example.contactservice.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
/**
 * Contact service
 */
@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    /**
     * Create or update Contact
     * @param email the Email ID
     * @param phoneNumber the phone number
     * @return Contact details
     */
    public Map<String, Object> processContact(String email, String phoneNumber) {
        List<Contact> existingContacts = contactRepository.findByEmailOrPhoneNumber(email, phoneNumber);

        Contact primaryContact;
        if (existingContacts.isEmpty()) {
            primaryContact = createPrimaryContact(email, phoneNumber);
        } else {
            primaryContact = updateContacts(existingContacts, email, phoneNumber);
        }

        return Map.of(
                "primaryContactId", primaryContact.getId(),
                "emails", getAllEmails(primaryContact.getId()),
                "phoneNumbers", getAllPhoneNumbers(primaryContact.getId()),
                "secondaryContactIds", getSecondaryContactIds(primaryContact.getId())
        );
    }

    /**
     * Create the primary contact
     * @param email the Email ID
     * @param phoneNumber the phone Number
     * @return Contact details
     */
    private Contact createPrimaryContact(String email, String phoneNumber) {
        Contact newContact = new Contact();
        newContact.setEmail(email);
        newContact.setPhoneNumber(phoneNumber);
        newContact.setLinkPrecedence(Contact.LinkPrecedence.PRIMARY);
        newContact.setCreatedAt(LocalDateTime.now());
        newContact.setUpdatedAt(LocalDateTime.now());
        return contactRepository.save(newContact);
    }

    /**
     *
     * @param existingContacts
     * @param email the emailId
     * @param phoneNumber the phoneNumber
     * @return Contact
     */
    private Contact updateContacts(List<Contact> existingContacts, String email, String phoneNumber) {
        Contact primaryContact = findPrimaryContact(existingContacts);

        Contact secondaryContact = new Contact();
        secondaryContact.setEmail(email);
        secondaryContact.setPhoneNumber(phoneNumber);
        secondaryContact.setLinkedId(primaryContact.getId());
        secondaryContact.setLinkPrecedence(Contact.LinkPrecedence.SECONDARY);
        secondaryContact.setCreatedAt(LocalDateTime.now());
        secondaryContact.setUpdatedAt(LocalDateTime.now());
        contactRepository.save(secondaryContact);

        return primaryContact;
    }

    private Contact findPrimaryContact(List<Contact> contacts) {
        return contacts.stream()
                .filter(contact -> contact.getLinkPrecedence() == Contact.LinkPrecedence.PRIMARY)
                .findFirst()
                .orElse(contacts.get(0));
    }

    public List<String> getAllEmails(Long primaryContactId) {
        return contactRepository.findAll().stream()
                .filter(contact -> contact.getId().equals(primaryContactId) || (contact.getLinkedId() != null && contact.getLinkedId().equals(primaryContactId)))
                .map(Contact::getEmail)
                .collect(Collectors.toList());
    }

    public List<String> getAllPhoneNumbers(Long primaryContactId) {
        return contactRepository.findAll().stream()
                .filter(contact -> contact.getId().equals(primaryContactId) || (contact.getLinkedId() != null && contact.getLinkedId().equals(primaryContactId)))
                .map(Contact::getPhoneNumber)
                .collect(Collectors.toList());
    }

    public List<Long> getSecondaryContactIds(Long primaryContactId) {
        return contactRepository.findAll().stream()
                .filter(contact -> contact.getLinkedId() != null && contact.getLinkedId().equals(primaryContactId))
                .map(Contact::getId)
                .collect(Collectors.toList());
    }

    /**
     * Find By Id Or Email Or PhoneNumber
     * @param contactId the contactID
     * @param email the email ID
     * @param phoneNumber the phoneNumber
     * @return the contactList
     */
    public List<Contact> findByIdOrEmailOrPhoneNumber(Long contactId, String email, String phoneNumber) {
        List<Contact> existingContacts = List.of();
        if(contactId!=null && !contactId.equals(0L)) {
            Optional<Contact> contacts = contactRepository.findById(contactId);
            existingContacts = contacts.map(Collections::singletonList).orElse(Collections.emptyList());
        }else if (email!=null || phoneNumber!=null) {
            existingContacts = contactRepository.findByEmailOrPhoneNumber(email, phoneNumber);
        }else{
            existingContacts = contactRepository.findAll();
        }
        return existingContacts;
    }

    /**
     * Delete the contact
     * @param contact
     */
    public void delete(Contact contact){
        this.contactRepository.delete(contact);
    }


}
