package io;

import entities.Library;
import entities.items.LibraryItem;

import java.io.*;

public class SerializationHandler {

    public void serializeLibrary(Library library, String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filename))) {
            oos.writeObject(library);
        }
    }

    public Library deserializeLibrary(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filename))) {
            return (Library) ois.readObject();
        }
    }

    public void serializeItem(LibraryItem item, String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filename))) {
            oos.writeObject(item);
        }
    }

    public LibraryItem deserializeItem(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filename))) {
            return (LibraryItem) ois.readObject();
        }
    }
}