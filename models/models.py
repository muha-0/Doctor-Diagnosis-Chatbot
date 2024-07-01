import tensorflow as tf
from tensorflow.keras.preprocessing.text import Tokenizer
from tensorflow.keras.preprocessing.sequence import pad_sequences
from tensorflow.keras.models import load_model
from tensorflow.keras.preprocessing.text import tokenizer_from_json
import nltk
from nltk.corpus import stopwords
import re
import numpy as np
from fastapi import FastAPI
import os
from pydantic import BaseModel
#import uvicorn


models = FastAPI()

# Determine the base directory
BASE_DIR = os.path.dirname(os.path.abspath(__file__))

# Load the model using an absolute path
model_path = os.path.join(BASE_DIR, 'lstm_acc0.777.h5')
try:
    model = tf.keras.models.load_model(model_path)
except Exception as e:
    raise RuntimeError(f"Error loading model: {e}")


# Load the tokenizer using an absolute path
tokenizer_path = os.path.join(BASE_DIR, 'tokenizer.json')
try:
    with open(tokenizer_path) as json_file:
        tokenizer_json = json_file.read()
        tokenizer = tokenizer_from_json(tokenizer_json)
except Exception as e:
    raise RuntimeError(f"Error loading tokenizer: {e}")



nltk.download('stopwords')


# Clean the Text
def clean(text):
  text = text.lower()

  # Remove punctuation and numbers
  SUB = re.compile(r'[^a-zA-Z]')
  text = SUB.sub(' ', text)

  # Remove multiple spaces
  text = re.sub(r'\s+', ' ', text)

  # Remove single words
  text = re.sub(r'\s+[a-zA-Z]\s+', ' ', text)

  # remove stop words
  expression = re.compile(r'\b(' + r'|'.join(stopwords.words('english')) + r')\b\s*')
  text = expression.sub('', text)

  return text

# Define a data model for the prediction request
class PredictionRequest(BaseModel):
    text: str


@models.get("/")
def read_root():
    return {"message": "Welcome to the FastAPI Deep Learning Model Server"}

@models.post("/predict/")
async def predict(sentence:  PredictionRequest):
    try:
        box = []
        
        cleaned_sentence = clean(sentence.text)
        
        box.append(cleaned_sentence)
        
        box = tokenizer.texts_to_sequences(box)
        padded = pad_sequences(box, padding = 'post', maxlen = 30)
        
        prediction = model.predict(padded) * 10
        return {"prediction": prediction.tolist()}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Prediction error: {e}")

#if __name__ == '__main__':
    #uvicorn.run('app:app', host='0.0.0.0', port=8000)