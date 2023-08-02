def hamming_encoder(data):
    # Encuentra el número de bits de paridad necesarios
    m = len(data)
    r = 0
    while (m + r + 1) > 2 ** r:
        r += 1
    
    # Crea una lista para almacenar la trama codificada
    encoded_data = [0] * (m + r)
    
    # Coloca los bits de datos en las posiciones que no son potencias de 2
    j = 0
    for i in range(1, m + r + 1):
        if (i & (i - 1)) != 0:  # No es una potencia de 2
            encoded_data[i - 1] = int(data[j])
            j += 1
    
    # Calcula los bits de paridad
    for i in range(r):
        parity_bit_pos = 2 ** i
        parity_bit = 0
        for j in range(parity_bit_pos, m + r + 1, 2 * parity_bit_pos):
            sub_data = encoded_data[j - 1:j + parity_bit_pos - 1]
            parity_bit ^= sum(sub_data) % 2  # Paridad par
        encoded_data[parity_bit_pos - 1] = parity_bit
    
    return ''.join(map(str, encoded_data))

# # Ejemplo de trama en binario
# data = "110101"
# encoded_data = hamming_encoder(data)
# print("Trama codificada: ", encoded_data)
