#!/bin/bash

# Change to cli directory
cd ..

# File path
protected_file="files/protected.json"

echo "Protect command"
# Step 4: Protect command
./blingbank protect files/input.json $protected_file files/aes.key files/priv.key

echo "Modifying the protected file..."

# Extracting the balance value
balance=$(cat "$protected_file" | grep -oP '(?<="balance": ")[^"]*')

# Inserting "401" in the middle of the balance value
length=${#balance}
middle=$((length / 2))
modified_balance="${balance:0:$middle}401${balance:$middle}"

# Updating the JSON data with the modified balance value
json_data=$(cat "$protected_file" | sed "s|\"balance\": \"$balance\"|\"balance\": \"$modified_balance\"|")
echo "$json_data" > "$protected_file"

echo "Checking if the file is still protected..."
# Check if the file is still protected
./blingbank check "$protected_file" files/aes.key files/pub.key

# Attempt to unprotect the file
echo "Attempting to unprotect the modified file"
./blingbank unprotect "$protected_file" files/output_attacked.json files/aes.key files/pub.key
