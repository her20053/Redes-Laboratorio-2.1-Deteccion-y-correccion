import socket

HOST = "127.0.0.1"
PORT = 6000

def descodificar_hamming(mensaje_binario):
    return mensaje_binario[:-1]

def descodificar_crc32(mensaje_binario):
    return mensaje_binario[:-32]

def binario_a_string(mensaje_binario):
    return ''.join(chr(int(mensaje_binario[i:i+8], 2)) for i in range(0, len(mensaje_binario), 8))

def procesar_mensaje(mensaje):
    prefijo = mensaje[:4]  # Tomamos los primeros 4 caracteres como prefijo
    contenido = mensaje[4:]

    mensaje_descodificado = ""
    if prefijo == "HAM:":
        print(" + Mensaje recibido con codificación Hamming")
        mensaje_descodificado = descodificar_hamming(contenido)
    elif prefijo == "CRC:":
        print(" + Mensaje recibido con codificación CRC-32")
        mensaje_descodificado = descodificar_crc32(contenido)
    else:
        print(" + Mensaje recibido sin codificación")
        mensaje_descodificado = contenido

    texto_descodificado = binario_a_string(mensaje_descodificado)

    print(f" + Contenido (binario): {mensaje_descodificado}")
    print(f" + Contenido (texto): {texto_descodificado}")

    # Escribir en el archivo
    with open("../simulacion.txt", "a") as file:
        file.write(texto_descodificado + "\n")

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    
    s.bind((HOST, PORT))
    
    s.listen()
    
    print(f" + Esperando conexiones en {HOST}:{PORT}")
    
    while True:  # Bucle infinito para escuchar conexiones
        conn, addr = s.accept()

        with conn:
            print(f" + Conectado a {addr}")
            
            while True:  # Bucle infinito para escuchar mensajes
                data = conn.recv(1024)
                
                if not data:
                    break   
                
                procesar_mensaje(data.decode())

