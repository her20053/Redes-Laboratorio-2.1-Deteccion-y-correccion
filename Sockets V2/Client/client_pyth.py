import socket
import zlib
import random
import string
import time
import base64
import os


HOST = "127.0.0.1"
PORT = 6000

def preguntar_codificacion():
    print("\n¿Cómo deseas codificar tu mensaje?")
    print("1. Hamming")
    print("2. CRC-32")
    print("3. Sin codificación")
    
    opcion = input("Opción: ")
    return opcion

def codificar_hamming(mensaje):
    # Para simplificar, usaremos paridad de un bit
    # Si hay un número impar de '1's, agregamos un '1' al final, de lo contrario un '0'
    paridad = '1' if mensaje.count('1') % 2 else '0'
    return mensaje + paridad

def codificar_crc32_binario(mensaje):
    crc_value = zlib.crc32(mensaje.encode())
    crc_binario = format(crc_value, '032b')  # Convertir a binario de 32 bits
    return mensaje + crc_binario  # Concatenamos el CRC-32 al final del mensaje

def agregar_ruido(mensaje_binario, umbral=0.05):
    mensaje_ruidoso = []
    for bit in mensaje_binario:
        if random.random() < umbral:
            mensaje_ruidoso.append('1' if bit == '0' else '0')
        else:
            mensaje_ruidoso.append(bit)
    return ''.join(mensaje_ruidoso)

def string_a_binario(mensaje):
    return ''.join(format(ord(i), '08b') for i in mensaje)

def generar_cadena_aleatoria(longitud):
    """Genera una cadena aleatoria de la longitud dada."""
    caracteres = string.ascii_letters
    return ''.join(random.choice(caracteres) for i in range(longitud))

def enviar_mensaje():
    codificacion = preguntar_codificacion()
    payload = input("Introduce el mensaje a enviar: ")
    payload_binario = string_a_binario(payload)

    # Agregamos ruido al mensaje binario
    payload_binario = agregar_ruido(payload_binario)
    
    prefijo = ""
    if codificacion == "1":
        payload_binario = codificar_hamming(payload_binario)
        prefijo = "HAM:"
    elif codificacion == "2":
        payload_binario = codificar_crc32_binario(payload_binario)
        prefijo = "CRC:"
    elif codificacion == "3":
        pass
    else:
        print("Opción no válida. Enviando mensaje sin codificar.")
    
    mensaje_final = prefijo + payload_binario
    
    # Creamos un socket
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        # Conectamos al servidor
        s.connect((HOST, PORT))
        print(" + Enviando Data\n")
        mensaje_original_encoded = base64.b64encode(payload.encode()).decode()
        mensaje_a_enviar = mensaje_final + "|" + mensaje_original_encoded
        s.sendall(mensaje_a_enviar.encode())
        print(" + Data enviada\n")

def simular_mensajes():

    codificacion = preguntar_codificacion()
    
    # Preguntar al usuario cuántas letras quiere en la cadena aleatoria
    longitud_mensaje = int(input("Introduce la cantidad de letras para el mensaje aleatorio: "))
    cantidad_iteraci = int(input("Introduce la cantidad de iteraciones: "))
    porcentaje_error = float(input("Introduce el porcentaje de error: "))

    archivo_a_eliminar = "../simulacion.txt"
    if os.path.exists(archivo_a_eliminar):
        os.remove(archivo_a_eliminar)

    for i in range(cantidad_iteraci):

        print("Enviando mensaje", i+1)

        # Generar una cadena aleatoria con esa cantidad de letras
        payload = generar_cadena_aleatoria(longitud_mensaje)
        print("Mensaje aleatorio generado:", payload)
        
        payload_binario = string_a_binario(payload)
        payload_binario = agregar_ruido(payload_binario, umbral=porcentaje_error)
        
        prefijo = ""
        if codificacion == "1":
            payload_binario = codificar_hamming(payload_binario)
            prefijo = "HAM:"
        elif codificacion == "2":
            payload_binario = codificar_crc32_binario(payload_binario)
            prefijo = "CRC:"
        elif codificacion == "3":
            pass
        else:
            print("Opción no válida. Enviando mensaje sin codificar.")
        
        mensaje_final = prefijo + payload_binario
        
        # Creamos un socket
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            # Conectamos al servidor
            s.connect((HOST, PORT))
            print(" + Enviando Data\n")
            # Enviamos el mensaje

            mensaje_original_encoded = base64.b64encode(payload.encode()).decode()
            mensaje_a_enviar = mensaje_final + "|" + mensaje_original_encoded
            s.sendall(mensaje_a_enviar.encode())
            print(" + Data enviada\n")
        
        # Pausa de 0.2 segundos
        time.sleep(0.05)


def mostrar_menu():
    print("\nSeleccione una opción:")
    print("1. Enviar mensajes")
    print("2. Simular mensajes")
    print("3. Salir")

    opcion = input("Opción: ")

    return opcion

if __name__ == "__main__":
    while True:
        opcion = mostrar_menu()
        
        if opcion == "1":
            enviar_mensaje()
        elif opcion == "2":
            simular_mensajes()
        elif opcion == "3":
            print("Saliendo...")
            break
        else:
            print("Opción no válida. Intente nuevamente.")
