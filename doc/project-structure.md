# Project Directory Structure

This document provides an overview of the repository’s layout.

---

## Top-level Directories

- **`./app/android`** - Android client app. This is basically just a shim.

- **`./app/desktop`** - Experimental JVM desktop build using Compose for Desktop.

- **`./app/ios`** - iOS client app. Contains all Swift code written for the project.

- **`./baselineprofile`** - Generates baseline profiles for optimizing Android app startup and runtime performance.

- **`./doc`** - Documentation files, setup guides, and screenshots used in the README.

- **`./firebase-backend`** - Tools for testing Firebase usage and verifying integrations.

- **`./script`** - A collection of utility scripts (build helpers, release scripts, etc.).

- **`./service`** - Helper tools for backend integration, e.g. to upload API endpoint data to Firebase.

- **`./shared`** - Core application logic shared across all platforms. Organized into well-defined domains with task-specific modules, it reflects a deliberate effort to enforce separation of concerns and support a scalable architecture. 

- **`./tooling`** - Gradle build system helpers and custom tasks used across the project.
