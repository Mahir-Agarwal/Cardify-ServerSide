#!/bin/bash
set -e

BASE_URL="http://localhost:8080/api"

echo "1. Registering Buyer..."
curl -s -X POST "$BASE_URL/auth/register" -H "Content-Type: application/json" \
  -d '{"name":"Buyer User", "email":"buyer@test.com", "password":"password123", "roles":["BUYER"]}'

echo -e "\n\n2. Registering Owner..."
curl -s -X POST "$BASE_URL/auth/register" -H "Content-Type: application/json" \
  -d '{"name":"Owner User", "email":"owner@test.com", "password":"password123", "roles":["OWNER"]}'

echo -e "\n\n3. Login Owner & Get Token..."
OWNER_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" -H "Content-Type: application/json" \
  -d '{"email":"owner@test.com", "password":"password123"}')
OWNER_TOKEN=$(echo $OWNER_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)

echo -e "\n\n4. Owner Adds Card..."
CARD_RESPONSE=$(curl -s -X POST "$BASE_URL/cards" \
  -H "Authorization: Bearer $OWNER_TOKEN" -H "Content-Type: application/json" \
  -d '{"bankName":"HDFC", "cardType":"CREDIT", "available":true}')
echo $CARD_RESPONSE
CARD_ID=$(echo $CARD_RESPONSE | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)

echo -e "\n\n5. Login Buyer & Get Token..."
BUYER_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" -H "Content-Type: application/json" \
  -d '{"email":"buyer@test.com", "password":"password123"}')
BUYER_TOKEN=$(echo $BUYER_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)

echo -e "\n\n6. Buyer Requests Order..."
ORDER_RESPONSE=$(curl -s -X POST "$BASE_URL/orders/request" \
  -H "Authorization: Bearer $BUYER_TOKEN" -H "Content-Type: application/json" \
  -d "{\"cardId\":$CARD_ID, \"amount\":1000.0, \"commission\":50.0}")
echo $ORDER_RESPONSE
ORDER_ID=$(echo $ORDER_RESPONSE | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)

echo -e "\n\n7. Owner Accepts Order..."
curl -s -X PUT "$BASE_URL/orders/$ORDER_ID/accept" \
  -H "Authorization: Bearer $OWNER_TOKEN"

echo -e "\n\n8. Buyer Pays..."
curl -s -X POST "$BASE_URL/orders/$ORDER_ID/pay" \
  -H "Authorization: Bearer $BUYER_TOKEN"

echo -e "\n\n9. Owner Places External Order..."
curl -s -X PUT "$BASE_URL/orders/$ORDER_ID/place?externalOrderId=AMZ123" \
  -H "Authorization: Bearer $OWNER_TOKEN"

echo -e "\n\n10. Buyer Confirms Delivery..."
curl -s -X PUT "$BASE_URL/orders/$ORDER_ID/confirm" \
  -H "Authorization: Bearer $BUYER_TOKEN"

echo -e "\n\n11. Buyer Submits Review..."
curl -s -X POST "$BASE_URL/reviews/$ORDER_ID" \
  -H "Authorization: Bearer $BUYER_TOKEN" -H "Content-Type: application/json" \
  -d '{"rating":5, "feedback":"Great owner!"}'
