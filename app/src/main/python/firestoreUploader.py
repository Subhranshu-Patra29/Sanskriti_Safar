import firebase_admin
from firebase_admin import credentials, firestore

# Initialize Firestore
cred = credentials.Certificate('serviceAccountsKey.json')
firebase_admin.initialize_app(cred)
db = firestore.client()

# Your JSON data
data = {
    "ChatbotConversations": [
        {
            "prompt": "Tell me about the Taj Mahal.",
            "response": "The Taj Mahal, located in Agra, is a UNESCO World Heritage Site and one of the Seven Wonders of the World. Built by Mughal Emperor Shah Jahan in memory of his wife Mumtaz Mahal, it symbolizes eternal love. The stunning white marble mausoleum features intricate carvings and reflects Indo-Islamic architectural brilliance. Visit during sunrise or sunset for the most mesmerizing views!",
            "timestamp": "2023-01-01T09:00:00"
        },
        {
            "prompt": "What is special about the Red Fort?",
            "response": "The Red Fort in Delhi, a UNESCO World Heritage Site, served as the main residence of Mughal emperors. Constructed by Shah Jahan in 1638, its red sandstone walls enclose a complex of palaces, gardens, and the famous Diwan-i-Khas. The fort is also the site where India's Prime Minister hoists the national flag on Independence Day.",
            "timestamp": "2023-01-01T15:00:00"
        },
        {
            "prompt": "Why is the Qutub Minar famous?",
            "response": "The Qutub Minar in Delhi is the tallest brick minaret in the world, standing at 73 meters. A UNESCO World Heritage Site, it was commissioned by Qutb al-Din Aibak in 1193 to celebrate Muslim rule in India. Its intricate carvings showcase verses from the Quran, reflecting the artistic excellence of the Delhi Sultanate.",
            "timestamp": "2023-01-01T21:00:00"
        },
        {
            "prompt": "Can you tell me about the Victoria Memorial?",
            "response": "The Victoria Memorial in Kolkata is a magnificent white marble structure built in honor of Queen Victoria. Opened in 1921, it combines British and Mughal architectural styles. The memorial houses a museum with artifacts from the British colonial era, offering a glimpse into India\u2019s historical past.",
            "timestamp": "2023-01-02T03:00:00"
        },
        {
            "prompt": "What makes the Konark Sun Temple unique?",
            "response": "The Konark Sun Temple in Odisha is a 13th-century marvel dedicated to Surya, the Sun God. Designed as a colossal chariot with intricately carved stone wheels and horses, it is a UNESCO World Heritage Site. The temple\u2019s exquisite carvings depict scenes from daily life, mythology, and the celestial.",
            "timestamp": "2023-01-02T09:00:00"
        },
        {
            "prompt": "What is the significance of Hampi?",
            "response": "Hampi, in Karnataka, was the capital of the Vijayanagara Empire and is now a UNESCO World Heritage Site. Known for its stunning ruins, temples, and bazaars, it includes iconic landmarks like the Virupaksha Temple and the Stone Chariot. Hampi's boulder-strewn landscape adds to its surreal charm.",
            "timestamp": "2023-01-02T15:00:00"
        },
        {
            "prompt": "Tell me about the Khajuraho Temples.",
            "response": "The Khajuraho Temples in Madhya Pradesh are renowned for their exquisite carvings and intricate sculptures depicting love, life, and devotion. Built by the Chandela dynasty between the 10th and 12th centuries, these UNESCO World Heritage Sites celebrate Indian art and spirituality.",
            "timestamp": "2023-01-02T21:00:00"
        },
        {
            "prompt": "What is special about the Golden Temple?",
            "response": "The Golden Temple, or Harmandir Sahib, in Amritsar is the holiest shrine for Sikhs. Its golden facade and serene water body, the Amrit Sarovar, attract millions of visitors annually. The temple symbolizes unity and equality, and its langar (community kitchen) serves free meals to thousands daily.",
            "timestamp": "2023-01-03T03:00:00"
        },
        {
            "prompt": "Why is the Charminar iconic?",
            "response": "The Charminar in Hyderabad is a symbol of the city's rich heritage. Built in 1591 by Muhammad Quli Qutb Shah, it marks the end of a devastating plague. The structure features four grand arches and is surrounded by the bustling Laad Bazaar, famous for bangles and traditional goods.",
            "timestamp": "2023-01-03T09:00:00"
        },
        {
            "prompt": "What makes Mysore Palace a must-visit?",
            "response": "The Mysore Palace in Karnataka is a stunning example of Indo-Saracenic architecture. It was the royal residence of the Wadiyar dynasty and is famous for its grand Dussehra celebrations. At night, the illuminated palace is a spectacular sight, drawing tourists from across the globe.",
            "timestamp": "2023-01-03T15:00:00"
        },
        {
            "prompt": "What is the importance of Sanchi Stupa?",
            "response": "The Sanchi Stupa in Madhya Pradesh is one of the oldest stone structures in India and a UNESCO World Heritage Site. Built by Emperor Ashoka in the 3rd century BCE, it symbolizes the life and teachings of Lord Buddha. The intricate gateways, or toranas, are masterpieces of Buddhist art.",
            "timestamp": "2023-01-03T21:00:00"
        },
        {
            "prompt": "Tell me about Nalanda Mahavihara.",
            "response": "Nalanda Mahavihara in Bihar was an ancient center of learning and a Buddhist monastery. A UNESCO World Heritage Site, it flourished from the 5th to 12th centuries, attracting scholars from across Asia. Its ruins reveal a rich legacy of education, philosophy, and art.",
            "timestamp": "2023-01-04T03:00:00"
        },
        {
            "prompt": "What makes the Ajanta and Ellora Caves famous?",
            "response": "The Ajanta and Ellora Caves in Maharashtra are UNESCO World Heritage Sites that showcase the artistry of ancient India. The Ajanta Caves are known for their Buddhist frescoes and sculptures, while the Ellora Caves feature a mix of Buddhist, Hindu, and Jain temples, including the remarkable Kailasa Temple.",
            "timestamp": "2023-01-04T09:00:00"
        },
        {
            "prompt": "Why was the Taj Mahal built?",
            "response": "The Taj Mahal was built by Mughal Emperor Shah Jahan in memory of his beloved wife, Mumtaz Mahal, who passed away during childbirth. It is a symbol of eternal love and is considered one of the finest examples of Mughal architecture, blending Persian, Islamic, and Indian styles.",
            "timestamp": "2023-01-04T15:00:00"
        },
        {
            "prompt": "What events are held at the Red Fort?",
            "response": "The Red Fort hosts the Independence Day celebrations in India every year. The Prime Minister hoists the national flag and delivers a speech to the nation. The fort also features light and sound shows depicting India's history and the Mughal era.",
            "timestamp": "2023-01-04T21:00:00"
        },
        {
            "prompt": "How tall is the Qutub Minar, and why was it constructed?",
            "response": "The Qutub Minar is 73 meters tall and is the tallest brick minaret in the world. It was constructed by Qutb al-Din Aibak in 1193 to celebrate the victory of Muslim rule in Delhi. Its inscriptions and carvings are a testament to the artistic legacy of the Delhi Sultanate.",
            "timestamp": "2023-01-05T03:00:00"
        },
        {
            "prompt": "Can I visit the Victoria Memorial at night?",
            "response": "Yes, the Victoria Memorial in Kolkata offers a beautiful view at night when it is illuminated. While the museum inside is closed after hours, the garden area around the memorial remains open for visitors to enjoy the lit structure and serene ambiance.",
            "timestamp": "2023-01-05T09:00:00"
        },
        {
            "prompt": "What is the meaning of the carvings on the Konark Sun Temple?",
            "response": "The carvings on the Konark Sun Temple depict various aspects of life, including scenes of daily activities, celestial beings, and mythological tales. The temple was designed as a chariot of the Sun God, symbolizing his journey across the sky. These carvings are a celebration of life and spirituality.",
            "timestamp": "2023-01-05T15:00:00"
        },
        {
            "prompt": "What are some must-see spots in Hampi?",
            "response": "In Hampi, don\u2019t miss the Virupaksha Temple, the Stone Chariot at the Vittala Temple complex, and the Hemakuta Hill for stunning views. Other highlights include the Royal Enclosure, Lotus Mahal, and the unique Tungabhadra River Coracles for a delightful experience.",
            "timestamp": "2023-01-05T21:00:00"
        },
        {
            "prompt": "What do the sculptures of the Khajuraho Temples represent?",
            "response": "The sculptures of the Khajuraho Temples represent various aspects of human life, including love, spirituality, and celebration. While famous for their depictions of intimacy, these carvings also showcase daily life, dancers, and deities, symbolizing the harmony between physical and spiritual existence.",
            "timestamp": "2023-01-06T03:00:00"
        },
        {
            "prompt": "What can I experience at the Golden Temple during my visit?",
            "response": "At the Golden Temple, you can witness the peaceful prayers, the sacred Guru Granth Sahib being read, and partake in the langar, a community meal that serves thousands every day. The temple complex is open to people of all religions and exudes a serene spiritual energy.",
            "timestamp": "2023-01-06T09:00:00"
        },
        {
            "prompt": "Why is the Charminar considered a historical landmark?",
            "response": "The Charminar, built in 1591 by Muhammad Quli Qutb Shah, marks the founding of Hyderabad. It was constructed to commemorate the city's recovery from a deadly plague and stands as a symbol of resilience. Its intricate arches and proximity to Laad Bazaar make it an iconic site.",
            "timestamp": "2023-01-06T15:00:00"
        },
        {
            "prompt": "What festivals are celebrated at the Mysore Palace?",
            "response": "The Mysore Palace is the center of the grand Dasara (Dussehra) festival, celebrated with great pomp every year. During this time, the palace is beautifully illuminated, and a procession featuring the idol of Goddess Chamundeshwari atop a golden throne is the highlight of the festivities.",
            "timestamp": "2023-01-06T21:00:00"
        },
        {
            "prompt": "What do the toranas at Sanchi Stupa signify?",
            "response": "The toranas, or gateways, at the Sanchi Stupa are intricately carved and depict the life and teachings of Lord Buddha. These carvings include scenes from Jataka tales, Buddha\u2019s enlightenment, and his path to Nirvana, symbolizing peace and spiritual growth.",
            "timestamp": "2023-01-07T03:00:00"
        },
        {
            "prompt": "Why is Nalanda Mahavihara considered a center of knowledge?",
            "response": "Nalanda Mahavihara was an ancient Buddhist university that attracted scholars from across Asia. It was a hub of learning for subjects like philosophy, medicine, and astronomy. Its ruins reveal an advanced education system with a rich legacy of intellectual and cultural exchange.",
            "timestamp": "2023-01-07T09:00:00"
        },
        {
            "prompt": "What is unique about the Kailasa Temple at Ellora?",
            "response": "The Kailasa Temple at Ellora Caves is a monolithic marvel carved entirely out of a single rock. Dedicated to Lord Shiva, it is part of the Ellora complex, which showcases Hindu, Buddhist, and Jain art. The temple\u2019s intricate carvings and massive scale make it a masterpiece of ancient engineering.",
            "timestamp": "2023-01-07T15:00:00"
        },
        {
            "prompt": "What can I learn from the paintings in Ajanta Caves?",
            "response": "The paintings in the Ajanta Caves depict scenes from the Jataka tales, illustrating the previous lives of Buddha. They also portray daily life, royal courts, and nature. These frescoes are renowned for their vibrant colors and intricate details, offering a glimpse into ancient Indian culture.",
            "timestamp": "2023-01-07T21:00:00"
        }
    ]
}

# Upload to Firestore
collection_name = 'ChatbotConversations'
for conversation in data["ChatbotConversations"]:
    db.collection(collection_name).add(conversation)

print("Data uploaded successfully!")
