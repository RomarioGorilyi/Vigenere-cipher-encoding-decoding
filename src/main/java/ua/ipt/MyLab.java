package ua.ipt;

import java.io.IOException;


/**
 * Created by Roman Horilyi on 12.03.2016.
 */
public class MyLab {

    public static void main(String args[]) throws IOException {
        Text myText = new Text("C://IdeaProjects/SymCrypt/Lab1/data/row/Толстой_Лев_Война_и_мир.txt");
        System.out.println("-------------------------------------------------------------------------");
        myText.findFrequenciesAndEntropy(1, true);
        myText.findFrequenciesAndEntropy(1, false);
        System.out.println("-------------------------------------------------------------------------");
        myText.findFrequenciesAndEntropy(2, true);
        myText.findFrequenciesAndEntropy(2, false);
        System.out.println("-------------------------------------------------------------------------");

        Text openText = new Text("C://IdeaProjects/SymCrypt/Lab1/data/row/openText.txt");
        CipheredText cipheredText1 = new CipheredText(openText, "на");                      // r = 2
        CipheredText cipheredText2 = new CipheredText(openText, "они");                     // r = 3
        CipheredText cipheredText3 = new CipheredText(openText, "клас");                    // r = 4
        CipheredText cipheredText4 = new CipheredText(openText, "ручка");                   // r = 5
        CipheredText cipheredText5 = new CipheredText(openText, "катастрофа");              // r = 10
        CipheredText cipheredText6 = new CipheredText(openText, "переопределение");         // r = 15
        CipheredText cipheredText7 = new CipheredText(openText, "валидацияверификация");    // r = 20

        System.out.println("Indexes of correspondence for ciphered texts with the keys of r length:");
        System.out.println("r = 2: " + cipheredText1.findIndexOfCorrespondence());
        System.out.println("r = 3: " + cipheredText2.findIndexOfCorrespondence());
        System.out.println("r = 4: " + cipheredText3.findIndexOfCorrespondence());
        System.out.println("r = 5: " + cipheredText4.findIndexOfCorrespondence());
        System.out.println("r = 10: " + cipheredText5.findIndexOfCorrespondence());
        System.out.println("r = 15: " + cipheredText6.findIndexOfCorrespondence());
        System.out.println("r = 20: " + cipheredText7.findIndexOfCorrespondence());

        System.out.println("-------------------------------------------------------------------------");
        CipheredText textToDecode = new CipheredText("C://IdeaProjects/SymCrypt/Lab1/data/row/variant_4.txt");
        System.out.println("Index of correspondence of ciphered text: " + textToDecode.findIndexOfCorrespondence());

        System.out.println("-------------------------------------------------------------------------");
        System.out.println("\nAlmost exact secret keyword for " + textToDecode.textName + " is: "
                + textToDecode.findKeys());
        DecipheredText decipheredText = new DecipheredText(textToDecode, "громыковедьма");
    }
}