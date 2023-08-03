# (m + p + 1 ) <= 2^p

def calculate_parity_bits(input_bits):
    m = len(input_bits)
    p = 0

    # Calculate the number of parity bits required
    while (m + p + 1) >= (2 ** p):
        p += 1

    # Add placeholder values for the parity bits
    hamming_code = [0] * (m + p)

    # Fill in the data bits in their correct positions
    j = 0
    for i in range(m + p):
        if i == (2 ** j - 1):
            # Skip the positions that are powers of 2 (parity bit positions)
            j += 1
        else:
            # Fill in the data bits
            hamming_code[i] = int(input_bits.pop(0))

    # Compute the parity bits
    for i in range(p):
        # pow 2
        parity_position = 2 ** i - 1
        num_ones = sum(hamming_code[j] for j in range(m + p) if (j >> i) & 1)
        # assign 1 if even and 0 if odd
        hamming_code[parity_position] = 0 if num_ones % 2 == 0 else 1

    return hamming_code

def main():
    input_bits = input("Ingrese los bits de datos: ")
    input_bits = list(input_bits)
    hamming_code = calculate_parity_bits(input_bits)

    print("\nBits de paridad:")
    m = len(hamming_code)
    p = 0

    # Calculate the number of parity bits required
    while (2 ** p) < m:
        p += 1

    for i in range(p - 1, -1, -1):
        print(f"p{i}: {hamming_code[2 ** i - 1]}")

if __name__ == "__main__":
    main()
