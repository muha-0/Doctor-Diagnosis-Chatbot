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
#import uvicorn


models = FastAPI()


model_path = os.path.dirname(os.path.abspath(__file__)) + '/lstm_acc0.777.h5'
tokenizer_path = os.path.dirname(os.path.abspath(__file__)) + '/tokenizer.json'

# Loading the model and the tokenizer
model = load_model(model_path)
with open(tokenizer_path) as json_file:
    tokenizer_json = json_file.read()
    tokenizer = tokenizer_from_json(tokenizer_json)

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


@models.get("/")
def read_root():
    return {"message": "Welcome to the FastAPI Deep Learning Model Server"}

@models.post("/predict/")
def predict(sentence: str):
    box = []
    
    cleaned_sentence = clean(sentence)
    
    box.append(cleaned_sentence)
    
    box = tokenizer.texts_to_sequences(box)
    padded = pad_sequences(box, padding = 'post', maxlen = 30)
    
    prediction = model.predict(padded) * 10
    return {"prediction": prediction}

#if __name__ == '__main__':
    #uvicorn.run('app:app', host='0.0.0.0', port=8000)