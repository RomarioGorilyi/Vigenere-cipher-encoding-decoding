package ua.ipt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Roman Horilyi on 15.03.2016.
 */
public class CipheredText extends Text {

    public CipheredText(Text plainText, String key) throws IOException {
        textName = "ciphered_" + plainText.textName + "_with_key_" + key;
        text = cipherText(plainText, key);
    }

    public CipheredText(String address) throws IOException {
        super(address);
        textName = "ciphered_Text_" + textName;
    }

    public String cipherText(Text plainText, String key) throws IOException {
        String text = plainText.filterText(false); // false - with no spaces
        char[] arrayOfKey = key.toCharArray();
        char[] lettersOfCT = new char[text.length()]; // here we will write letters of a enciphered text one by one
        for (int i = 0; i < text.length(); i++) {
            lettersOfCT[i] = (char) ((text.charAt(i) + arrayOfKey[i % key.length()]) % 32 + 1072);
        }
        String cipheredText = String.copyValueOf(lettersOfCT);
        writeText(cipheredText);
        return cipheredText;
    }

    public double findIndexOfCorrespondence() {
        double indexOfCorrespondence = 0.0;
        String text = this.text;
        char[] abc = {'а','б','в','г','д','е','ж','з','и','й','к','л','м','н','о','п','р','с','т','у','ф','х','ц','ч',
                'ш','щ','ы','ь','э','ю','я'};
        for (char letterOfAbc : abc) {
            long frequency = 0;
            for (int j = 0; j < text.length(); j++) {
                if (text.charAt(j) == letterOfAbc) {
                    frequency++;
                }
            }
            indexOfCorrespondence += frequency * (frequency - 1);
        }
        indexOfCorrespondence = indexOfCorrespondence / (text.length() * (text.length() - 1));
        return indexOfCorrespondence;
    }

    /**
     * In order to find genuine length r (r > 5) of a key we count statistic values D of letters coincidence.
     * Letters are disposed in the distance of r symbols.
     *
     * For candidates that are equal or multiple of the valid period values D will be much bigger than others.
     **/
    public int findBigLengthOfKey() {
        String text = this.text;
        HashMap<Integer, Integer> map = new HashMap<>();

        System.out.println("Values of Kronecker symbols of r length:");
        for (int length = 6; length < 31; length++) {
            int valueOfLettersMatchStatistics = 0;
            for (int i = 0; i < text.length() - length; i++) {
                if (text.charAt(i) == text.charAt(i + length)) {
                    valueOfLettersMatchStatistics++;
                }
            }
            map.put(length, valueOfLettersMatchStatistics);
            System.out.println(length + " - " + valueOfLettersMatchStatistics);
        }

        Map.Entry<Integer, Integer> maxEntry = null;
        for (Map.Entry<Integer, Integer> entry: map.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                maxEntry = entry;
            }
        }

        assert maxEntry != null;
        System.out.printf("%nLength of cipher keyword is: %d %n", maxEntry.getKey());
        return maxEntry.getKey();
    }

    /**
     * Process of deciphering works with blocks of the text that contain letters on the distance of the period of our
     * key.
     */
    public ArrayList<String> splitText() {
        ArrayList<String> blocksOfText = new ArrayList<>();
        int lengthOfKey = findBigLengthOfKey();

        for (int i = 0; i < lengthOfKey; i++) {
            StringBuilder tmp = new StringBuilder();
            for (int j = 0; (i + j) < this.text.length(); j += lengthOfKey) {
                tmp.append(this.text.charAt(i + j));
            }
            blocksOfText.add(tmp.toString());
        }

        return blocksOfText;
    }

    public String findKeys() {
        ArrayList<String> blocksOfText = splitText();
        StringBuilder key = new StringBuilder();

        // If keyword with the first most frequent letters won't be very precise, use the 2nd, 3rd, etc. most popular
        // letters and choose letters instead of incorrect ones in the first variant
        //
        // IMPORTANT: not forget to change both frequent letters in ciphered text (CT) and opened text (OT)
        for (String blockOfText : blocksOfText) {
            ArrayList<Character> characters = findLetterFrequencies(blockOfText);
            char mostFrequentLetterInCT = characters.get(0); // for CT
            char keyLetter = (char) ((mostFrequentLetterInCT - 'о') % 32 + 1072); // for OT
            key.append(keyLetter);
        }

        return key.toString().toLowerCase();
    }
}