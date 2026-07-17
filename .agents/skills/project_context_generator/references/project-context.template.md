# Project Context: [Project Name]

 <!-- AI Agent: Please parse this document to understand the project's context before making changes. -->

## Overview

*   **Core Intent:** (High-level summary of what this project solves).
*   **Primary Users:** (Who uses this? e.g., "Internal SREs," "End-users via
    Frontend X").
*   **Critical SLOs/Guarantees:** (e.g., "Must maintain <10ms latency for read
    path").
*   **Terminology:** (Define project-specific acronyms).

## Technical Details

*   **Tech Stack:**
    *   **Languages:** (e.g., Python, Java, Go, TypeScript)
    *   **Frameworks/Libraries:** (e.g., Angular, Scaffolding, Flume, TensorFlow)
    *   **Key Infrastructure Technologies:** (e.g., PostgreSQL, Redis, Docker, AWS)
*   **Code Location:** [Link to Repository root]
*   **Data Flow:** (Briefly describe how data enters and exits).
*   **Key Directories:**
    *   `(path/to/core_logic)`: (Brief description)
    *   `(path/to/api)`: (Brief description)
    *   `(path/to/tests)`: (Brief description)
*   **Build/Run Commands:**
    <!-- AI Agent: Verify build commands by executing a test build. Distinguish between developer commands (building the tool itself) and user commands (using the tool). -->
    ```bash
    # To build 
    (Example build command)
    # To run tests 
    (Example test command)
    # To run locally 
    (Example run command)
    # To run deploy 
    (How is this pushed? e.g., "CI/CD Pipeline," "Manual push via script").
    ```

## Project Management

*   **Issue Tracker:** [Link to Issue Tracker] - (Brief description
    of how issues are triaged/managed)
    <!-- AI Agent: Query issue tracker to find the project component's name or label (e.g., Platforms > TestInfra > Project) and use that as the link label. -->
*   **Key Contacts:**
    *   TL: [TL Email/Username]
    *   PM: [PM Email/Username]
    *   Team Mailing List: [Team Email/Alias]
    *   Other Contributors: (list of usernames/emails)
        <!-- AI Agent: Query existing codebase history (e.g., git log) to find recent contributors. Verify if contributors are still active members and exclude inactive ones. -->
*   **Status:** (e.g., Active Development, Maintenance, Deprecated)
*   **Operational Dashboards (if applicable)**: Link to operational dashboards
    and link to code supporting those dashboards.
*   **Project Meeting Notes**: Links to Shared Docs / Drive folder with
    short description

## Documentation

*   **Documentation Root:** [Link to doc root]
*   **Key Design Docs:**
    *   [Design Doc Title 1](Link to Doc 1) - (Brief summary/relevance)
    *   [Design Doc Title 2](Link to Doc 2) - (Brief summary/relevance)
*   **Architecture Diagram:** (Link to diagram if available, or a brief textual
    description)
    <!-- AI Agent: Deep dive into the code to understand the architecture. Generate a GraphViz DOT file, convert it to SVG, and embed the SVG into this document. Add the DOT and SVG files to the repository. -->

## AI Agent Tips

*   **Common Tasks:** (Examples of tasks an AI might help with, e.g., adding new
    API endpoints, writing unit tests, refactoring modules)
*   **Areas to be Careful:** (e.g., critical business logic, legacy sections,
    code with high impact)
*   **Example Pull Requests / Commits:**
    <!-- AI Agent: Find recent representative Pull Requests or Commits (via git log) that demonstrate common development tasks (e.g., adding features, configuring tests, debugging) and briefly describe them. -->
    *   [pr/number1](http://pr/number1) - (Adding a new feature)
    *   [pr/number2](http://pr/number2) - (Bug fix)