public class Main {
    public static void main(String[] args) {
        String[] testFrames = { "110101", "100110", "111000" };

        System.out.println();
        System.out.println();
        System.out.println("Pruebas para el Código de Hamming:");
        for (String frame : testFrames) {
            System.out.println("Trama original: " + frame);
            String encodedFrameHamming = hammingEncoder(frame);
            System.out.println("Trama codificada (Emisor): " + encodedFrameHamming);
            String[] decodedFrameHamming = hammingDecoder(encodedFrameHamming);
            System.out.println("Trama decodificada (Receptor): " + decodedFrameHamming[0]);
            System.out.println("Mensaje: " + decodedFrameHamming[1] + "\n");
        }

        System.out.println("Pruebas para CRC-32:");
        for (String frame : testFrames) {
            System.out.println("Trama original: " + frame);
            String encodedFrameCRC32 = crc32Encoder(frame);
            System.out.println("Trama codificada (Emisor): " + encodedFrameCRC32);
            String[] decodedFrameCRC32 = crc32Decoder(encodedFrameCRC32);
            System.out.println("Trama decodificada (Receptor): " + decodedFrameCRC32[0]);
            System.out.println("Mensaje: " + decodedFrameCRC32[1] + "\n");
        }

        // Pruebas con errores
        String[] errorFramesHamming = { "1110100101", "1011001111", "0010110010" };
        String[] errorFramesCRC32 = {
                "11010111101000100000001101110010001101",
                "10011000001110100101001110001001000111",
                "11100011001011001111000100001011111110"
        };

        System.out.println("Pruebas con errores para el Código de Hamming:");
        for (String frame : errorFramesHamming) {
            String[] decodedFrameHamming = hammingDecoder(frame);
            System.out.println("Trama con error: " + frame);
            System.out.println("Trama decodificada (Receptor): " + decodedFrameHamming[0]);
            System.out.println("Mensaje: " + decodedFrameHamming[1] + "\n");
        }

        System.out.println("Pruebas con errores para CRC-32:");
        for (String frame : errorFramesCRC32) {
            String[] decodedFrameCRC32 = crc32Decoder(frame);
            System.out.println("Trama con error: " + frame);
            System.out.println("Trama decodificada (Receptor): " + decodedFrameCRC32[0]);
            System.out.println("Mensaje: " + decodedFrameCRC32[1] + "\n");
        }
    }

    public static String hammingEncoder(String data) {
        int m = data.length();
        int r = 0;
        while ((m + r + 1) > Math.pow(2, r)) {
            r++;
        }

        char[] encodedData = new char[m + r];
        for (int i = 0; i < r; i++) {
            encodedData[(int) Math.pow(2, i) - 1] = '.';
        }

        int j = 0;
        for (int i = 0; i < m + r; i++) {
            if (encodedData[i] != '.') {
                encodedData[i] = data.charAt(j);
                j++;
            }
        }

        for (int i = 0; i < r; i++) {
            int parityBitPos = (int) Math.pow(2, i);
            int parityBit = 0;
            for (int k = parityBitPos; k <= m + r; k += 2 * parityBitPos) {
                for (int l = k - 1; l < Math.min(k + parityBitPos - 1, m + r); l++) {
                    if (encodedData[l] != '.') {
                        parityBit ^= Character.getNumericValue(encodedData[l]);
                    }
                }
            }
            encodedData[parityBitPos - 1] = (char) (parityBit + '0');
        }

        return new String(encodedData);
    }

    public static String[] hammingDecoder(String encodedData) {
        int n = encodedData.length();
        int r = 0;
        while (Math.pow(2, r) < (n + 1)) {
            r++;
        }

        int errorPos = 0;
        for (int i = 0; i < r; i++) {
            int parityBitPos = (int) Math.pow(2, i);
            int parityBit = 0;
            for (int j = parityBitPos; j <= n; j += 2 * parityBitPos) {
                for (int k = j - 1; k < Math.min(j + parityBitPos - 1, n); k++) {
                    parityBit ^= Character.getNumericValue(encodedData.charAt(k));
                }
            }
            if (parityBit != 0) {
                errorPos += parityBitPos;
            }
        }

        if (errorPos > 0) {
            encodedData = encodedData.substring(0, errorPos - 1) +
                    (encodedData.charAt(errorPos - 1) == '1' ? '0' : '1') +
                    encodedData.substring(errorPos);
        }

        StringBuilder decodedData = new StringBuilder();
        for (int i = 0; i < n; i++) {
            if (((i + 1) & i) == 0) {
                continue;
            }
            decodedData.append(encodedData.charAt(i));
        }

        String message = errorPos > 0
                ? "Se detectaron y corrigieron errores en la posición " + errorPos + ". Trama corregida: " + encodedData
                : "No se detectaron errores";

        return new String[] { decodedData.toString(), message };
    }

    public static String crc32Encoder(String data) {
        int POLYNOMIAL = 0x04C11DB7;
        int crc = 0xFFFFFFFF;

        for (char bit : data.toCharArray()) {
            crc ^= Character.getNumericValue(bit) << 31;
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x80000000) != 0) {
                    crc = (crc << 1) ^ POLYNOMIAL;
                } else {
                    crc <<= 1;
                }
            }
            crc &= 0xFFFFFFFF;
        }

        return data + String.format("%32s", Integer.toBinaryString(crc ^ 0xFFFFFFFF)).replace(' ', '0');
    }

    public static String[] crc32Decoder(String encodedData) {
        int POLYNOMIAL = 0x04C11DB7;
        String data = encodedData.substring(0, encodedData.length() - 32);
        int crcReceived = Integer.parseUnsignedInt(encodedData.substring(encodedData.length() - 32), 2);
        int crc = 0xFFFFFFFF;

        for (char bit : data.toCharArray()) {
            crc ^= Character.getNumericValue(bit) << 31;
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x80000000) != 0) {
                    crc = (crc << 1) ^ POLYNOMIAL;
                } else {
                    crc <<= 1;
                }
            }
            crc &= 0xFFFFFFFF;
        }

        String message = (crc ^ 0xFFFFFFFF) == crcReceived ? "No se detectaron errores"
                : "Se detectaron errores, la trama se descarta";

        return new String[] { data, message };
    }

}
