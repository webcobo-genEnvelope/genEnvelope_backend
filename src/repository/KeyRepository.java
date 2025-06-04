package repository;

import java.io.*;
import java.security.Key;

public class KeyRepository {
    public void saveKeyFile(Key key, String filePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(key);
        }
    }

}
