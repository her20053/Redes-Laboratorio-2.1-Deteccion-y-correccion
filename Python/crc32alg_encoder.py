def crc32_encoder(data):
    POLYNOMIAL = 0x04C11DB7
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

    # Devuelve la trama original concatenada con el CRC-32 en formato binario
    return data + format(crc ^ 0xFFFFFFFF, '032b')

# # Ejemplo de trama en binario
# data_crc = "110101"
# encoded_data_crc = crc32_encoder(data_crc)
# print("Trama codificada: ", encoded_data_crc)
