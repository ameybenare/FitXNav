# FitXNav


## **Overview**
This fitness tracking application enables users to monitor and manage their physical activities through four main fragments: **Home**, **Steps**, **Settings**, and **Route**. The app allows users to set fitness goals, track steps, view their walking route on a map, and manage location and physical activity permissions for seamless real-time tracking.

This app was developed as a project for **Mobile Systems** by developers  **Ameya Benare** and **Amit Yadav**.

## **Key Features**
1. **Home Fragment**: Set and track your fitness goals. View real-time step counts and distance covered based on your activity.
2. **Steps Fragment**: Start and stop step counting sessions, with a visual progress bar and toast notifications when goals are achieved.
3. **Settings Fragment**: Manage location and GPS permissions to enable live tracking and view real-time addresses.
4. **Route Fragment**: Visualize your walking path in real-time on a map, with a red line indicating your route and markers revealing location addresses.
5. **Login Activity**: Users can sign up manually using email and password or via Google. Firebase authentication and real-time database are used for user management.
6. **Navigation Drawer**: Access to all fragments and display user details like name and email.
7. **SharedPreferences**: Used to store and retrieve user preferences locally, ensuring smooth transitions between app components.

## **Permissions Required**
- **Location Permission**: To enable the Route and Settings fragments for live tracking and route display.
- **Physical Activity Permission**: For accurate step counting in the Steps fragment.

## **Sign Up Options**
- **Manual Sign Up**: Email, username, and password (with 8 characters and special characters) are required.
- **Google Sign Up**: Users can select their Google account to register seamlessly.

## **Sensors Used**
- **Accelerometer**: For step counting.
- **GPS**: For live tracking and route mapping.
- **Activity Recognition**: For step counter initialization and session-based step counting.

