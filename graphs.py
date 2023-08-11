import matplotlib.pyplot as plt

# Datos para cada conjunto
pruebas = [1, 2, 3, 4, 5, 'Promedio']
respuestas_correctas1 = [7796.0, 7866.0, 7919.0, 7765.0, 7856.0, 7840.4]
tiempo_ejecucion1 = [6.500, 6.553, 6.552, 6.468, 6.547, 6.524]

respuestas_correctas2 = [10000.0, 10000.0, 10000.0, 10000.0, 10000.0, 10000.0]
tiempo_ejecucion2 = [8.431, 8.575, 8.546, 8.549, 8.524, 8.525]

respuestas_correctas3 = [6202.0, 6245.0, 6312.0, 6249.0, 6275.0, 6256.6]
tiempo_ejecucion3 = [6.413, 6.587, 6.536, 6.571, 6.550, 6.531]

respuestas_correctas4 = [10000.0, 10000.0, 10000.0, 10000.0, 10000.0, 10000.0]
tiempo_ejecucion4 = [8.137, 8.710, 8.821, 8.846, 8.837, 8.670]

respuestas_correctas5 = [3058.0, 2962.0, 3070.0, 3102.0, 2905.0, 3019.4]
tiempo_ejecucion5 = [6.561, 6.481, 6.487, 6.486, 6.522, 6.507]

respuestas_correctas6 = [10000.0, 10000.0, 10000.0, 10000.0, 10000.0, 10000.0]
tiempo_ejecucion6 = [8.769, 8.907, 8.922, 8.879, 8.917, 8.879]

# Crear la gráfica de líneas
plt.figure(figsize=(12, 6))

# Gráfica para respuestas correctas
plt.subplot(1, 2, 1)
plt.plot(pruebas, respuestas_correctas1, marker='o', label='Prueba de 10k mensajes y error de 1/100 -CRC-32-')
plt.plot(pruebas, respuestas_correctas2, marker='o', label='Prueba de 10k mensajes y error de 1/100 -Hamming(7, 4)-')
plt.plot(pruebas, respuestas_correctas3, marker='o', label='Prueba de 10k mensajes y error de 2/100 -CRC-32-')
plt.plot(pruebas, respuestas_correctas4, marker='o', label='Prueba de 10k mensajes y error de 2/100 -Hamming(7, 4)-')
plt.plot(pruebas, respuestas_correctas5, marker='o', label='Prueba de 10k mensajes y error de 5/100 -CRC-32-')
plt.plot(pruebas, respuestas_correctas6, marker='o', label='Prueba de 10k mensajes y error de 5/100 -Hamming(7, 4)-')
plt.title('Respuestas Correctas')
plt.xlabel('Prueba')
plt.ylabel('Respuestas Correctas')
plt.legend()
plt.grid(True)

# Gráfica para tiempo de ejecución
plt.subplot(1, 2, 2)
plt.plot(pruebas, tiempo_ejecucion1, marker='o', label='Prueba de 10k mensajes y error de 1/100 -CRC-32-')
plt.plot(pruebas, tiempo_ejecucion2, marker='o', label='Prueba de 10k mensajes y error de 1/100 -Hamming(7, 4)-')
plt.plot(pruebas, tiempo_ejecucion3, marker='o', label='Prueba de 10k mensajes y error de 2/100 -CRC-32-')
plt.plot(pruebas, tiempo_ejecucion4, marker='o', label='Prueba de 10k mensajes y error de 2/100 -Hamming(7, 4)-')
plt.plot(pruebas, tiempo_ejecucion5, marker='o', label='Prueba de 10k mensajes y error de 5/100 -CRC-32-')
plt.plot(pruebas, tiempo_ejecucion6, marker='o', label='Prueba de 10k mensajes y error de 5/100 -Hamming(7, 4)-')
plt.title('Tiempo de Ejecución (s)')
plt.xlabel('Prueba')
plt.ylabel('Tiempo (s)')
plt.legend()
plt.grid(True)

# Ajustar el espacio entre las subgráficas
plt.tight_layout()

# Mostrar la gráfica
plt.show()
