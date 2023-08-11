import socket
import string
import random
import matplotlib.pyplot as plt
import numpy as np
from emisor import *



def introduce_noise(data, error_rate):
    """Introduce ruido en la data."""
    return ''.join('1' if bit == '0' and random.random() < error_rate else '0' if bit == '1' and random.random() < error_rate else bit for bit in data)

def generate_random_texts(n=1000, min_len=1, max_len=20):
    """Genera textos aleatorios."""
    lengths = np.random.randint(min_len, max_len + 1, size=n)
    return [''.join(random.choice(string.ascii_lowercase) for _ in range(length)) for length in lengths]

def convert_text_to_binary(text):
    """Convierte un texto en su representación binaria."""
    return ''.join(format(ord(c), '08b') for c in text)

def transmit_data(data, port, model_choice, action):
    """Transmite datos a un puerto específico."""
    address = ('localhost', port)
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.connect(address)
        s.sendall(f"{data},{model_choice},{action}".encode())

def get_model_choice():
    """Obtiene la elección del modelo del usuario."""
    while True:
        print("=======================")
        print("Choose the model to use:")
        print("1. CRC-32")
        print("2. Hamming")
        choice = int(input("Enter a choice: "))
        if choice in [1, 2]:
            return choice
        else:
            print("Error, choose a valid option.")

def main_menu():
    """Menu principal."""
    port = 12345
    while True:
        print("=======================")
        print("1. Send message")
        print("2. Run simulation")
        print("3. Exit")
        action = int(input("Enter a choice: "))
        if action == 1:
            data = input("Enter the message to send: ")
            data = convert_text_to_binary(data)
            model_choice = get_model_choice()
            if model_choice == 1:
                data = apply_crc_to_input(data)
            elif model_choice == 2:
                pass  
            data = introduce_noise(data, 0.00)
            transmit_data(data, port, model_choice, action)
        elif action == 2:
            texts = generate_random_texts()
            model_choice = get_model_choice()
            for text in texts:
                print("Text:", text)
                data = convert_text_to_binary(text)
                if model_choice == 1:
                    data = apply_crc_to_input(data)
                elif model_choice == 2:
                    pass  
                data = introduce_noise(data, 0.01)
                transmit_data(data, port, model_choice, action)
        elif action == 3:
            print("Exiting")
            break
        else:
            print("Error, enter again.")

main_menu()
