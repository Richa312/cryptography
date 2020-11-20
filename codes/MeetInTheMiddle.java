/* Demonstrating Meet in the Middle Attack on a DoubleDES system 
** Author: Richa Verma */

import javax.xml.bind.DatatypeConverter;
import java.math.BigInteger;
import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.util.Scanner;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.ArrayList;

public class MeetInTheMiddle {
	public static final String hardcoded_keybits = "00000000";

	public static void attack(String p1, String c1, String p2, String c2) throws Exception{
		
		DoubleDES des = new DoubleDES();
		
		// Calculating some loop limit constants
		int dynamic_keybits_length = 14 - hardcoded_keybits.length();
		String formatStr = "%" + dynamic_keybits_length + "s";
		int possibleKeysCount = (int)Math.pow(2, dynamic_keybits_length * 4);
		System.out.println("Will loop through 2^"+ (dynamic_keybits_length * 4)+ " possibilities.");

		String[][] array1 = new String[possibleKeysCount][3];
		String key;
		byte[] middleCipher; 
		String middleCiphertext1; String middleCiphertext2;

		//Round 1 Encryption for plaintext1:
		byte[] plainText1 = DatatypeConverter.parseHexBinary(p1);
		byte[] plainText2 = DatatypeConverter.parseHexBinary(p2);
		Cipher myDesCipher1 = Cipher.getInstance("DES/ECB/PKCS5Padding"); //to allow for any input size, we use padding
		
		for(int y=0; y<possibleKeysCount;y++){
			key = hardcoded_keybits + String.format(formatStr,Integer.toHexString(y)).replace(' ', '0');
			SecretKey myKey = des.convertKey(key);
			myDesCipher1.init(Cipher.ENCRYPT_MODE, myKey);
			middleCipher = myDesCipher1.doFinal(plainText1);
			middleCiphertext1 = (new String(middleCipher)).trim();
			middleCipher = myDesCipher1.doFinal(plainText2);
			middleCiphertext2 = (new String(middleCipher)).trim();

			array1[y][0] = middleCiphertext1;
			array1[y][1] = middleCiphertext2;
			array1[y][2] = key;
		}
		

		// Round 2 Decryption for ciphertext1:
		System.out.println("\nGenerated middle-ciphers after round-1 encryption. \nComparing with middle-ciphers after round-2 decryption. \nThis will take a while...\n");
		byte[] cipherText1 = DatatypeConverter.parseHexBinary(c1);
		byte[] cipherText2 = DatatypeConverter.parseHexBinary(c2);
		ArrayList<String> computedKeys1 = new ArrayList<String>();
		ArrayList<String> computedKeys2 = new ArrayList<String>();
		Cipher myDesCipher2 = Cipher.getInstance("DES/ECB/NoPadding");

		secondLoop: for (int y = 0; y < possibleKeysCount; y++) {
			key = hardcoded_keybits + String.format(formatStr,Integer.toHexString(y)).replace(' ', '0');
			SecretKey myKey = des.convertKey(key);
			myDesCipher2.init(Cipher.DECRYPT_MODE, myKey);
			middleCipher = myDesCipher2.doFinal(cipherText1);
			middleCiphertext1 = (new String(middleCipher)).trim();
			middleCipher = myDesCipher2.doFinal(cipherText2);
			middleCiphertext2 = (new String(middleCipher)).trim();
			
			int z=0;
			do {
				if ( middleCiphertext1.equals(array1[z][0]) || middleCiphertext2.equals(array1[z][1]) ) {
					computedKeys1.add(array1[z][2]);
					computedKeys2.add(key);
				}
			} while (++z < possibleKeysCount);
			
		}

		//Computed possible keys, verifying if they are the actual keys used...
		for(int y=0; y<computedKeys1.size();y++) {
			SecretKey myKey;

			myKey = des.convertKey(computedKeys1.get(y));
			myDesCipher1.init(Cipher.ENCRYPT_MODE, myKey);
			String encrypted = (new String(myDesCipher1.doFinal(plainText1))).trim();

			myKey = des.convertKey(computedKeys2.get(y));
			myDesCipher2.init(Cipher.DECRYPT_MODE, myKey);
			String decrypted = (new String(myDesCipher2.doFinal(cipherText1))).trim();

			if (encrypted.equals(decrypted)) {
				System.out.println("Key 1: " + computedKeys1.get(y));
				System.out.println("Key 2: " + computedKeys2.get(y));
				break;
			}
		}		
	}

	public static void main(String[] args) throws Exception{
		DoubleDES cipher = new DoubleDES();

		String key1, key2;
		String usr_key1; String usr_key2; 
		String plainText1; String ciphertext1;
		String plainText2; String ciphertext2; 
		int choice;

		ipLoop: do {
			Scanner in = new Scanner(System.in);
			System.out.println("\n\nChoose 1 of the following: ");
			System.out.println("1 > Get Plaintext, Ciphertext pair using 2xDES ");
			System.out.println("2 > Meet in the Middle attack ");
			System.out.println("0 > Exit");

			choice = in.nextInt();
			in.nextLine();

			switch(choice) {
				case 0: 
					break ipLoop;
	
				case 1:
					System.out.print("Enter message (max 8 ASCII characters): ");
					plainText1 = in.nextLine();
					System.out.print("\nEnter last HEX digits for key1 (no spaces, total 14 digits): " + hardcoded_keybits);
					usr_key1 = in.nextLine();
					System.out.print("Enter last HEX digits for key2 (no spaces, total 14 digits): " + hardcoded_keybits);
					usr_key2 = in.nextLine();
					key1 = hardcoded_keybits + usr_key1;
					key2 = hardcoded_keybits + usr_key2;

					String plainTextHex = DatatypeConverter.printHexBinary(plainText1.getBytes());

					String ciphertext = cipher.encrypt(plainTextHex, key1, key2);
					System.out.println("\nPlaintext:: " + plainTextHex);					
					System.out.println("Ciphertext:: " + ciphertext);
					break;

				case 2: 
					System.out.println("Please enter TWO plaintext-ciphertext pairs. Ensure both pairs were encrypted with the same set of keys.");
					System.out.print("\nEnter plainText1 (as hex string): ");
					plainText1 = in.nextLine();
					System.out.print("Enter ciphertext1 (as hex string): ");
					ciphertext1 = in.nextLine();
					System.out.print("\nEnter plainText2 (as hex string): ");
					plainText2 = in.nextLine();
					System.out.print("Enter ciphertext2 (as hex string): ");
					ciphertext2 = in.nextLine();
					System.out.println("\nProcessing. Please hold on... ");

					attack(plainText1, ciphertext1, plainText2, ciphertext2);
					break;
				default:
					System.out.println("Invalid Input!");
					break;
			}
		} while (true);
	
	}

}


class DoubleDES {
	public SecretKey convertKey(String hexKey) throws Exception{		
		// first convert the first cipher's key from hex to binary 
		long longArgKey1 = Long.parseLong(hexKey, 16);
		String binaryArgKey1 = String.format("%56s", Long.toBinaryString(longArgKey1));
		binaryArgKey1 = binaryArgKey1.replace(' ', '0');
		
		// I now add the parity bits (so that every byte in the key has an odd number of "1" bits)
		for (int i = 0; i < 8; i++) {
			int numberOnes = 0;
			for (int x = i*7; x < (i*7)+7; x++) {
				if (binaryArgKey1.charAt(x) == '1') 
					numberOnes++;
			}
			if ((numberOnes % 2) == 0)
				binaryArgKey1 = new StringBuilder(binaryArgKey1).insert(((i+1)*8)-1, '1').toString();
			else
				binaryArgKey1 = new StringBuilder(binaryArgKey1).insert(((i+1)*8)-1, '0').toString();
		} 

		// we now convert back both keys to hex format
		String parityKey1 = new BigInteger(binaryArgKey1, 2).toString(16);

		// convert hex to byte array
		if (parityKey1.length() % 2 == 1) 
			parityKey1 = "0" + parityKey1;
		byte[] byteKey1 = DatatypeConverter.parseHexBinary(parityKey1);
		byte[] encodedKey1 = new byte[8]; 
		for (int i = 0; i < byteKey1.length; i++){
			encodedKey1[i] = byteKey1[i];
		}
		
		//generate secret key in JCE acceptable format using DES SecretKeyFactor
		SecretKeyFactory secretKey1 = SecretKeyFactory.getInstance("DES");
		return (SecretKey) secretKey1.generateSecret(new DESKeySpec(encodedKey1));
	}

	public String encrypt(String msg_str, String key1, String key2) throws Exception {
		SecretKey myKey1 = convertKey(key1);
		SecretKey myKey2 = convertKey(key2);

		byte[] plainText = DatatypeConverter.parseHexBinary(msg_str); 

		//DES round1:
		Cipher myDesCipher1 = Cipher.getInstance("DES/ECB/PKCS5Padding"); 
		myDesCipher1.init(Cipher.ENCRYPT_MODE, myKey1);
		byte[] firstCiphertext = myDesCipher1.doFinal(plainText);

		//DES round2:
		Cipher myDesCipher2 = Cipher.getInstance("DES/ECB/NoPadding");	
		myDesCipher2.init(Cipher.ENCRYPT_MODE, myKey2);
		byte[] secondCipherText = myDesCipher2.doFinal(firstCiphertext);

		return DatatypeConverter.printHexBinary(secondCipherText);
	}

	public String decrypt(String cipher_str, String key1, String key2) throws Exception {
		SecretKey myKey1 = convertKey(key1);
		SecretKey myKey2 = convertKey(key2);

		byte[] cipherText = DatatypeConverter.parseHexBinary(cipher_str);

		//Round 2 decryption:
		Cipher myDesCipher2 = Cipher.getInstance("DES/ECB/NoPadding");	
		myDesCipher2.init(Cipher.DECRYPT_MODE, myKey2);
		byte[] secondCipherText = myDesCipher2.doFinal(cipherText);

		//Round 1 decryption:
		Cipher myDesCipher1 = Cipher.getInstance("DES/ECB/PKCS5Padding"); //to allow for any input size, we use padding
		myDesCipher1.init(Cipher.DECRYPT_MODE, myKey1);
		byte[] plainText = myDesCipher1.doFinal(secondCipherText);

		return DatatypeConverter.printHexBinary(plainText);
	}

}
