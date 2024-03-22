This repository presents usage of libraries to manage data in Firestore from Spring Boot app.
The app publishes messages to PubSub when `users` Firestore collection is changed.

# To run:
1. Either
* use existing compute engine service account  
* or create new service account with permissions to manage Firestore
  
  Download and place service account file as src/main/resources/firestore-service-account-key.json
2. Run `gcloud auth application-default login` to use application default credentials to allow pubsub client to push messages
3. Set GOOGLE_CLOUD_PROJECT to point to your GCP project ID
3. Run main class
4. Perform http requests to trigger controller endpoints

[postman collection](users.postman_collection.json)
