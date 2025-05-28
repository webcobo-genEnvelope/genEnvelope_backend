package repository;

import java.io.*;
import java.security.Key;

public class KeyRepository {

    public void saveKeyToFile(Key key, String filePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(key);
        }
    }

    public Key loadKeyFromFile(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (Key) ois.readObject();
        }
    }
}
