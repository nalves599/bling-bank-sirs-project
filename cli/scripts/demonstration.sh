#!/bin/bash

echo "Change to project root directory"
# Change to project root directory
cd ../..

echo "Step 1: Install the library"
# Step 1: Install the library
cd library
npm i &> /dev/null
npm run build &> /dev/null
npm pack &> /dev/null

echo "Step 2: Install CLI dependencies"
# Step 2: Install CLI dependencies
cd ../cli
npm i &> /dev/null
npm run build &> /dev/null

echo "Step 3: Help command"
# Step 3: Help command
./blingbank help

echo "Step 4: Protect command"
# Step 4: Protect command
./blingbank protect files/input.json files/protected.json files/aes.key files/priv.key

echo "Step 5: Check command"
# Step 5: Check command
./blingbank check files/protected.json files/aes.key files/pub.key

echo "Step 6: Unprotect command"
# Step 6: Unprotect command
./blingbank unprotect files/protected.json files/output.json files/aes.key files/pub.key

echo "Step 7: Verify output"
# Step 7: Verify output
if diff -q files/input.json files/output.json > /dev/null; then
  echo "Files are the same"
else
  echo "Files are different"
fi
