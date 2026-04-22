/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.dao;

/**
 *
 * @author thivi
 */

import com.smartcampus.model.BaseModel;
import java.util.List;
import java.util.UUID;

public class GenericDAO<T extends BaseModel> {
    private final List<T> items;

    public GenericDAO(List<T> items) {
        this.items = items;
    }

    public List<T> getAll() {
        return items;
    }

    public T getById(String id) {
        for (T item : items) {
            if (item.getId() != null && item.getId().equals(id)) { 
                return item; 
            }
        }
        return null;
    }

    public void add(T item) {
        // Generates a unique String ID if the client doesn't provide one
        if (item.getId() == null || item.getId().trim().isEmpty()) {
            item.setId(UUID.randomUUID().toString());
        }
        items.add(item);
    }

    public void update(T updatedItem) {
        for (int i = 0; i < items.size(); i++) {
            T item = items.get(i);
            if (item.getId().equals(updatedItem.getId())) {
                items.set(i, updatedItem);
                return;
            }
        }
    }

    public void delete(String id) {
        items.removeIf(item -> item.getId().equals(id));
    }
}
