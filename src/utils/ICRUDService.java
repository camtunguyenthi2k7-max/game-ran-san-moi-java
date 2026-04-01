package utils;

import java.util.List;

public interface ICRUDService<T> {
    void addRecord(T item) throws GameException;       // Create
    List<T> readAllRecords() throws GameException;     // Read
    void updateRecord(int index, T item) throws GameException; // Update
    void deleteRecord(int index) throws GameException; // Delete
}