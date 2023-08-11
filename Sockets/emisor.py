# Refactorización de crc_emisor.py

def xor_operation(bits_a, bits_b):
    """Realiza la operación XOR entre dos listas de bits."""
    return [a ^ b for a, b in zip(bits_a, bits_b)]

def compute_crc(data_bits, crc_pattern):
    """Calcula el CRC para una lista de bits dada."""
    data = data_bits.copy()
    current_bits = data[:len(crc_pattern)]
    while len(data) > len(crc_pattern):
        if current_bits[0] == 1:
            current_bits = xor_operation(current_bits, crc_pattern)
        current_bits.pop(0)
        current_bits.append(data.pop(0))
    if current_bits[0] == 1:
        current_bits = xor_operation(current_bits, crc_pattern)
    return current_bits[1:]

def add_crc_to_data(data, crc_pattern):
    """Agrega el CRC a la data."""
    data_bits = [int(bit) for bit in data]
    # Añadir ceros al final para el cálculo CRC
    data_bits += [0] * (len(crc_pattern) - 1)
    crc_result = compute_crc(data_bits, crc_pattern)
    data_with_crc = data_bits[:-(len(crc_pattern)-1)] + crc_result
    return ''.join(str(bit) for bit in data_with_crc)

# Definir los patrones de CRC
CRC_32_PATTERN = [1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 0, 1]
CRC_3_PATTERN = [1, 0, 0, 1]

def apply_crc_to_input(data):
    """Función principal para aplicar CRC-32 a una entrada."""
    return add_crc_to_data(data, CRC_32_PATTERN)
