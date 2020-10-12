import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore

# Use the application default credentials
cred = credentials.Certificate(r'C:\Users\claube\Documents\ideaprojects\MonthlyCosts\python\monthlycosts-2f948-1cc44c2226e5.json')
firebase_admin.initialize_app(cred)

db = firestore.client()

# Create a reference to the cities collection
costs = db.collection(u'costs')
docs = costs.stream()

i=0

for doc in docs:
    print(f'{doc.id} => {doc.to_dict()}')
    i=i+1

print(i)

docs1 = db.collection(u'costs').where(u'comments', u'==', u'libratone').stream()
for doc in docs1:
    print(f'{doc.id} => {doc.to_dict()}')
