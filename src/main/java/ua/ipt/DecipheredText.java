package ua.ipt;

import java.io.IOException;


/**
 * Created by Roman Horilyi on 15.03.2016.
 */
public class DecipheredText extends Text {

    public DecipheredText(CipheredText cipheredText, String key) throws IOException {
        textName = "deciphered_" + cipheredText.textName;
        text = decipherText(cipheredText, key);
    }

    public String decipherText(CipheredText cipheredText, String key) throws IOException {
        String text = cipheredText.text;
        char[] arrayOfKey = key.toCharArray();
        char[] lettersOfOT = new char[text.length()]; // here we will write letters of a deciphered text one by one
        for (int i = 0; i < text.length(); i++) {
            lettersOfOT[i] = (char) ((text.charAt(i) - arrayOfKey[i % key.length()]) % 32 + 1072);
        }
        String decipheredText = String.copyValueOf(lettersOfOT).toLowerCase();
        writeText(decipheredText);
        return decipheredText;
    }
}