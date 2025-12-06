package com.decen;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class PrimaryController {

    @FXML
    private TextField inputlabel;


    @FXML
    private void onEncryptClick() {

        String value = inputlabel.getText();

        if (value == null || value.trim().isEmpty()) {
            System.out.println("No path entered!");
            return;
        }

        value = value.trim().replace("\"", "");

        try {
            Path input = Paths.get(value);

            if (ChunkedAESRSA.savedRSAKeys == null)
                ChunkedAESRSA.savedRSAKeys = ChunkedAESRSA.generateRSAKeyPair(2048);

            ChunkedAESRSA.encryptFileToChunks(input, ChunkedAESRSA.savedRSAKeys.getPublic());

            System.out.println("Encrypted successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void onDecryptClick() {

        String value = inputlabel.getText();
        if (value == null || value.trim().isEmpty()) {
            System.out.println("No path entered!");
            return;
        }

        value = value.trim().replace("\"", "");

        try {
            Path input = Paths.get(value);

            Path chunk = input.resolveSibling(input.getFileName().toString() + ".chunk0001.enc");

            String originalName = input.getFileName().toString();
            int i = originalName.lastIndexOf(".");
            String restoredName;

            if (i > 0) {
                restoredName = originalName.substring(0, i) + "_restored" + originalName.substring(i);
            } else {
                restoredName = originalName + "_restored";
            }

            Path output = input.getParent().resolve(restoredName);

            ChunkedAESRSA.decryptChunksToFile(chunk, ChunkedAESRSA.savedRSAKeys.getPrivate(), output);

            System.out.println("Decrypted successfully to: " + output);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
