package ua.ipt;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


/**
 * Created by Roman Horilyi on 12.03.2016.
 * version 1.1
 */
public class Text {

    protected String textName;
    protected String text;


    public Text() {
        textName = "Default_text";
        text = "";
    }

    public Text(String address) throws IOException {
        textName = getTextName(address);
        text = readText(address);
    }

    public String getTextName(String address) {
        Path path = Paths.get(address);
        String textName = path.getFileName().toString();
        return this.textName = textName.substring(0, textName.length() - 4);    // cut off 4 symbols from the end of the
                                                                                // path: [.txt]
    }

    public String readText(String address) throws IOException {
        StringBuilder tmpStringBuilder = new StringBuilder();
        File file = new File(address);

        try (FileInputStream inputStream = new FileInputStream(file);
             InputStreamReader streamReader = new InputStreamReader(inputStream, "Windows-1251");
             BufferedReader bufferedReader = new BufferedReader(streamReader)
        ) {
            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                tmpStringBuilder.append(currentLine);
            }

            text = tmpStringBuilder.toString();

        } catch (IOException e) {
            System.out.println("Error. Can't read the file.");
            e.printStackTrace();
        }

        return text;
    }

    public String filterText(boolean withSpaces) {
        String unfilteredText = text.toLowerCase();
        String filteredText = unfilteredText.replace('ъ', 'ь').replace('ё', 'е');
        if (withSpaces) {
            filteredText = filteredText.replaceAll("[^а-я]+", " ");
        } else {
            filteredText = filteredText.replaceAll("[^а-я]+", "");
        }
        return filteredText;
    }

    public void findFrequenciesAndEntropy(int lengthOfNgram, boolean withSpaces) throws IOException {
        if (lengthOfNgram <= 0) {
            System.out.println("Error. Length of your N-gram has to be 1 or more.");
        } else {
            String text = filterText(withSpaces);
            Map<String, Double> frequencies = new HashMap<>();
            double entropy = 0.0;
            String spaces = (withSpaces) ? " with spaces" : " with no spaces"; // is used in the output of the entropy

            for (int i = 0; i <= text.length() - lengthOfNgram; i++) {
                String substring = text.substring(i, i + lengthOfNgram);
                if (frequencies.containsKey(substring)) {
                    Double temp = frequencies.get(substring);
                    frequencies.put(substring, temp + 1);
                } else {
                    frequencies.put(substring, 1.0);
                }
            }
            for (String ngram : frequencies.keySet()) {
                // Find probability of the exact N-gram (letter, bigram etc.) occurrence in the text dividing its
                // frequency by overall amount of N-gram entries.
                double prob = frequencies.get(ngram) / (text.length() - lengthOfNgram + 1);
                frequencies.put(ngram, prob);
                entropy -= prob * (Math.log(prob) / Math.log(2));
            }
            entropy /= lengthOfNgram;
            writeFrequencies(frequencies, lengthOfNgram, withSpaces);
            System.out.println("Entropy H" + lengthOfNgram + spaces + ":\t\t\t\t\t\t\t\t" + entropy);

            // Also for N > 1 we  find probabilities of nonintersectable N-grams
            if (lengthOfNgram > 1) {
                Map<String, Double> frequenciesOfNonintersectableNgrams = new HashMap<>();
                double entropy2 = 0.0;

                for (int i = 0; i <= text.length() - lengthOfNgram; i += lengthOfNgram) {
                    String substring = text.substring(i, i + lengthOfNgram);
                    if (frequenciesOfNonintersectableNgrams.containsKey(substring)) {
                        Double tmp = frequenciesOfNonintersectableNgrams.get(substring);
                        frequenciesOfNonintersectableNgrams.put(substring, tmp + 1);
                    } else {
                        frequenciesOfNonintersectableNgrams.put(substring, 1.0);
                    }
                }
                for (String ngram : frequenciesOfNonintersectableNgrams.keySet()) {
                    double prob = frequenciesOfNonintersectableNgrams.get(ngram) / (text.length() / lengthOfNgram);
                    frequenciesOfNonintersectableNgrams.put(ngram, prob);
                    entropy2 -= prob * (Math.log(prob) / Math.log(2));
                }

                entropy2 /= lengthOfNgram;
                System.out.println("Entropy H" + lengthOfNgram + spaces + " of nonintersectable " + lengthOfNgram
                        + "-grams" + ":\t" + entropy2);
            }
        }
    }

    public void writeFrequencies(Map<String, Double> frequencies, int lengthOfNgram, boolean withSpaces)
            throws IOException {
        try (FileOutputStream stream = new FileOutputStream("C://IdeaProjects/SymCrypt/Lab1/data/clean/" + textName
                + "_freq" + lengthOfNgram + ((withSpaces) ? "_with_spaces" : "_with_no_spaces") + ".txt");
             OutputStreamWriter file = new OutputStreamWriter(stream)
        ) {
            List<Map.Entry<String,Double>> entryList = new ArrayList<>();
            entryList.addAll(frequencies.entrySet());
            Collections.sort(entryList, (o1, o2) -> (o2.getValue()).compareTo( o1.getValue() ));

            for (Map.Entry<String,Double> entry : entryList) {
                file.write(entry.getKey() + ": " + String.format("%.8f", entry.getValue()) + "\r\n");
            }

        } catch (IOException e){
            System.out.println("Can't write the file");
        }
    }

    // Write text to the file
    public void writeText(String text) throws IOException {
        try (FileOutputStream stream = new FileOutputStream("C://IdeaProjects/SymCrypt/Lab1/data/clean/" + textName
                + ".txt");
             OutputStreamWriter file = new OutputStreamWriter(stream)
        ) {
            file.write(text);

        } catch (IOException e) {
            System.out.println("Can't write the file");
        }
    }

    // findLetterFrequencies() is used only in deciphering of an encoded text with the Vigenere cipher
    public ArrayList<Character> findLetterFrequencies(String text) {
        HashMap<Character, Integer> frequencies = new HashMap<>();
        ArrayList<Character> characters = new ArrayList<>();

        for (int i = 0; i < text.length(); i++) {
            char letter = text.charAt(i);
            if (frequencies.containsKey(letter)) {
                int temp = frequencies.get(letter);
                frequencies.put(letter, temp + 1);
            } else {
                frequencies.put(letter, 1);
            }
        }

        List<Map.Entry<Character, Integer>> entryList = new ArrayList<>();
        entryList.addAll(frequencies.entrySet());
        Collections.sort(entryList, (o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));
        for (Map.Entry<Character, Integer> entry : entryList) {
            characters.add(entry.getKey());
        }

        return characters;
    }
}