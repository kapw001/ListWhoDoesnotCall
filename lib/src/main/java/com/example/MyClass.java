package com.example;

import java.util.ArrayList;
import java.util.List;

public class MyClass {

    public static void main(String[] arg) {
        System.out.println("---------------------------------------------------");


        List<Contact> aList = new ArrayList<>();
        aList.add(new Contact("Test", "96"));
        aList.add(new Contact("Kar", "90"));


        List<Contact> bList = new ArrayList<>();
        bList.add(new Contact("T", "98"));
        bList.add(new Contact("K", "91"));
        bList.add(new Contact("Test", "96"));

        List<Contact> fList = new ArrayList<>();

        for (Contact contact : aList
                ) {
            for (Contact contact1 : bList
                    ) {
                if (contact.getPhoneNumber().equalsIgnoreCase(contact1.getPhoneNumber())) {
                    fList.add(contact1);
                    break;
                }
            }
        }


        for (Contact contact : fList
                ) {

            System.out.println(contact.getPhoneNumber() + " " + contact.getName());
        }
    }
}
