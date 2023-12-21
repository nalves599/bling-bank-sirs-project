#!/bin/bash

# Change to cli directory
cd ..

# File path
protected_file="files/protected.json"

echo "Protect command"
# Step 4: Protect command
./blingbank protect files/input.json $protected_file files/aes.key files/attacker.key

echo "Verifying the protected file..."
./blingbank check "$protected_file" files/aes.key files/pub.key

# Attempt to unprotect the file
echo "Attempting to unprotect the modified file"
./blingbank unprotect "$protected_file" files/output_attacked.json files/aes.key files/pub.key

