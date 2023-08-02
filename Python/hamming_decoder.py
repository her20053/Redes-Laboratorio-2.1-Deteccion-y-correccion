def hamming_decoder(encoded_data):
    # Encuentra el número de bits de paridad
    n = len(encoded_data)
    r = 0
    while 2 ** r < (n + 1):
        r += 1

    # Verifica los bits de paridad
    error_pos = 0
    for i in range(r):
        parity_bit_pos = 2 ** i
        parity_bit = 0
        for j in range(parity_bit_pos, n + 1, 2 * parity_bit_pos):
            sub_data = [int(encoded_data[k]) for k in range(j - 1, min(j + parity_bit_pos - 1, n))]
            parity_bit ^= sum(sub_data) % 2  # Paridad par
        if parity_bit != 0:
            error_pos += parity_bit_pos

    # Corrige el error si se encuentra
    if error_pos > 0:
        encoded_data_list = list(encoded_data)
        encoded_data_list[error_pos - 1] = '0' if encoded_data_list[error_pos - 1] == '1' else '1'
        encoded_data = ''.join(encoded_data_list)

    # Extrae la trama original
    decoded_data = ''
    for i in range(n):
        if (i + 1) & (i) == 0:  # Es una potencia de 2
            continue
        decoded_data += encoded_data[i]

    if error_pos > 0:
        return decoded_data, f"Se detectaron y corrigieron errores en la posición {error_pos}. Trama corregida: {encoded_data}"
    else:
        return decoded_data, "No se detectaron errores"

# # Ejemplo de trama codificada con un error en la posición 7
# encoded_data_with_error = "1110100101" # Error en la posición 7
# decoded_data, message = hamming_decoder(encoded_data_with_error)
# print("Trama decodificada: ", decoded_data)
# print("Mensaje: ", message)
