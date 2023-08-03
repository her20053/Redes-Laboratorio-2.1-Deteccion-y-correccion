import hamming_encoder  as he 
import hamming_decoder  as hd
import crc32alg_encoder as ce
import crc32alg_decoder as cd

def main():
    # Lista de tramas de prueba
    test_frames = ["110101", "100110", "111000"]

    print("\n\n\nPruebas para el Código de Hamming:")
    for frame in test_frames:
        print(f"Trama original: {frame}")
        encoded_frame_hamming = he.hamming_encoder(frame)
        print(f"Trama codificada (Emisor): {encoded_frame_hamming}")
        decoded_frame_hamming, message_hamming = hd.hamming_decoder(encoded_frame_hamming)
        print(f"Trama decodificada (Receptor): {decoded_frame_hamming}")
        print(f"Mensaje: {message_hamming}\n")

    print("Pruebas para CRC-32:")
    for frame in test_frames:
        print(f"Trama original: {frame}")
        encoded_frame_crc32 = ce.crc32_encoder(frame)
        print(f"Trama codificada (Emisor): {encoded_frame_crc32}")
        decoded_frame_crc32, message_crc32 = cd.crc32_decoder(encoded_frame_crc32)
        print(f"Trama decodificada (Receptor): {decoded_frame_crc32}")
        print(f"Mensaje: {message_crc32}\n")

def main_with_errors():
    # Lista de tramas de prueba con errores
    test_frames_hamming = [
        ("1110101101", "Trama con error en la posición 7"),
        ("1011001111", "Trama con error en la posición 10"),
        ("0010110010", "Trama con errores en las posiciones 7 y 10")
    ]

    test_frames_crc32 = [
        ("11010111101000100000001101110010001101", "Trama con error en el último bit"),
        ("10011000001110100101001110001001000111", "Trama con error en el último bit"),
        ("11100011001011001111000100001011111110", "Trama con error en el último bit")
    ]

    print("\n\n\nPruebas con errores para el Código de Hamming:")
    for frame, description in test_frames_hamming:
        print(description)
        decoded_frame_hamming, message_hamming = hd.hamming_decoder(frame)
        print(f"Trama decodificada (Receptor): {decoded_frame_hamming}")
        print(f"Mensaje: {message_hamming}\n")

    print("Pruebas con errores para CRC-32:")
    for frame, description in test_frames_crc32:
        print(description)
        decoded_frame_crc32, message_crc32 = cd.crc32_decoder(frame)
        print(f"Trama decodificada (Receptor): {decoded_frame_crc32}")
        print(f"Mensaje: {message_crc32}\n")

# Ejecución de la función principal con errores
main_with_errors()

# Ejecución de la función principal
main()
