#!/bin/bash

# Script to download OpenNLP models for the ApplicantAI Resume Optimizer

MODELS_DIR="src/main/resources/nlp/models"
OPENNLP_MODELS_URL="https://opennlp.sourceforge.net/models-1.5"

# Create models directory if it doesn't exist
mkdir -p $MODELS_DIR

# Download sentence detector model
echo "Downloading sentence detector model..."
curl -o $MODELS_DIR/en-sent.bin $OPENNLP_MODELS_URL/en-sent.bin

# Download tokenizer model
echo "Downloading tokenizer model..."
curl -o $MODELS_DIR/en-token.bin $OPENNLP_MODELS_URL/en-token.bin

# Download POS tagger model
echo "Downloading POS tagger model..."
curl -o $MODELS_DIR/en-pos-maxent.bin $OPENNLP_MODELS_URL/en-pos-maxent.bin

# Download name finder models
echo "Downloading name finder models..."
curl -o $MODELS_DIR/en-ner-person.bin $OPENNLP_MODELS_URL/en-ner-person.bin
curl -o $MODELS_DIR/en-ner-organization.bin $OPENNLP_MODELS_URL/en-ner-organization.bin
curl -o $MODELS_DIR/en-ner-location.bin $OPENNLP_MODELS_URL/en-ner-location.bin

echo "All models downloaded successfully!" 