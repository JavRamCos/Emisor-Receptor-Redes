import socket

# Función para aplicar la codificación Hamming74
def apply_hamming74(trama):
    # Validar que la trama tenga 4 bits (para Hamming74)
    if len(trama) != 4 or not all(bit in {'0', '1'} for bit in trama):
        print("La trama debe contener exactamente 4 bits (0 o 1) para Hamming74.")
        exit(1)

    # Calcular bits de paridad
    p1 = (int(trama[0]) + int(trama[1]) + int(trama[3])) % 2
    p2 = (int(trama[0]) + int(trama[2]) + int(trama[3])) % 2
    p4 = (int(trama[1]) + int(trama[2]) + int(trama[3])) % 2

    # Concatenar bits de paridad y trama original para formar la trama codificada
    codificada = f"{trama}{p1}{p2}{p4}"

    return codificada


# Función para aplicar la codificación CRC
def apply_crc(trama):
    # Validar que la trama tenga al menos 8 bits (para CRC-32)
    if len(trama) < 8 or not all(bit in {'0', '1'} for bit in trama):
        print("La trama debe contener al menos 8 bits (0 o 1) para CRC-32.")
        exit(1)

    # Polinomio generador para CRC-32
    polynomial = [1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1]

    # Agregar ceros al final de la trama para el proceso de división
    trama += '0' * (len(polynomial) - 1)

    # Convertir trama a lista de enteros
    trama_int = [int(bit) for bit in trama]

    # Realizar la división de la trama por el polinomio
    for i in range(len(trama) - len(polynomial) + 1):
        if trama_int[i] == 1:
            for j in range(len(polynomial)):
                trama_int[i + j] ^= polynomial[j]

    # Obtener los bits de paridad resultantes
    paridad = ''.join(map(str, trama_int[-len(polynomial) + 1:]))

    # Concatenar bits de paridad y trama original para formar la trama codificada
    codificada = f"{trama[:-len(polynomial) + 1]}{paridad}"

    return codificada


# Datos del receptor
host = 'localhost'
port = 65432

# Solicitar al usuario que ingrese los bits de la trama
original_trama = input("Ingrese los 8 bits de la trama: ")

# Solicitar al usuario que seleccione el algoritmo
algorithm = input("Seleccione el algoritmo (1 para CRC o 2 para Hamming74): ")

# Validar que el algoritmo sea 1 (CRC) o 2 (Hamming74)
if algorithm not in {"1", "2"}:
    print("Algoritmo no válido. Debe ser '1' (CRC) o '2' (Hamming74).")
    exit(1)

# Aplicar la codificación según el algoritmo seleccionado
if algorithm == "1":  # CRC
    codificada = apply_crc(original_trama)
elif algorithm == "2":  # Hamming74
    codificada = apply_hamming74(original_trama)
else:
    print("Algo salió mal en la codificación.")
    exit(1)

# Formatear el mensaje a enviar: trama codificada + ":" + algoritmo + "\r\n"
message = f"{codificada}:{algorithm}\r\n"

# Crear el socket y conectar al receptor
with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.connect((host, port))
    print(f"Connected to {host}:{port}")

    # Enviar el mensaje
    s.sendall(message.encode())
    print(f"Sent: {message}")
