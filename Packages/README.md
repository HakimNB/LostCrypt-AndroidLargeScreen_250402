# Packages Directory Overview

The [Packages](./) directory is responsible for managing the project's external package dependencies and module extensions through Unity's Package Manager (UPM). It dictates which library packages, tools, and engine subsystems are imported into the project, ensuring version parity and dependency resolution for team members and automated builds.

This directory contains configuration files that list active dependencies (such as Universal Render Pipeline, Input System, and 2D tools) and freeze their exact versions and resolved sub-dependencies. Through UPM, these files allow Unity to automatically retrieve and download required modules from the official package registry or custom Git repositories when initializing the project.

## Configuration Files

*   [manifest.json](./manifest.json): The package manifest file that declares direct dependencies for the project along with their target version constraints or Git URLs. It acts as the primary configuration input for the Unity Package Manager, specifying which packages should be loaded.
*   [packages-lock.json](./packages-lock.json): The package lock file generated and managed by Unity to record the exact resolved versions and checksum hashes of all direct and indirect dependencies. It ensures deterministic builds by locking the entire dependency graph to specific versions, preventing unexpected updates.
