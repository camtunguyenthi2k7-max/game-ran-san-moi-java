package controller;

import model.PlayerRecord;
import utils.GameException;
import utils.ICRUDService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileStorageService implements ICRUDService<PlayerRecord> {
    private final String FILE_NAME = "leaderboard.dat";

    @Override
    public void addRecord(PlayerRecord item) throws GameException {
        List<PlayerRecord> list = readAllRecords();
        list.add(item);
        saveToFile(list);
    }

    @Override
    public List<PlayerRecord> readAllRecords() throws GameException {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new ArrayList<>();
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<PlayerRecord>) ois.readObject();
        } catch (Exception e) {
            throw new GameException("Lỗi đọc dữ liệu: " + e.getMessage());
        }
    }

    @Override
    public void updateRecord(int index, PlayerRecord item) throws GameException {
        List<PlayerRecord> list = readAllRecords();
        if (index >= 0 && index < list.size()) {
            list.set(index, item);
            saveToFile(list);
        }
    }

    @Override
    public void deleteRecord(int index) throws GameException {
        List<PlayerRecord> list = readAllRecords();
        if (index >= 0 && index < list.size()) {
            list.remove(index);
            saveToFile(list);
        }
    }

    private void saveToFile(List<PlayerRecord> list) throws GameException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(list);
        } catch (IOException e) {
            throw new GameException("Lỗi ghi dữ liệu: " + e.getMessage());
        }
    }
}