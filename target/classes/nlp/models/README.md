# OpenNLP Models

This directory should contain the following OpenNLP model files:

- `en-sent.bin` - English sentence detector model
- `en-token.bin` - English tokenizer model
- `en-pos-maxent.bin` - English POS tagger model
- `en-ner-person.bin` - English person name finder model
- `en-ner-organization.bin` - English organization name finder model
- `en-ner-location.bin` - English location name finder model

## How to Download

You can download these models from the OpenNLP website:

1. Visit [https://opennlp.sourceforge.net/models-1.5/](https://opennlp.sourceforge.net/models-1.5/)
2. Download each of the required model files
3. Place them in this directory

Alternatively, you can use the provided scripts:

- For Linux/Mac: Run `./download-nlp-models.sh` from the project root
- For Windows: Run `powershell -ExecutionPolicy Bypass -File download-nlp-models.ps1` from the project root

## Fallback Mechanism

If the models are not available at runtime, the application will fall back to using pattern-based extraction methods, which are less accurate but don't require pre-trained models. 