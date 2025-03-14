# PowerShell script to download OpenNLP models for the ApplicantAI Resume Optimizer

$MODELS_DIR = "src\main\resources\nlp\models"
$OPENNLP_MODELS_URL = "https://opennlp.sourceforge.net/models-1.5"

# Create models directory if it doesn't exist
if (-not (Test-Path $MODELS_DIR)) {
    New-Item -ItemType Directory -Path $MODELS_DIR -Force
}

# Download sentence detector model
Write-Host "Downloading sentence detector model..."
Invoke-WebRequest -Uri "$OPENNLP_MODELS_URL/en-sent.bin" -OutFile "$MODELS_DIR\en-sent.bin"

# Download tokenizer model
Write-Host "Downloading tokenizer model..."
Invoke-WebRequest -Uri "$OPENNLP_MODELS_URL/en-token.bin" -OutFile "$MODELS_DIR\en-token.bin"

# Download POS tagger model
Write-Host "Downloading POS tagger model..."
Invoke-WebRequest -Uri "$OPENNLP_MODELS_URL/en-pos-maxent.bin" -OutFile "$MODELS_DIR\en-pos-maxent.bin"

# Download name finder models
Write-Host "Downloading name finder models..."
Invoke-WebRequest -Uri "$OPENNLP_MODELS_URL/en-ner-person.bin" -OutFile "$MODELS_DIR\en-ner-person.bin"
Invoke-WebRequest -Uri "$OPENNLP_MODELS_URL/en-ner-organization.bin" -OutFile "$MODELS_DIR\en-ner-organization.bin"
Invoke-WebRequest -Uri "$OPENNLP_MODELS_URL/en-ner-location.bin" -OutFile "$MODELS_DIR\en-ner-location.bin"

Write-Host "All models downloaded successfully!" 