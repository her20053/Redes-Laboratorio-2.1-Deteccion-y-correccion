def crc32_decoder(encoded_data):
    POLYNOMIAL = 0x04C11DB7
    data, crc_received = encoded_data[:-32], int(encoded_data[-32:], 2)
    crc = 0xFFFFFFFF

    # Procesa cada bit de la trama
    for bit in data:
        crc ^= int(bit) << 31
        for _ in range(8):
            if crc & 0x80000000:
                crc = (crc << 1) ^ POLYNOMIAL
            else:
                crc <<= 1
        crc &= 0xFFFFFFFF

    # Compara el CRC calculado con el CRC recibido
    if crc ^ 0xFFFFFFFF == crc_received:
        return data, "No se detectaron errores"
    else:
        return data, "Se detectaron errores, la trama se descarta"

# # Ejemplo de trama codificada
# encoded_data_crc_with_error = '11010111101000100000001101110010001101' # Error en el último bit
# decoded_data_crc, message_crc = crc32_decoder(encoded_data_crc_with_error)
# print("Trama decodificada: ", decoded_data_crc)
# print("Mensaje: ", message_crc)
