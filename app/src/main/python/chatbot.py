# --------------------------------------------------------------------------------------------------------
#                                         IMPORTING LIBRARIES

import firebase_admin
from firebase_admin import credentials, firestore
import google.generativeai as genai
import logging

# --------------------------------------------------------------------------------------------------------
#                                         FIREBASE INITIALIZATION

# Initialize Firebase app
cred = credentials.Certificate("serviceAccountsKey.json")  # Path to your Firebase service account key
firebase_admin.initialize_app(cred)
db = firestore.client()

# --------------------------------------------------------------------------------------------------------

def getChats():
    conversationHistory = []
    try:
        # Fetch chat history from Firestore
        chats_ref = db.collection("ChatbotConversations")
        docs = chats_ref.order_by("timestamp").stream()

        for doc in docs:
            data = doc.to_dict()
            prompt = data.get("prompt", "")
            response = data.get("response", "")
            
            conversationHistory.append({
                "role": "user",
                "parts": [prompt]
            })
            conversationHistory.append({
                "role": "model",
                "parts": [response]
            })

        return conversationHistory
    except Exception as e:
        print(f"Error fetching chats: {e}")
        return []

def uploadChat(prompt, response):
    try:
        # Add a chat entry to Firestore
        chat_data = {
            "prompt": prompt,
            "response": response,
            "timestamp": firestore.SERVER_TIMESTAMP,
        }
        db.collection("ChatbotConversations").add(chat_data)
    except Exception as e:
        print(f"Error uploading chat: {e}")

# --------------------------------------------------------------------------------------------------------
#                                         GEMINI MODEL CONNECTION

def connect():
    try:
        # Configure the Generative AI model
        genai.configure(api_key="AIzaSyCufaJ-njreSU-aZx2A-wAdQiOUpcSM52k")  # Replace with your actual API key

        generation_config = {
            "temperature": 0.5,
            "top_p": 0.95,
            "top_k": 64,
            "max_output_tokens": 250,
            "response_mime_type": "text/plain",
        }

        safety_settings = getSafetySettings()

        model = genai.GenerativeModel(
            model_name="gemini-1.5-pro",
            generation_config=generation_config,
            safety_settings=safety_settings,
            system_instruction = """
Your name is Samarth. You are a cultural and heritage conversation assistant for the Sanskriti Safar app. 
Your role is to provide meaningful replies by offering accurate information about India's cultural and heritage sites, 
traditions, and practices. 

1. Your responses should highlight India's rich heritage, incorporating cultural anecdotes and historical context 
   wherever applicable.
2. Always use a conversational, friendly, and engaging tone that resonates with users of all ages and backgrounds, 
   especially young learners and travelers.
3. Adapt your tone to reflect the user’s mood—e.g., excited when discussing festivals or solemn when discussing memorials.
4. Answer queries about heritage sites, traditions, festivals, languages, art forms, and cuisines in a relatable and 
   easy-to-understand way.
5. If users ask for travel recommendations, provide insights about nearby places, must-visit spots, and unique cultural 
   experiences.
6. Infuse a subtle Indian essence in your responses—using examples, quotes, or phrases related to Indian culture, 
   avoiding stereotypes.
7. Ensure all information is factually accurate, concise, and within 200 words where required.
8. Avoid sounding robotic; maintain a warm and approachable personality, reflecting enthusiasm for Indian culture and 
   heritage.

You are an empathetic, knowledgeable, and engaging guide, passionate about spreading awareness and appreciation of 
India's heritage through meaningful interactions. Remember to stay authentic and human-like in your responses.
""",
        )
        return model
    except Exception as e:
        logging.error(f"Error connecting to Gemini: {e}")
        return None

def getSafetySettings():
    safety = [
        {
            "category": "HARM_CATEGORY_HATE_SPEECH",
            "threshold": "BLOCK_ONLY_HIGH"
        },
        {
            "category": "HARM_CATEGORY_DANGEROUS_CONTENT",
            "threshold": "BLOCK_ONLY_HIGH"
        },
        {
            "category": "HARM_CATEGORY_SEXUALLY_EXPLICIT",
            "threshold": "BLOCK_ONLY_HIGH"
        },
        {
            "category": "HARM_CATEGORY_HARASSMENT",
            "threshold": "BLOCK_ONLY_HIGH"
        }
    ]
    return safety

# --------------------------------------------------------------------------------------------------------
#                                         CHAT SESSION CREATION

def createChatSession(prompt):
    conversationHistory = getChats()
    model = connect()

    edited_prompt = prompt + "\nWithin 150 words"

    if model:
        # Create the Chat Session with history fetched from Firestore
        chat_session = model.start_chat(
            history=conversationHistory
        )

        # Get the response from Gemini
        response = chat_session.send_message(edited_prompt)

        # Upload the prompt and response to Firestore
        uploadChat(prompt, response.text)

        return response.text
    else:
        return "Error: Could not connect to the AI model."

# --------------------------------------------------------------------------------------------------------
#                                         MAIN FUNCTION

def main():
    try:
        # Prompt input
        prompt = input("Enter your query: ")
        response = createChatSession(prompt)
        print(response)
    except Exception as e:
        logging.error(f"Error in main function: {e}")

if __name__ == "__main__":
    main()
