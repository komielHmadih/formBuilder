package com.example.formbuilder.controller;

import com.example.formbuilder.model.Form;
import com.example.formbuilder.model.Field;
import com.example.formbuilder.repository.FormRepository;
import com.example.formbuilder.repository.FieldRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//import java.util.Optional;

@RestController
@RequestMapping("/forms")
public class FormController {

    private final FormRepository formRepository;
    private final FieldRepository fieldRepository;

    public FormController(FormRepository formRepository, FieldRepository fieldRepository) {
        this.formRepository = formRepository;
        this.fieldRepository = fieldRepository;
    }

    // Get all forms
    @GetMapping
    public List<Form> getAllForms() {
        return formRepository.findAll();
    }

    // Create a new form
    @PostMapping
    public Form createForm(@RequestBody Form form) {
        return formRepository.save(form);
    }

    // Get a form by ID
    @GetMapping("/{id}")
    public ResponseEntity<Form> getFormById(@PathVariable Long id) {
        return formRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update a form
    @PutMapping("/{id}")
    public ResponseEntity<Form> updateForm(@PathVariable Long id, @RequestBody Form updatedForm) {
        return formRepository.findById(id).map(form -> {
            form.setName(updatedForm.getName());
            form.setPublished(updatedForm.isPublished());
            form.setSubmitUrl(updatedForm.getSubmitUrl());
            return ResponseEntity.ok(formRepository.save(form));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Delete a form
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteForm(@PathVariable Long id) {
        if (formRepository.existsById(id)) {
            formRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Get all fields of a form
    @GetMapping("/{id}/fields")
    public ResponseEntity<List<Field>> getFormFields(@PathVariable Long id) {
        return formRepository.findById(id)
                .map(form -> ResponseEntity.ok(form.getFields()))
                .orElse(ResponseEntity.notFound().build());
    }

    // Update fields of a form
    @PutMapping("/{id}/fields")
    public ResponseEntity<Form> updateFormFields(@PathVariable Long id, @RequestBody List<Field> fields) {
        return formRepository.findById(id).map(form -> {
            for (Field field : fields) {
                field.setForm(form);
            }
            fieldRepository.saveAll(fields);
            form.setFields(fields);
            return ResponseEntity.ok(formRepository.save(form));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Publish a form
    @PostMapping("/{id}/publish")
    public ResponseEntity<Form> publishForm(@PathVariable Long id) {
        return formRepository.findById(id).map(form -> {
            form.setPublished(true);
            return ResponseEntity.ok(formRepository.save(form));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Get all published forms
    @GetMapping("/published")
    public List<Form> getPublishedForms() {
        return formRepository.findAll().stream()
                .filter(Form::isPublished)
                .toList();
    }
}
