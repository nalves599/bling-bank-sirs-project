#!/bin/bash

# Change to cli directory
cd ..

# File path
protected_file="files/protected.json"

echo "Modifying the protected file..."
# Add 'a' to the beginning of the "balance" field's value
jq '.account.balance |= "a" + .' "$protected_file" > "${protected_file}.temp" && mv "${protected_file}.temp" "$protected_file"

echo "Checking if the file is still protected..."
# Check if the file is still protected
./blingbank check "$protected_file" files/aes.key files/pub.key

# Attempt to unprotect the file
echo "Attempting to unprotect the modified file"
./blingbank unprotect "$protected_file" files/output_attacked.json files/aes.key files/pub.key
