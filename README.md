# Emergency App

Implemented an Android Application for Emergencies in Kotlin 1.7.10 version and stored data with Firebase, which is meant to help any person, at any time, even those with disabilities, very easy. :heart:


## Project Overview

Emergency App provides users with a personal assistant feature that offers quick access to trained assistants who can guide and assist them with any issues or problems they may encounter. These assistants provide relevant information and guidance to help users navigate any emergency or difficult situations they may face. Additionally, the app includes a map feature that displays the user's current location and nearby points of interest, such as hospitals and pharmacies, allowing them to easily locate the resources they need in a time of crisis.

Furthermore, the app includes a list of emergency phone numbers for various institutions in Romania, making it quick and easy for users to reach out for help. The app is designed to be user-friendly and intuitive, enabling users to access critical information and resources with ease. Its development has allowed us to improve our skills in Kotlin programming, Android app development, and integration of various APIs, ensuring that we continue to provide the best possible experience for our users.

By combining these various features, our app provides users with a comprehensive emergency response tool that can help them navigate any situation they may encounter. Our personal assistant feature serves as a crucial first point of contact, providing users with the guidance and support they need during a crisis. The map feature and emergency phone number list further enhance the app's functionality, providing users with additional resources to help them address their needs quickly and efficiently.

**The team is composed of**:

- Popescu Paullo-Robertto-Karloss (331)
- Voiculescu Alina-Virginia (331)


## Project Requirements

- RecyclerView with search function ✔️
- A navigation method: Navigation Drawer ✔️
- A Share method (Android ShareSheet) ✔️
- Local Notifications with Deep Links or using Firebase ✔️
- An operation with a camera (picture) ✔️
- Maps: permission from the user + overlays and markers + detail activity ✔️
- An animation using ObjectAnimator and one using MotionLayout ✔️
- Social Login (Google) ✔️
- UI adapted for landscape mode ✔️
- Data persistence using databases (remote: Firebase) ✔️
- Web services (Firebase) ✔️
- Video Playback ✔️


## App Features
- User registration and login with email or Gmail account
- User profile with personal information
- Email confirmation with "OPEN GMAIL APP" and "OPEN OUTLOOK APP" buttons for easy email confirmation
- Quick guide (preview) of the app for user convenience
- Chat feature for users to communicate with assistants for help
- Assistant search bar for faster access to available assistants
- Ability for assistants to communicate with colleagues
- Image upload feature for sending images from gallery or taking real-time photos
- Navigation drawer for faster navigation between app pages
- Share feature to share the app via WhatsApp, message, etc.
- Assistance with medical emergencies, pet emergencies, and safety emergencies
- Emergency phone numbers page with all the emergency numbers in Romania
- Map feature with real-time location display and nearby locations (hospitals, police centers, vet, etc.)
- Location details displayed when clicking on a location on the map
- Main menu animations for buttons and logo animation on app launch
- UI adapted for landscape mode
- Tutorial video accessible from the navigation drawer to help users understand how to use the app


## Demo of the app

You can find our Demo [(here)](https://www.youtube.com/watch?v=kEvRWK-4Mks)
or by clicking on this photo:

<a href="http://www.youtube.com/watch?feature=player_embedded&v=kEvRWK-4Mks" target="_blank"><img src="http://img.youtube.com/vi/kEvRWK-4Mks/0.jpg" alt="Video - Traffic lights for a crosswalk" width="480" height="360" border="10"></a>

## Technologies used

- Android Studio
- Firebase
- Kotlin
- Git


## Build tools used

- Gradle


## Latest version

The stable version of the app can be found on the `main` branch of this repository.


### Additional

- Used Adapter Design Pattern for Messages and User.
- Used regex expressions for some validations:
  - Year of birth
  - Email
  - Phone number
  - Password
- Every user who wants to register must confirm his account through his personal email.
- Every user who forgot his password and wants to reset it must confirm this action through his personal email.
- Handled various exceptions, such as those generated by Firebase Database statements, to make debugging easier.


## Support

If you have any issues or questions about the app, please contact us at emergencyapp.lifesavers@gmail.com. We're here to help!
