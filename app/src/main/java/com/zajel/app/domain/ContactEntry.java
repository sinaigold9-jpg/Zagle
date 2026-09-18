package com.zajel.app.domain;

public final class ContactEntry {
    public final String name, phone, email;
    public ContactEntry(String name, String phone, String email) { this.name = name; this.phone = phone; this.email = email; }
    public String identifier() { return phone.isEmpty() ? email : phone; }
}
