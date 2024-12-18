package com.burpcrack;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class App {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("java -javaagent:burp_crack_1337.jar -jar (pwd_do_burp)");    


        System.out.println("1337 Burp Crack - Console Interface");

        System.out.println("Informe o caminho do arquivo Burp Suite JAR (--path):");
        String jarPath = scanner.nextLine();


        System.out.println("Você já ativou manualmente? (sim/não):");
        String activated = scanner.nextLine().trim().toLowerCase();

        if (activated.equals("sim")) {
            LoaderAgent1337.runWithAgent(jarPath);
        } else {
            System.out.println("Informe o nome do usuário para gerar a licença:");
            String userName = scanner.nextLine();

            String licenseKey = Keygen1337.generateLicense(userName);
            System.out.println("Licença Gerada: " + licenseKey);

            System.out.println("Informe o request de ativação manual:");
            String activationRequest = scanner.nextLine();

            String activationResponse = Keygen1337.generateActivationResponse(activationRequest);
            System.out.println("Resposta de Ativação Gerada: " + activationResponse);

            System.out.println("Utilize esta resposta para ativar manualmente o Burp Suite.");
            System.out.println("Pressione Enter para iniciar o Burp Suite...");
            scanner.nextLine();

            LoaderAgent1337.runWithAgent(jarPath);
        }

        scanner.close();
    }
}

class Keygen1337 {
    private static final byte[] ENCRYPTION_KEY = "burpr0x!".getBytes();

    public static String generateLicense(String userName) {
        List<String> licenseArray = new ArrayList<>();
        licenseArray.add(getRandomString());
        licenseArray.add("license");
        licenseArray.add(userName);
        licenseArray.add("4102415999000");
        licenseArray.add("1");
        licenseArray.add("full");
        licenseArray.add(" ignore SHA256withRSA");
        licenseArray.add(" ignore SHA1withRSA");
        return prepareArray(licenseArray);
    }

    public static String generateActivationResponse(String activationRequest) {
        List<String> decodedRequest = decodeActivationRequest(activationRequest);
        if (decodedRequest == null) {
            return "Erro ao decodificar o request.";
        }
        List<String> responseArray = new ArrayList<>();
        responseArray.add("0.4315672535134567");
        responseArray.add(decodedRequest.get(0));
        responseArray.add("activation");
        responseArray.add(decodedRequest.get(1));
        responseArray.add("True");
        responseArray.add("");
        responseArray.add(decodedRequest.get(2));
        responseArray.add(decodedRequest.get(3));
        responseArray.add(" ignore SHA256withRSA");
        responseArray.add(" ignore SHA1withRSA");
        return prepareArray(responseArray);
    }

    private static List<String> decodeActivationRequest(String activationRequest) {
        try {
            byte[] decodedBytes = decrypt(Base64.getDecoder().decode(activationRequest));
            List<String> result = new ArrayList<>();
            int from = 0;
            for (int i = 0; i < decodedBytes.length; i++) {
                if (decodedBytes[i] == 0) {
                    result.add(new String(decodedBytes, from, i - from));
                    from = i + 1;
                }
            }
            result.add(new String(decodedBytes, from, decodedBytes.length - from));
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getRandomString() {
        StringBuilder str = new StringBuilder();
        Random rnd = new Random();
        while (str.length() < 32) {
            int index = rnd.nextInt("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".length());
            str.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".charAt(index));
        }
        return str.toString();
    }

    private static String prepareArray(List<String> list) {
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < list.size() - 1; i++) {
                sb.append(list.get(i)).append('\0');
            }
            sb.append(list.get(list.size() - 1));
            byte[] encrypted = encrypt(sb.toString().getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static byte[] encrypt(byte[] input) {
        try {
            SecretKeySpec key = new SecretKeySpec(ENCRYPTION_KEY, "DES");
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(input);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static byte[] decrypt(byte[] input) {
        try {
            SecretKeySpec key = new SecretKeySpec(ENCRYPTION_KEY, "DES");
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(input);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}

class LoaderAgent1337 {

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("Loader iniciado, interceptando verificações...");
        inst.addTransformer(new SimpleTransformer());
    }

    public static void runWithAgent(String jarPath) {
        try {
            String command = String.format("java -javaagent:%s -jar %s", jarPath, jarPath);
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class SimpleTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (className != null && className.contains("license")) {
            System.out.println("Interceptando classe: " + className);
        }
        return classfileBuffer;
    }
}

